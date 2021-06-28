# Estudio de los Microservicios

Objetivos de este estudio: 
- Comprender qué es un microservicio. 
- Conocer la arquitectura basada en microservicios, sus ventajas y desventajas. 
- Utilizar SpringBoot. 
- Contruir una aplicación basada en MicroServicios. 

## Aplicaciones monolíticas o tradicionales.
El modelo o arquitectura tradicional de aplicaciones se caracteriza por que todas las partes que componen el producto software, están desplegados en un solo equipo. 

Es decir que la Interfaz de Usuario, la lógica, la base de datos están corriendo en un servidor. 

Esta arquitectura suele estar formada por tres capas: 
- La presentación. Front End.
- La lógica. Back End.
- Los datos. BBDD. 

Se conoce a esta arquitectura como "monolítica", porque es un producto pensado y desarrollado como una unidad. 

Las bases de esta arquitectura son sencillas de comprender, pero trae consigo una serie de problemas y riesgos. 

## Problemas y riesgos
El mayor problema de esta arquitectura surge cuando el equipo tiene que realizar cambios una vez la aplicación ya ha sido desplegada. 

Tanto si añadimos una nueva característica o cambiamos algún fragmento de código, por pequeño que sea, para poder poner estos cambios en el entorno de producción, la aplicación entera tiene que volver a ser redesplegada. 

El tiempo que se tarde vovler a desplegar la aplicación será directamente proporcional al tamaño de esta.

Otros problemas y factores de riesgo son: la limitada escalabilidad, el complejo mantenimiento y prevención de degradación de software, y su poca vérsatilidad ante cambios importantes.

# Aplicaciones basadas en Microservicios.

Factores que impulsaron a la creación de arq. de microservicios: 
- Complejidad: Externalizar los servicios, que antes estaban incluidos en la propia estructura. Surjen aplicaciones que se comunican con varios servicios y bases de datos, que pueden pertencer a la misma aplicación o no. 

- Rapidez y fiabilidad . Las aplicaciones que sobreviven son las que tardan menos en cargarse, actualizarse, descargar archivos, etc.

- Rendimiento y Escalabilidad. El mercado está totalmente globalizado. Las aplicaciones deben tener mecanismos para escalar y desescalar según la demanda y , sobretodo , ser capaces de mantener el rendimiento en cualquier situación. Es importante por que no podemos saber el volumen de peticiones/transacciones que habrá en un momento determinado. 

El concepto fundamental destrás de los microservicios es que para construir servicios altamente escalables necesitamos desgranar nuestras aplicaciones en pequeños servicios. 

Estos servicios van a poder ser construidos y desplegados de manera independiente. Y nos aporta:
- Flexibilidad. Dado que nuestra aplicación se compone de servicios pequeños y de una única función, podemos reemplazarlos o cambiarlos para ofrecer nuevas funcionalidades en poco tiempo. 

- Resilentes. Esta arquitectura tiene una gran resistencia ante los fallos, gracias a la modularidad del sistema. Es decir, que cuando un servicio falla, puede ser sustituido fácilmente y rápidamente por otro. 

- Escalables. Nuestra aplicación se debe poder escalar horizontalmente a través de diferentes servidores. Esto nos permite responder horizontalmente a los aumentos de la demanda de cualquier servicio de la aplicación, haciendo ese coste efectivo ya que únicamente potenciamos la disponibilidad del recurso necesario. 

## Definición y principios de los microservicios. 

Los microservicios nos permiten desarrollar aplicaciones con módulos físicamente separados. 

Amazon, Netflix  y eBay son ejemplos de aplicación exitosa de esta arquitectura. Surgieron basados en la idea propuesta por Alister Cockburn en 2005 de la Arquitectura Exagonal (también conocida como Arquitectura de Puertos y Adaptadores). 

Esta arquitectura proponía encapsular las funciones de negocio del resto de la aplicación. Estas funciones al estar aisladas, no conocían los canles de entrada o los formatos de mensajes que podían recibir. 

Esto se solucionaba convirtiendo los diferentes mensajes que recibía la aplicación a uno que fuese conocido por las funciones. El proceso que llevan a cabo los puertos y adaptadores pasa desapercibido tanto por las aplicaciones externas como las funciones internas. 

## Principios de los MicroServicios

- Principio de única responsabilidad. Es uno de los patrones de diseño software SOLID, que están orientados al desarrollo de aplicaciones que usen la programación orientada a objetos. Describe que una unidad debe tener una única responsabilidad. Este principio nos aporta una alta cohesión en cada uno de los microservicios. 

- Principio de Autonomía. Los microservicios son servicios autónomos e independientemente desplegables, que toman la responsabilidad completa sobre una tarea y su ejecución. cuando un servicio es desplegado, decimos que se ha instanciado una copia de él. Pueden operar y cambiar independientemente del resto que le rodean. Recogen las dependencias como librerías, entornos de ejecución, e incluso a sí mismos en servidores, conten