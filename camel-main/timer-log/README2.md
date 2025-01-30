# Camel Timer-Log Example

In addition to Quarkus and Spring Boot, Camel can also run natively on Kubernetes. 

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

We now export the integration as a native Camel project.

```shell 
camel kubernetes export timer-log-route.yaml \
  --gav=examples:timer-log:1.0.0 \
  --trait container.image-pull-policy=IfNotPresent \
  --trait service.type=NodePort \
  --runtime=camel-main
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

A health endpoint is available at
* http://127.0.0.1:8080/q/health


## Running the Docker container

You can also run this application in plain Docker like this ...

```shell
docker run -it --rm -p 8080:8080 examples/timer-log:1.0.0 
```

## Deploy on Kubernetes

You can deploy/run this application on Minikube like this ...

```shell
kubectl apply -f ./target/kubernetes/kubernetes.yml
kubectl logs -f --tail 400  -l app.kubernetes.io/name=timer-log

2024-07-24 10:28:10.917  INFO 1 --- [           main] org.apache.camel.main.MainSupport        : Apache Camel (Main) 4.8.0-SNAPSHOT is starting
2024-07-24 10:28:10.949  INFO 1 --- [           main] org.apache.camel.main.BaseMainSupport    : Classpath scanning enabled from base package: examples.timerlog
2024-07-24 10:28:11.434  INFO 1 --- [           main] org.apache.camel.main.BaseMainSupport    : Auto-configuration summary
2024-07-24 10:28:11.434  INFO 1 --- [           main] org.apache.camel.main.BaseMainSupport    :     [application.properties]       camel.main.basePackageScan=examples.timerlog
2024-07-24 10:28:11.434  INFO 1 --- [           main] org.apache.camel.main.BaseMainSupport    :     [application.properties]       camel.server.enabled=true
2024-07-24 10:28:11.435  INFO 1 --- [           main] org.apache.camel.main.BaseMainSupport    :     [application.properties]       camel.server.healthCheckEnabled=true
2024-07-24 10:28:11.698  INFO 1 --- [           main] e.camel.impl.engine.AbstractCamelContext : Apache Camel 4.8.0-SNAPSHOT (camel-1) is starting
2024-07-24 10:28:11.734  INFO 1 --- [           main] vertx.core.spi.resolver.ResolverProvider : Using the default address resolver as the dns resolver could not be loaded
2024-07-24 10:28:11.781  INFO 1 --- [ntloop-thread-0] tform.http.vertx.VertxPlatformHttpServer : Vert.x HttpServer started on 0.0.0.0:8080
2024-07-24 10:28:11.788  INFO 1 --- [           main] e.camel.impl.engine.AbstractCamelContext : Routes startup (total:1)
2024-07-24 10:28:11.788  INFO 1 --- [           main] e.camel.impl.engine.AbstractCamelContext :     Started route1 (timer://yaml)
2024-07-24 10:28:11.788  INFO 1 --- [           main] e.camel.impl.engine.AbstractCamelContext : Apache Camel 4.8.0-SNAPSHOT (camel-1) started in 89ms (build:0ms init:0ms start:89ms)
2024-07-24 10:28:12.807  INFO 1 --- [ - timer://yaml] route1                                   : Hello Camel from route1
2024-07-24 10:28:13.792  INFO 1 --- [ - timer://yaml] route1                                   : Hello Camel from route1
2024-07-24 10:28:14.793  INFO 1 --- [ - timer://yaml] route1                                   : Hello Camel from route1
```

The health endpoint is accessible on the node port.

```
$kubectl get svc
NAME         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
kubernetes   ClusterIP   10.96.0.1       <none>        443/TCP        82m
timer-log    NodePort    10.104.23.166   <none>        80:30931/TCP   13m

$ curl -s http://127.0.0.1:30931/q/health
```

## Delete the application

When done, you can delete the application like this ...

```shell
kubectl delete --all -f ./target/kubernetes/kubernetes.yml
```

## Related Guides

- Kubernetes ([guide](https://camel.apache.org/manual/camel-jbang-kubernetes.html)): Export Camel project and generate Kubernetes resources
- Camel Log ([guide](https://camel.apache.org/components/log-component.html)): Prints data form the routed message (such as body and headers) to the logger
- Camel YAML DSL ([guide](https://camel.apache.org/components/others/yaml-dsl.html)): An YAML stack for parsing YAML route definitions
- Camel Timer ([guide](https://camel.apache.org/components/timer-component.html)): Generate messages in specified intervals using java.util.Timer
