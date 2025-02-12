package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.Exchange;

public interface OAuthConnector {

    void discover(OAuthConfig config) throws Exception;

    String authRequestUrl(AuthRequestParams params);

    String logoutRequestUrl(LogoutRequestParams params);

    Optional<UserProfile> tokenRequest(TokenRequestParams params);

    Optional<UserProfile> getUserProfile(Exchange exchange);

    void putUserProfile(Exchange exchange, UserProfile userProfile);

    void removeUserProfile(Exchange exchange);
}
