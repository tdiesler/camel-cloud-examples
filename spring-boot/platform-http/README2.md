
# Build & Run

```
make k8s-package k8s-deploy
...
podName=$(kubectl get pod -l app.kubernetes.io/name=platform-http -o jsonpath='{.items[0].metadata.name}')
kubectl port-forward ${podName} 8080:8080
...
make k8s-delete
```

http://127.0.0.1:8080/hello
