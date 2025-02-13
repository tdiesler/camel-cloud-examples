package org.apache.camel.oauth;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.oauth.vertx.OAuthFactoryVertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOAuthProcessor implements Processor {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public static final String CAMEL_OAUTH_BASE_URI = "camel.oauth.baseUri";
    public static final String CAMEL_OAUTH_CLIENT_ID = "camel.oauth.clientId";
    public static final String CAMEL_OAUTH_CLIENT_SECRET = "camel.oauth.clientSecret";
    public static final String CAMEL_OAUTH_LOGOUT_REDIRECT_URI = "camel.oauth.logout.redirectUri";
    public static final String CAMEL_OAUTH_REDIRECT_URI = "camel.oauth.redirectUri";

    OAuthFactory getOAuthFactory(CamelContext ctx) {
        var registry = ctx.getRegistry();
        var factory = registry.lookupByNameAndType(OAuthFactory.class.getName(), OAuthFactory.class);
        if (factory == null) {
            // Note, different implementations would require a plugin mechanism
            factory = new OAuthFactoryVertx();
            registry.bind(OAuthFactory.class.getName(), factory);
        }
        return factory;
    }

    Optional<OAuth> findOAuth(CamelContext camelContext) {
        return getOAuthFactory(camelContext).findOAuth(camelContext);
    }

    OAuth findOAuthOrThrow(CamelContext context) {
        return findOAuth(context).orElseThrow(() -> new NoSuchElementException("No OAuth"));
    }

    Optional<String> getProperty(Exchange exchange, String key) {
        var ctx = exchange.getContext();
        return ctx.getPropertiesComponent().resolveProperty(key);
    }

    String getRequiredProperty(Exchange exchange, String key) {
        Optional<String> optval = getProperty(exchange, key);
        return optval.orElseThrow(() -> new NoSuchElementException(key));
    }

    protected void logRequestHeaders(Message msg) {
        log.info("Request headers ...");
        msg.getHeaders().forEach((k, v) -> {
            log.info("   {}: {}", k, v);
        });
    }
}
