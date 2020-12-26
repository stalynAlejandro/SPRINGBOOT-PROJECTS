package madstodolist;

import madstodolist.model.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TareaTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TareaRepository tareaRepository;

    @Autowired
    EquipoRepository equipoRepository;

    //
    // Tests modelo Tarea
    //

    @Test
    public void crearTarea() throws Exception {
        // GIVEN
        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");

        // WHEN
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");
        
        // THEN
        assertThat(tarea.getTitulo()).isEqualTo("Práctica 1 de MADS");
        assertThat(tarea.getUsuario()).isEqualTo(usuario);
    }

    @Test
    public void comprobarIgualdadSinId() throws Exception {
        // GIVEN

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");


        Tarea tarea1 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea2 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler");

        // THEN

        assertThat(tarea1).isEqualTo(tarea2);
        assertThat(tarea1).isNotEqualTo(tarea3);
    }

    @Test
    public void comprobarIgualdadConId() throws Exception {
        // GIVEN

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Tarea tarea1 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea2 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler");

        tarea1.setId(1L);
        tarea2.setId(2L);
        tarea3.setId(1L);

        // THEN

        assertThat(tarea1).isEqualTo(tarea3);
        assertThat(tarea1).isNotEqualTo(tarea2);
    }

    //
    // Tests TareaRepository
    //

    @Test
    @Transactional
    public void crearTareaEnBaseDatos() throws Exception {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql

        Usuario usuario = usuarioRepository.findById(1L).orElse(null);

        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

        // WHEN

        tareaRepository.save(tarea);

        // THEN

        assertThat(tarea.getId()).isNotNull();
        assertThat(tarea.getUsuario()).isEqualTo(usuario);
        assertThat(tarea.getTitulo()).isEqualTo("Práctica 1 de MADS");
    }

    /*@Test(expected = Exception.class)
    @Transactional
    @Ignore
    public void salvarTareaEnBaseDatosConUsuarioNoBDLanzaExcepcion() throws Exception{
        // GIVEN
        // Creamos un usuario sin ID y, por tanto, sin estar en gestionado
        // por JPA
        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

        // WHEN

        tareaRepository.save(tarea);

        // THEN
        // Se lanza una excepción (capturada en el test)
    }*/

    @Test
    @Transactional(readOnly = true)
    public void unUsuarioTieneUnaListaDeTareas() throws Exception{
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql

        Usuario usuario = usuarioRepository.findById(1L).orElse(null);

        // WHEN
        Set<Tarea> tareas = usuario.getTareas();

        // THEN

        assertThat(tareas).isNotEmpty();
    }

    @Test
    @Transactional
    public void unaTareaNuevaSeAñadeALaListaDeTareas() throws Exception{
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql

        Usuario usuario = usuarioRepository.findById(1L).orElse(null);

        // WHEN

        Set<Tarea> tareas = usuario.getTareas();
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");
        tareaRepository.save(tarea);

        // THEN

        assertThat(usuario.getTareas()).contains(tarea);
        assertThat(tareas).isEqualTo(usuario.getTareas());
        assertThat(usuario.getTareas()).contains(tarea);
    }

    @Test
    @Transactional
    public void nuevasPropiedadesTareas() throws Exception {
        
        //GIVEN
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Tarea tarea = new Tarea(usuario, "Nuevas Tareas");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //WHEN
        tarea.setDescripcion("descripcion");
        tarea.setFechaInicio(sdf.parse("2020-02-01"));
        tarea.setFechaFin(sdf.parse("2020-02-02"));

        tareaRepository.save(tarea);

        // THEN
        assertThat(usuario.getTareas()).contains(tarea);
        assertThat(tarea.getFechaInicio()).isNotNull();
        assertThat(tarea.getFechaFin()).isNotNull();
        assertThat(tarea.getDescripcion()).isNotNull();
    }

    @Test
    @Transactional
    public void comprobarEstadoTarea() throws Exception {

        // Given
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Tarea tarea = new Tarea(usuario, "Sin terminar");
        Tarea tarea2 = new Tarea(usuario, "Terminada");

        // When
        tarea2.setFinalizada(true);
        tareaRepository.save(tarea);
        tareaRepository.save(tarea2);


        // Then
        assertThat(tarea.getFinalizada()).isEqualTo(false);
        assertThat(tarea2.getFinalizada()).isEqualTo(true);
    }


    @Test
    @Transactional
    public void asignarTareaDeEquipo() throws Exception{
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Equipo equipo = equipoRepository.findById(1L).orElse(null);
        Tarea tarea = new Tarea(usuario, "Tarea de Equipo");

        //tarea asignada a al equipo de domingo y a el mismo
        tarea.setEquipo(equipo);
        tarea.setAsignadoA(usuario);

        tareaRepository.save(tarea);

        assertThat(tarea.getEquipo().getId()).isEqualTo(1L);
        assertThat(tarea.getAsignadoA().getId()).isEqualTo(1L);

    }

    @Test
    @Transactional
    public void duracionDeTarea(){
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Tarea tarea = new Tarea(usuario, "Tarea de Equipos 1");

        assertEquals(tarea.getDuracion(), 0);
    }


}
