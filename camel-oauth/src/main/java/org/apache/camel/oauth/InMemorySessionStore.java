package org.apache.camel.oauth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemorySessionStore implements OAuthSessionStore {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, OAuthSession> sessions = new HashMap<>();

    @Override
    public Optional<OAuthSession> getSession(Exchange exchange) {

        Map<String, String> cookies = getCookies(exchange);
        var maybeSessionId = Optional.ofNullable(cookies.get("session"));
        if (maybeSessionId.isEmpty()) {
            log.warn("No 'session' Cookie in HTTP request");
            return Optional.empty();
        }
        var sessionId = maybeSessionId.get();

        var session = sessions.get(sessionId);
        if (session == null) {
            log.warn("Creating new OAuthSession for Cookie: {}", sessionId.substring(8) + "...");
            session = new InMemorySession();
            sessions.put(sessionId, session);
        }

        return Optional.of(session);
    }

    private Map<String, String> getCookies(Exchange exchange) {
        var msg = exchange.getMessage();
        var maybeCookie = Optional.ofNullable(msg.getHeader("Cookie"));
        if (maybeCookie.isEmpty()) {
            log.warn("No Cookie in HTTP request");
            return Map.of();
        }

        var value = maybeCookie.get().toString();
        var cookieMap = Arrays.stream(value.split(";"))
                .map(String::trim)
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr.length > 1 ? arr[1] : ""
                ));

        return cookieMap;
    }
}
