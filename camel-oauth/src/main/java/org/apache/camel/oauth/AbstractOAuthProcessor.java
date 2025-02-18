package org.apache.camel.oauth;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.oauth.OAuth.CAMEL_OAUTH_COOKIE;

public abstract class AbstractOAuthProcessor implements Processor {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final String procName = getClass().getSimpleName();

    protected Optional<OAuth> findOAuth(CamelContext camelContext) {
        return OAuthFactory.getOAuthFactory(camelContext).findOAuth(camelContext);
    }

    protected OAuth findOAuthOrThrow(CamelContext context) {
        return findOAuth(context).orElseThrow(() -> new NoSuchElementException("No OAuth"));
    }

    protected void logRequestHeaders(String msgPrefix, Message msg) {
        log.debug("{} - Request headers ...", msgPrefix);
        msg.getHeaders().forEach((k, v) -> {
            log.debug("   {}: {}", k, v);
        });
    }

    protected void sendRedirect(Message msg, String redirectUrl) {
        log.debug("Redirect to: {}", redirectUrl);
        msg.setHeader(Exchange.HTTP_RESPONSE_CODE, 302);
        msg.setHeader("Location", redirectUrl);
        msg.setBody("");
    }

    protected void setSessionCookie(Message msg, OAuthSession session) {
        var sessionId = session.getSessionId();
        var cookie = "%s=%s; Path=/; HttpOnly; SameSite=None; Secure".formatted(CAMEL_OAUTH_COOKIE, sessionId);
        msg.setHeader("Set-Cookie", cookie);
        log.debug("Set-Cookie: {}", cookie);
    }
}
