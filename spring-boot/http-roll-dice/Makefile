# Variables

package: clean generate
	@./mvnw clean package

clean:
	@CUR_DIR=$$(pwd | awk -F'/' '{print $$(NF-1)"/"$$(NF)}') && echo "Clean $$CUR_DIR"
	@rm -rf .camel-jbang .mvn src target mvnw mvnw.cmd mpom.xml

generate:
	@jbang camel kubernetes export roll-dice-route.yaml \
	   --gav=examples:roll-dice:1.0.0 \
	   --trait container.imagePullPolicy=IfNotPresent \
	   --trait service.type=NodePort \
	   --runtime=spring-boot

run-java:
	@java -jar target/roll-dice-1.0.0.jar

run-docker:
	@docker run -it --rm -p 8080:8080 examples/roll-dice:1.0.0

run-k8s:
	@kubectl apply -f ./target/kubernetes/kubernetes.yml && sleep 3 && \
	 kubectl logs -f --tail 400 -l app.kubernetes.io/name=roll-dice

k8s-delete:
	@kubectl delete --all -f ./target/kubernetes/kubernetes.yml
