
## Building the route with Kaoto

```
docker run --rm --name kaoto -p8080:8080 quay.io/kaotoio/kaoto-app:main
```

## Keycloak on Kubernetes

This has moved to ../../infra/keycloak

## Notes

There currently is an issue with adding TLS certificates through JKube
https://issues.apache.org/jira/browse/CAMEL-21751

Until this gets fixed we cannot access Keycloak on https from a pod on k8s.

For now, use ...

```
# Import TLS Certificate to Java Keystore (i.e. trust the certificate)
sudo keytool -import -alias keycloak -file ./etc/keycloak.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

make k8s-package
mvn camel:run
```

