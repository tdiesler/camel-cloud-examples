package org.apache.camel.oauth;

import java.util.Optional;

public interface OAuthSession {

    default Optional<UserProfile> getUserProfile() {
        return getValue(UserProfile.class.getName(), UserProfile.class);
    }

    default void putUserProfile(UserProfile profile) {
        putValue(UserProfile.class.getName(), profile);
    }

    default void removeUserProfile() {
        removeValue(UserProfile.class.getName());
    }

    <T> Optional<T> getValue(String key, Class<T> clazz);

    <T> void putValue(String key, T value);

    <T> Optional<T> removeValue(String key);
}
