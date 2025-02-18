package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.oauth.vertx.VertxOAuthFactory;

public abstract class OAuthFactory {

    public static OAuthFactory getOAuthFactory(CamelContext ctx) {
        var registry = ctx.getRegistry();
        var factory = registry.lookupByNameAndType(OAuthFactory.class.getName(), OAuthFactory.class);
        if (factory == null) {
            // Note, different implementations would require a plugin mechanism
            factory = new VertxOAuthFactory();
            registry.bind(OAuthFactory.class.getName(), factory);
        }
        return factory;
    }

    public abstract OAuth createOAuth(Exchange exchange);

    public Optional<OAuth> findOAuth(CamelContext ctx) {
        var registry = ctx.getRegistry();
        var oauth = registry.lookupByNameAndType(OAuth.class.getName(), OAuth.class);
        return Optional.ofNullable(oauth);
    }

}
