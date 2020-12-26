package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Test
    public void obtenerListadoEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql
        // WHEN
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        // THEN
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto P1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto P3");
    }

    @Test
    public void obtenerEquipo(){
        //GIVEN
        //En el application.properties se cargan los datos de prueba del fichero datos-tests.sql
        //WHEN
        Equipo equipo = equipoService.findById(1L);
        //THEN
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
        //Comprobamos que la relacion con Usuarios es lazy: al
        //intentar acceder a la coleccion de usuarios se debe lanzar una
        //excepcion de tipo LazyInitializationException.
        assertThatThrownBy(() -> {
            equipo.getUsuarios().size();
        }).isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void comprobarRelacionUsuarioEquipos(){
        //GIVEN
        //En el application.properties se cargan los datos de prueba del fichero datos-tests.sql
        //WHEN
        Usuario usuario = usuarioService.findById(1L);
        //THEN
        assertThat(usuario.getEquipos()).hasSize(1);
    }

    @Test
    public void obtenerUsuariosEquipo(){
        //GIVEN
        //En el application.properties se cargan lso datos de prueba del fichero datos-test.sql
        //WHEN
        List<Usuario> usuarios = equipoService.usuariosEquipo(1L);
        //THEN
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("ana.garcia@gmail.com");
        //Comprobamos que la relacion entre usuarios y equipos es eager
        //Primero comprobamos que la coleccion de equipos tiene 1 elemento
        assertThat(usuarios.get(0).getEquipos()).hasSize(1);
        //Y despues que el elemento es el equipo Proyecto P1
        assertThat(usuarios.get(0).getEquipos().stream().findFirst().get().getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    @Transactional
    public void crearNuevoEquipo(){
        //GIVEN
        //En el application.properties se cargan los datos de prueba del fichero datos-tests.sql
        //WHEN
        Usuario admin = usuarioService.findById(1L);
        equipoService.crearEquipo("Nuevo Proyecto", admin);
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        // THEN
        assertThat(equipos).hasSize(3);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Nuevo Proyecto");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto P1");
        assertThat(equipos.get(2).getNombre()).isEqualTo("Proyecto P3");
    }

    @Test
    @Transactional
    public void aniadirUsuarioAEquipo(){
        //GIVEN
        //En el application.properties se cargan los datos de prueba del fichero datos-tests.sql
        //WHEN
        //usuario ID:1 = ana.garcia@gmail.com
        //equipos ID:2 = Proyecto P3
        Equipo equipo = equipoService.aniadirUsuarioAEquipo(1L, 2L);
        List<Usuario> listaUsuariosEquipo = equipoService.usuariosEquipo(2L);
        List<Equipo> listaEquiposUsuario = equipoService.equiposUsuario(1L);

        assertThat(listaUsuariosEquipo).hasSize(1);
        assertThat(listaEquiposUsuario).hasSize(2);
        assertThat(listaUsuariosEquipo.get(0).getEmail()).isEqualTo("ana.garcia@gmail.com");
        assertThat(listaEquiposUsuario.get(0).getNombre()).isEqualTo("Proyecto P1");
        assertThat(listaEquiposUsuario.get(1).getNombre()).isEqualTo("Proyecto P3");
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P3");
    }

    @Test
    @Transactional
    public void borrarUsuarioDeEquipo(){
        //GIVEN
        //En el application.properties se cargan los datos de prueba del fichero datos-tests.sql
        //WHEN
        //usuario ID:1 = ana.garcia@gmail.com
        //equipo  ID:1 = Proyecto P1
        Equipo equipo = equipoService.borrarUsuarioDeEquipo(1L, 1L);
        List<Usuario> listaUsuariosEquipo = equipoService.usuariosEquipo(1L);
        List<Equipo> listaEquiposUsuario = equipoService.equiposUsuario(1L);

        assertThat(listaUsuariosEquipo).hasSize(0);
        assertThat(listaEquiposUsuario).hasSize(0);
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
        assertThat(equipoService.usuariosEquipo(1L)).hasSize(0);

    }

    @Test
    @Transactional
    public void borrarEquipo(){
        Set<Usuario> usuariosEquipo1 = equipoService.findById(1L).getUsuarios();
        equipoService.borrarEquipo(1L);
        List<Equipo> listaEquipos = equipoService.findAllOrderedByName();

        assertThat(usuariosEquipo1.size()).isEqualTo(0);
        assertThat(listaEquipos.size()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void cambiarNombreEquipo(){
        equipoService.cambiarNombreEquipo(1L, "Nombre modificado");
        Equipo equipo = equipoService.findById(1L);

        assertThat(equipo.getNombre()).isEqualTo("Nombre modificado");
    }

    @Test
    public void obtenerAdministradorEquipo(){
        Usuario admin = equipoService.getAdministradorEquipo(1L);
        assertThat(admin.getId()).isEqualTo(1L);
        assertThat(admin.getNombre()).isEqualTo("Ana García");
    }
    
}