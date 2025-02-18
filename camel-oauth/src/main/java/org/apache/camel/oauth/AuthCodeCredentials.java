package org.apache.camel.oauth;

public class AuthCodeCredentials implements Credentials {

    private String code;
    private String redirectUri;

    public String getCode() {
        return code;
    }

    public AuthCodeCredentials setCode(String code) {
        this.code = code;
        return this;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public AuthCodeCredentials setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }
}
