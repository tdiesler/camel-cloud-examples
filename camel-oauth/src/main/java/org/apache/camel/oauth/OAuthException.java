package org.apache.camel.oauth;

import java.util.Optional;

import org.apache.camel.CamelContext;

public class OAuthException extends RuntimeException {

    public OAuthException(String msg) {
        super(msg);
    }

    public OAuthException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
