package org.apache.camel.cloud.oauth;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenIDCallbackProcessor implements Processor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void process(Exchange exchange) throws Exception {
        var camelContext = exchange.getContext();
        var camelRegistry = camelContext.getRegistry();
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

        var oidc = camelRegistry.lookupByNameAndType("OpenIDConnect", OpenIDConnect.class);
        if (oidc == null) {
            log.error("OpenIDConnect object not found in CamelContext");
            exchange.getMessage().setHeader("CamelHttpResponseCode", 500);
            exchange.getMessage().setBody("OpenIDConnect not initialized");
            return;
        }

        try {
            String redirectUri = PropertiesHelper.getProperty(camelContext, "camel.oidc.auth.redirectUri");
            var user = oidc.tokenRequest(new TokenRequestParams()
                    .setRedirectUri(redirectUri)
                    .setFlowType(OpenIDFlowType.AUTH_CODE)
                    .setCode(code));

            log.info("User Authenticated - attributes: {}", user.attributes());
            log.info("User Authenticated - principal: {}", user.principal());
            camelRegistry.bind("OpenIDUser", user);

            // [TODO] needs to be scoped on the session
            var postLoginUrl = camelRegistry.lookupByNameAndType("OIDCPostLoginUrl", String.class);
            if (postLoginUrl == null) {
                postLoginUrl = "http://localhost:8080/";
            }

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
