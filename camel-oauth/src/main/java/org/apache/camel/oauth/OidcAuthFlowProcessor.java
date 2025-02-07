package org.apache.camel.oauth;

import org.apache.camel.Exchange;
import org.apache.camel.component.platform.http.vertx.VertxPlatformHttpRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OidcAuthFlowProcessor extends AbstractOAuthProcessor {

    private static final Logger log = LoggerFactory.getLogger(OidcAuthFlowProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        var camelContext = exchange.getContext();
        var camelRegistry = camelContext.getRegistry();
        var msg = exchange.getMessage();

        log.info("OidcAuthProcessor - Request headers ...");
        msg.getHeaders().forEach((k, v) -> log.info("   {}: {}", k, v));

        var oidc = getOpenIdConnector(camelContext, () -> {
            var vertx = VertxPlatformHttpRouter.lookup(camelContext).vertx();
            var newOidc = new VertxOpenIdConnector(vertx);
            putOpenIdConnector(camelContext, newOidc);
            return newOidc;
        }).orElseThrow();

        // [TODO] GHI-6 Validate current user profile
        var maybeUser = oidc.getUserProfile(exchange);
        if (maybeUser.isEmpty()) {

            // [TODO] GHI-5 Post login url binds to camel context rather than http session
            var postLoginUrl = msg.getHeader(Exchange.HTTP_URL, String.class);
            camelRegistry.bind("OidcPostLoginUrl", postLoginUrl);

            String baseUrl = getRequiredProperty(exchange, CAMEL_OIDC_AUTH_BASE_URI);
            String redirectUri = getRequiredProperty(exchange, CAMEL_OIDC_AUTH_REDIRECT_URI);
            String clientId = getRequiredProperty(exchange, CAMEL_OIDC_AUTH_CLIENT_ID);
            String clientSecret = getProperty(exchange, CAMEL_OIDC_AUTH_CLIENT_SECRET).orElse(null);
            oidc.discover(new OidcConfig(baseUrl).setClientId(clientId).setClientSecret(clientSecret));

            var authUrl = oidc.authRequestUrl(new AuthRequestParams().setRedirectUri(redirectUri));

            log.info("Redirect to: {}", authUrl);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
            exchange.getMessage().setHeader("Location", authUrl);
            exchange.getMessage().setBody("");

        } else {
            var userProfile = maybeUser.orElseThrow();
            log.info("OidcAuthProcessor - User attributes: {}", userProfile.attributes());
            log.info("OidcAuthProcessor - User principal: {}", userProfile.principal());
        }

        log.info("OidcAuthProcessor - Done");
    }
}
