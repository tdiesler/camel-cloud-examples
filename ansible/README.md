
## Run the Playbook

After setting the VPS connect parameters, run ...

```
ansible-playbook \
    -i ansible/hosts.ini \
    ansible/step00-prepare-vps.yml \
    ansible/step01-install-k3s.yml \
    ansible/step02-registry.yml
```

## Fetch the Kubectl Config

Note, this is done in step01-install-k3s already.

```
K3S_HOST=camel01
scp -P 22 core@${K3S_HOST}:/etc/rancher/k3s/k3s.yaml ./k3s.yaml \
  && k3s_ip=$(grep ${K3S_HOST} /etc/hosts | awk '{print $1}') \
  && sed -i . "s/127.0.0.1/${k3s_ip}/" ./k3s.yaml \
  && sed -i . "s/default/${K3S_HOST}/" ./k3s.yaml \
  && KUBECONFIG=~/.kube/config:./k3s.yaml kubectl config view --flatten > merged-config.yaml \
  && mv merged-config.yaml ~/.kube/config \
  && kubectl config use-context ${K3S_HOST} \
  && kubectl config get-contexts \
  && kubectl get pod -n kube-system
```