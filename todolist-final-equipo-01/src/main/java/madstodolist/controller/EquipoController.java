package madstodolist.controller;


import madstodolist.authentication.ManagerUserSesion;
import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.TareaServiceException;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;

    @Autowired
    ManagerUserSesion managerUserSesion;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @GetMapping("/equipos")
    public String listadoEquipos(Model model, HttpSession session){

        //obtengo el id del usuario logeado
        //comprobar si ese usuario es esta logueado, si no lo es lanza una excepcion
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        //obtengo la lista de los equipos
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        Usuario usuario = usuarioService.findById(idUsuarioLogeado);
        model.addAttribute("usuario", usuario);
        model.addAttribute("equipos", equipos);

        return "equipos";
    }

    @GetMapping("/equipos/{id}")
    public String descripcionEquipo(@PathVariable(value="id")Long idEquipo,
                                    Model model, HttpSession session){
        //obtengo el usuario logeado
        //comprobar si ese usuario el usuario pertenece al equipo o no
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);
        Equipo equipo = equipoService.findById(idEquipo);
        if(equipo == null){
            throw new EquipoNotFoundException();
        }
        Usuario admin = equipoService.getAdministradorEquipo(idEquipo);
        List<Tarea> tareasEquipoPendientes = tareaService.getTareasdeEquipoPendientes(idEquipo);
        List<Tarea> tareasEquipoTerminadas = tareaService.getTareasdeEquipoTerminadas(idEquipo);
        model.addAttribute("administrador", admin);
        model.addAttribute("usuario", usuarioLogueado);
        model.addAttribute("equipo", equipo);
        model.addAttribute("tareasEquipoPendientes", tareasEquipoPendientes);
        model.addAttribute("tareasEquipoTerminadas", tareasEquipoTerminadas);
        return "descripcionEquipo";
    }

    @GetMapping("/equipos/nuevo")
    public String nuevoEquipo(@ModelAttribute EquipoData equipoData, Model model, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuario = usuarioService.findById(idUsuarioLogeado);
        model.addAttribute("usuario", usuario);

        return "formNuevoEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String formNuevoEquipo(@ModelAttribute EquipoData equipoData, Model model,
                                  RedirectAttributes flash, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuario = usuarioService.findById(idUsuarioLogeado);
        Equipo equipo = equipoService.crearEquipo(equipoData.getNombre(), usuario);
        equipoService.aniadirUsuarioAEquipo(usuario.getId(), equipo.getId());
        model.addAttribute("usuario", usuario);

        flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{id}/gestionpertenencia")
    public String incorporarUsuarioAEquipo(@PathVariable(value="id")Long idEquipo,Model model,
                                           RedirectAttributes flash, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioLogeado(session, idUsuarioLogeado);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);
        Equipo equipo = equipoService.findById(idEquipo);
        if(equipo == null){
            throw new EquipoNotFoundException();
        }

        List<Usuario> listaUsuariosEquipo = equipoService.usuariosEquipo(equipo.getId());

        //si el usuario está ya en el equipo, hay que eliminarlo de él
        if(listaUsuariosEquipo.contains(usuarioLogueado)){
            equipoService.borrarUsuarioDeEquipo(usuarioLogueado.getId(), equipo.getId());
            flash.addFlashAttribute("mensaje", "Has sido eliminado del equipo" + equipo.getId() + ", " + equipo.getNombre());
        }else{ //si no esta ya en el equipo, hay que añadirlo
            equipoService.aniadirUsuarioAEquipo(usuarioLogueado.getId(), equipo.getId());
            flash.addFlashAttribute("mensaje", "Has sido añadido al equipo " + equipo.getId() + ", " + equipo.getNombre());
        }

        return "redirect:/equipos";
    }

    @GetMapping("/equipos/{id}/modificacion")
    public String formModificarNombreEquipo(@PathVariable(value="id")Long idEquipo,@ModelAttribute EquipoData equipoData,
                                        Model model, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);
        Equipo equipo = equipoService.findById(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        model.addAttribute("equipo", equipo);
        model.addAttribute("usuario",usuarioLogueado);
        equipoData.setNombre(equipo.getNombre());

        return "formModificarNombreEquipo";
    }

    @PostMapping("/equipos/{id}/modificacion")
    public String modificarNombreEquipo(@PathVariable(value="id")Long idEquipo,@ModelAttribute EquipoData equipoData,
                                        Model model, RedirectAttributes flash, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);


        Equipo equipo = equipoService.findById(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();

        }

        equipoService.cambiarNombreEquipo(idEquipo, equipoData.getNombre());
        flash.addFlashAttribute("mensaje", "Nombre de equipo modificado correctamente");

        return "redirect:/equipos";

    }

    @DeleteMapping("/equipos/{id}")
    @ResponseBody
    public String borrarEquipo(@PathVariable(value="id") Long idEquipo, RedirectAttributes flash,
                               HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Equipo equipo = equipoService.findById(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        equipoService.borrarEquipo(equipo.getId());
        flash.addFlashAttribute("mensaje", "Equipo borrado correctamente");

        return "";
    }

    @PostMapping("/equipos/{idEquipo}/expulsion/usuario/{idUsuarioExpulsion}")
    public String expulsarMiembro(@PathVariable(value="idEquipo")Long idEquipo,
                                  @PathVariable(value="idUsuarioExpulsion")Long idUsuarioExpulsion,
                                  Model model, RedirectAttributes flash, HttpSession session){

        //comprobar que el usuario logueado es el administrador del equipo
        //comprobar que el equipo existe
        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Equipo equipo = equipoService.findById(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        equipoService.borrarUsuarioDeEquipo(idUsuarioExpulsion, idEquipo);
        Usuario usuarioExpulsado = usuarioService.findById(idUsuarioExpulsion);
        flash.addFlashAttribute("mensaje", usuarioExpulsado.getNombre() + " ha sido expulsado del equipo");

        return "redirect:/equipos/" + idEquipo;

    }

    @GetMapping("/equipos/{id}/tarea/nueva")
    public String formCrearTareaEquipo(@PathVariable(value="id")Long idEquipo,@ModelAttribute TareaData tareaData,
                                   Model model, HttpSession session){

        Equipo equipo = equipoService.findById(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);


        model.addAttribute("equipo", equipo);
        model.addAttribute("usuario",usuarioLogueado);

        return "formCrearTareaEquipo";
    }

    @PostMapping("/equipos/{idEquipo}/tarea/nueva")
    public String crearTareaEquipo(@PathVariable(value="idEquipo")Long idEquipo, @ModelAttribute TareaData tareaData,
                                   BindingResult result, Model model, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);
        Equipo equipo = equipoService.findById(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        if (result.hasErrors()) {
            model.addAttribute("equipo", equipo);
            model.addAttribute("usuario",usuarioLogueado);
            return "formCrearTareaEquipo";
        }

        Usuario usuarioAsignado = usuarioService.findById(tareaData.getAsignadoA());

        Tarea tarea = new Tarea(usuarioLogueado, tareaData.getTitulo());
        tarea.setDescripcion(tareaData.getDescripcion());
        tarea.setFechaInicio(tareaData.getFechaInicio());
        tarea.setFechaFin(tareaData.getFechaFin());
        tarea.setEquipo(equipo);
        tarea.setAsignadoA(usuarioAsignado);
        tareaService.nuevaTareaUsuario(usuarioLogueado.getId(), tarea);
        return "redirect:/equipos/" + idEquipo;
    }


    @GetMapping("/equipos/{idEquipo}/tarea/{idTarea}/editar")
    public String formEditarTareaEquipo(@PathVariable(value="idEquipo")Long idEquipo, @PathVariable(value="idTarea") Long idTarea,
                                    @ModelAttribute TareaData tareaData,
                                    Model model, HttpSession session){

        Equipo equipo = equipoService.findById(idEquipo);
        Tarea tarea = tareaService.findById(idTarea);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }
        if (tarea == null){
            throw new TareaNotFoundException();
        }

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);




        model.addAttribute("tarea",tarea);
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuario",usuarioLogueado);

        tareaData.setAsignadoA(tarea.getAsignadoA().getId());
        tareaData.setDescripcion(tarea.getDescripcion());
        tareaData.setFechaFin(tarea.getFechaFin());
        tareaData.setFechaInicio(tarea.getFechaInicio());
        tareaData.setTitulo(tarea.getTitulo());

        return "formEditarTareaEquipo";
    }

    @PostMapping("/equipos/{idEquipo}/tarea/{idTarea}/editar")
    public String editarTareaEquipo(@PathVariable(value="idEquipo")Long idEquipo, @PathVariable(value="idTarea")Long idTarea,
                                    @ModelAttribute TareaData tareaData, RedirectAttributes flash,
                                    BindingResult result, Model model, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Usuario usuarioLogueado = usuarioService.findById(idUsuarioLogeado);
        Equipo equipo = equipoService.findById(idEquipo);
        Tarea tarea = tareaService.findById(idTarea);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }
        if (tarea == null){
            throw new TareaNotFoundException();
        }


        if (result.hasErrors()) {
            model.addAttribute("equipo", equipo);
            model.addAttribute("usuario",usuarioLogueado);
            return "formCrearTareaEquipo";
        }


        tareaService.modificaTarea(idTarea, tareaData);
        flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");

        return "redirect:/equipos/" + idEquipo;

    }

    @DeleteMapping("/equipos/{idEquipo}/tarea/{idTarea}")
    @ResponseBody
    public String borrarTareaEquipo(@PathVariable(value="idEquipo")Long idEquipo, @PathVariable(value="idTarea")Long idTarea,
                                    RedirectAttributes flash, Model model, HttpSession session){

        Long idUsuarioLogeado = (Long)session.getAttribute("idUsuarioLogeado");
        managerUserSesion.comprobarUsuarioAdmin(idUsuarioLogeado, idEquipo, usuarioService, equipoService);

        Equipo equipo = equipoService.findById(idEquipo);
        Tarea tarea = tareaService.findById(idTarea);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        tareaService.borraTarea(tarea.getId());
        flash.addFlashAttribute("mensaje", "Tarea borrada correctamente");

        return "";
    }





}
