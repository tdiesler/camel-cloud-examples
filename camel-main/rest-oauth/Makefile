# Variables

CAMEL_CMD ?= jbang camel
RUN_MODE ?= dev

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml README.md

# Local Kubernetes (e.g. DockerDesktop) ================================================================================
#
# podName=$(kubectl get pod -l app.kubernetes.io/name=rest-oauth -o jsonpath='{.items[0].metadata.name}')
# kubectl port-forward ${podName} 8080:8080
#
k8s-package: clean k8s-export
	@./mvnw clean package

k8s-export:
	@$(CAMEL_CMD) kubernetes export rest-oauth-files/* tls/* \
	--dep=org.apache.camel:camel-oauth:4.11.0-SNAPSHOT \
	--gav=examples:rest-oauth:1.0.0 \
	--property=camel.oauth.baseUri=https://keycloak.local/kc/realms/camel \
	--property=camel.oauth.clientId=camel-client \
	--property=camel.oauth.clientSecret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--trait container.image-pull-policy=IfNotPresent \
	--ignore-loading-error=true \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name rest-oauth

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name rest-oauth

k8s-run:
	@$(CAMEL_CMD) kubernetes run rest-oauth-files/* tls/* --$(RUN_MODE) \
	--dep=org.apache.camel:camel-oauth:4.11.0-SNAPSHOT \
	--gav=examples:rest-oauth:1.0.0 \
	--property=camel.oauth.baseUri=https://keycloak.local/kc/realms/camel \
	--property=camel.oauth.clientId=camel-client \
	--property=camel.oauth.clientSecret=camel-client-secret \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--trait container.image-pull-policy=IfNotPresent \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main
