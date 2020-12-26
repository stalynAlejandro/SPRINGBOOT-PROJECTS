package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EquipoTest {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void crearEquipo() {

        Usuario admin = usuarioRepository.findById(1L).orElse(null);
        Equipo equipo = new Equipo("Proyecto P1", admin);
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    @Transactional
    public void grabarEquipo() {
        // GIVEN
        Usuario admin = usuarioRepository.findById(1L).orElse(null);
        Equipo equipo = new Equipo("Proyecto P1", admin);

        // WHEN
        equipoRepository.save(equipo);

        // THEN
        assertThat(equipo.getId()).isNotNull();
    }

    @Test
    public void comprobarIgualdadEquipos() {
        // GIVEN
        // Creamos tres equipos sin id, sólo con el nombre
        Usuario admin = usuarioRepository.findById(1L).orElse(null);
        Equipo equipo1 = new Equipo("Proyecto P1", admin);
        Equipo equipo2 = new Equipo("Proyecto P2", admin);
        Equipo equipo3 = new Equipo("Proyecto P2", admin);
        // THEN
        // Comprobamos igualdad basada en el atributo nombre
        assertThat(equipo1).isNotEqualTo(equipo2);
        assertThat(equipo2).isEqualTo(equipo3);
        // WHEN
        // Añadimos identificadores y comprobamos igualdad por identificadores
        equipo1.setId(1L);
        equipo2.setId(1L);
        equipo3.setId(2L);
        // THEN
        // Comprobamos igualdad basada en el atributo nombre
        assertThat(equipo1).isEqualTo(equipo2);
        assertThat(equipo2).isNotEqualTo(equipo3);
    }

    @Test
    public void comprobarRecuperarEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql
        // WHEN
        Equipo equipo = equipoRepository.findById(1L).orElse(null);
        // THEN
        assertThat(equipo).isNotNull();
        assertThat(equipo.getId()).isEqualTo(1L);
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    public void relacionMuchosAMuchosVacia(){
        //GIVEN
        Usuario admin = usuarioRepository.findById(1L).orElse(null);
        Equipo equipo = new Equipo("Proyecto P1", admin);
        Usuario usuario = new Usuario("prueba@gmail.com");
        //WHEN
        //THEN
        assertThat(equipo.getUsuarios()).isEmpty();
        assertThat(usuario.getEquipos()).isEmpty();
    }

    @Test
    @Transactional
    public void comprobarRelacionBaseDatos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql
        // WHEN
        Equipo equipo = equipoRepository.findById(1L).orElse(null);
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        // THEN
        assertThat(equipo.getUsuarios()).hasSize(1);
        assertThat(equipo.getUsuarios()).contains(usuario);
        assertThat(usuario.getEquipos()).hasSize(1);
        assertThat(usuario.getEquipos()).contains(equipo);
    }

    @Test
    public void comprobarFindAll(){
        //GIVEN
        //En el aplication.properties se cargan los datos e prueba del fichero datos-test.sql
        //WHEN
        List<Equipo> equipos = equipoRepository.findAll();
        //THEN
        assertThat(equipos).hasSize(2);
    }

    @Test
    public void administradorEquipo(){

        Equipo equipo = equipoRepository.findById(1L).orElse(null);
        Usuario admin = equipo.getAdmin();

        assertThat(admin.getNombre()).isEqualTo("Ana García");
        assertThat(admin.getId()).isEqualTo(1L);

    }

}