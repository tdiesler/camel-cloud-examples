package org.apache.camel.oauth;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOAuthProcessor implements Processor {

    public static final String CAMEL_OAUTH_BASE_URI = "camel.oauth.baseUri";
    public static final String CAMEL_OAUTH_CLIENT_ID = "camel.oauth.clientId";
    public static final String CAMEL_OAUTH_CLIENT_SECRET = "camel.oauth.clientSecret";
    public static final String CAMEL_OAUTH_LOGOUT_REDIRECT_URI = "camel.oauth.logout.redirectUri";
    public static final String CAMEL_OAUTH_REDIRECT_URI = "camel.oauth.redirectUri";

    protected final Logger log = LoggerFactory.getLogger(getClass());

    Optional<OAuthConnector> getOAuthConnector(CamelContext context) {
        return getOAuthConnector(context, null);
    }

    Optional<OAuthConnector> getOAuthConnector(CamelContext context, Supplier<OAuthConnector> supplier) {
        var camelRegistry =context.getRegistry();
        var oauth = camelRegistry.lookupByNameAndType("OAuthConnector", OAuthConnector.class);
        if (oauth == null && supplier != null) {
            oauth = supplier.get();
        }
        return Optional.ofNullable(oauth);
    }

    OAuthConnector assertOAuthConnector(CamelContext context) {
        return getOAuthConnector(context, null).orElseThrow(() -> new NoSuchElementException("No OAuthConnector"));
    }

    void storeOAuthConnector(CamelContext context, OAuthConnector oauth) {
        var camelRegistry = context.getRegistry();
        camelRegistry.bind("OAuthConnector", oauth);
    }

    Optional<String> getProperty(Exchange exchange, String key) {
        var ctx = exchange.getContext();
        return ctx.getPropertiesComponent().resolveProperty(key);
    }

    String getRequiredProperty(Exchange exchange, String key) {
        Optional<String> optval = getProperty(exchange, key);
        return optval.orElseThrow(() -> new NoSuchElementException(key));
    }
}
