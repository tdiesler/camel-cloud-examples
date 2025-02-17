package org.apache.camel.oauth;

import org.apache.camel.Exchange;

public class OAuthLogoutProcessor extends AbstractOAuthProcessor {

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();

        findOAuth(context).ifPresent(oauth -> {

            var maybeSession = oauth.getSession(exchange);
            maybeSession.flatMap(OAuthSession::getUserProfile).ifPresent(user -> {

                maybeSession.get().removeUserProfile();

                var postLogoutUrl = getProperty(exchange, CAMEL_OAUTH_LOGOUT_REDIRECT_URI)
                        .orElse(null);

                var params = new LogoutRequestParams()
                        .setRedirectUri(postLogoutUrl)
                        .setUser(user);

                var logoutUrl = oauth.logoutRequestUrl(params);

                log.info("OAuth logout: {}", logoutUrl);
                exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
                exchange.getMessage().setHeader("Location", logoutUrl);
                exchange.getMessage().setBody("");
            });
        });
    }
}