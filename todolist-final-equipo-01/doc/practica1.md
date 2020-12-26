# Listado de nuevas clases y metodos implementados

-   authentication/ManagerUserSesion (métodos añadidos):
    - comprobarUsuarioAdmin: Comprueba si el usuario de la id pasada como parámetro es un usuario administrador o no. En el caso de que no sea administrador lanzara la excepción UsuarioNoLogeadoException. 
    - comprobarUsuarioBloqueado: Comprueba si el usuario de la id pasada como parámetro es un usuario bloqueado. Si el usuario está bloqueado está bloqueado se lanzará la excepción UsuarioNoLogeadoException.

-   controller/HomeController (método añadido):
    - about (GET: "/about"): URL accesible tanto para usuarios no registrados como usuarios registrados. Si el usuario esta logueado se utiliza el método usuarioService.findById(idUsuarioLogeado) que devuelve el usuario logueado y se le pasa a la vista con el nombre de la variable "usuario". Si no hay usuario logueado no se devuelve ningún usuario. Devuelve la vista "about".

-   controller/LoginController (modificación de métodos):
    - loginSubmit (POST: "/login"): Ahora, antes de loguear al usuario se comprueba que no esté bloqueado con managerUserSesion.comprobarUsuarioBloqueado. Además, si el usuario es de tipo administrador se redigirá a la vista "/usuarios", y si no es administrador se redigirá a "/usuarios/{id}/tareas", siendo {id} la id del usuario logueado.
    - registroForm (GET: "/registro"): Este método ahora hace uso de usuarioService.existeAdmin(), el cual recorre la lista de usuarios registrados y comprueba si existe algún usuario de tipo administrador. Si existe se le manda una variable a la vista con el nombre "existeAdmin" y valor true. En caso de no existir se manda la misma variable con valor false. Esta variable se usará en la vista para mostrar el correspondiente checkbox en caso de que sea necesario para la creación de un usuario administrador. Devuelve la vista "formRegistro".
    - registroSubmit (POST: "/registro"): El único cambio de este método ha sido establecer la propiedad de usuario.esAdmin según los datos del formulario y usuario.bloqueado a false.  

-   controller/RegistroData (atributos añadidos):
    - Se ha añadido el atributo booleano esAdmin con sus get y set.
    - Se ha añadido el atributo booleano bloqueado con sus get y set.
    
-   controller/UsuarioController (clase nueva):
    - listadoUsuarios (GET: "/usuarios"): URL solo accesible con un usuario de tipo administrador. Si se intenta acceder y el usuario no es de tipo administrador se lanzara UsuarioNoLogeadoException. Este método utiliza el método usuarioService.getAllUsuarios(), el cual devuelve una lista de todos los usuarios registrados y la devuelve a la vista con el nombre de la variable "listaUsuarios". Este método utiliza el método usuarioService.findById(idUsuarioLogeado) que devuelve el usuario logueado y se le pasa a la vista con el nombre de la variable "usuario". Devuelve la vista "usuarios".
    - descripcionUsuario (GET: "/usuarios/{id}"): URL solo accesible con un usuario de tipo administrador. Si se intenta acceder y el usuario no es de tipo administrador se lanzara UsuarioNoLogeadoException. Este método recibe como parámetro el id del usuario que se quiere obtener la descripción. Utiliza el método usuarioService.findBy(id) para obtener tanto al usuario logueado como al usuario del que se quiera obtener la descripción y se le pasa a la vista con el nombre de la variable "usuario" y "usuarioDescripcion" respectivamente. Devuelve la vista "descripcionUsuario".
    - bloquearUsuario (POST: /usuarios/{id}/bloqueo"): Solo accesible con un usuario de tipo administrador. Si se intenta acceder y el usuario no es de tipo administrador se lanzara UsuarioNoLogueadoException. Este método recibe como parámetro el id del usuario que se desea bloquear o desbloquear (según sea su estado en ese momento). Utiliza el método usuarioService.findById(idUsuarioLogeado) para obtener al usuario logueado y lo devuelve a la vista con el nombre de la variable "usuario". Este método utiliza usuarioService.bloquearDesbloquearUsuario(idUsuario) el cual se encarga de bloquear o desbloquear al usuario en cuestión, y además devuelve un String con el mensaje que se mostrará en la vista con el nombre de la variable "mensaje". Devuelve una redirección a la vista "usuarios".
    
-   model/Usuario (atributos añadidos):
    - Se ha añadido el atributo booleano esAdmin con sus get y set.
    - Se ha añadido el atributo booleano bloqueado con sus get y set.    

-   service/UsuarioService (métodos añadidos):
    - getAllUsuarios: Método que devuelve una List con todos los usuarios registrados.
    - existeAdmin: Método que devuelve un booleano si existe un usuario de tipo administrador. Devuelve false en caso de no existir.
    - bloquearDesbloquearUsuario: Método que encarga de bloquear o desbloquear al usuario cuya ID sea la que se ha pasado por parámetro. En caso de que haya sido bloqueado devolverá un String con el mensaje "Usuario bloqueado!" y en caso contrario devolverá "Usuario desbloqueado!".


   
# Listado de modificaciones sobre las vistas ya existentes.

-   fragments.html:
    - Esta vista contiene un nuevo fragmento de código llamado navbar. Este fragmento de código recibe una variable llamada usuario (representa al usuario logeado). 
    - Este fragmento contiene un menú que se personaliza para cada usuario. 
    - Muestra su nombre y redirige a las secciones privadas para su propio usuario.
    - Además si el usuario es de tipo administrador se mostrara una nueva entrada en el menú con la lista de usuarios.

-   formRegistro.html:
    - Esta vista ahora contiene un nuevo campo que representa el tipo de usuario que se creara.
    - Este campo solo se muestra cuando no hay un usuario de tipo administrador en la base de datos.
    - El campo es un checkbox que su estado será "hidden" cuando la variable "existeAdmin" esté a true.
    - Solo mostrara el texto "Administrador" cuando la variable "existeAdmin" sea false.
    
-   formEditarTarea.html, formNuevaTarea.html, listaTareas.html
    - Estas vistas ahora incluyen el fragmento de código "navbar" incluido en "fragments.html". Este fragmento recibe un parámetro llamado "usuaro".
    - TareaController se encarga de enviar a las vistas la variable "usuario" la cual representa al usuario logueado.
    
    

# Listado de nuevas vistas.

-   about.html:
    - Esta vista muestra información sobre el proyecto.
    - Puede ser accedida tanto por usuarios no registrados como por registrados.
    - Tiene la característica de que muestra la barra de menu sólo cuando se le pasa la variable "usuario". Si esta variable no existe no se incluye el fragmento de código. Esto se ha conseguido gracias a la siguiente directiva de Thymeleaf: <nav th:replace="${usuario} ? ~{fragments :: navbar(usuario=${usuario})} : ~{}"> . Esto quiere decir que si la variable ${usuario} tiene algún valor se incluya el fragmento de código "navbar" de "fragments.html", y si no tiene valor no incluya nada.

-   descripcionUsuario.html
    - Esta vista muestra información sobre un usuario concreto.
    - Sólo puede ser accedida por un usuario de tipo administrador.
    - Incluye el menu "navbar" de "fragments.html".
    - Se le pasan dos variables: "usuario" (hace referencia al usuario logueado) y "usuarioDescripcion" (hace referencia al usuario que se va a mostrar).

-   usuarios.html
    - Esta vista muestra una lista con todos los usuarios de la base de datos.
    - Sólo puede ser accedida por un usuario de tipo administrador.
    - Incluye el menú "navbar" de "fragments.html".
    - Se le pasan dos variables: "usuario" (hace referencia al usuario logueado) y "listaUsuarios" (hace referencia a la lista de usuarios).
    
# Modificación sobre el archivo de autoinserción de valores en la base de datos.

-   datos-dev.sql:
    - Se han añadido valores false a las nuevas columnas "bloqueado" y "es_admin" al usuario que se creaba por defecto anteriormente.
    
    
# Base de datos de TEST:
-   datos-test.sql:
    - Al usuario que estaba anteriormente se le ha añadido que no esta bloqueado y no es administrador.
    - Se ha añadido un nuevo usuario, está bloqueado y no es administrador.
    
# Nuevos tests añadidos.

-   AboutWebTests:
    - aboutSinLoguear: Comprueba que se puede acceder a la URL "/about" sin estar logueado.
    - aboutLogeado: Comprueba que se puede acceder a la URL "/about" estando logueado y se contiene el menu "navbar".
    
-   UsuarioServiceTest:
    - servicioObtenerTodosUsuarios: Se inicializa la BD con dos usuarios, se registra un nuevo usuario, se comprueba que la lista devuelta por usuarioService.getAllUsuarios() contiene 3 elementos y se comprueban sus datos.
    - serviciobloquearDesbloquearUsuario: Se bloquea al usuario con id = 1 con usuarioService.bloquearDesbloquearUsuario(1L) y luego se comprueba que se ha devuelto "Usuario bloqueado!".
    - serviciobloquearDesbloquearUsuario2: Se desbloquea al usuario con id = 2 con usuarioService.bloquearDesbloquearUsuario(2L); y luego se comprueba que se ha devuelto "Usuario desbloqueado!".
    
-   UsuarioWebTest:
    - listaUsuariosSinLoguear: Se intenta acceder a la URL "/usuarios", se comprueba que ha devuelto un error 4xx y no se ha podido acceder.
    - listaUsuariosLogueadoNoAdmin: Se intenta acceder a la URL "/usuarios" pasandole un usuario no administrador, se comrpeuba que se ha devuelto un error 4xx y no se ha podido acceder.
    - descripcionUsuarioSinLogear: Se intenta acceder a la URL "/usuarios/1", se comprueba que ha devuelto un error 4xx y no se ha podido acceder.
    - descripcionUsuarioLogeado: Se intenta acceder a la URL "/usuarios/1" pasandole un usuario no administrador, se comprueba que se ha devuelto un error 4xx y no se ha podido acceder. 
