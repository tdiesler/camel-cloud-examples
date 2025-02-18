package org.apache.camel.oauth.vertx;

import org.apache.camel.Exchange;
import org.apache.camel.component.platform.http.vertx.VertxPlatformHttpRouter;
import org.apache.camel.oauth.OAuth;
import org.apache.camel.oauth.OAuthFactory;

public final class VertxOAuthFactory extends OAuthFactory {

    // Register BouncyCastle as a security provider
    // Does not fix: java.security.NoSuchAlgorithmException: RSA-OAEP
    // Security.addProvider(new BouncyCastleProvider())

    public OAuth createOAuth(Exchange exchange) {
        var context = exchange.getContext();
        var registry = context.getRegistry();
        var router = VertxPlatformHttpRouter.lookup(context);
        var oauth = new VertxOAuth(router.vertx());
        oauth.discoverOAuthConfig(exchange);
        registry.bind(OAuth.class.getName(), oauth);
        return oauth;
    }
}
