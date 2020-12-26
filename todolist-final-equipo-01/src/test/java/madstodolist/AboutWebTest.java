package madstodolist;


import madstodolist.authentication.ManagerUserSesion;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AboutWebTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UsuarioService usuarioService;


    @MockBean
    private ManagerUserSesion managerUserSesion;

    @MockBean
    private HttpSession httpSession;

    @Test
    public void aboutSinLoguear() throws Exception{

        this.mockMvc.perform(get("/about"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Desarrollado por: Carlos Torralba, Stalyn Alejandro y Víctor Navarro")));

    }

    @Test
    public void aboutLogeado() throws Exception{

        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setNombre("Domingo Gallardo");
        usuario.setId(1L);

        this.mockMvc.perform(get("/about")
                .flashAttr("usuario", usuario))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Domingo Gallardo"),
                        containsString("Tareas"),
                        containsString("Cuenta"),
                        containsString("Cerrar sesión"))));

    }

}
