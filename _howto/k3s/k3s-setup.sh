#! /bin/bash

EXTERNAL_IP=$(curl -s ipinfo.io/ip)
echo "EXTERNAL_IP=${EXTERNAL_IP}"

NGINX_VERSION='1.27'
REGISTRY_USER='registry'
REGISTRY_PASSWORD='changeme'

# Update/Install packages
sudo yum update -y
sudo yum install -y buildah httpd-tools

# Map the EXTERNAL_IP to k3s-host k3s-registry
echo "${EXTERNAL_IP} k3s-host k3s-registry" | sudo tee -a /etc/hosts \
  && cat /etc/hosts

# Install K3S ----------------------------------------------------------------------------------------------------------
# https://k3s.io/
if [ -n "${EXTERNAL_IP}" ]; then
  echo "Installing K3S ..." \
    && curl -sfL https://get.k3s.io | sh -s - --write-kubeconfig-mode 660 --tls-san ${EXTERNAL_IP} \
    && sudo chmod 775 /etc/rancher/k3s \
    && kubectl version
    # k3s-uninstall.sh
fi

# Create TLS Certificate -----------------------------------------------------------------------------------------------
#
if [ -n "${EXTERNAL_IP}" ]; then
  echo "Creating TLS Certificate ..." \
    && echo "[ req ]
default_bits       = 4096
distinguished_name = req_distinguished_name
req_extensions     = req_ext
x509_extensions    = v3_req
prompt             = no

[ req_distinguished_name ]
C  = US
ST = State
L  = City
O  = Organization
OU = Unit

[ req_ext ]
subjectAltName = @alt_names

[ v3_req ]
keyUsage = keyEncipherment, dataEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = k3s-host
DNS.2 = k3s-registry
IP.1  = ${EXTERNAL_IP}
" > "./k3s/san.cnf" \
    && openssl req -x509 -nodes -days 365 -newkey rsa:4096 -keyout "./k3s/tls.key" -out "./k3s/tls.crt" -config "./k3s/san.cnf" \
    && kubectl delete secret tls k3s-host-cert --ignore-not-found \
    && kubectl create secret tls k3s-host-cert --cert=k3s/tls.crt --key=k3s/tls.key \
    && openssl x509 -in ./k3s/tls.crt -text -noout \
    && openssl x509 -in ./k3s/tls.crt -text -noout | grep -A1 "Subject Alternative Name" \
    && openssl x509 -in ./k3s/tls.crt -noout -modulus | openssl md5 \
    && openssl rsa -in ./k3s/tls.key -noout -modulus | openssl md5 \
    && openssl x509 -in ./k3s/tls.crt -enddate -noout
fi

# Install Nginx --------------------------------------------------------------------------------------------------------
#
if [ -n "${NGINX_VERSION}" ]; then
  echo "Installing Nginx ..." \
    && kubectl delete configmap nginx-config --ignore-not-found \
    && kubectl create configmap nginx-config --from-literal=nginx.conf='server {
        listen 443 ssl;
        server_name localhost;

        ssl_certificate /etc/nginx/certs/tls.crt;
        ssl_certificate_key /etc/nginx/certs/tls.key;

        location / {
            root /usr/share/nginx/html;
            index index.html;
        }
    }' \
    && kubectl delete -f ./k3s/nginx-deployment.yml --ignore-not-found \
    && kubectl apply -f ./k3s/nginx-deployment.yml \
    && kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=nginx --timeout=20s \
    && kubectl logs --tail 400 -l app.kubernetes.io/name=nginx

  curl -vk https://k3s-host:30443
fi

# Install Registry -----------------------------------------------------------------------------------------------------
#
if [[ -n "${REGISTRY_USER}" && -n ${REGISTRY_PASSWORD} ]]; then
  # Note, k3s-registry-creds need to exist in the namespace of deployments that use
  # imagePullSecrets: k3s-registry-creds
  echo "Install a Registry..." \
    && echo ${REGISTRY_PASSWORD} | htpasswd -Bic ./k3s/registry.password ${REGISTRY_USER} \
    && kubectl get namespace registry || kubectl create namespace registry \
    && kubectl delete secret generic registry-auth -n registry --ignore-not-found \
    && kubectl create secret generic registry-auth -n registry --from-file=htpasswd=./k3s/registry.password \
    && kubectl delete secret docker-registry k3s-registry-creds --ignore-not-found \
    && kubectl create secret docker-registry k3s-registry-creds \
         --docker-server=k3s-registry:31443 \
         --docker-username=${REGISTRY_USER} \
         --docker-password=${REGISTRY_PASSWORD} \
    && echo "
mirrors:
  k3s-registry:31443:
    endpoint:
      - "https://${EXTERNAL_IP}:31443"
configs:
  k3s-registry:31443:
    tls:
      insecure_skip_verify: true
" | tee /etc/rancher/k3s/registries.yaml \
    && sudo systemctl restart k3s \
    && kubectl delete secret tls k3s-host-cert -n registry --ignore-not-found \
    && kubectl create secret tls k3s-host-cert -n registry --cert=k3s/tls.crt --key=k3s/tls.key \
    && kubectl delete -f ./k3s/registry-deployment.yml -n registry --ignore-not-found \
    && kubectl apply -f ./k3s/registry-deployment.yml -n registry \
    && kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=registry -n registry --timeout=20s \
    && kubectl logs --tail 400 -l app.kubernetes.io/name=registry -n registry

  echo "Configure Registry for Buildah..." \
    && echo '
[[registry]]
location = "k3s-registry:31443"
insecure = true
' | sudo tee -a /etc/containers/registries.conf

  # Try to access the registry from a remote host
  # docker login k3s-registry:31443
  # docker tag nginx:latest k3s-host:31443/nginx:latest
  # docker push k3s-host:31443/nginx:latest

  curl -u ${REGISTRY_USER}:${REGISTRY_PASSWORD} -k https://k3s-host:31443/v2/_catalog
  curl -u ${REGISTRY_USER}:${REGISTRY_PASSWORD} -k https://k3s-registry:31443/v2/_catalog
  curl -u ${REGISTRY_USER}:${REGISTRY_PASSWORD} -k https://207.244.234.85:31443/v2/_catalog
fi

# Kubeconfig -----------------------------------------------------------------------------------------------------------
#
scp -P 10178 core@k3s-host:/etc/rancher/k3s/k3s.yaml ./k3s.yaml \
  && k3s_ip=$(grep "k3s-host" /etc/hosts | awk '{print $1}') \
  && sed -i . "s/127.0.0.1/${k3s_ip}/" ./k3s.yaml \
  && sed -i . "s/default/k3s/" ./k3s.yaml \
  && KUBECONFIG=~/.kube/config:./k3s.yaml kubectl config view --flatten > merged-config.yaml \
  && mv merged-config.yaml ~/.kube/config \
  && kubectl config get-contexts \
  && kubectl config use-context k3s \
  && kubectl get pod -n registry

