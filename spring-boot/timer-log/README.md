# Camel Timer-Log Example

Camel support for Spring Boot provides auto-configuration of the Camel and starters for many Camel components.

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
      period: "1000"
    steps:
      - setBody:
          simple: "Hello Camel from ${routeId}"
      - log: "${body}"
```

We now export the integration as a Camel SpringBoot project.

```shell 
camel kubernetes export timer-log-route.yaml \
  --gav=examples:timer-log:1.0.0 \
  --trait container.imagePullPolicy=IfNotPresent \
  --runtime=spring-boot
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

## Running the Docker container

You can also run this application in plain Docker like this ...

```shell
docker run -it --rm examples/timer-log:1.0.0 
```

## Deploy on Kubernetes

You can deploy/run this application on Minikube like this ...

```shell
kubectl apply -f ./target/kubernetes/kubernetes.yml
kubectl logs -f --tail 400  -l app.kubernetes.io/name=timer-log

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.1)

2024-07-17T11:05:57.169Z  INFO 1 --- [           main] examples.timerlog.CamelApplication       : Starting CamelApplication using Java 17.0.11 with PID 1 (/app/classes started by root in /)
2024-07-17T11:05:57.171Z  INFO 1 --- [           main] examples.timerlog.CamelApplication       : No active profile set, falling back to 1 default profile: "default"
2024-07-17T11:05:58.745Z  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2024-07-17T11:05:58.755Z  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2024-07-17T11:05:58.756Z  INFO 1 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.25]
2024-07-17T11:05:58.788Z  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2024-07-17T11:05:58.788Z  INFO 1 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1539 ms
2024-07-17T11:05:59.972Z  INFO 1 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint beneath base path '/actuator'
2024-07-17T11:06:00.146Z  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2024-07-17T11:06:00.379Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.8.0-SNAPSHOT (camel-1) is starting
2024-07-17T11:06:00.383Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Routes startup (total:1)
2024-07-17T11:06:00.383Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   :     Started route1 (timer://yaml)
2024-07-17T11:06:00.383Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.8.0-SNAPSHOT (camel-1) started in 3ms (build:0ms init:0ms start:3ms)
2024-07-17T11:06:00.384Z  INFO 1 --- [           main] examples.timerlog.CamelApplication       : Started CamelApplication in 3.417 seconds (process running for 3.612)
2024-07-17T11:06:01.400Z  INFO 1 --- [ - timer://yaml] route1                                   : Hello Camel from route1
2024-07-17T11:06:02.389Z  INFO 1 --- [ - timer://yaml] route1                                   : Hello Camel from route1
2024-07-17T11:06:03.389Z  INFO 1 --- [ - timer://yaml] route1                                   : Hello Camel from route1
```

## Delete the application

When done, you can delete the application like this ...

```shell
kubectl delete -f ./target/kubernetes/kubernetes.yml
```

## Related Guides

- Kubernetes ([guide](https://quarkus.io/guides/kubernetes)): Generate Kubernetes resources from annotations
- Camel Log ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/log.html)): Prints data form the routed message (such as body and headers) to the logger
- Camel YAML DSL ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/yaml-dsl.html)): An YAML stack for parsing YAML route definitions
- Camel Timer ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/timer.html)): Generate messages in specified intervals using java.util.Timer
