# Camel Http Roll Dice Example

Camel support for Spring Boot provides auto-configuration of the Camel and starters for many Camel components.

## How this was created

First we create a simple Camel route that shows the outcome of rolling a dice.

```yaml
- from:
    uri: 'platform-http:/roll-dice'
    steps:
      - set-body:
          simple: 'roll: $simple{random(1,7)}'
```

We now export the integration as a Camel SpringBoot project.

```shell 
camel kubernetes export roll-dice-route.yaml \
  --gav=examples:roll-dice:1.0.0 \
  --trait container.imagePullPolicy=IfNotPresent \
  --trait service.type=NodePort \
  --runtime=spring-boot
```

## Packaging and running the application

We can then package the application with an ordinary maven build.

```shell
./mvnw clean package
```

We can now verify that the plain Java application runs as expected.

```shell
java -jar target/roll-dice-1.0.0.jar
```

## Running the Docker container

You can also run this application in plain Docker like this ...

```shell
docker run -it --rm -p 8080:8080 examples/roll-dice:1.0.0 
```

## Deploy on Kubernetes

You can deploy/run this application on Minikube like this ...

```shell
kubectl apply -f ./target/kubernetes/kubernetes.yml
kubectl logs -f --tail 400  -l app.kubernetes.io/name=roll-dice

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.1)

2024-07-30T12:25:48.666Z  INFO 1 --- [           main] examples.rolldice.CamelApplication       : Starting CamelApplication v1.0.0 using Java 17.0.12 with PID 1 (/maven/roll-dice-1.0.0.jar started by root in /)
2024-07-30T12:25:48.669Z  INFO 1 --- [           main] examples.rolldice.CamelApplication       : No active profile set, falling back to 1 default profile: "default"
2024-07-30T12:25:49.683Z  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2024-07-30T12:25:49.712Z  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2024-07-30T12:25:49.712Z  INFO 1 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.25]
2024-07-30T12:25:49.747Z  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2024-07-30T12:25:49.751Z  INFO 1 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 996 ms
2024-07-30T12:25:50.465Z  INFO 1 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint beneath base path '/actuator'
2024-07-30T12:25:50.540Z  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2024-07-30T12:25:51.278Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.8.0-SNAPSHOT (camel-1) is starting
2024-07-30T12:25:51.289Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Routes startup (total:1)
2024-07-30T12:25:51.290Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   :     Started route1 (platform-http:///roll-dice)
2024-07-30T12:25:51.290Z  INFO 1 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.8.0-SNAPSHOT (camel-1) started in 10ms (build:0ms init:0ms start:10ms)
2024-07-30T12:25:51.291Z  INFO 1 --- [           main] examples.rolldice.CamelApplication       : Started CamelApplication in 2.867 seconds (process running for 3.343)
2024-07-30T12:25:57.357Z  INFO 1 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2024-07-30T12:25:57.357Z  INFO 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2024-07-30T12:25:57.358Z  INFO 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
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
- Camel Http ([guide](https://camel.apache.org/components/platform-http-component.html)): Expose the existing HTTP server from the runtime
