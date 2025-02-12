package org.apache.camel.oauth;

import org.apache.camel.Exchange;
import org.apache.camel.component.platform.http.vertx.VertxPlatformHttpRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthCodeFlowProcessor extends AbstractOAuthProcessor {

    private static final Logger log = LoggerFactory.getLogger(AuthCodeFlowProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        var camelContext = exchange.getContext();
        var camelRegistry = camelContext.getRegistry();
        var msg = exchange.getMessage();

        log.info("OAuthProcessor - Request headers ...");
        msg.getHeaders().forEach((k, v) -> log.info("   {}: {}", k, v));

        var oauth = getOAuthConnector(camelContext, () -> {
            var vertx = VertxPlatformHttpRouter.lookup(camelContext).vertx();
            var newOauth = new VertxOAuthConnector(vertx);
            storeOAuthConnector(camelContext, newOauth);
            return newOauth;
        }).orElseThrow();

        // [TODO] GHI-6 Validate current user profile
        var maybeUser = oauth.getUserProfile(exchange);
        if (maybeUser.isEmpty()) {

            // [TODO] GHI-5 Post login url binds to camel context rather than http session
            var postLoginUrl = msg.getHeader(Exchange.HTTP_URL, String.class);
            camelRegistry.bind("OAuthPostLoginUrl", postLoginUrl);

            var baseUrl = getRequiredProperty(exchange, CAMEL_OAUTH_BASE_URI);
            var redirectUri = getRequiredProperty(exchange, CAMEL_OAUTH_REDIRECT_URI);
            var clientId = getRequiredProperty(exchange, CAMEL_OAUTH_CLIENT_ID);
            var clientSecret = getProperty(exchange, CAMEL_OAUTH_CLIENT_SECRET).orElse(null);

            var config = new OAuthConfig(baseUrl).
                    setClientSecret(clientSecret).
                    setClientId(clientId);

            oauth.discover(config);

            var params = new AuthRequestParams().setRedirectUri(redirectUri);
            var authUrl = oauth.authRequestUrl(params);

            log.info("Redirect to: {}", authUrl);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
            exchange.getMessage().setHeader("Location", authUrl);
            exchange.getMessage().setBody("");

        } else {
            var userProfile = maybeUser.orElseThrow();
            log.info("OAuthProcessor - User attributes: {}", userProfile.attributes());
            log.info("OAuthProcessor - User principal: {}", userProfile.principal());
        }

        log.info("OAuthProcessor - Done");
    }
}
