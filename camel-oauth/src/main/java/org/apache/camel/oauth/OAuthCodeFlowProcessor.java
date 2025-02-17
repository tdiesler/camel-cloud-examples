package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthCodeFlowProcessor extends AbstractOAuthProcessor {

    private static final Logger log = LoggerFactory.getLogger(OAuthCodeFlowProcessor.class);

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();
        var msg = exchange.getMessage();

        logRequestHeaders("OAuthCodeFlowProcessor", msg);

        // Find or create the OAuth instance
        //
        var oauth = findOAuth(context).orElseGet(() -> {
            var factory = getOAuthFactory(context);
            return factory.createOAuth(context);
        });

        // Get or create the OAuth session
        //
        var session = oauth.getSession(exchange)
                .or(() -> Optional.of(oauth.createSession(exchange)))
                .get();

        var userProfile = session.getUserProfile().orElse(null);

        // Verify an existing UserProfile
        //
        if (userProfile != null) {

            log.info("Existing ...");
            userProfile.logAttributes();
            userProfile.logPrincipal();

            if (userProfile.ttl() < 0L) {
                userProfile = oauth.refresh(userProfile);
                log.info("Refreshed ...");
            } else {
                userProfile = oauth.authenticate(userProfile);
                log.info("Authenticated ...");
            }

            session.putUserProfile(userProfile);
            userProfile.logAttributes();
            userProfile.logPrincipal();
        }

        // If there is no authenticated UserProfile, or
        // it is not valid (anymore), initiate authentication
        if (userProfile == null) {
            initiateAuthenticationCodeFlow(oauth, session, exchange);
        }

        log.info("OAuthCodeFlowProcessor - Done");
    }

    private void initiateAuthenticationCodeFlow(OAuth oauth, OAuthSession session, Exchange exchange) {
        var msg = exchange.getMessage();

        var postLoginUrl = msg.getHeader(Exchange.HTTP_URL, String.class);
        session.putValue("OAuthPostLoginUrl", postLoginUrl);

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
        msg.setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
        msg.setHeader("Location", authUrl);
        msg.setBody("");
    }
}
