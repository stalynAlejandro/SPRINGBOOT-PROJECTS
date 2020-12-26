package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.controller.TareaData;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TareaServiceTest {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    EquipoService equipoService;

    @Test
    @Transactional
    public void testNuevaTareaUsuario() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql

        // WHEN
        Usuario usuario = usuarioService.findByEmail("ana.garcia@gmail.com");
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");
        tareaService.nuevaTareaUsuario(1L, tarea);

        // THEN

        assertThat(usuario.getTareas()).contains(tarea);
    }

    @Test
    public void testListadoTareas() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql

        Usuario usuario = new Usuario("ana.garcia@gmail.com");
        usuario.setId(1L);

        Tarea lavarCoche = new Tarea(usuario, "Lavar coche");
        lavarCoche.setId(1L);

        // WHEN

        List<Tarea> tareas = tareaService.allTareasUsuarioIndividuales(1L);

        // THEN

        assertThat(tareas.size()).isEqualTo(2);
        assertThat(tareas).contains(lavarCoche);
    }

    @Test
    public void testBuscarTarea() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql

        // WHEN

        Tarea lavarCoche = tareaService.findById(1L);

        // THEN

        assertThat(lavarCoche).isNotNull();
        assertThat(lavarCoche.getTitulo()).isEqualTo("Lavar coche");
    }

    @Test
    @Transactional
    public void testModificarTarea() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Pagar el recibo");
        tareaService.nuevaTareaUsuario(usuario.getId(), tarea);
        Long idNuevaTarea = tarea.getId();

        // WHEN

        TareaData editTarea = new TareaData();
        editTarea.setTitulo("Pagar la matrícula");
        Tarea tareaModificada = tareaService.modificaTarea(idNuevaTarea, editTarea);
        Tarea tareaBD = tareaService.findById(idNuevaTarea);

        // THEN

        assertThat(tareaModificada.getTitulo()).isEqualTo("Pagar la matrícula");
        assertThat(tareaBD.getTitulo()).isEqualTo("Pagar la matrícula");
    }

    @Test
    @Transactional
    public void testBorrarTarea() {
        // GIVEN
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Estudiar MADS");
        tareaService.nuevaTareaUsuario(usuario.getId(), tarea);

        // WHEN

        tareaService.borraTarea(tarea.getId());

        // THEN

        assertThat(tareaService.findById(tarea.getId())).isNull();
    }

    @Test
    @Transactional
    public void testModificarDescripcionTarea() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Hacer Mock");
        tarea.setDescripcion("descripcion");
        tareaService.nuevaTareaUsuario(usuario.getId(), tarea);
        Long idNuevaTarea = tarea.getId();

        // WHEN

        TareaData editTarea = new TareaData();
        editTarea.setDescripcion("nuevadescripcion");
        Tarea tareaModificada = tareaService.modificaTarea(idNuevaTarea, editTarea);
        Tarea tareaBD = tareaService.findById(idNuevaTarea);

        // THEN

        assertThat(tareaModificada.getDescripcion()).isEqualTo("nuevadescripcion");
        assertThat(tareaBD.getDescripcion()).isEqualTo("nuevadescripcion");
    }

    @Test
    @Transactional
    public void testModificarFechaInicioYFin() throws Exception {

        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero
        // datos-test.sql
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Pagar el recibo");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tarea.setFechaInicio(sdf.parse("2020-12-20"));
        tarea.setFechaFin(sdf.parse("2020-12-22"));
        tareaService.nuevaTareaUsuario(usuario.getId(), tarea);
        Long idNuevaTarea = tarea.getId();

        // WHEN

        TareaData editTarea = new TareaData();
        editTarea.setFechaFin(sdf.parse("2020-12-25"));
        editTarea.setFechaInicio(sdf.parse("2020-12-22"));
        Tarea tareaModificada = tareaService.modificaTarea(idNuevaTarea, editTarea);
        Tarea tareaBD = tareaService.findById(idNuevaTarea);

        // THEN

        assertThat(tareaModificada.getFechaFin()).isEqualTo("2020-12-25");
        assertThat(tareaModificada.getFechaInicio()).isEqualTo("2020-12-22");
        assertThat(tareaBD.getFechaFin()).isEqualTo("2020-12-25");
        assertThat(tareaBD.getFechaInicio()).isEqualTo("2020-12-22");
    }

    @Test
    @Transactional
    public void finalizarTarea() throws Exception {

        // GIVEN
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Sin Terminar");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tarea.setFechaInicio(sdf.parse("2020-12-20"));
        tarea.setFechaFin(sdf.parse("2020-12-22"));
        tareaService.nuevaTareaUsuario(usuario.getId(), tarea);

        // When
        tareaService.finalizarTarea(tarea.getId(), true);

        // Then
        assertThat(tarea.getFinalizada()).isEqualTo(true);

    }

    @Test
    @Transactional
    public void recomenzarTarea() throws Exception {

        // GIVEN
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Sin Terminar");
        tarea.setFinalizada(true);
        tareaService.nuevaTareaUsuario(usuario.getId(), tarea);

        // When
        tareaService.finalizarTarea(tarea.getId(), false);

        // Then
        assertThat(tarea.getFinalizada()).isEqualTo(false);

    }

    @Test
    @Transactional
    public void hayTareasTerminadas() throws Exception {

        // GIVEN
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Terminada");
        tarea.setFinalizada(true);
        tarea.setFechaInicio(sdf.parse("2020-12-20"));

        // WHEN
        List<Tarea> finalizadas = tareaService.tareasTerminadas(usuario.getId());

        // THEN
        assertTrue(finalizadas.contains(tarea));
    }

    @Test
    @Transactional
    public void hayTareasPendientes() throws Exception {

        // GIVEN
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Pendiente");
        tarea.setFinalizada(false);

        // WHEN
        List<Tarea> finalizadas = tareaService.tareasPendientes(usuario.getId());

        // THEN
        assertThat(finalizadas).contains(tarea);
    }

    @Test
    @Transactional
    public void testModificarAsigacionTareaDeEquipoAUsuario() throws Exception {
        Usuario usuarioAdministrador = usuarioService.findById(1L);
        Equipo equipo = equipoService.findById(1L);
        Usuario usuarioEquipo = usuarioService.findById(2L);
        equipoService.aniadirUsuarioAEquipo(usuarioEquipo.getId(), equipo.getId());

        // creo la tarea original
        Tarea tarea = new Tarea(usuarioAdministrador, "Nueva tarea de equipo");
        tarea.setAsignadoA(usuarioAdministrador);
        tareaService.nuevaTareaUsuario(usuarioAdministrador.getId(), tarea);
        // creo la tarea con los datos modificados
        TareaData tareaModificada = new TareaData();
        tareaModificada.setTitulo("Nueva tarea de equipo Modificada");
        tareaModificada.setAsignadoA(usuarioEquipo.getId());
        // modifico y recupero de la base de datos
        tareaService.modificaTarea(tarea.getId(), tareaModificada);
        Tarea tareaBD = tareaService.findById(tarea.getId());

        assertThat(tareaModificada.getAsignadoA()).isEqualTo(usuarioEquipo.getId());
        assertThat(tareaModificada.getTitulo()).isEqualTo("Nueva tarea de equipo Modificada");
        assertThat(tareaBD.getAsignadoA().getId()).isEqualTo(usuarioEquipo.getId());
        assertThat(tareaBD.getTitulo()).isEqualTo("Nueva tarea de equipo Modificada");

    }

    @Test
    @Transactional
    public void duracionServiceTarea() throws ParseException {
        
        // GIVEN
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Usuario usuario = usuarioService.findById(1L);
        Tarea tarea = new Tarea(usuario, "Terminada");
        tarea.setFinalizada(true);
        tarea.setFechaInicio(sdf.parse("2020-12-20"));

        // WHEN
        Tarea duracion = tareaService.finalizarTarea(usuario.getId(), true);

        // THEN
        assertNotEquals(duracion.getDuracion(), 0);
    }


}
