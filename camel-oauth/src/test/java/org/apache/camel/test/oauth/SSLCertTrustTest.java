package org.apache.camel.test.oauth;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SSLCertTrustTest {

    private final String HTTPS_SERVICE_URL = "https://127.0.0.1:30443/";

    @Test
    void testTrustedCertificate() {
        Assertions.assertDoesNotThrow(() -> connectToUrl(HTTPS_SERVICE_URL), "Certificate should be trusted");
    }

    @Test
    void testUntrustedCertificate() {
        String url = "https://untrusted-root.badssl.com"; // Example of an untrusted cert
        Assertions.assertThrows(SSLHandshakeException.class, () -> connectToUrl(url), "Certificate should not be trusted");
    }

    private static void connectToUrl(String httpsUrl) throws IOException {
        URL url = new URL(httpsUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.connect();
    }
}