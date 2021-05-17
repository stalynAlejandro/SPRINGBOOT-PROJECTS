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
