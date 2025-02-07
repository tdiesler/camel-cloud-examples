package org.apache.camel.oauth;

public class LogoutRequestParams {

    private UserProfile user;
    private String redirectUri;

    public UserProfile getUser() {
        return user;
    }

    public LogoutRequestParams setUser(UserProfile user) {
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
