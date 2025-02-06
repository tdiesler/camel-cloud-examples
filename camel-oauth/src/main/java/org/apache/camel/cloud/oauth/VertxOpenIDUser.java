package org.apache.camel.cloud.oauth;

import java.util.Map;

import io.vertx.ext.auth.User;

public class VertxOpenIDUser implements OpenIDUser {

    private final User delegate;

    public VertxOpenIDUser(User delegate) {
        this.delegate = delegate;
    }

    User getDelegate() {
        return delegate;
    }

    @Override
    public Map<String, Object> attributes() {
        return delegate.attributes().getMap();
    }

    @Override
    public Map<String, Object> principal() {
        return delegate.principal().getMap();
    }

    @Override
    public String subject() {
        if (principal().containsKey("username")) {
            return principal().get("username").toString();
        } else if (principal().containsKey("userHandle")) {
            return principal().get("userHandle").toString();
        } else {
            if (attributes().containsKey("idToken")) {
                var idToken = delegate.attributes().getJsonObject("idToken");
                if (idToken.containsKey("sub")) {
                    return idToken.getString("sub");
                }
            }
            return getClaim("sub");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getClaim(String key) {
        if (attributes().containsKey("rootClaim")) {
            var rootClaim = delegate.attributes().getJsonObject("rootClaim");
            if (rootClaim.containsKey(key)) {
                return (T)rootClaim.getValue(key);
            }
        }
        if (attributes().containsKey(key)) {
            return (T)attributes().get(key);
        } else {
            return (T)(principal().getOrDefault(key, null));
        }
    }
}
