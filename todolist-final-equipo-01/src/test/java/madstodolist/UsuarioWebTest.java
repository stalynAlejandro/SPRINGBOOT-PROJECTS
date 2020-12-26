package madstodolist;

import madstodolist.authentication.ManagerUserSesion;
import madstodolist.controller.LoginController;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private HttpSession httpSession;

    @Test
    public void servicioLoginUsuarioOK() throws Exception {

        Usuario anaGarcia = new Usuario("ana.garcia@gmail.com");
        anaGarcia.setBloqueado(false);
        anaGarcia.setId(1L);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678")).thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com")).thenReturn(anaGarcia);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        this.mockMvc.perform(post("/login")
                .param("eMail", "ana.garcia@gmail.com")
                .param("password", "12345678"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {

        when(usuarioService.login("pepito.perez@gmail.com", "12345678")).thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        this.mockMvc.perform(post("/login")
                    .param("eMail","pepito.perez@gmail.com")
                    .param("password","12345678"))
                .andDo(print())
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {

        when(usuarioService.login("ana.garcia@gmail.com", "000")).thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        this.mockMvc.perform(post("/login")
                    .param("eMail","ana.garcia@gmail.com")
                    .param("password","000"))
                .andDo(print())
                .andDo(print())
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void servicioLoginRedirectContraseñaIncorrecta() throws Exception {
        this.mockMvc.perform(get("/login")
                .flashAttr("error", "Contraseña incorrecta"))
                .andDo(print())
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void servicioLoginRedirectUsuarioNotFound() throws Exception {
        this.mockMvc.perform(get("/login")
                .flashAttr("error", "No existe usuario"))
                .andDo(print())
                .andExpect(content().string(containsString("No existe usuario")));
    }


    @Test
    public void listaUsuariosSinLoguear() throws Exception{


        this.mockMvc.perform(get("/usuarios/"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void listaUsuariosLogueadoNoAdmin() throws Exception{

        //creo el usuario logeado
        Usuario usuarioLogueado = new Usuario("vnm12@alu.ua.es");
        usuarioLogueado.setNombre("Víctor");
        usuarioLogueado.setId(1L);
        usuarioLogueado.setEsAdmin(false);

        this.mockMvc.perform(get("/usuarios")
                .flashAttr("usuario", usuarioLogueado))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    /*@Test
    public void listaUsuariosLogueadoAdmin() throws Exception{

        //creo el usuario logeado
        Usuario usuarioLogueado = new Usuario("admin@ua.es");
        usuarioLogueado.setNombre("Administrador");
        usuarioLogueado.setId(1L);
        usuarioLogueado.setEsAdmin(true);

        //creo el usuario y lo añado a la lista de usuarios de la pagina
        Usuario usuarioLista1 = new Usuario("vnm12@alu.ua.es");
        usuarioLista1.setId(2L);
        usuarioLista1.setEsAdmin(false);
        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios.add(usuarioLista1);

        when(httpSession.getAttribute("idUsuarioLogeado")).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(usuarioLogueado);
        when(usuarioService.getAllUsuarios()).thenReturn(listaUsuarios);

        this.mockMvc.perform(get("/usuarios")
                .flashAttr("usuario", usuarioLogueado))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Administrador"),
                        containsString("Tareas"),
                        containsString("Cuenta"),
                        containsString("Cerrar sesión"),
                        containsString("vnm12@alu.ua.es"),
                        containsString("Descripción"),
                        containsString("href=\"/usuarios/2\""))));

    }*/

    @Test
    public void descripcionUsuarioSinLogear() throws Exception{

        this.mockMvc.perform(get("/usuarios/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void descripcionUsuarioLogeado() throws Exception{

        //creo el usuario logeado
        Usuario usuarioLogueado = new Usuario("vnm12@alu.ua.es");
        usuarioLogueado.setNombre("Víctor");
        usuarioLogueado.setId(1L);
        usuarioLogueado.setEsAdmin(false);

        //creo el usuario del que se quiere ver la descripcion
        Usuario usuarioDescripcion = new Usuario("mbmr4@alu.ua.es");
        usuarioDescripcion.setNombre("María Belen");
        usuarioDescripcion.setId(2L);

        when(usuarioService.findById(2L)).thenReturn(usuarioDescripcion);

        this.mockMvc.perform(get("/usuarios/2")
                .flashAttr("usuario", usuarioLogueado))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
