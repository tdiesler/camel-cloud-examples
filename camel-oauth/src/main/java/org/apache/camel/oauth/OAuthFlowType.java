package org.apache.camel.oauth;

public enum OAuthFlowType {
    AUTH_CODE("authorization_code"),
    CLIENT_CREDS("client_credentials");

    OAuthFlowType(String flowType) {
    }
}
