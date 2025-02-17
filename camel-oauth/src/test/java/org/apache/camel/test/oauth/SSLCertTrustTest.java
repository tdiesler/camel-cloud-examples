package org.apache.camel.test.oauth;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class SSLCertTrustTest {

    private static final String KEYCLOAK_SERVER_URL = "https://keycloak.local:30443/";

    @Test
    void testTrustedCertificate() {

        var admin = new KeycloakAdmin(new KeycloakAdmin.AdminParams(KEYCLOAK_SERVER_URL));
        Assumptions.assumeTrue(admin.isKeycloakRunning(), "Keycloak is not running");

        Assertions.assertDoesNotThrow(() -> connectToUrl(KEYCLOAK_SERVER_URL), "Certificate should be trusted");
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