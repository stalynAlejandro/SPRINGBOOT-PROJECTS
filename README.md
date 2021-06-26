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
Los métodos en los que se definen las respuestas HTTP están anotados con anotaciones en las que se indica el tipo de petición y la URL a la que se responde. 

Por ejemplo, en la clase anterior el método `saludo` constesta a las peticiones dirigidas a la URL `/saudo/Ana`. La cadena *Ana* en la URL es decodificada y pasada en el parámetro *nombre* al método. 


El método devuelve la respuesta HTTP. La anotación *@ResponseBody* construye automáticamente esta respuesta, añadiendo como contenido de la misma la cadena devuelta por el servicio. 

En este caso la respuesta es: 
```
HTTP/1.1 200 
Content-Type: text/plain;charset=UTF-8
Content-Length: 8
Date: Mon, 02 Sep 2019 14:59:04 GMT

Hola Ana
```

## Clases de Servicio

Las clases de Servicio implementan la lógica de negocio de la aplicación. Las clases *controller* llaman a las clases servicio, que son las que realmente realizan todo el procesamiento. 

De está forma se separan las responsabilildades. Las clases *controller* se encargan de procesar las peticiones y las respuestas HTTP y las clases de servicio son las que realmente realizan la lógica de negocio y devuelven el contenido de las respuestas. 

La separación de la lógica de negocio en las clases de servicio permite también realizar tests que trabajan sobre objetos JAVA, independientes de los formatos de entrada/salida manejados por los controladores.

Fichero : `src/main/demoapp/service/SaludoService.java`
```
package demoapp.service;

import org.springframework.stereotype.Service;

@Service
public class SaludoService {
    public String saluda(String nombre) {
        return "Hola " + nombre;
    }
}
```

## Inyección de dependencias en Spring
Spring Boot utiliza la anotación *@Autowired* para inyectar en la variable anotada un objeto nuevo del tipo indicado. Se puede definir la anotación en la variable o en el constructor de la clase. 

En los ejemplos anteriores podemos comprobar estas anotaciones. En la aplicación ejemplo se define un controlador y un servicio que devuelve un saludo. El servicio se anota con la anotación *@Service* y esta anotación le indica a SpringBoot que la clase va a poder ser inyectada. 

En el controlador se necesita instanciar un objeto de la clase *SaludoService* y se hace usando inyección de dependencias. En este caso lo hacemos anotando el constructor. 

SpringBoot se encarga de obtener una instancia y de inyectarla en la variable *service* que se pasa como parámetro al constructor. 

Más info: en **Spring Beans and Dependency Injection**

## Alcanze de los objetos inyectados
Por defecto el alcanze (scope) de todas las anotaciones de Spring (@service, @controller, @component, etc) es un *Singleton*. Existe un única instancia de ese objeto que es la que se inyecta en las variables. 

Al estar funcionando en una aplicación web, el *singleton* que hace de controlador recibirá múltiples peticiones concurrrentemente. Cada petición irá en su propio hilo de Java, por lo que múltiples hilos podrán estar ejecutando el mismo código del controlador. 

Por ello hay que tener cuidado en *no definir variables de instancia múltiples (con estado)* dentro del controlador, porque podrían producirse errores debidos a condiciones de carrera (un hilo modifica la misma variable que otro está leyendo). Es conveniente que todos los *beans* (controladores, servicios, etc.) sean objetos sin estado. 

También es posible definir otros alcances, como **@RequestScope** o **@SessionScope**. En el primer caso se crea una instancia nueva del objeto para cada petición HTTP y en el segundo se crea una instancia nueva en cada sesión HTTP. 

## Test
En la aplicación de demostración hay varios ejemplos que muestran posibles formas de realizar pruebas en una aplicación SpringBoot. 

SpringBoot incluye el framework **AssertJ** que permite realizar expresiones de prueba con un lenguaje muy expresivo. 

Los test se pueden ejecutar usando el comando típico de Maven:
> mvn test

O también, usando el comando de Maven Wrapper:
> ./mnvw  test