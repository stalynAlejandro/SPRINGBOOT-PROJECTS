package madstodolist;

import madstodolist.authentication.ManagerUserSesion;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TareaEquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private EquipoService equipoService;

    @MockBean
    private TareaService tareaService;

    @MockBean
    private ManagerUserSesion managerUserSesion;

    @Test
    public void comprobarBotonNuevaTareaEquipo() throws Exception{
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setId(1L);

        Equipo equipo = new Equipo("Equipo de Test", usuario);
        equipo.setId(1L);

        when(usuarioService.findById(null)).thenReturn(usuario);
        when(equipoService.findById(1L)).thenReturn(equipo);
        when(equipoService.getAdministradorEquipo(1L)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos/1"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("href=\"/equipos/1/tarea/nueva\""),
                        containsString("Lista de miembros del equipo 1, Equipo de Test"))));
    }

    @Test
    public void comprobarBotonExpulsion() throws Exception{
        Usuario admin = new Usuario("domingo@ua.es");
        admin.setNombre("Domingo Gallardo");
        admin.setId(1L);
        Usuario usuarioEquipo = new Usuario("usuario@ua.es");
        usuarioEquipo.setNombre("Usuario Test");
        usuarioEquipo.setId(2L);

        Equipo equipo = new Equipo("Equipo de Test", admin);
        equipo.setId(1L);
        Set<Usuario> setUsuarios = new HashSet<>();
        setUsuarios.add(admin);
        setUsuarios.add(usuarioEquipo);
        equipo.setUsuarios(setUsuarios);

        when(usuarioService.findById(null)).thenReturn(admin);
        when(equipoService.findById(1L)).thenReturn(equipo);
        when(equipoService.getAdministradorEquipo(1L)).thenReturn(admin);

        this.mockMvc.perform(get("/equipos/1"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("action=\"/equipos/1/expulsion/usuario/2\""),
                        containsString("Lista de miembros del equipo 1, Equipo de Test"),
                        containsString("Usuario Test"))));
    }

    @Test
    public void formularioCrearTareaEquipo() throws Exception{
        Usuario admin = new Usuario("domingo@ua.es");
        admin.setNombre("Domingo Gallardo");
        admin.setId(1L);
        Usuario usuarioEquipo = new Usuario("usuario@ua.es");
        usuarioEquipo.setNombre("Usuario Test");
        usuarioEquipo.setId(2L);

        Equipo equipo = new Equipo("Equipo de Test", admin);
        equipo.setId(1L);
        Set<Usuario> setUsuarios = new HashSet<>();
        setUsuarios.add(admin);
        setUsuarios.add(usuarioEquipo);
        equipo.setUsuarios(setUsuarios);

        when(usuarioService.findById(null)).thenReturn(admin);
        when(equipoService.findById(1L)).thenReturn(equipo);

        this.mockMvc.perform(get("/equipos/1/tarea/nueva"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("Nueva tarea para el equipo 1, Equipo de Test"),
                        containsString("Título de la tarea"),
                        containsString("Descripción"),
                        containsString("Fecha de Inicio"),
                        containsString("Fecha Fin"),
                        containsString("Asignar a:"),
                        containsString("Domingo Gallardo, domingo@ua.es"),
                        containsString("Usuario Test, usuario@ua.es"),
                        containsString("action=\"/equipos/1/tarea/nueva\""))));

    }

    @Test
    public void borrarTareaEquipo() throws Exception{
        Usuario admin = new Usuario("domingo@ua.es");
        admin.setNombre("Domingo Gallardo");
        admin.setId(1L);

        Equipo equipo = new Equipo("Equipo de Test", admin);
        Tarea tarea = new Tarea(admin, "Tarea de Equipo Test");
        tarea.setId(3L);

        when(usuarioService.findById(null)).thenReturn(admin);
        when(equipoService.findById(1L)).thenReturn(equipo);
        when(tareaService.findById(3L)).thenReturn(tarea);

        this.mockMvc.perform(delete("/equipos/1/tarea/3"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }





}
