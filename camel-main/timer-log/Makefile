# Variables

# make package CAMEL_CMD=camel
CAMEL_CMD ?= jbang camel
# make k3s-run RUN_MODE=dev
RUN_MODE ?= logs

package: k3s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml readme.md

k3s-package: clean k3s-export
	# -Djkube.docker.username=<registry-user> -Djkube.docker.password=<registry-password>
	# Alternatively: docker login k3s-registry:30443
	@./mvnw clean package

k3s-export:
	@$(CAMEL_CMD) kubernetes export timer-log-route.yaml \
	   --gav=examples:timer-log:1.0.0 \
       --image-registry k3s-registry:30443 \
	   --trait container.image-pull-policy=Always \
	   --trait container.image-push=true \
	   --trait service.type=NodePort \
	   --runtime=camel-main

k3s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name timer-log

k3s-delete:
	@kubectl delete --all -f ./target/kubernetes/kubernetes.yml

k8s-package: clean k8s-export
	@./mvnw clean package

k8s-export:
	@$(CAMEL_CMD) kubernetes export timer-log-route.yaml \
       --gav=examples:timer-log:1.0.0 \
       --trait container.image-pull-policy=IfNotPresent \
       --trait service.type=NodePort \
       --runtime=camel-main

k8s-deploy:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml
	@$(CAMEL_CMD) kubernetes logs --name timer-log

k8s-delete:
	@kubectl delete --all -f ./target/kubernetes/kubernetes.yml

k8s-run:
	@$(CAMEL_CMD) kubernetes run timer-log-route.yaml --$(RUN_MODE) \
       --gav=examples:timer-log:1.0.0 \
       --trait container.image-pull-policy=IfNotPresent \
       --trait service.type=NodePort \
       --runtime=camel-main

# For Openshift, users will need to make sure that the target project/namespace name is aligned with the quarkus.container-image.group.
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
       --cluster-type openshift \
       --runtime=camel-main

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
       --cluster-type openshift \
	   --runtime=camel-main

run-java:
	@java -jar target/timer-log-1.0.0.jar

run-docker:
	@docker run -it --rm -p 8080:8080 examples/timer-log:1.0.0

