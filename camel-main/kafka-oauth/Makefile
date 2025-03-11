# Variables

CAMEL_CMD ?= jbang camel
RUN_MODE ?= dev

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml README.md

# Local Kubernetes (e.g. DockerDesktop) ================================================================================

k8s-package: clean k8s-export
	@./mvnw clean package

k8s-export:
	@$(CAMEL_CMD) kubernetes export kafka-oauth-files/* tls/* \
	--dep=io.strimzi:kafka-oauth-client:0.15.0 \
	--gav=examples:kafka-oauth:1.0.0 \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name kafka-oauth

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name kafka-oauth

k8s-run:
	@$(CAMEL_CMD) kubernetes run kafka-oauth-files/* tls/* --$(RUN_MODE) \
	--dep=io.strimzi:kafka-oauth-client:0.15.0 \
	--gav=examples:kafka-oauth:1.0.0 \
	--property=ssl.truststore.certificates=tls/cluster.crt \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main
