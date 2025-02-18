package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.oauth.OAuth.CAMEL_OAUTH_COOKIE;
import static org.apache.camel.oauth.OAuth.CAMEL_OAUTH_REDIRECT_URI;
import static org.apache.camel.oauth.OAuthProperties.getRequiredProperty;

public class OAuthCodeFlowProcessor extends AbstractOAuthProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();
        var msg = exchange.getMessage();

        logRequestHeaders(procName, msg);

        // Find or create the OAuth instance
        //
        var oauth = findOAuth(context).orElseGet(() -> {
            var factory = OAuthFactory.getOAuthFactory(context);
            return factory.createOAuth(exchange);
        });

        // Get or create the OAuthSession
        //
        var session = oauth.getSession(exchange)
                .or(() -> Optional.of(oauth.createSession(exchange)))
                .get();

        // Authenticate an existing UserProfile from the OAuthSession
        //
        if (session.getUserProfile().isPresent()) {
            var userProfile = session.getUserProfile().get();
            if (userProfile.ttl() < 0L) {
                userProfile = oauth.refresh(userProfile);
                userProfile.logDetails("Refreshed");
            } else {
                var creds = new UserCredentials(userProfile);
                var updProfile = oauth.authenticate(creds);
                userProfile.merge(updProfile);
                userProfile.logDetails("Re-Authenticated");
            }
            session.putUserProfile(userProfile);
        }

        // Fallback to the authorization code flow
        //
        if (session.getUserProfile().isEmpty()) {

            var postLoginUrl = msg.getHeader(Exchange.HTTP_URL, String.class);
            session.putValue("OAuthPostLoginUrl", postLoginUrl);

            var redirectUri = getRequiredProperty(exchange, CAMEL_OAUTH_REDIRECT_URI);
            var params = new OAuthCodeFlowParams().setRedirectUri(redirectUri);
            var authRequestUrl = oauth.buildCodeFlowAuthRequestUrl(params);

            setSessionCookie(msg, session);
            sendRedirect(msg, authRequestUrl);
        }

        log.info("{} - Done", procName);
    }
}
