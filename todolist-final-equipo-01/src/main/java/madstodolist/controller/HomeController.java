package madstodolist.controller;

import madstodolist.authentication.ManagerUserSesion;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSesion managerUserSesion;

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {

        Long idUsuarioLogeado = (Long) session.getAttribute("idUsuarioLogeado");

        if(idUsuarioLogeado != null) {
            Usuario usuario = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("usuario", usuario);
        }

        
        return "about";
    }

}