package org.apache.camel.oauth;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Registry;

public class OidcLogoutProcessor extends AbstractOAuthProcessor {

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();

        var maybeOidc = getOpenIdConnector(context);
        if (maybeOidc.isPresent()) {
            var oidc = maybeOidc.get();

            var maybeUser = oidc.getUserProfile(exchange);
            if (maybeUser.isPresent()) {

                var user = maybeUser.get();
                oidc.removeUserProfile(exchange);

                String postLogoutUrl = getProperty(exchange, CAMEL_OIDC_AUTH_LOGOUT_REDIRECT_URI).orElse(null);
                var logoutUrl = oidc.logoutRequestUrl(new LogoutRequestParams()
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