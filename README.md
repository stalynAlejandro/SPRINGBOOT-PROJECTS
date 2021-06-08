# SpringBoot-Projects
Es muy sencillo crear aplicaciones de Spring Boot desde cero usando la web **Spring Initilizer**. En esa página puedes configurar los metadatos del proyecto y sus dependencias y descarga el proyecto como un ZIP con la estructura de directorios y el POM ya configurados. 

La aplicación es un secillo `Hola Mundo`. En los siguientes apartados explicamos cómo lanzarla y cómo funciona. 

## Ejecución de aplicaciones Spring Boot
SpringBoot permite ejecutar aplicaciones de Spring de forma *standalone*, sin necesidad de un servidor de aplicaciones. Una aplicación de SpringBoot lleva incluido un servidor web embebido (Tomcat) que se pone en marcha al lanzar la aplicación y sirve todas las páginas de la aplicación web. 

Para lanzar una aplicación SpringBoot es suficiente tener instalados: 
    - JDK Java (8 en adelante)
    - Maven 

Maven incluso no es necesario si la aplicación SpringBoot lo tiene ya instalado utilizando Maven Wrapper, como es el caso de la aplicación de ejemplo. 

Desde el directorio donde está la aplicación que queremos lanzar podemos arrancarla como una aplicación Java. Podemos llamar a `./mvnw` para usar Maven Wrapper: 

````
./mvnw package
java -jar target/demoapp-0.0.1-SNAPSHOT.jar
````

También podemos lanzarla usando el plugin `spring-boot`de Mave:

````
./mvnw spring-boot:run
````

La aplicación se arrancará por defecto en el puerto 8080. Podemos probar las siguientes páginas: 

> http://localhost:8080
> http://localhost:8080/saludo/Pepito
> http://localhost:8080/saludoplantilla/Pepito

## Conceptos de SpringBoot

Estructura de directorios típica de los proyectos Java construidos con Maven. 
```
|- demoapp
    |-- src
        | -- main
            | -- java
                | -- demoapp
                    | -- controller
                            --- HomeController.java
                            --- SaludoController.java
                    | -- service
                            --- SaludoService.java
            | -- resources
                | -- templates
                        --- saludo.html
        | -- test
            | -- java
                | -- demoapp
                        --- ServiceTest.java
                        --- HttpRequestTest.java
                        --- WebMockTest.java
| -- target
--- .gitignore
--- pom.xml
--- README.md

```

El fichero `pom.xml` declara las dependencias. SpringBoot proporciona *starters* que agrupan un conjunto de dependencias comunes. 

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>es.ua.mads</groupId>
    <artifactId>demoapp</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demoapp</name>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.12.RELEASE</version>
    </parent>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <argLine>-Dfile.encoding=UTF8</argLine>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```

En el panel de Maven se puede consultar las líbrerias concretas que se han descargado. 

En el fichero de configuración de la aplicación se definen propiedades que configuran distintos aspectos de la misma, como la base de datos con la que se va a trabajar o el puerto en el que debe ejecutarse. Conforme necesitemos configurar estas propiedades iremos añadiendo elementos al fichero. 

Fichero: `resources/application.properties`
> spring.application.name = demoapp

## Controladores
Los controladores definen el código a ejecutar como respuesta a una petición HTTP. Son clases que se suelen colocar en el paquete `controller`y están anotadas con *@Controller*. 

Ejemplo: 
```
package demoapp.controller;

import demoapp.service.SaludoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class SaludoController {

    private final SaludoService service;

    @Autowired
    public SaludoController(SaludoService service) {
        this.service = service;
    }

    @RequestMapping("/saludo/{nombre}")
    public @ResponseBody String saludo(@PathVariable(value="nombre") String nombre) {
        return service.saluda(nombre);
    }

}
```
