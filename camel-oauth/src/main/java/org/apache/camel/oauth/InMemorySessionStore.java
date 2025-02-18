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

import static org.apache.camel.oauth.OAuth.CAMEL_OAUTH_COOKIE;
import static org.apache.camel.oauth.OAuth.CAMEL_OAUTH_SESSION_ID;

public class InMemorySessionStore implements OAuthSessionStore {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, OAuthSession> sessions = new HashMap<>();

    @Override
    public Optional<OAuthSession> getSession(Exchange exchange) {
        var msg = exchange.getMessage();

        // First, try the CamelOAuthSessionId header
        var sessionId = msg.getHeader(CAMEL_OAUTH_SESSION_ID, String.class);

        // Fallback to our session cookie
        if (sessionId == null) {
            var cookies = getCookies(exchange);
            if (cookies.get(CAMEL_OAUTH_COOKIE) == null) {
                log.warn("No '{}' Cookie in HTTP request", CAMEL_OAUTH_COOKIE);
                return Optional.empty();
            }
            sessionId = cookies.get(CAMEL_OAUTH_COOKIE);
        }

        var maybeSession = Optional.ofNullable(sessions.get(sessionId));
        if (maybeSession.isEmpty()) {
            log.warn("No OAuthSession for: {}", sessionId);
        }

        return maybeSession;
    }

    public OAuthSession createSession(Exchange exchange) {

        var sessionId = UUID.randomUUID().toString();
        var session = new InMemorySession(sessionId);
        sessions.put(sessionId, session);

        var msg = exchange.getMessage();
        log.info("New OAuthSession: {}", sessionId);
        msg.setHeader(OAuth.CAMEL_OAUTH_SESSION_ID, sessionId);

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
