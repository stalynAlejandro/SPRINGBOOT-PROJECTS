package madstodolist;

import madstodolist.controller.RegistroData;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import madstodolist.service.UsuarioServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsuarioServiceTest {

    Logger logger = LoggerFactory.getLogger(UsuarioServiceTest.class);

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void servicioLoginUsuario() {
        // GIVEN
        // Datos cargados de datos-test.sql

        // WHEN

        UsuarioService.LoginStatus loginStatusOK = usuarioService.login("ana.garcia@gmail.com", "12345678");
        UsuarioService.LoginStatus loginStatusErrorPassword = usuarioService.login("ana.garcia@gmail.com", "000");
        UsuarioService.LoginStatus loginStatusNoUsuario = usuarioService.login("pepito.perez@gmail.com", "12345678");

        // THEN

        assertThat(loginStatusOK).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);
        assertThat(loginStatusErrorPassword).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);
        assertThat(loginStatusNoUsuario).isEqualTo(UsuarioService.LoginStatus.USER_NOT_FOUND);
    }

    @Test
    @Transactional
    public void servicioRegistroUsuario() {
        // GIVEN

        Usuario usuario = new Usuario("usuario.prueba2@gmail.com");
        usuario.setPassword("12345678");

        // WHEN

        usuarioService.registrar(usuario);

        // THEN

        Usuario usuarioBaseDatos = usuarioService.findByEmail("usuario.prueba2@gmail.com");
        assertThat(usuarioBaseDatos).isNotNull();
        assertThat(usuarioBaseDatos.getPassword()).isEqualTo(usuario.getPassword());
    }

    @Test(expected = UsuarioServiceException.class)
    public void servicioRegistroUsuarioExcepcionConNullPassword() {
        // Pasamos como argumento un usario sin contraseña
        Usuario usuario =  new Usuario("usuario.prueba@gmail.com");
        usuarioService.registrar(usuario);
    }


    @Test(expected = UsuarioServiceException.class)
    public void servicioRegistroUsuarioExcepcionConEmailRepetido() {
        // GIVEN
        // Datos cargados de datos-test.sql

        // WHEN
        // Pasamos como argumento un usario con emaii existente en datos-test.sql
        Usuario usuario =  new Usuario("ana.garcia@gmail.com");
        usuario.setPassword("12345678");
        usuarioService.registrar(usuario);

        // THEN
        // Se produce una excepción comprobada con el expected del test
    }

    @Test
    @Transactional
    public void servicioRegistroUsuarioDevuelveUsuarioConId() {
        // GIVEN

        Usuario usuario = new Usuario("usuario.prueba@gmail.com");
        usuario.setPassword("12345678");

        // WHEN

        usuario = usuarioService.registrar(usuario);

        // THEN

        assertThat(usuario.getId()).isNotNull();
    }

    @Test
    public void servicioConsultaUsuarioDevuelveUsuario() {
        // GIVEN
        // Datos cargados de datos-test.sql

        // WHEN

        Usuario usuario = usuarioService.findByEmail("ana.garcia@gmail.com");

        // THEN

        assertThat(usuario.getId()).isEqualTo(1L);
    }

    @Test
    public void servicioObtenerTodosUsuarios() {
        Usuario nuevoUsuario = new Usuario("vnm12@alu.ua.es");
        nuevoUsuario.setPassword("123");
        usuarioService.registrar(nuevoUsuario);

        List<Usuario> todosUsuarios = usuarioService.getAllUsuarios();

        assertThat(todosUsuarios.size()).isEqualTo(3);
        assertThat(todosUsuarios.get(0).getEmail()).isEqualTo("ana.garcia@gmail.com");
        assertThat(todosUsuarios.get(1).getEmail()).isEqualTo("victor@gmail.com");
        assertThat(todosUsuarios.get(2).getEmail()).isEqualTo("vnm12@alu.ua.es");
    }

    /*@Test
    public void servicioExisteAdmin(){
        Boolean resultado = usuarioService.existeAdmin();
        assertThat(resultado).isFalse();
    }*/

    @Test
    public void serviciobloquearDesbloquearUsuario(){
        String resultado = usuarioService.bloquearDesbloquearUsuario(1L);
        assertThat(resultado).isEqualTo("Usuario bloqueado!");
    }

    @Test
    public void serviciobloquearDesbloquearUsuario2(){
        String resultado = usuarioService.bloquearDesbloquearUsuario(2L);
        assertThat(resultado).isEqualTo("Usuario desbloqueado!");
    }

    @Test
    @Transactional
    public void servicioModificarUsuarioCuenta() {
        // GIVEN

        Usuario usuario = new Usuario("update@email.com");
        usuario.setPassword("123");
        usuario.setFechaNacimiento(new Date(1998-12-01));
        usuario.setNombre("Prueba");
        usuarioService.registrar(usuario);

        Usuario usuarioModifcado = usuarioService.findByEmail("update@email.com");
        usuarioModifcado.setNombre("Modificado");
        usuarioModifcado.setFechaNacimiento(new Date(1998-9-02));
        usuarioModifcado.setEmail("c@gmail.com");
        usuarioModifcado.setPassword("456");

        // WHEN

        usuarioService.modificar(usuario.getId(), usuarioModifcado);

        // THEN

        Usuario usuarioResultado = usuarioService.findByEmail("c@gmail.com");
        assertThat(usuarioResultado).isNotNull();
        assertThat(usuarioResultado.getEmail()).isEqualTo("c@gmail.com");
        assertThat(usuarioResultado.getNombre()).isEqualTo("Modificado");
        assertThat(usuarioResultado.getFechaNacimiento()).isEqualTo(new Date(1998-9-02));
        assertThat(usuarioResultado.getPassword()).isEqualTo("456");
    }

    @Test
    @Transactional
    public void servicioBorrarUsuarioSinTareasYsinEquipos() {
        // GIVEN

        Usuario usuario = new Usuario("update@email.com");
        usuario.setPassword("123");
        usuario.setFechaNacimiento(new Date(1998 - 12 - 01));
        usuario.setNombre("Prueba");
        usuarioService.registrar(usuario);

        // WHEN

        usuarioService.borrarCuenta(usuario.getId());

        // THEN

        Usuario usuarioResultado = usuarioService.findByEmail("update@email.com");
        assertThat(usuarioResultado).isNull();
    }

    @Test
    @Transactional
    public void servicioBorrarUsuarioConTareasYconEquipos() {
        // GIVEN

        // Datos cargados de datos-test.sql
        Usuario usuario = usuarioService.findById(1L);

        // WHEN

        usuarioService.borrarCuenta(usuario.getId());

        // THEN

        Usuario usuarioResultado = usuarioService.findById(1L);
        assertThat(usuarioResultado).isNull();
    }

}