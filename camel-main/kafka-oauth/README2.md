
# Keycloak

Deploy Keycloak as Identity Provider

```
kubectl config use-context docker-desktop \
    && helm upgrade --install keycloak ./helm -f ./helm/values-keycloak.yaml \
    && kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=keycloak --timeout=20s \
    && kubectl logs --tail 400 -f -l app.kubernetes.io/name=keycloak

helm uninstall keycloak
```

# Kafka OAuth

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

echo 'Hello Kermit!' | kafka-console-producer --broker-list localhost:30092 --topic test

kafka-console-consumer --bootstrap-server localhost:30092 --topic test --from-beginning
```
