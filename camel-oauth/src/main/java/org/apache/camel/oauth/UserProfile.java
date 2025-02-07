package org.apache.camel.oauth;

import java.util.Map;

public interface UserProfile {

    Map<String, Object> attributes();
    Map<String, Object> principal();
    <T> T getClaim(String key);
    String subject();
}
