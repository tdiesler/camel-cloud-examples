package org.apache.camel.test.oauth;

import org.junit.jupiter.api.Assumptions;

import static org.apache.camel.test.oauth.KeycloakAdmin.AdminParams;
import static org.apache.camel.test.oauth.KeycloakAdmin.ClientParams;
import static org.apache.camel.test.oauth.KeycloakAdmin.RealmParams;
import static org.apache.camel.test.oauth.KeycloakAdmin.UserParams;

abstract class AbstractKeycloakTest {

    static final int port = 8080; // AvailablePortFinder.getNextAvailable();

    static final String APP_BASE_URL =  "http://127.0.0.1:" + port + "/";
    static final String KEYCLOAK_BASE_URL = "https://keycloak.local:30443/";
    static final String TEST_REALM = "camel";
    static final String TEST_CLIENT_ID = "camel-client";
    static final String TEST_CLIENT_SECRET = "camel-client-secret";

    static KeycloakAdmin admin;
    static boolean removeRealm;

    static void setupKeycloakRealm() throws Exception {

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
                            .setRedirectUri(APP_BASE_URL + "auth"));
            removeRealm = true;
        }
    }

    static void removeKeycloakRealm() {
        if (admin != null && removeRealm) {
            admin.removeRealm().close();
        }
    }
}
