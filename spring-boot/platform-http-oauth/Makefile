# Variables

CAMEL_CMD ?= jbang camel
RUN_MODE ?= dev

DEBUG_OPTS ?= "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:4004"

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml README.md

# Local Kubernetes (e.g. DockerDesktop) ================================================================================
#
k8s-package: clean k8s-export
	@./mvnw clean package

k8s-fetch-cert:
	@mkdir -p tls
	@echo -n | openssl s_client -connect keycloak.local:443 | openssl x509 > tls/cluster.crt

k8s-export: k8s-fetch-cert
	@$(CAMEL_CMD) kubernetes export platform-http-files/* tls/* \
	--dep=org.apache.camel:camel-oauth:4.12.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oauth.base-uri=https://keycloak.local/kc/realms/camel \
	--property=camel.oauth.redirect-uri=https://example.local/auth \
	--property=camel.oauth.logout.redirect-uri=https://example.local/ \
	--property=camel.oauth.client-id=camel-client \
	--property=camel.oauth.client-secret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--trait container.image-pull-policy=IfNotPresent \
	--trait ingress.enabled=true \
	--trait ingress.host=example.local \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--ignore-loading-error=true \
	--image-builder=docker \
	--image-push=false \
	--runtime=spring-boot

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name platform-http-oauth

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name platform-http-oauth

k8s-run: k8s-fetch-cert
	@$(CAMEL_CMD) kubernetes run --verbose=true platform-http-files/* tls/* --$(RUN_MODE) \
	--dep=org.apache.camel:camel-oauth:4.12.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oauth.base-uri=https://keycloak.local/kc/realms/camel \
	--property=camel.oauth.redirect-uri=https://example.local/auth \
	--property=camel.oauth.logout.redirect-uri=https://example.local/ \
	--property=camel.oauth.client-id=camel-client \
	--property=camel.oauth.client-secret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--trait container.image-pull-policy=IfNotPresent \
	--trait ingress.enabled=true \
	--trait ingress.host=example.local \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--image-builder=docker \
	--image-push=false \
	--runtime=spring-boot

run-java:
	@java -jar $(DEBUG_OPTS) target/platform-http-oauth-1.0.0.jar

# K3S ==================================================================================================================

k3s-package: clean k3s-export
	# -Djkube.docker.username=<registry-user> -Djkube.docker.password=<registry-password>
	# Alternatively: docker login registry.k3s:30443
	@./mvnw clean package

k3s-fetch-cert:
	@mkdir -p tls
	@echo -n | openssl s_client -connect keycloak.k3s:443 | openssl x509 > tls/cluster.crt

k3s-export: k3s-fetch-cert
	@$(CAMEL_CMD) kubernetes export platform-http-files/* tls/* \
	--dep=org.apache.camel:camel-oauth:4.12.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oauth.base-uri=https://keycloak.k3s/kc/realms/camel \
	--property=camel.oauth.redirect-uri=https://example.k3s/auth \
	--property=camel.oauth.logout.redirect-uri=https://example.k3s/ \
	--property=camel.oauth.client-id=camel-client \
	--property=camel.oauth.client-secret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--trait container.image-pull-policy=Always \
	--trait ingress.enabled=true \
	--trait ingress.host=example.k3s \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--ignore-loading-error=true \
	--image-registry registry.k3s:30443 \
	--runtime=spring-boot

k3s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name platform-http-oauth

k3s-delete:
	@$(CAMEL_CMD) kubernetes delete --name platform-http-oauth

k3s-run: k3s-fetch-cert
	@$(CAMEL_CMD) kubernetes run --verbose=true platform-http-files/* tls/* --$(RUN_MODE) \
	--dep=org.apache.camel:camel-oauth:4.12.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oauth.base-uri=https://keycloak.k3s/kc/realms/camel \
	--property=camel.oauth.redirect-uri=https://example.k3s/auth \
	--property=camel.oauth.logout.redirect-uri=https://example.k3s/ \
	--property=camel.oauth.client-id=camel-client \
	--property=camel.oauth.client-secret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--trait container.image-pull-policy=Always \
	--trait ingress.enabled=true \
	--trait ingress.host=example.k3s \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--image-registry registry.k3s:30443 \
	--runtime=spring-boot

# OpenShift ============================================================================================================

# Make sure that the target project/namespace is aligned with the image.group
# https://spring-boot.io/guides/deploying-to-openshift
#
# oc new-project examples

oce-package: clean oce-export
	@./mvnw clean package

oce-fetch-cert:
	@test -n "$(OPENSHIFT_HOSTNAME)" || { echo "Error: OPENSHIFT_HOSTNAME is not defined"; exit 1; }
	@echo -n | openssl s_client -connect keycloak.$(OPENSHIFT_HOSTNAME):443 | openssl x509 > tls/cluster.crt

oce-export: oce-fetch-cert
	@$(CAMEL_CMD) kubernetes export platform-http-files/* tls/* \
	--dep=org.apache.camel:camel-oauth:4.12.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oauth.base-uri=https://keycloak.$(OPENSHIFT_HOSTNAME)/realms/camel \
	--property=camel.oauth.redirect-uri=https://webapp.$(OPENSHIFT_HOSTNAME)/auth \
	--property=camel.oauth.logout.redirect-uri=https://webapp.$(OPENSHIFT_HOSTNAME)/ \
	--property=camel.oauth.client-id=camel-client \
	--property=camel.oauth.client-secret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--image-registry image-registry.openshift-image-registry.svc:5000 \
	--trait container.image-pull-policy=Always \
	--trait route.enabled=true \
	--trait route.host=webapp.$(OPENSHIFT_HOSTNAME) \
	--trait route.tls-insecure-edge-termination-policy=Redirect \
	--trait route.tls-termination=edge \
	--ignore-loading-error=true \
	--cluster-type=openshift \
	--runtime=spring-boot

oce-deploy:
	@kubectl apply -f ./target/kubernetes/openshift.yml
	@$(CAMEL_CMD) kubernetes logs --name platform-http-oauth

oce-delete:
	@$(CAMEL_CMD) kubernetes delete --name platform-http-oauth

oce-run: oce-fetch-cert
	@$(CAMEL_CMD) kubernetes run --verbose=true platform-http-files/* tls/* --$(RUN_MODE) \
	--dep=org.apache.camel:camel-oauth:4.12.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oauth.base-uri=https://keycloak.$(OPENSHIFT_HOSTNAME)/realms/camel \
	--property=camel.oauth.redirect-uri=https://webapp.$(OPENSHIFT_HOSTNAME)/auth \
	--property=camel.oauth.logout.redirect-uri=https://webapp.$(OPENSHIFT_HOSTNAME)/ \
	--property=camel.oauth.client-id=camel-client \
	--property=camel.oauth.client-secret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--image-registry image-registry.openshift-image-registry.svc:5000 \
	--trait container.image-pull-policy=Always \
	--trait route.enabled=true \
	--trait route.host=webapp.$(OPENSHIFT_HOSTNAME) \
	--trait route.tls-insecure-edge-termination-policy=Redirect \
	--trait route.tls-termination=edge \
	--cluster-type=openshift \
	--runtime=spring-boot
