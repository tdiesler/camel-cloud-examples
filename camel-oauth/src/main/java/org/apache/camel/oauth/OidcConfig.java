package org.apache.camel.oauth;

public class OidcConfig {

    private final String baseUrl;
    private String clientId;
    private String clientSecret;
    private String authorizationPath;
    private String tokenPath;
    private String revocationPath;
    private String logoutPath;
    private String userInfoPath;
    private String introspectionPath;

    public OidcConfig(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public OidcConfig setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public OidcConfig setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getAuthorizationPath() {
        return authorizationPath;
    }

    public OidcConfig setAuthorizationPath(String authorizationPath) {
        this.authorizationPath = authorizationPath;
        return this;
    }

    public String getTokenPath() {
        return tokenPath;
    }

    public OidcConfig setTokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
        return this;
    }

    public String getRevocationPath() {
        return revocationPath;
    }

    public OidcConfig setRevocationPath(String revocationPath) {
        this.revocationPath = revocationPath;
        return this;
    }

    public String getLogoutPath() {
        return logoutPath;
    }

    public OidcConfig setLogoutPath(String logoutPath) {
        this.logoutPath = logoutPath;
        return this;
    }

    public String getUserInfoPath() {
        return userInfoPath;
    }

    public OidcConfig setUserInfoPath(String userInfoPath) {
        this.userInfoPath = userInfoPath;
        return this;
    }

    public String getIntrospectionPath() {
        return introspectionPath;
    }

    public OidcConfig setIntrospectionPath(String introspectionPath) {
        this.introspectionPath = introspectionPath;
        return this;
    }
}
