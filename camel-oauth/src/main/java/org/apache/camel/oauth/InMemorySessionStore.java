package org.apache.camel.oauth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemorySessionStore implements OAuthSessionStore {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, OAuthSession> sessions = new HashMap<>();
    private final String cookieName = "oauth.session";

    @Override
    public Optional<OAuthSession> getSession(Exchange exchange) {

        var cookies = getCookies(exchange);

        if (cookies.get(cookieName) == null) {
            log.warn("No '{}' Cookie in HTTP request", cookieName);
            return Optional.empty();
        }

        var sessionId = cookies.get(cookieName);
        var session = sessions.get(sessionId);
        if (session == null) {
            log.warn("No OAuthSession for: {}", sessionId);
            return Optional.empty();
        }

        return Optional.of(session);
    }

    public OAuthSession createSession(Exchange exchange) {

        var session = new InMemorySession();
        var sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, session);

        var msg = exchange.getMessage();
        var cookie = "%s=%s; Path=/; HttpOnly; SameSite=None; Secure".formatted(cookieName, sessionId);
        msg.setHeader("Set-Cookie", cookie);
        log.info("New OAuthSession: 'Set-Cookie: {}'", cookie);

        return session;
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
