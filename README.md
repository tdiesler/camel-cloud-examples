# JBang Camel Kubernetes Examples

Here you find a set of Camel Kubernetes projects generated with [jbang camel kubernetes](https://camel.apache.org/manual/camel-jbang-kubernetes.html).

These projects are organized by their respective runtime option.

* [main](./camel-main)
* [quarkus](./quarkus)
* [spring-boot](./spring-boot)

For convenience, you'll find a Makefile in each subdir. You could inspect/try ...

```
make k8s-package

make run-java
make run-docker

make k8s-run
make k8s-delete
```

## Running on K3S

Setup your local k8s environment to work with a remote k3s cluster.

1. Get Kubeconfig from Remote k3s Instance

    ```
    scp ec2-user@k3s-host:/etc/rancher/k3s/k3s.yaml ./k3s.yaml
    ```

2. Edit the Kubeconfig File

   Modify the server URL to point to the remote k3s server's IP address or hostname and rename references 
   from 'default' to 'k3s'

   ```
   sed -i . "s/127.0.0.1/k3s-host/" ./k3s.yaml
   sed -i . "s/default/k3s/" ./k3s.yaml
   ```

3. Merge the Kubeconfig files

   ```
   KUBECONFIG=~/.kube/config:./k3s.yaml kubectl config view --flatten > merged-config.yaml \
      && mv merged-config.yaml ~/.kube/config \
      && kubectl config get-contexts \
      && kubectl config use-context k3s
   ```
