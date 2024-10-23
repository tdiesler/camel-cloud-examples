# Variables

package: k8s-package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml readme.md

# For Openshift, users will need to make sure that the target project/namespace name is aligned with the quarkus.container-image.group.
# https://quarkus.io/guides/deploying-to-openshift
#
# oc new-project examples

oce-package: clean oce-export
	@./mvnw clean package

oce-export:
	@jbang camel kubernetes export timer-log-route.yaml \
       --gav=examples:timer-log:1.0.0 \
       --image-registry image-registry.openshift-image-registry.svc:5000 \
       --trait container.imagePullPolicy=IfNotPresent \
       --cluster-type openshift \
       --runtime=camel-main

oce-deploy:
	@kubectl apply -f ./target/kubernetes/openshift.yml && \
	kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=timer-log --timeout=20s && \
    kubectl logs -f --tail 400 -l app.kubernetes.io/name=timer-log

oce-delete:
	@kubectl delete --all -f ./target/kubernetes/openshift.yml

oce-run:
	@jbang camel kubernetes run timer-log-route.yaml --logs \
	   --gav=examples:timer-log:1.0.0 \
       --image-registry image-registry.openshift-image-registry.svc:5000 \
       --trait container.imagePullPolicy=IfNotPresent \
       --cluster-type openshift \
	   --runtime=camel-main

k8s-package: clean k8s-export
	@./mvnw clean package

k8s-export:
	@jbang camel kubernetes export timer-log-route.yaml \
       --gav=examples:timer-log:1.0.0 \
       --trait container.imagePullPolicy=IfNotPresent \
       --trait service.type=NodePort \
       --runtime=camel-main

k8s-deploy:
	@kubectl create -f ./target/kubernetes/kubernetes.yml && \
	kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=timer-log --timeout=20s && \
    kubectl logs -f --tail 400 -l app.kubernetes.io/name=timer-log

k8s-delete:
	@kubectl delete --all -f ./target/kubernetes/kubernetes.yml

k8s-run:
	@jbang camel kubernetes run timer-log-route.yaml --logs \
       --gav=examples:timer-log:1.0.0 \
       --trait container.imagePullPolicy=IfNotPresent \
       --trait service.type=NodePort \
       --runtime=camel-main

run-java:
	@java -jar target/timer-log-1.0.0.jar

run-docker:
	@docker run -it --rm -p 8080:8080 examples/timer-log:1.0.0

