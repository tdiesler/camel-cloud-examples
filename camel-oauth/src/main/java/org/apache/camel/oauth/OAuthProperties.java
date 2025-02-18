package org.apache.camel.oauth;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.camel.Exchange;

public final class OAuthProperties {

    public static Optional<String> getProperty(Exchange exchange, String key) {
        var ctx = exchange.getContext();
        return ctx.getPropertiesComponent().resolveProperty(key);
    }

    public static String getRequiredProperty(Exchange exchange, String key) {
        Optional<String> optval = getProperty(exchange, key);
        return optval.orElseThrow(() -> new NoSuchElementException(key));
    }
}
