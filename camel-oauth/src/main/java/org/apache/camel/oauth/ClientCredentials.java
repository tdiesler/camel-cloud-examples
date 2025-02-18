package org.apache.camel.oauth;

public class ClientCredentials implements Credentials {

    private String clientId;
    private String clientSecret;

    public String getClientId() {
        return clientId;
    }

    public ClientCredentials setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public ClientCredentials setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
