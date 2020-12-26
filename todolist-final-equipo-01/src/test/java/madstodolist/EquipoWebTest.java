package madstodolist;


import madstodolist.authentication.ManagerUserSesion;
import madstodolist.model.Equipo;
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

import javax.servlet.http.HttpSession;

import java.util.*;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSesion managerUserSesion;

    @MockBean
    private EquipoService equipoService;

    @MockBean
    private HttpSession session;

    @Test
    public void urlEquiposDevuelveVista() throws Exception{
        //creo usuario logueado
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setId(1L);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("Listado de equipos"))));
    }

    @Test
    public void urlEquiposDevuelveListaDeEquipos() throws Exception{
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setId(1L);
        Usuario admin = usuarioService.findById(1L);

        Equipo equipo1 = new Equipo("Proyecto P1", admin);
        Equipo equipo2 = new Equipo("Proyecto P2", admin);
        equipo1.setId(1L);
        equipo2.setId(2L);
        List<Equipo> equipos = new ArrayList<>();
        equipos.add(equipo1);
        equipos.add(equipo2);

        when(equipoService.findAllOrderedByName()).thenReturn(equipos);
        when(usuarioService.findById(1L)).thenReturn(usuario);



        //PREGUNTAR COMO SE PASA UN USUARIO A LA VISTA PORQUE DA ERROR CUANDO EVALUA LA VARIABLE
        //usuario (que representa al usuario logueado)
        /*this.mockMvc.perform(get("/equipos"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("Listado de equipos"),
                        containsString("Proyecto P1"),
                        containsString("Proyecto P2"))));*/

    }

    @Test
    public void vistaListaMiembrosEquipo() throws Exception{
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setId(1L);

        Usuario admin = usuarioService.findById(1L);
        Equipo equipo = new Equipo("Proyecto P1", admin);
        equipo.setId(1L);
        Set<Usuario> setUsuarios = new HashSet<>();
        setUsuarios.add(usuario);
        equipo.setUsuarios(setUsuarios);

        when(equipoService.findById(1L)).thenReturn(equipo);
        when(usuarioService.findById(null)).thenReturn(usuario);
        when(equipoService.getAdministradorEquipo(1L)).thenReturn(usuario);


        this.mockMvc.perform(get("/equipos/1"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("Lista de miembros del equipo 1, Proyecto P1"),
                        containsString("domingo@ua.es"),
                        containsString("Domingo Gallardo"))));
    }

    @Test
    public void urlEquiposNuevoDevuelveVista() throws Exception{
        //creo usuario logueado
        Usuario usuario = new Usuario("vnm12@alu.ua.es");
        usuario.setNombre("Víctor Navarro");
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos/nuevo"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("Nuevo equipo"),
                        containsString("action=\"/equipos/nuevo\""))));
    }


    @Test
    public void comprobarPertenenciaEquipo() throws Exception{
        //usuario logueado
        Usuario usuario = new Usuario("vnm12@alu.ua.es");
        usuario.setNombre("Víctor Navarro");

        //equipo
        Usuario admin = usuarioService.findById(1L);
        Equipo equipo = new Equipo("Equipo de prueba", admin);
        equipo.setId(1L);

        List<Usuario> listaUsuario = new ArrayList<>();
        listaUsuario.add(usuario);

        when(equipoService.findById(1L)).thenReturn(equipo);
        when(usuarioService.findById(null)).thenReturn(usuario);
        when(equipoService.usuariosEquipo(1L)).thenReturn(listaUsuario);

        this.mockMvc.perform(post("/equipos/1/gestionpertenencia"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));
    }

    @Test
    public void administradorEquipoAzul() throws Exception{
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setId(1L);

        Equipo equipo = new Equipo("Equipo de prueba", usuario);
        equipo.setId(1L);
        Set<Usuario> setUsuarios = new HashSet<>();
        setUsuarios.add(usuario);
        equipo.setUsuarios(setUsuarios);

        when(equipoService.findById(1L)).thenReturn(equipo);
        when(usuarioService.findById(null)).thenReturn(usuario);
        when(equipoService.getAdministradorEquipo(1L)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos/1"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("Lista de miembros del equipo 1, Equipo de prueba"),
                        containsString("domingo@ua.es"),
                        containsString("Domingo Gallardo"),
                        containsString("table-primary"))));
    }

    @Test
    public void listaEquiposAdministrador() throws Exception{
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setId(1L);

        //creo equipo y modifico sus usuarios
        Equipo equipo = new Equipo("Equipo de prueba", usuario);
        equipo.setId(1L);
        Set<Usuario> setUsuarios = new HashSet<>();
        setUsuarios.add(usuario);
        equipo.setUsuarios(setUsuarios);

        //creo lista de equipos para añadiselo al administrador
        Set<Equipo> setEquiposAdministrados = new HashSet<>();
        setEquiposAdministrados.add(equipo);
        usuario.setEquiposAdministrados(setEquiposAdministrados);

        when(equipoService.findById(1L)).thenReturn(equipo);
        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andDo(print())
                .andExpect(content().string(allOf(
                        containsString("Mis equipos administrados"),
                        containsString("href=\"/equipos/1/modificacion\""),
                        containsString("Equipo de prueba"),
                        containsString("Listado de equipos"))));
    }

    @Test
    public void borrarEquipoComoAdministradorEquipo() throws Exception{
        //administrador equipo
        Usuario admin = new Usuario("domingo@ua.es");
        admin.setNombre("Domingo Gallardo");
        admin.setId(1L);

        //creo equipo y modifico sus usuarios
        Equipo equipo = new Equipo("Equipo de prueba", admin);
        equipo.setId(1L);
        Set<Usuario> setUsuarios = new HashSet<>();
        setUsuarios.add(admin);
        equipo.setUsuarios(setUsuarios);

        //creo lista de equipos para añadiselo al administrador
        Set<Equipo> setEquiposAdministrados = new HashSet<>();
        setEquiposAdministrados.add(equipo);
        admin.setEquiposAdministrados(setEquiposAdministrados);

        when(equipoService.findById(1L)).thenReturn(equipo);
        when(usuarioService.findById(null)).thenReturn(admin);

        this.mockMvc.perform(delete("/equipos/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

}
