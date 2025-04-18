# Variables

CAMEL_CMD ?= jbang camel
RUN_MODE ?= dev

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml README.md

# Local Kubernetes (e.g. DockerDesktop) ================================================================================
#
k8s-package: clean k8s-export
	@./mvnw clean package

k8s-export:
	@$(CAMEL_CMD) kubernetes export platform-http-files/* \
	--gav=examples:platform-http:1.0.0 \
	--trait ingress.enabled=true \
	--trait ingress.host=example.local \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--image-builder=docker \
	--image-push=false \
	--runtime=spring-boot

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name platform-http

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name platform-http

k8s-run:
	@$(CAMEL_CMD) kubernetes run --verbose=true platform-http-files/* --$(RUN_MODE) \
	--gav=examples:platform-http:1.0.0 \
	--trait ingress.enabled=true \
	--trait ingress.host=example.local \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--image-builder=docker \
	--image-push=false \
	--runtime=spring-boot

# K3S ==================================================================================================================

k3s-package: clean k3s-export
	# -Djkube.docker.username=<registry-user> -Djkube.docker.password=<registry-password>
	# Alternatively: docker login registry.k3s:30443
	@./mvnw clean package

k3s-export:
	@$(CAMEL_CMD) kubernetes export platform-http-files/* \
	--gav=examples:platform-http:1.0.0 \
	--trait container.image-pull-policy=Always \
	--trait ingress.enabled=true \
	--trait ingress.host=example.k3s \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--image-registry registry.k3s:30443 \
	--runtime=spring-boot

k3s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name platform-http

k3s-delete:
	@$(CAMEL_CMD) kubernetes delete --name platform-http

k3s-run:
	@$(CAMEL_CMD) kubernetes run --verbose=true platform-http-files/* --$(RUN_MODE) \
	--gav=examples:platform-http:1.0.0 \
	--trait container.image-pull-policy=Always \
	--trait ingress.enabled=true \
	--trait ingress.host=example.k3s \
	--trait ingress.ingressClass=traefik \
	--trait ingress.tls-secret-name=traefik-tls \
	--image-registry registry.k3s:30443 \
	--runtime=spring-boot

# OpenShift ============================================================================================================

# Make sure that the target project/namespace is aligned with the image.group
# https://quarkus.io/guides/deploying-to-openshift
#
# oc new-project examples

oce-package: clean oce-export
	@./mvnw clean package

oce-export:
	@$(CAMEL_CMD) kubernetes export platform-http-files/* \
	--gav=examples:platform-http:1.0.0 \
	--image-registry image-registry.openshift-image-registry.svc:5000 \
	--trait container.image-pull-policy=IfNotPresent \
	--trait route.enabled=true \
	--trait route.host=webapp.$(OPENSHIFT_HOSTNAME) \
	--trait route.tls-insecure-edge-termination-policy=Redirect \
	--trait route.tls-termination=edge \
	--cluster-type=openshift \
	--runtime=spring-boot

oce-deploy:
	@kubectl apply -f ./target/kubernetes/openshift.yml
	@$(CAMEL_CMD) kubernetes logs --name platform-http

oce-delete:
	@$(CAMEL_CMD) kubernetes delete --name platform-http

oce-run:
	@$(CAMEL_CMD) kubernetes run --verbose=true platform-http-files/* --$(RUN_MODE) \
	--gav=examples:platform-http:1.0.0 \
	--image-registry image-registry.openshift-image-registry.svc:5000 \
	--trait container.image-pull-policy=IfNotPresent \
	--trait route.enabled=true \
	--trait route.host=webapp.$(OPENSHIFT_HOSTNAME) \
	--trait route.tls-insecure-edge-termination-policy=Redirect \
	--trait route.tls-termination=edge \
	--cluster-type=openshift \
	--runtime=spring-boot

run-java:
	@java -jar target/platform-http-1.0.0.jar

run-docker:
	@docker run -it --rm -p 8080:8080 examples/platform-http:1.0.0

