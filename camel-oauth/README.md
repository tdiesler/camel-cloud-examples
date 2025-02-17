
## Keycloak

Run Keycloak in Docker

```
docker run -d \
    --name=keycloak \
    -p 8443:8443 \
    -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
    -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
    -v $(pwd)/../infra/keycloak/etc/cert.pem:/etc/x509/cert.pem:ro \
    -v $(pwd)/../infra/keycloak/etc/key.pem:/etc/x509/key.pem:ro \
    quay.io/keycloak/keycloak:26.1.2 \
        start-dev \
        --https-port=8443 \
        --https-certificate-file=/etc/x509/cert.pem \
        --https-certificate-key-file=/etc/x509/key.pem
```