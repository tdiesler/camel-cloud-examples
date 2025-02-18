package org.apache.camel.oauth;

public class OAuthException extends RuntimeException {

    public OAuthException(String msg) {
        super(msg);
    }

    public OAuthException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
