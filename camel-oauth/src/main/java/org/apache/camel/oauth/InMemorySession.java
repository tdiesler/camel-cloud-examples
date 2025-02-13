package org.apache.camel.oauth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemorySession implements OAuthSession {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, Object> values = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getValue(String key, Class<T> clazz) {
        return (Optional<T>) Optional.ofNullable(values.get(key));
    }

    @Override
    public <T> void putValue(String key, T value) {
        values.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> removeValue(String key) {
        var maybeValue = getValue(key, Object.class);
        values.remove(key);
        return (Optional<T>) maybeValue;
    }
}
