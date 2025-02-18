package org.apache.camel.oauth;

public class TokenCredentials implements Credentials {

    private String token;

    public String getToken() {
        return token;
    }

    public TokenCredentials setToken(String token) {
        this.token = token;
        return this;
    }
}
