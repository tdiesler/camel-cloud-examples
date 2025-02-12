package org.apache.camel.oauth;

import org.apache.camel.Exchange;

public class AuthLogoutProcessor extends AbstractOAuthProcessor {

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();

        var maybeOauth = getOAuthConnector(context);
        if (maybeOauth.isPresent()) {
            var oauth = maybeOauth.get();

            var maybeUser = oauth.getUserProfile(exchange);
            if (maybeUser.isPresent()) {

                var user = maybeUser.get();
                oauth.removeUserProfile(exchange);

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