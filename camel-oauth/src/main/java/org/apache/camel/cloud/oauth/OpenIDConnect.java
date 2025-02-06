package org.apache.camel.cloud.oauth;

public interface OpenIDConnect {

    OpenIDConfig discover(OpenIDConfig config) throws Exception;

    String authRequestUrl(AuthRequestParams params);

    String logoutRequestUrl(LogoutRequestParams params);

    OpenIDUser tokenRequest(TokenRequestParams params);
}
