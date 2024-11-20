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

Run this Ansible Playbook to setup a remote k3s cluster.

```
ansible-playbook \
   -i ansible/hosts.ini \
   ansible/step00-prepare-vps.yml \
   ansible/step01-install-k3s.yml \
   ansible/step02-install-registry.yml
```

kubectl config set-cluster camel03 --insecure-skip-tls-verify=true