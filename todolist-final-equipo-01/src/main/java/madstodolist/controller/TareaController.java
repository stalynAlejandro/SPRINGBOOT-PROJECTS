package madstodolist.controller;

import madstodolist.authentication.ManagerUserSesion;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TareaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    ManagerUserSesion managerUserSesion;


    @GetMapping("/usuarios/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable(value="id") Long idUsuario,
                                 @ModelAttribute TareaData tareaData, Model model,
                                 HttpSession session) {

        managerUserSesion.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuario);
        return "formNuevaTarea";
    }

    @PostMapping("/usuarios/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable(value="id") Long idUsuario, @ModelAttribute TareaData tareaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        managerUserSesion.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        Tarea tarea = new Tarea(usuario, tareaData.getTitulo());
        tarea.setDescripcion(tareaData.getDescripcion());
        tarea.setFechaInicio(tareaData.getFechaInicio());
        tarea.setFechaFin(tareaData.getFechaFin());
        
        tareaService.nuevaTareaUsuario(idUsuario, tarea);

        flash.addFlashAttribute("mensaje", "Tarea creada correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
    }

    @GetMapping("/usuarios/{id}/tareas")
    public String listadoTareas(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session) {

        managerUserSesion.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }

        List<Tarea> tareasPendientes = tareaService.tareasPendientes(idUsuario);
        List<Tarea> tareasTerminadas = tareaService.tareasTerminadas(idUsuario);


        Map<String, List<Tarea>> tareasEquipoPendientes = new HashMap<String, List<Tarea>>();
        Map<String, List<Tarea>> tareasEquipoTerminadas = new HashMap<String, List<Tarea>>();



        if(usuario.getEquipos().size()>0) {
            tareasEquipoPendientes = tareaService.allTareasPendientesUsuarioEnEquipos(idUsuario);
            tareasEquipoTerminadas = tareaService.allTareasTerminadasUsuarioEnEquipos(idUsuario);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("tareasPendientes", tareasPendientes);
        model.addAttribute("tareasTerminadas", tareasTerminadas);
        model.addAttribute("tareasEnEquiposPendientes", tareasEquipoPendientes);
        model.addAttribute("tareasEnEquiposTerminadas", tareasEquipoTerminadas);

        return "listaTareas";
    }

    @GetMapping("/tareas/{id}/editar")
    public String formEditaTarea(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                 Model model, HttpSession session) {

        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        managerUserSesion.comprobarUsuarioLogeado(session, tarea.getUsuario().getId());
        //obtengo el usuario logeado

        Usuario usuario = tarea.getUsuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("tarea", tarea);
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setDescripcion(tarea.getDescripcion());
        tareaData.setFechaInicio(tarea.getFechaInicio());
        tareaData.setFechaFin(tarea.getFechaFin());
        return "formEditarTarea";
    }

    @PostMapping("/tareas/{id}/editar")
    public String grabaTareaModificada(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                       Model model, RedirectAttributes flash, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        managerUserSesion.comprobarUsuarioLogeado(session, tarea.getUsuario().getId());

        tareaService.modificaTarea(idTarea, tareaData);
        flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");
        return "redirect:/usuarios/" + tarea.getUsuario().getId() + "/tareas";
    }

    @DeleteMapping("/tareas/{id}")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borrarTarea(@PathVariable(value="id") Long idTarea, RedirectAttributes flash, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        managerUserSesion.comprobarUsuarioLogeado(session, tarea.getUsuario().getId());

        tareaService.borraTarea(idTarea);
        return "";
    }

    @GetMapping("/tareas/{id}/finalizada/{finalizada}")
    public String terminarTareas(@PathVariable(value="id") Long idTarea, Model model, HttpSession session,
                                 @PathVariable(value="finalizada") Boolean finalizada) {

        Tarea tarea = tareaService.findById(idTarea);
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }

        tareaService.finalizarTarea(idTarea, finalizada);

        return "redirect:/usuarios/" + idUsuario + "/tareas";
    }


}

