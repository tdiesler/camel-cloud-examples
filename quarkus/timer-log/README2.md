# Camel Timer-Log Example

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## How this was created

First we create a simple Camel route that sends a message to the log component every second.

```shell 
camel init timer-log-route.yaml --dir ./timer-log
```

Here the created route ...

```yaml
- from:
    uri: "timer:yaml"
    parameters:
      period: "2000"
    steps:
      - setBody:
          simple: "Hello Camel from ${routeId}"
      - log: "${body}"
```

We now export the integration as a Camel Quarkus project with additional dependencies for building with [Docker](https://quarkus.io/extensions/io.quarkus/quarkus-container-image-docker/).

```shell 
camel kubernetes export timer-log-route.yaml \
  --gav=examples:timer-log:1.0.0 \
  --trait container.imagePullPolicy=IfNotPresent \
  --trait service.type=NodePort \
  --runtime=quarkus
```

## Packaging and running the application

We can then package the application with an ordinary maven build.

```shell
./mvnw clean package
```

We can now verify that the plain Java application runs as expected.

```shell
java -jar target/timer-log-1.0.0.jar
```

A health endpoints are available at
* http://127.0.0.1:8080/q/health/ready
* http://127.0.0.1:8080/q/health/live

## Running the Docker container

You can also run this application in plain Docker like this ...

```shell
docker run -it --rm -p 8080:8080 examples/timer-log:1.0.0 
```

## Deploy on Kubernetes

You can deploy/run this application on Minikube like this ...

```shell
kubectl create -f ./target/kubernetes/kubernetes.yml
kubectl logs -f --tail 400  -l app.kubernetes.io/name=timer-log

INFO exec -a "java" java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -XX:MaxRAMPercentage=50.0 -XX:+UseParallelGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:+ExitOnOutOfMemoryError -cp "." -jar /deployments/quarkus-run.jar 
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2024-07-11 11:00:03,203 INFO  [org.apa.cam.qua.cor.CamelBootstrapRecorder] (main) Bootstrap runtime: org.apache.camel.quarkus.main.CamelMainRuntime
2024-07-11 11:00:03,206 INFO  [org.apa.cam.mai.MainSupport] (main) Apache Camel (Main) 4.6.0 is starting
2024-07-11 11:00:03,237 INFO  [org.apa.cam.mai.BaseMainSupport] (main) Auto-configuration summary
2024-07-11 11:00:03,237 INFO  [org.apa.cam.mai.BaseMainSupport] (main)     [MicroProfilePropertiesSource] camel.main.routesIncludePattern=camel/timer-log-route.yaml
2024-07-11 11:00:03,256 INFO  [org.apa.cam.cli.con.LocalCliConnector] (main) Camel JBang CLI enabled
2024-07-11 11:00:03,322 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Apache Camel 4.6.0 (camel-1) is starting
2024-07-11 11:00:03,526 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Routes startup (total:1)
2024-07-11 11:00:03,526 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main)     Started route1 (timer://yaml)
2024-07-11 11:00:03,526 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Apache Camel 4.6.0 (camel-1) started in 204ms (build:0ms init:0ms start:204ms)
2024-07-11 11:00:03,628 INFO  [io.quarkus] (main) timer-log 1.0.0 on JVM (powered by Quarkus 3.12.2) started in 1.159s. Listening on: http://0.0.0.0:8080
2024-07-11 11:00:03,628 INFO  [io.quarkus] (main) Profile prod activated. 
2024-07-11 11:00:03,628 INFO  [io.quarkus] (main) Installed features: [camel-attachments, camel-cli-connector, camel-console, camel-core, camel-management, camel-microprofile-health, camel-platform-http, camel-rest, camel-rest-openapi, camel-timer, camel-xml-io-dsl, camel-xml-jaxb, camel-yaml-dsl, cdi, kubernetes, smallrye-context-propagation, smallrye-health, vertx]
2024-07-11 11:00:04,531 INFO  [route1] (Camel (camel-1) thread #2 - timer://yaml) Hello Camel from route1
2024-07-11 11:00:05,528 INFO  [route1] (Camel (camel-1) thread #2 - timer://yaml) Hello Camel from route1
2024-07-11 11:00:06,529 INFO  [route1] (Camel (camel-1) thread #2 - timer://yaml) Hello Camel from route1
```

## Delete the application

When done, you can delete the application like this ...

```shell
kubectl delete --all -f ./target/kubernetes/kubernetes.yml
```

## Related Guides

- Kubernetes ([guide](https://quarkus.io/guides/kubernetes)): Generate Kubernetes resources from annotations
- Camel Log ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/log.html)): Prints data form the routed message (such as body and headers) to the logger
- Camel YAML DSL ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/yaml-dsl.html)): An YAML stack for parsing YAML route definitions
- Camel Timer ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/timer.html)): Generate messages in specified intervals using java.util.Timer
