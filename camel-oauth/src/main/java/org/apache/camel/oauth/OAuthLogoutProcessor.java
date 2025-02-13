package org.apache.camel.oauth;

import org.apache.camel.Exchange;

public class OAuthLogoutProcessor extends AbstractOAuthProcessor {

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();

        var maybeOauth = findOAuth(context);
        if (maybeOauth.isPresent()) {
            var oauth = maybeOauth.get();

            var session = oauth.getSession(exchange);
            var maybeUser = session.getUserProfile();
            if (maybeUser.isPresent()) {

                var user = maybeUser.get();
                session.removeUserProfile();

                String postLogoutUrl = getProperty(exchange, CAMEL_OAUTH_LOGOUT_REDIRECT_URI).orElse(null);
                var logoutUrl = oauth.logoutRequestUrl(new LogoutRequestParams()
                        .setRedirectUri(postLogoutUrl)
                        .setUser(user));

                log.info("OAuth logout: {}", logoutUrl);
                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
                exchange.getMessage().setHeader("Location", logoutUrl);
                exchange.getMessage().setBody("");
            }
        }
    }

}