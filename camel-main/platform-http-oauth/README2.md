
# Keycloak on Kubernetes

This has moved to ../../infra

# Build & Run

There currently is an issue with adding TLS certificates through JKube
https://issues.apache.org/jira/browse/CAMEL-21751

Until this gets fixed we manually need to add some stuff as documented in the Jira issue. 

For now, don't use `k8s-run` - instead use 

```
mvn clean install
make k8s-deploy
...
podName=$(kubectl get pod -l app.kubernetes.io/name=platform-http-oauth -o jsonpath='{.items[0].metadata.name}')
kubectl port-forward ${podName} 8080:8080
...
make k8s-delete
```

http://127.0.0.1:8080/
