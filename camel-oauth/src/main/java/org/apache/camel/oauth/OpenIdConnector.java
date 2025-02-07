package org.apache.camel.oauth;

import java.util.Optional;
import java.util.function.Supplier;

import org.apache.camel.Exchange;

public interface OpenIdConnector {

    void discover(OidcConfig config) throws Exception;

    String authRequestUrl(AuthRequestParams params);

    String logoutRequestUrl(LogoutRequestParams params);

    Optional<UserProfile> tokenRequest(TokenRequestParams params);

    Optional<UserProfile> getUserProfile(Exchange exchange);

    void putUserProfile(Exchange exchange, UserProfile userProfile);

    void removeUserProfile(Exchange exchange);
}
