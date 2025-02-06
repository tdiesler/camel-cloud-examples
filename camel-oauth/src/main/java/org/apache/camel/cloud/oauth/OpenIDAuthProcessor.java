package org.apache.camel.cloud.oauth;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.platform.http.vertx.VertxPlatformHttpRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenIDAuthProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(OpenIDAuthProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        var camelContext = exchange.getContext();
        var camelRegistry = camelContext.getRegistry();
        var msg = exchange.getMessage();

        log.info("OidcAuthProcessor - Request headers ...");
        msg.getHeaders().forEach((k, v) -> {
            log.info("   {}: {}", k, v);
        });

        var user = camelRegistry.lookupByNameAndType("OpenIDUser", OpenIDUser.class);
        if (user == null) {
            var httpRouter = VertxPlatformHttpRouter.lookup(camelContext);
            var vertx = httpRouter.vertx();

            // [TODO] needs to be scoped on the session
            var postLoginUrl = msg.getHeader(Exchange.HTTP_URL, String.class);
            camelRegistry.bind("OIDCPostLoginUrl", postLoginUrl);

            var oidc = new VertxOpenIDConnect(vertx);
            camelRegistry.bind("OpenIDConnect", oidc);

            // [TODO] use constants
            String baseUrl = PropertiesHelper.getProperty(camelContext, "camel.oidc.auth.baseUri");
            String clientId = PropertiesHelper.getProperty(camelContext, "camel.oidc.auth.clientId");
            String clientSecret = PropertiesHelper.getProperty(camelContext, "camel.oidc.auth.clientSecret");
            String redirectUri = PropertiesHelper.getProperty(camelContext, "camel.oidc.auth.redirectUri");
            oidc.discover(new OpenIDConfig(baseUrl).setClientId(clientId).setClientSecret(clientSecret));

            var authUrl = oidc.authRequestUrl(new AuthRequestParams().setRedirectUri(redirectUri));

            log.info("Redirect to: {}", authUrl);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
            exchange.getMessage().setHeader("Location", authUrl);
            exchange.getMessage().setBody("");

        } else {
            log.info("OidcAuthProcessor - User attributes: {}", user.attributes());
            log.info("OidcAuthProcessor - User principal: {}", user.principal());
        }

        log.info("OidcAuthProcessor - Done");
    }
}
