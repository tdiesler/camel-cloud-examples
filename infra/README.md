
# Ingress with Traefik

https://doc.traefik.io/traefik/

```
helm repo add traefik https://traefik.github.io/charts
helm repo update
helm install traefik traefik/traefik
```

## Traefik TLS Certificate

```
# Generate TLS Certificate
openssl req -x509 -newkey rsa:4096 -keyout ./helm/etc/traefik.key -out ./helm/etc/traefik.crt -days 365 -nodes -config ./helm/etc/san.cnf

# Import TLS Certificate to Java Keystore (i.e. trust the certificate)
sudo keytool -import -alias traefik -file ./helm/etc/traefik.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

# Remove TLS Certificate from Java Keystore
sudo keytool -delete -alias traefik -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit
```

## Verify with TLS access 

```
helm upgrade --install traefik-secret ./helm -f ./helm/values-traefik.yaml
helm upgrade --install whoami ./helm -f ./helm/values-whoami.yaml
```

https://k8s.local/who

# Keycloak on Kubernetes

Keycloak can be configured/deployed via Helm

```
kubectl config use-context docker-desktop \
    && helm upgrade --install keycloak ./helm -f ./helm/values-keycloak.yaml \
    && kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=keycloak --timeout=20s \
    && kubectl logs --tail 400 -f -l app.kubernetes.io/name=keycloak

helm uninstall keycloak
```

https://k8s.local/kc

Admin:  admin/admin
User:   alice/alice

## Keycloak Admin Tasks

Create realm 'camel' if not already imported

```
kcadm config credentials --server https://k8s.local/kc --realm master --user admin --password admin

kcadm create realms -s realm=camel -s enabled=true

kcadm create clients -r camel \
    -s clientId=camel-client \
    -s publicClient=false \
    -s standardFlowEnabled=true \
    -s serviceAccountsEnabled=true \
    -s "redirectUris=[\"http://127.0.0.1:8080/auth\"]" \
    -s "attributes.\"post.logout.redirect.uris\"=\"http://127.0.0.1:8080/\""
    
clientId=$(kcadm get clients -r camel -q clientId=camel-client --fields id --format csv --noquotes)
kcadm update clients/${clientId} -r camel -s secret="camel-client-secret"

kcadm create users -r camel \
    -s username=alice \
    -s email=alice@example.com \
    -s emailVerified=true \
    -s firstName=Alice \
    -s lastName=Brown \
    -s enabled=true
    
userid=$(kcadm get users -r camel -q username=alice --fields id --format csv --noquotes)
kcadm set-password -r camel --userid=${userid} --new-password alice    

kcadm delete realms/camel -r master
```

Show realm, client, user configuration

```
kcadm get realms | jq -r '.[] | select(.realm=="camel")'

kcadm get clients -r camel | jq -r '.[] | select(.clientId=="camel-client")'

kcadm get users -r camel | jq -r '.[] | select(.username=="alice")'
```

# Kafka on Kubernetes

Deploy a single node Kafka cluster

```
kubectl config use-context docker-desktop \
    && helm upgrade --install kafka ./helm -f ./helm/values-kafka.yaml \
    && kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=kafka --timeout=20s \
    && kubectl logs --tail 400 -f -l app.kubernetes.io/name=kafka

helm uninstall kafka
```
Create a topic and send/receive messages

```
kafka-topics --create \
  --bootstrap-server 127.0.0.1:30092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic test

echo 'Hello Kermit!' | kafka-console-producer --broker-list localhost:30094 --topic test

kafka-console-consumer --bootstrap-server localhost:30092 --topic test --from-beginning
```
