# Variables

package: clean k8s-export
	@./mvnw clean package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-* .mvn src target mvnw mvnw.cmd pom.xml readme.md

k8s-export:
	@jbang camel kubernetes export timer-log-route.yaml \
       --gav=examples:timer-log:1.0.0 \
       --trait container.imagePullPolicy=IfNotPresent \
       --trait service.type=NodePort \
       --runtime=quarkus

k8s-deploy:
	@kubectl create -f ./target/kubernetes/kubernetes.yml && sleep 3 && \
    kubectl logs -f --tail 400 -l app.kubernetes.io/name=timer-log

k8s-delete:
	@kubectl delete --all -f ./target/kubernetes/kubernetes.yml

k8s-run:
	@jbang camel kubernetes run timer-log-route.yaml --logs \
       --gav=examples:timer-log:1.0.0 \
       --trait container.imagePullPolicy=IfNotPresent \
       --trait service.type=NodePort \
       --runtime=quarkus

run-java:
	@java -jar target/quarkus-app/quarkus-run.jar

run-docker:
	@docker run -it --rm -p 8080:8080 examples/timer-log:1.0.0

