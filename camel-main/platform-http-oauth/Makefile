# Variables

CAMEL_CMD ?= jbang camel
RUN_MODE ?= dev

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml README.md

# Local Kubernetes (e.g. DockerDesktop) ================================================================================
#
# podName=$(kubectl get pod -l app.kubernetes.io/name=platform-http-oauth -o jsonpath='{.items[0].metadata.name}')
# kubectl port-forward ${podName} 8080:8080
#
k8s-package: clean k8s-export
	@./mvnw clean package

k8s-export:
	@$(CAMEL_CMD) kubernetes export platform-http-route.yaml files/webapp/* \
	--dep=org.apache.camel.cloud:camel-oauth:0.1.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oidc.auth.baseUri=http://192.168.0.10:30100/realms/camel \
	--property=camel.oidc.auth.redirectUri=http://127.0.0.1:8080/auth \
	--property=camel.oidc.auth.logout.redirectUri=http://127.0.0.1:8080/ \
	--property=camel.oidc.auth.clientId=camel-client \
	--ignore-loading-error=true \
	--trait container.image-pull-policy=IfNotPresent \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name platform-http-oauth

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name platform-http-oauth

k8s-run:
	@$(CAMEL_CMD) kubernetes run platform-http-route.yaml files/webapp/* --$(RUN_MODE) \
	--dep=org.apache.camel.cloud:camel-oauth:0.1.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--property=camel.oidc.auth.baseUri=http://192.168.0.10:30100/realms/camel \
	--property=camel.oidc.auth.redirectUri=http://127.0.0.1:8080/auth \
	--property=camel.oidc.auth.logout.redirectUri=http://127.0.0.1:8080/ \
	--property=camel.oidc.auth.clientId=camel-client \
	--trait container.image-pull-policy=IfNotPresent \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main
