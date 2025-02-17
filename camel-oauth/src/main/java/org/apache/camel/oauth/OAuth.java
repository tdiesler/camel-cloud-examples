package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.Exchange;

public interface OAuth {

    void discover(OAuthConfig config) throws OAuthException;

    String authRequestUrl(AuthRequestParams params);

    UserProfile authenticate(UserProfile user) throws OAuthException;

    UserProfile tokenRequest(TokenRequestParams params) throws OAuthException;

    UserProfile refresh(UserProfile user) throws OAuthException;

    String logoutRequestUrl(LogoutRequestParams params);

    default Optional<OAuthSession> getSession(Exchange exchange) {
        return getSessionStore().getSession(exchange);
    }

    default OAuthSession createSession(Exchange exchange) {
        return getSessionStore().createSession(exchange);
    }

    OAuthSessionStore getSessionStore();
}
