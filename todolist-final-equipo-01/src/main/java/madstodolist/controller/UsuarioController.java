package madstodolist.controller;


import madstodolist.authentication.ManagerUserSesion;
import madstodolist.authentication.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    EquipoService equipoService;

    @Autowired
    ManagerUserSesion managerUserSesion;

    @GetMapping("/usuarios")
    public String listadoUsuarios(Model model, HttpSession session){

        //obtengo el usuario logeado
        //comprobar si ese usuario es admin, si no lo es lanza una excepcion
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdminPagina(idUsuarioLogeado, usuarioService);

        //obtengo todos los usuarios
        List<Usuario> listaUsuarios = usuarioService.getAllUsuarios();
        Usuario usuario = usuarioService.findById(idUsuarioLogeado);
        model.addAttribute("listaUsuarios", listaUsuarios);
        model.addAttribute("usuario", usuario);

        return "usuarios";
    }


    @GetMapping("/usuarios/{id}")
    public String descripcionUsuario(@PathVariable(value="id")Long idUsuario, Model model, HttpSession session){

        //obtengo el usuario logeado
        //comprobar si ese usuario es admin, si no lo es lanza una excepcion
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdminPagina(idUsuarioLogeado, usuarioService);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);
        //obtengo usuario para la descripcion
        Usuario usu = usuarioService.findById(idUsuario);
        if (usu == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuarioLogueado);
        model.addAttribute("usuarioDescripcion",usu);

        return "descripcionUsuario";
    }

    @PostMapping("/usuarios/{id}/bloqueo")
    public String bloquearUsuario(@PathVariable(value="id")Long idUsuario, Model model, RedirectAttributes flash, HttpSession session){

        //obtengo el usuario logeado
        //comprobar si ese usuario es admin, si no lo es lanza una excepcion
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdminPagina(idUsuarioLogeado, usuarioService);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);


        String mensaje = usuarioService.bloquearDesbloquearUsuario(idUsuario);
        flash.addFlashAttribute("mensaje", mensaje);
        model.addAttribute("usuario", usuarioLogueado);


        return "redirect:/usuarios";
    }

    @GetMapping("/usuario/cuenta")
    public String cuentaUsuario(Model model, HttpSession session){

        //obtengo el usuario logeado
        //comprobar si ese usuario es admin, si no lo es lanza una excepcion
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);

        if (usuarioLogueado == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuarioLogueado);


        return "cuentaUsuario";
    }

    @GetMapping("/usuario/cuenta/editar")
    public String editarCuentaUsuario(Model model, HttpSession session, @ModelAttribute RegistroData registroData){

        //obtengo el usuario logeado
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);

        if (usuarioLogueado == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuarioLogueado);

        // Para el formulario
        registroData.seteMail(usuarioLogueado.getEmail());
        registroData.setNombre(usuarioLogueado.getNombre());
        registroData.setPassword(usuarioLogueado.getPassword());
        registroData.setFechaNacimiento(usuarioLogueado.getFechaNacimiento());

        return "editarCuentaUsuario";
    }


    @PostMapping("/usuario/cuenta/editar")
    public String guardarUsuarioModificado(HttpSession session, @ModelAttribute RegistroData registroData,
                                       RedirectAttributes flash) {
        //obtengo el usuario logeado
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);

        if (usuarioLogueado == null) {
            throw new UsuarioNotFoundException();
        }
        Usuario usuarioModificado = new Usuario(registroData.geteMail());
        usuarioModificado.setNombre(registroData.getNombre());
        usuarioModificado.setFechaNacimiento(registroData.getFechaNacimiento());
        usuarioModificado.setPassword(registroData.getPassword());

        usuarioService.modificar(idUsuarioLogeado, usuarioModificado);
        flash.addFlashAttribute("mensaje", "Datos modificados correctamente");
        return "redirect:/usuario/cuenta";
    }

    @DeleteMapping("/usuario/cuenta")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borraCuenta(HttpSession session) {
        //obtengo el usuario logeado
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);

        if (usuarioLogueado == null) {
            throw new UsuarioNotFoundException();
        }

        usuarioService.borrarCuenta(idUsuarioLogeado);

        return "";
    }


}
