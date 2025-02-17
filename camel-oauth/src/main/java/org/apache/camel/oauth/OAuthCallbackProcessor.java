package org.apache.camel.oauth;

import org.apache.camel.Exchange;

public class OAuthCallbackProcessor extends AbstractOAuthProcessor {

    @Override
    public void process(Exchange exchange) {
        var context = exchange.getContext();
        var msg = exchange.getMessage();

        logRequestHeaders("OAuthCallbackProcessor", msg);

        // Validate auth callback request headers/parameters
        //
        var code = msg.getHeader("code", String.class);
        if (code == null) {
            log.error("Authorization code is missing in the request");
            msg.setHeader("CamelHttpResponseCode", 400);
            msg.setBody("Authorization code missing");
            return;
        }

        // Require an active OAuthSession
        //
        var oauth = findOAuthOrThrow(context);
        var session = oauth.getSession(exchange).orElseThrow();

        try {
            var userProfile = sendTokenRequest(oauth, exchange, code);
            log.info("Authenticated ...");
            userProfile.logAttributes();
            userProfile.logPrincipal();

            session.putUserProfile(userProfile);

            var postLoginUrl = session.removeValue("OAuthPostLoginUrl").orElse(null);
            if (postLoginUrl == null) {
                var redirectUri = getRequiredProperty(exchange, CAMEL_OAUTH_REDIRECT_URI);
                var lastSlashIdx = redirectUri.lastIndexOf('/');
                postLoginUrl = redirectUri.substring(0, lastSlashIdx + 1);
            }

            log.info("Redirect to: {}", postLoginUrl);
            msg.setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
            msg.setHeader("Location", postLoginUrl);
            msg.setBody("");

        } catch (Exception err) {
            log.error("Access Token error", err);
            msg.setHeader("CamelHttpResponseCode", 500);
            msg.setBody("Authentication failed");
        }

        log.info("OAuthCallbackProcessor - Done");
    }

    private UserProfile sendTokenRequest(OAuth oauth, Exchange exchange, String code) {
        String redirectUri = getRequiredProperty(exchange, CAMEL_OAUTH_REDIRECT_URI);
        var userProfile = oauth.tokenRequest(new TokenRequestParams()
                .setFlowType(OAuthFlowType.AUTH_CODE)
                .setRedirectUri(redirectUri)
                .setCode(code));
        return userProfile;
    }
}
