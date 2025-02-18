package org.apache.camel.oauth;

public class UserCredentials implements Credentials {

    private final UserProfile userProfile;

    public UserCredentials(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
}
