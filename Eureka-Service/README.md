# Spring.Io

![Tutorial](https://spring.io/guides/gs/service-registration-and-discovery/)

## Service Registration and Discovery

This sets up a **Netflix Eureka Service** and then build a client that both registers itself with the registry and uses it to resolve its own host. 

A service registry is useful because it enables clientside load-balancing and decouples service providers from consumers without the need for DNS.

## Start with Spring Intialzr.

The serve application needs only the **Eureka Server**.

The client application needs the **Eureka Server and Eureka Discovery Client**.

## Start a Eureka Service Registry.

First we need a **Eureka Service Registry**. Use **@EnableEurekaServer** to stand up a registry with which other applications can communicate. This is a regular Spring Boot application with one annotation (@EnableEurekaServer) added to enable the service registry. 

```java
package com.eureka.server;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplicaton{
	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}

```

When the registry starts, it will complain (with a stacktrace) that there are no replica nodes to which the registry can connect. In a production environment, you wil want more thant one instance of the registry. 

By default, the registry (server) tries to register itself, so we disable that behaviour as well. 

```json

eureka-server/src/main/resources/application.properties

server.port=8001

eureka.client.regsiter-with=false
eureka.client.fetch-registry=false

logging.level.com.netflix.eureka=OFF
logging.level.com.netflix.discovery=OFF

```


## Talking to the Registry (Server)

Now the registry is up, we can stand up a **client** that both registers *itself* with *the registry (server)* and uses the SpringCloud **DiscoveryClient** abstraction to interrogate the registry for its own host and port. 

The **@EnableDiscoveryClient** activates the Netflix Eureka *Discovery Client* implementation. 

The Eureka-Client defines a Spring MVC Rest endpoint (*ServiceInstanceRestController*) that returns an enumeration of all the registered **ServiceInstance** instances at *http://localhost:8080/service-intance/a-bootiful-client*. 

## Test the Application

Test the end-to-end result by starting the *eureka-service* first and then, once the has loaded, starting the *eureka-client*.

To Run the Eureka service with Maven, 
```
./mvnw spring-boot:run -pl eureka-service
```

To run the Eureka Client with Maven.
```
./mvnw spring-boot:ru -pl eureka-client
```


