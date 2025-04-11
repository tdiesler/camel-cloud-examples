# Variables

CAMEL_CMD ?= jbang camel
RUN_MODE ?= dev

KEYCLOAK_URL ?= https://keycloak.local/kc/realms/camel
CLIENT_ID ?= camel-client
CLIENT_SECRET ?= camel-client-secret

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml README.md

fetch-access-token:
	@echo "Fetching access token for client: $(CLIENT_ID)"
	$(eval ACCESS_TOKEN := $(shell curl -s -X POST "$(KEYCLOAK_URL)/protocol/openid-connect/token" \
	  -H "Content-Type: application/x-www-form-urlencoded" \
	  -d "grant_type=client_credentials" \
	  -d "client_id=$(CLIENT_ID)" \
	  -d "client_secret=$(CLIENT_SECRET)" | jq -r .access_token))

# Local Kubernetes (e.g. DockerDesktop) ================================================================================

k8s-package: clean k8s-export
	@./mvnw clean package

k8s-fetch-cert:
	@mkdir -p tls
	@echo -n | openssl s_client -connect keycloak.local:443 | openssl x509 > tls/cluster.crt

k8s-export: k8s-fetch-cert fetch-access-token
	@$(CAMEL_CMD) kubernetes export amq7-oauth-files/* tls/* \
	--gav=examples:amq7-oauth:1.0.0 \
	--dep=org.apache.activemq:artemis-jakarta-client:2.40.0 \
	--property=camel.component.sjms2.connection-factory=#class:org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory \
	--property=camel.component.sjms2.connection-factory.broker-url=tcp://artemis:61616 \
	--property=camel.component.sjms2.connection-factory.user="Bearer $(ACCESS_TOKEN)" \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--trait container.image-pull-policy=IfNotPresent \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name amq7-oauth

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name amq7-oauth

k8s-run:
	@$(CAMEL_CMD) kubernetes run --verbose=true amq7-oauth-files/* --$(RUN_MODE) \
	--gav=examples:amq7-oauth:1.0.0 \
	--dep=org.apache.activemq:artemis-jakarta-client:2.40.0 \
	--property=camel.component.sjms2.connection-factory=#class:org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory \
    --property=camel.component.sjms2.connection-factory.broker-url=tcp://artemis:61616 \
	--property=camel.component.sjms2.connection-factory.user=admin \
    --property=camel.component.sjms2.connection-factory.password=admin \
	--trait container.image-pull-policy=IfNotPresent \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main
