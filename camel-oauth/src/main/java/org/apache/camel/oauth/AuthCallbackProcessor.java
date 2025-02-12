package org.apache.camel.oauth;

import org.apache.camel.Exchange;

public class AuthCallbackProcessor extends AbstractOAuthProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        var context = exchange.getContext();
        var registry = context.getRegistry();
        var msg = exchange.getMessage();

        log.info("OpenIDAuthCallback - Request headers ...");
        msg.getHeaders().forEach((k, v) -> {
            log.info("   {}: {}", k, v);
        });

        var code = msg.getHeader("code", String.class);
        if (code == null) {
            log.error("Authorization code is missing in the request");
            exchange.getMessage().setHeader("CamelHttpResponseCode", 400);
            exchange.getMessage().setBody("Authorization code missing");
            return;
        }

        var oauth = assertOAuthConnector(context);
        try {

            String redirectUri = getRequiredProperty(exchange, CAMEL_OAUTH_REDIRECT_URI);
            var userProfile = oauth.tokenRequest(new TokenRequestParams()
                    .setRedirectUri(redirectUri)
                    .setFlowType(OAuthFlowType.AUTH_CODE)
                    .setCode(code)).orElseThrow();

            log.info("User Authenticated - attributes: {}", userProfile.attributes());
            log.info("User Authenticated - principal: {}", userProfile.principal());

            oauth.putUserProfile(exchange, userProfile);

            // [TODO] GHI-5 Post login url binds to camel context rather than http session
            var postLoginUrl = registry.lookupByNameAndType("OAuthPostLoginUrl", String.class);

            log.info("Redirect to: {}", postLoginUrl);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
            exchange.getMessage().setHeader("Location", postLoginUrl);
            exchange.getMessage().setBody("");

        } catch (Exception err) {
            log.error("Access Token error", err);
            exchange.getMessage().setHeader("CamelHttpResponseCode", 500);
            exchange.getMessage().setBody("Authentication failed");
        }

        log.info("OpenIDAuthCallback - Done");
    }
}
