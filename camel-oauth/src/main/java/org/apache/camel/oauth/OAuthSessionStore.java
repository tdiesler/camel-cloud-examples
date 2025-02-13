package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.Exchange;

public interface OAuthSessionStore {

    Optional<OAuthSession> getSession(Exchange exchange);
}
