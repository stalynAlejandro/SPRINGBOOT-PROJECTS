package madstodolist.service;

import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.TareaRepository;
import madstodolist.controller.TareaData;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TareaService {

    Logger logger = LoggerFactory.getLogger(TareaService.class);

    @Autowired
    private UsuarioService usuarioService;

    private UsuarioRepository usuarioRepository;
    private TareaRepository tareaRepository;

    @Autowired
    public TareaService(UsuarioRepository usuarioRepository, TareaRepository tareaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
    }

    @Transactional
    public Tarea nuevaTareaUsuario(Long idUsuario,Tarea tarea) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tarea.getTitulo());
        }

        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional(readOnly = true)
    public List<Tarea> allTareasUsuarioIndividuales(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }
        List<Tarea> tareas = new ArrayList(usuario.getTareas());
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return tareas;
    }

    @Transactional(readOnly = true)
    public Map<String, List<Tarea>> allTareasUsuarioEnEquipos(Long idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if(usuario == null){
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas");
        }
        Map<String, List<Tarea>> dicc = new HashMap<String, List<Tarea>>();

        // List<Tarea> tareas = tareaRepository.findByAsignadoId(idUsuario); //Una manera más eficiente.
        List<Tarea> tareas = (List<Tarea>) tareaRepository.findAll();
        tareas.removeIf(t -> t.getAsignadoA() == null || t.getAsignadoA().getId() != idUsuario);

        for(Equipo equipo: usuario.getEquipos()){
            List<Tarea> ts = (List<Tarea>) tareas.stream().filter(t -> t.getEquipo().getId() == equipo.getId()).collect(Collectors.toList());
            dicc.put(equipo.getNombre(), ts );
        }
    
        return dicc;
    }

    @Transactional(readOnly = true)
    public Map<String, List<Tarea>> allTareasPendientesUsuarioEnEquipos(Long idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if(usuario == null){
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas");
        }
        Map<String, List<Tarea>> dicc = new HashMap<String, List<Tarea>>();

        // List<Tarea> tareas = tareaRepository.findByAsignadoId(idUsuario); //Una manera más eficiente.
        List<Tarea> tareas = (List<Tarea>) tareaRepository.findAll();
        tareas.removeIf(t -> t.getAsignadoA() == null || t.getAsignadoA().getId() != idUsuario || t.getFinalizada());

        for(Equipo equipo: usuario.getEquipos()){
            List<Tarea> ts = (List<Tarea>) tareas.stream().filter(t -> t.getEquipo().getId() == equipo.getId()).collect(Collectors.toList());
            dicc.put(equipo.getNombre(), ts );
        }

        return dicc;
    }

    @Transactional(readOnly = true)
    public Map<String, List<Tarea>> allTareasTerminadasUsuarioEnEquipos(Long idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if(usuario == null){
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas");
        }
        Map<String, List<Tarea>> dicc = new HashMap<String, List<Tarea>>();

        // List<Tarea> tareas = tareaRepository.findByAsignadoId(idUsuario); //Una manera más eficiente.
        List<Tarea> tareas = (List<Tarea>) tareaRepository.findAll();
        tareas.removeIf(t -> t.getAsignadoA() == null || t.getAsignadoA().getId() != idUsuario || !t.getFinalizada());

        for(Equipo equipo: usuario.getEquipos()){
            List<Tarea> ts = (List<Tarea>) tareas.stream().filter(t -> t.getEquipo().getId() == equipo.getId()).collect(Collectors.toList());
            dicc.put(equipo.getNombre(), ts );
        }

        return dicc;
    }

    @Transactional(readOnly = true)
    public List<Tarea> getTareasdeEquipoPendientes(Long idEquipo){
        List<Tarea> tareas = (List<Tarea>) tareaRepository.findAll();
        tareas.removeIf(t ->  t.getEquipo() == null || t.getEquipo().getId() != idEquipo || t.getFinalizada()) ;
        return tareas;
    }

    @Transactional(readOnly = true)
    public List<Tarea> getTareasdeEquipoTerminadas(Long idEquipo){
        List<Tarea> tareas = (List<Tarea>) tareaRepository.findAll();
        tareas.removeIf(t ->  t.getEquipo() == null || t.getEquipo().getId() != idEquipo || !t.getFinalizada()) ;
        return tareas;
    }

    @Transactional(readOnly = true)
    public Tarea findById(Long tareaId) {
        return tareaRepository.findById(tareaId).orElse(null);
    }

    @Transactional
    public Tarea modificaTarea(Long idTarea, TareaData nuevaTarea) {
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        Usuario asignadoA;
        //si es tarea individual
        if (nuevaTarea.getAsignadoA() == null){
            asignadoA = null;
        }else{
            //si es tarea de equipo
            asignadoA = usuarioService.findById(nuevaTarea.getAsignadoA());

        }

        tarea.setTitulo(nuevaTarea.getTitulo());
        tarea.setDescripcion(nuevaTarea.getDescripcion());
        tarea.setFechaInicio(nuevaTarea.getFechaInicio());
        tarea.setFechaFin(nuevaTarea.getFechaFin());
        tarea.setAsignadoA(asignadoA);
        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public void borraTarea(Long idTarea) {
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tareaRepository.delete(tarea);
    }

    @Transactional
    public Tarea finalizarTarea(Long idTarea, Boolean finalizada) {
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }

        if(!finalizada) tarea.setDuracion(0);
        else{
            Date fechaFin = new Date();
            long diffInMillies = Math.abs(fechaFin.getTime() - tarea.getFechaInicio().getTime());
            Integer diffDays = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            tarea.setDuracion(diffDays);
        }

        tarea.setFinalizada(finalizada);

        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public List<Tarea> tareasTerminadas(Long idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new UsuarioServiceException("El usuario con id: " + idUsuario + " no existe");
        }
        
        List<Tarea> terminadas = new ArrayList<>();

        List<Tarea> tareas = new ArrayList<>(usuario.getTareas());

        for(Tarea tarea: tareas){

            if (tarea.getFinalizada() && tarea.getEquipo() == null){

                terminadas.add(tarea);
            }
        }

        return terminadas;

    }

    @Transactional
    public List<Tarea> tareasPendientes(Long idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        List<Tarea> pendientes = new ArrayList<>();

        if (usuario == null) {
            throw new UsuarioServiceException("El usuario con id: " + idUsuario + " no existe");
        }

        List<Tarea> tareas = new ArrayList<>(usuario.getTareas());
        for(Tarea tarea: tareas){

            if (!tarea.getFinalizada() && tarea.getEquipo() == null){

                pendientes.add(tarea);
            }
        }

        return pendientes;

    }

}
