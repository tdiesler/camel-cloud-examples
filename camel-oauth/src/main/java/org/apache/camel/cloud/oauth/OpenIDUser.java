package org.apache.camel.cloud.oauth;

import java.util.Map;

public interface OpenIDUser {

    Map<String, Object> attributes();
    Map<String, Object> principal();
    <T> T getClaim(String key);
    String subject();
}
