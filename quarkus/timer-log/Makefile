# Variables

CAMEL_CMD ?= jbang camel
RUN_MODE ?= dev

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml README.md

# Kubernetes ===========================================================================================================

k8s-package: clean k8s-export
	@./mvnw clean package

k8s-export:
	@$(CAMEL_CMD) kubernetes export timer-log-route.yaml \
	--gav=examples:timer-log:1.0.0 \
	--trait container.image-pull-policy=IfNotPresent \
	--trait service.type=NodePort \
	--image-builder=docker \
	--runtime=quarkus

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name timer-log

k8s-delete:
	@$(CAMEL_CMD) kubernetes delete --name timer-log

k8s-run:
	@$(CAMEL_CMD) kubernetes run timer-log-route.yaml --$(RUN_MODE) \
	--gav=examples:timer-log:1.0.0 \
	--trait container.image-pull-policy=IfNotPresent \
	--trait service.type=NodePort \
	--image-builder=docker \
    --runtime=quarkus

# OpenShift ============================================================================================================

# Make sure that the target project/namespace is aligned with the image.group
# https://quarkus.io/guides/deploying-to-openshift
#
# oc new-project examples

oce-package: clean oce-export
	@./mvnw clean package

oce-export:
	@$(CAMEL_CMD) kubernetes export timer-log-route.yaml \
	--gav=examples:timer-log:1.0.0 \
	--image-registry image-registry.openshift-image-registry.svc:5000 \
	--trait container.image-pull-policy=IfNotPresent \
	--cluster-type=openshift \
    --runtime=quarkus

oce-deploy:
	@kubectl apply -f ./target/kubernetes/openshift.yml
	@$(CAMEL_CMD) kubernetes logs --name timer-log

oce-delete:
	@kubectl delete --all -f ./target/kubernetes/openshift.yml

oce-run:
	@$(CAMEL_CMD) kubernetes run timer-log-route.yaml --$(RUN_MODE) \
	--gav=examples:timer-log:1.0.0 \
	--image-registry image-registry.openshift-image-registry.svc:5000 \
	--trait container.image-pull-policy=IfNotPresent \
	--cluster-type=openshift \
	--runtime=quarkus

run-java:
	@java -jar target/quarkus-app/quarkus-run.jar

run-docker:
	@docker run -it --rm -p 8080:8080 examples/timer-log:1.0.0
