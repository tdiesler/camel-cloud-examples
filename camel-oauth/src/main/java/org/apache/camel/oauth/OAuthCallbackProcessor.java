package org.apache.camel.oauth;

import org.apache.camel.Exchange;

public class OAuthCallbackProcessor extends AbstractOAuthProcessor {

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();
        var msg = exchange.getMessage();

        logRequestHeaders(msg);

        // Validate auth callback request headers/parameters
        var code = msg.getHeader("code", String.class);
        if (code == null) {
            log.error("Authorization code is missing in the request");
            exchange.getMessage().setHeader("CamelHttpResponseCode", 400);
            exchange.getMessage().setBody("Authorization code missing");
            return;
        }

        var oauth = findOAuthOrThrow(context);
        try {

            var userProfile = sendTokenRequest(oauth, exchange, code);
            log.info("Authenticated ...");
            userProfile.logAttributes();
            userProfile.logPrincipal();

            var session = oauth.getSession(exchange);
            var postLoginUrl = session.removeValue("OAuthPostLoginUrl")
                    .orElseThrow(() -> new RuntimeException("No OAuthPostLoginUrl"));

            log.info("Redirect to: {}", postLoginUrl);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
            exchange.getMessage().setHeader("Location", postLoginUrl);
            exchange.getMessage().setBody("");

        } catch (Exception err) {
            log.error("Access Token error", err);
            exchange.getMessage().setHeader("CamelHttpResponseCode", 500);
            exchange.getMessage().setBody("Authentication failed");
        }

        log.info("OAuthCallbackProcessor - Done");
    }

    private UserProfile sendTokenRequest(OAuth oauth, Exchange exchange, String code) {

        String redirectUri = getRequiredProperty(exchange, CAMEL_OAUTH_REDIRECT_URI);
        var userProfile = oauth.tokenRequest(new TokenRequestParams()
                .setRedirectUri(redirectUri)
                .setFlowType(OAuthFlowType.AUTH_CODE)
                .setCode(code));


        var session = oauth.getSession(exchange);
        session.putUserProfile(userProfile);

        return userProfile;
    }
}
