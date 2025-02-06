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
	@$(CAMEL_CMD) kubernetes export WebAppMain.java WebAppRoute.java application.properties files/webapp/* \
	--dep=org.apache.camel.cloud:camel-cloud-oauth:0.1.0-SNAPSHOT \
	--gav=examples:platform-http-oauth:1.0.0 \
	--package-name=com.example \
	--main-classname=WebAppMain \
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
