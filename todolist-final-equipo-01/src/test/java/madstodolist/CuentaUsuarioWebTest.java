package madstodolist;


import madstodolist.authentication.ManagerUserSesion;
import madstodolist.model.Usuario;
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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CuentaUsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private HttpSession httpSession;


    @MockBean
    private ManagerUserSesion managerUserSesion;

    @Test
    public void paginaCuentaUsuario() throws Exception {

        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setPassword("123");
        usuario.setId(null);


        when(usuarioService.findById(null)).thenReturn(usuario);
        //when(usuarioService.login("domingo@ua.es", "123")).thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        this.mockMvc.perform(get("/usuario/cuenta"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(containsString("Mis Datos"),
                        containsString("Domingo Gallardo"))));
        //);
    }

    @Test
    public void paginaEditarCuentaUsuario() throws Exception {

        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setPassword("123");
        usuario.setId(null);


        when(usuarioService.findById(null)).thenReturn(usuario);
        //when(usuarioService.login("domingo@ua.es", "123")).thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        this.mockMvc.perform(get("/usuario/cuenta/editar"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(containsString("Editar Mis Datos"),
                        containsString("Domingo Gallardo"))));
        //);
    }

    @Test
    public void EditarCuentaUsuario() throws Exception {

        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setPassword("123");
        usuario.setId(null);


        when(usuarioService.findById(null)).thenReturn(usuario);
        //when(usuarioService.login("domingo@ua.es", "123")).thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        this.mockMvc.perform(post("/usuario/cuenta/editar"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/cuenta"));
        //);
    }

}