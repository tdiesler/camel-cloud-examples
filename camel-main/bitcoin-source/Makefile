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
	@$(CAMEL_CMD) kubernetes export btcprice-route.yaml \
	--gav=examples:btcprice:1.0.0 \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name btcprice

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name btcprice

k8s-run:
	@$(CAMEL_CMD) kubernetes run --verbose=true btcprice-route.yaml --$(RUN_MODE) \
	--gav=examples:btcprice:1.0.0 \
	--image-builder=docker \
	--image-push=false \
	--runtime=camel-main

