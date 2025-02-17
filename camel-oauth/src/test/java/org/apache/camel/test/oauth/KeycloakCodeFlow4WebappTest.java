package org.apache.camel.test.oauth;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.platform.http.main.MainHttpServer;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.oauth.OAuthCallbackProcessor;
import org.apache.camel.oauth.OAuthCodeFlowProcessor;
import org.apache.camel.oauth.OAuthLogoutProcessor;
import org.apache.camel.spi.PropertiesComponent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.apache.camel.oauth.AbstractOAuthProcessor.CAMEL_OAUTH_BASE_URI;
import static org.apache.camel.oauth.AbstractOAuthProcessor.CAMEL_OAUTH_CLIENT_ID;
import static org.apache.camel.oauth.AbstractOAuthProcessor.CAMEL_OAUTH_CLIENT_SECRET;
import static org.apache.camel.oauth.AbstractOAuthProcessor.CAMEL_OAUTH_LOGOUT_REDIRECT_URI;
import static org.apache.camel.oauth.AbstractOAuthProcessor.CAMEL_OAUTH_REDIRECT_URI;
import static org.apache.camel.test.oauth.KeycloakAdmin.AdminParams;
import static org.apache.camel.test.oauth.KeycloakAdmin.ClientParams;
import static org.apache.camel.test.oauth.KeycloakAdmin.RealmParams;
import static org.apache.camel.test.oauth.KeycloakAdmin.UserParams;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeycloakCodeFlow4WebappTest {

    private static final int port = 8080; // AvailablePortFinder.getNextAvailable();

    private static final String APP_BASE_URL =  "http://127.0.0.1:" + port + "/";
    private static final String KEYCLOAK_BASE_URL = "https://keycloak.local:30443/";
    private static final String TEST_REALM = "camel";
    private static final String TEST_CLIENT_ID = "camel-client";
    private static final String TEST_CLIENT_SECRET = "camel-client-secret";

    private static KeycloakAdmin admin;
    private static CamelContext camelContext;

    @BeforeAll
    static void setUp() throws Exception {

        String callbackUri = APP_BASE_URL + "auth";

        admin = new KeycloakAdmin(new AdminParams(KEYCLOAK_BASE_URL));
        Assumptions.assumeTrue(admin.isKeycloakRunning(), "Keycloak is not running");

        // Setup Keycloak realm, client, user
        //
        if (!admin.realmExists(TEST_REALM)) {
            admin.withRealm(new RealmParams(TEST_REALM))
                    .withUser(new UserParams("alice")
                            .setEmail("alice@example.com")
                            .setFirstName("Alice")
                            .setLastName("Brown"))
                    .withClient(new ClientParams(TEST_CLIENT_ID)
                            .setClientSecret(TEST_CLIENT_SECRET)
                            .setLogoutRedirectUri(APP_BASE_URL)
                            .setServiceAccountsEnabled(true)
                            .setRedirectUri(callbackUri));
        }

        camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("platform-http:/")
                        .setBody(simple("resource:classpath:index.html"));
                from("platform-http:/static/styles.css")
                        .setBody(simple("resource:classpath:styles.css"));
                from("platform-http:/protected")
                        .process(new OAuthCodeFlowProcessor())
                        .setBody(simple("resource:classpath:protected.html"));
                from("platform-http:/logout")
                        .process(new OAuthLogoutProcessor())
                        .process(exc -> exc.getContext().getGlobalOptions().put("OAuthLogout", "ok"));
                from("platform-http:/auth")
                        .process(new OAuthCallbackProcessor());
            }
        });

        PropertiesComponent props = camelContext.getPropertiesComponent();
        props.addInitialProperty(CAMEL_OAUTH_BASE_URI, KEYCLOAK_BASE_URL + "realms/" + TEST_REALM);
        props.addInitialProperty(CAMEL_OAUTH_REDIRECT_URI, callbackUri);
        props.addInitialProperty(CAMEL_OAUTH_CLIENT_ID, TEST_CLIENT_ID);
        props.addInitialProperty(CAMEL_OAUTH_CLIENT_SECRET, TEST_CLIENT_SECRET);
        props.addInitialProperty(CAMEL_OAUTH_LOGOUT_REDIRECT_URI, APP_BASE_URL);

        MainHttpServer httpServer = new MainHttpServer();
        httpServer.setPort(port);

        camelContext.addService(httpServer);
        camelContext.start();
    }

    @AfterAll
    static void tearDown() {
        if (camelContext != null) {
            camelContext.stop();
        }
        if (admin != null) {
            admin.removeRealm().close();
        }
    }

    @Test
    void testCodeFlowAuth() throws Exception {

        // Verify Realm, Client, and User exist
        var keycloak = admin.getKeycloak();
        Assertions.assertNotNull(keycloak.realm(TEST_REALM).toRepresentation());
        assertEquals(1, keycloak.realm(TEST_REALM).clients().findByClientId(TEST_CLIENT_ID).size());
        assertEquals(1, keycloak.realm(TEST_REALM).users().search("alice").size());

        System.out.println("✅ Keycloak realm, client, and user created successfully!");
        System.out.println("✅ Open: " + APP_BASE_URL);

        // Open WebApp in Browser (works on MacOS)
        // Runtime.getRuntime().exec("open " + APP_BASE_URL);

        boolean logoutOk = false;
        for (int i=0; !logoutOk && i < 40; i++) { // timeout after 20sec
            var options = camelContext.getGlobalOptions();
            if ("ok".equals(options.get("OAuthLogout"))) {
                System.out.println("✅ OAuthLogout - ok");
                logoutOk = true;
            }
            if (i % 4 == 0 ) {
                System.out.printf("Waiting on logout: %d/%d - %s%n", i, 40, APP_BASE_URL+ "/logout");
            }
            Thread.sleep(500L);
        }
    }
}
