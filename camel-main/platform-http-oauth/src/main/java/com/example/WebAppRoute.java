package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cloud.oauth.OpenIDAuthProcessor;
import org.apache.camel.cloud.oauth.OpenIDCallbackProcessor;
import org.apache.camel.cloud.oauth.OpenIDLogoutProcessor;

/***********************************************
 *
 * This WebApp runs on the default platform-http component with an underlying Vertx engine.
 * Home and static content is not protected. Access to /protected requires user authetication in Keycloak
 *
 * WebApp and Keycloak endpoints are configured for external access on:
 *
 *      Keycloak: 192.168.0.10:30100
 *      WebApp  : 127.0.0.1:8080
 *
 *      jbang camel kubernetes export WebAppMain.java WebAppRoute.java application.properties files/webapp/* \
 * 	        --dep=org.apache.camel.cloud:camel-cloud-oauth:0.1.0-SNAPSHOT \
 * 	        --gav=examples:platform-http-oauth:1.0.0 \
 * 	        --package-name=com.example \
 * 	        --main-classname=WebAppMain \
 * 	        --ignore-loading-error=true \
 * 	        --trait container.image-pull-policy=IfNotPresent \
 * 	        --trait service.type=NodePort \
 * 	        --image-builder=docker \
 * 	        --image-push=false \
 * 	        --runtime=camel-main
 *
 * 	   podName=$(kubectl get pod -l app.kubernetes.io/name=platform-http-oauth -o jsonpath='{.items[0].metadata.name}')
 * 	   kubectl port-forward ${podName} 8080:8080
 */
public class WebAppRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("platform-http:/")
                .setBody().simple("resource:classpath:META-INF/resources/index.html");
        from("platform-http:/static/styles.css")
                .setBody().simple("resource:classpath:META-INF/resources/styles.css");
        from("platform-http:/auth")
                .process(new OpenIDCallbackProcessor());
        from("platform-http:/protected")
                .process(new OpenIDAuthProcessor())
                .setBody().simple("resource:classpath:META-INF/resources/protected.html");
        from("platform-http:/logout")
                .process(new OpenIDLogoutProcessor());
    }
}
