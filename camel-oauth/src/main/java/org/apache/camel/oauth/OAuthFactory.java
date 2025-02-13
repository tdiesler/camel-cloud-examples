package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.CamelContext;

public interface OAuthFactory {

    default Optional<OAuth> findOAuth(CamelContext ctx) {
        var registry = ctx.getRegistry();
        var oauth = registry.lookupByNameAndType(OAuth.class.getName(), OAuth.class);
        return Optional.ofNullable(oauth);
    }

    OAuth createOAuth(CamelContext ctx);
}
