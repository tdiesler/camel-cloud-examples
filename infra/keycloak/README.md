
## Keycloak Certificate

```
# Generate TLS Certificate
openssl req -x509 -newkey rsa:4096 -keyout ./helm/etc/keycloak.key -out ./helm/etc/keycloak.crt -days 365 -nodes -config ./helm/etc/san.cnf

# Import TLS Certificate to Java Keystore (i.e. trust the certificate)
sudo keytool -import -alias keycloak -file ./helm/etc/keycloak.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

# Remove TLS Certificate from Java Keystore
sudo keytool -delete -alias keycloak -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit
```

# Keycloak on Kubernetes

Deploy Keycloak as Identity Provider

Admin:  admin/admin
User:   alice/alice

https://keycloak.local:30443/

```
kubectl config use-context docker-desktop \
    && helm upgrade --install keycloak ./helm -f ./helm/values-keycloak.yaml \
    && kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=keycloak --timeout=20s \
    && kubectl logs --tail 400 -f -l app.kubernetes.io/name=keycloak

helm uninstall keycloak
```

# Camel platform-http on Kubernetes

Port Forwarding

```
podName=$(kubectl get pod -l app.kubernetes.io/name=platform-http-oauth -o jsonpath='{.items[0].metadata.name}')
kubectl port-forward ${podName} 8080:8080
```

