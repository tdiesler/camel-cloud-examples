package org.apache.camel.cloud.oauth;

public class LogoutRequestParams {

    private OpenIDUser user;
    private String redirectUri;

    public OpenIDUser getUser() {
        return user;
    }

    public LogoutRequestParams setUser(OpenIDUser user) {
        this.user = user;
        return this;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public LogoutRequestParams setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }
}
