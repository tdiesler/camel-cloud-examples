package org.apache.camel.cloud.oauth;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenIDLogoutProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(OpenIDLogoutProcessor.class);

    @Override
    public void process(Exchange exchange) {
        var camelContext = exchange.getContext();
        Registry camelRegistry = camelContext.getRegistry();

        var oidc = camelRegistry.lookupByNameAndType("OpenIDConnect", OpenIDConnect.class);
        var user = camelRegistry.lookupByNameAndType("OpenIDUser", OpenIDUser.class);
        if (oidc != null && user != null) {

            camelRegistry.unbind("OpenIDUser");

            // [TODO] use constants
            String postLogoutUrl = PropertiesHelper.getProperty(camelContext, "camel.oidc.auth.logout.redirectUri");
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
