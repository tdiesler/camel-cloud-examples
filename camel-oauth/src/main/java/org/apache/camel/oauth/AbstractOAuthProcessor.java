package org.apache.camel.oauth;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.platform.http.vertx.VertxPlatformHttpRouter;
import org.apache.camel.spi.PropertiesComponent;
import org.apache.camel.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOAuthProcessor implements Processor {

    public static final String CAMEL_OIDC_AUTH_BASE_URI = "camel.oidc.auth.baseUri";
    public static final String CAMEL_OIDC_AUTH_CLIENT_ID = "camel.oidc.auth.clientId";
    public static final String CAMEL_OIDC_AUTH_CLIENT_SECRET = "camel.oidc.auth.clientSecret";
    public static final String CAMEL_OIDC_AUTH_LOGOUT_REDIRECT_URI = "camel.oidc.auth.logout.redirectUri";
    public static final String CAMEL_OIDC_AUTH_REDIRECT_URI = "camel.oidc.auth.redirectUri";

    protected final Logger log = LoggerFactory.getLogger(getClass());

    Optional<OpenIdConnector> getOpenIdConnector(CamelContext context) {
        return getOpenIdConnector(context, null);
    }

    Optional<OpenIdConnector> getOpenIdConnector(CamelContext context, Supplier<OpenIdConnector> supplier) {
        var camelRegistry =context.getRegistry();
        var oidc = camelRegistry.lookupByNameAndType("OpenIdConnector", OpenIdConnector.class);
        if (oidc == null && supplier != null) {
            oidc = supplier.get();
        }
        return Optional.ofNullable(oidc);
    }

    OpenIdConnector getRequiredOpenIdConnector(CamelContext context) {
        return getOpenIdConnector(context, null).orElseThrow(() -> new NoSuchElementException("No OpenIdConnector"));
    }

    void putOpenIdConnector(CamelContext context, OpenIdConnector oidc) {
        var camelRegistry = context.getRegistry();
        camelRegistry.bind("OpenIdConnector", oidc);
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
