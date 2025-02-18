package org.apache.camel.oauth.vertx;

import org.apache.camel.CamelContext;
import org.apache.camel.component.platform.http.vertx.VertxPlatformHttpRouter;
import org.apache.camel.oauth.OAuth;
import org.apache.camel.oauth.OAuthFactory;

public final class OAuthFactoryVertx implements OAuthFactory {

    // Register BouncyCastle as a security provider
    // Does not fix: java.security.NoSuchAlgorithmException: RSA-OAEP
    // Security.addProvider(new BouncyCastleProvider())

    public OAuth createOAuth(CamelContext ctx) {
        var registry = ctx.getRegistry();
        var router = VertxPlatformHttpRouter.lookup(ctx);
        var oauth = new OAuthVertx(router.vertx());
        registry.bind(OAuth.class.getName(), oauth);
        return oauth;
    }
}
