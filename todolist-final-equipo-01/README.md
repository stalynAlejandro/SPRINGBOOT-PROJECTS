# Aplicación inicial ToDoList

Aplicación ToDoList.

## Requisitos

Necesitas tener instalado en tu sistema:

- Java 8 / 11.

## Ejecución

Se puede ejecutar una aplicación usando el _goal_ `run` del _plugin_ Maven
de Spring Boot:

```
$ ./mvnw spring-boot:run
```

También se puede generar un `jar` y ejecutarlo:

```
$ ./mvnw package
$ java -jar target/mads-todolist-victorNAV-1.2.0.jar
```

Una vez lanzada la aplicación puedes abrir un navegador y probar la página de inicio accediendo a:

- [http://localhost:8080/login](http://localhost:8080/login)

# Ejecución de Aplicaciones SPRING-BOOT

Spring Boot permite ejecutar aplicaciones Spring de forma _standalone_. Una aplicación Spring Boot lleva
incluido un servidor web embebido (Tomcat) que se pone en marcha al lanzar la aplicación y sirve todas
las páginas de la aplicación web.

Para lanzar una aplicación Spring Boot es suficiente tener instalados:

- JDK Java (8 en adelante)
- Maven

Maven incluso no es necesario si la aplicación Spring Boot lo tiene ya instalado utilizando Maven Wrapper,
como es el caso de la aplicación ejemplo.

Desde el directorio donde está la aplicación que queremos lanzar podemos arrancarla como una aplicación
Java. Podemos llamar a `mvn` si tenemos instalado Maven o a `./mvnw` para usar Maven Wrapper:

- ./mvnw package
- java -jar target/demoapp-0.0.1-SNAPSHOT.jar

También podemos lanzarla usando el plugin `spring-boot` de Maven:

```
./mvnw spring-boot:run
```

La aplicación se arranca por defecto en el puerto local 8080. Una vez arrancada la aplicación podemos
conectarnos desde un navegador a sus páginas de inicio.

En el caso de la aplicación demo descargada, podemos probar las siguientes páginas.

# Desarrollo y ejecución con IntelliJ

Para importar un proyecto SpringBoot en IntelliJ basta con importar el directorio donde se encuentre
el fichero `pom.xml`. Se puede hacer desde la pantalla de bienvenida de IntelliJ con la opción
_Import Project_ o usando la opción _File > New > Project from Existing Sources_, aparecerá la pantalla
de importación y seleccionamos el importador _Maven_.

Podemos configurarlo abriendo un terminal y lanzándolo con Maven. O también desde la _configuración de Run_
que ha creado IntelliJ al realizar la importación.

# Conceptos de SpringBoot

Estructura de la aplicación.
Estructura de directorios típicos de los proyectos Java construidos con Maven.

- spring-boot.
  - src
    - main
      - java
        - demoapp
          - controller
            - HomeContoller.java
            - SaludoController.java
            - SaludoControllerPlantilla.java
          - service
            - SaludoService.java
        - resources
          - templates
            - saludo.html
      - test
        - java
          - demoapp
            - AutoConfigureWebMockTest.java
            - HttpRequestTest.java
            - ServiceTests.java
            - WebMockTest.java
    - target
      .gitignore
      pom.xml
      README.md

El fichero `pom.xml` declara las dependencias. Spring Boot proporciona _starters_ que agrupan un conjunto
de dependencias comunes.

# Fichero pom.xml

```
<?xml version="1.0" encoding="UTF-8">
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
>

<groupId>es.mads</groupId>
<artifactId>demoapp</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>demoapp</name>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.16</version>
</parent>

<properties>
    <java.version>1.8</java.version>
</properties>

<dependencies>
        <dependency>
            <groupid>org.springframework.boot</groupid>
            <artifactid>spring-boot-starter-web</artifactid>
        </dependency>
        <dependency>
            <groupid>org.springframework.boot</groupid>
            <artifactid>spring-boot-starter-thymeleaf</artifactid>
        </dependency>
        <dependency>
            <groupid>org.springframework.boot</groupid>
            <artifactid>spring-boot-starter-test</artifactid>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
       <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
En el panel de Maven de IntelliJ se puede consultar las librerías concretas que se han descargado.

En el fichero de configuración de la aplicación se definen propiedades que configuran distintos aspectos
de la misma, como la base de datos con la que se va a trabajar o el puerto en el que debe ejecutarse. 
Conforme necesitemos configurar estas propiedades iremos añadiendo elementos al fichero.

# Fichero `resources/application.properties`
```
spring.application.name = demoapp
```

# Controladores

Los controladores definen el código a ejecutar como respuesta a una petición HTTP. Son clases que se suelen
colocar en el paquete `controller` y están anotadas con `@Contoller`. 

Vemos un ejemplo en la clase `SaludoController`. 

Fichero *src/main/java/demoapp/controller/SaludoController.java*

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

Los métodos en los que se definen las respuestas a las peticiones HTTP están anotados con anotaciones en las
que se indica el tipo de petición y la URL a la que se responde. 

Por ejemplo, en la clase anterior el método `saludo` contesta a las peticiones dirigidas a la URL 
`/saludo/Ana`. La cadena `Ana` en la URL es decodificada y pasada en el parámetro `nombre` al método. 

El método devuelve la respuesta HTTP. La anotación `@ResponseBody` construye automáticamente esta respuesta,
añadiendo como contenido de la misma la cadena devuelta por el servicio. 

