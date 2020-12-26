package madstodolist.service;

import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EquipoService {

    private EquipoRepository equipoRepository;
    private UsuarioRepository usuarioRepository;
    private TareaRepository tareaRepository;

    @Autowired
    public EquipoService (EquipoRepository equipoRepository, UsuarioRepository usuarioRepository, TareaRepository tareaRepository){
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;

    }


    public List<Equipo> findAllOrderedByName(){
        List<Equipo> listaEquipos = equipoRepository.findAll();
        Collections.sort(listaEquipos, new Comparator<Equipo>(){
            public int compare(Equipo e1, Equipo e2){
                return e1.getNombre().compareTo(e2.getNombre());
            }
        });
        return listaEquipos;
    }


    @Transactional(readOnly = true)
    public Equipo findById(Long equipoId) {
        return equipoRepository.findById(equipoId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Usuario> usuariosEquipo(long idEquipo) {
        Equipo equip = equipoRepository.findById(idEquipo).orElse(null);
        if( equip == null){
            throw new EquipoServiceException("No existe equipo con id " + idEquipo);
        }

        List<Usuario> usuariosequipo = new ArrayList(equip.getUsuarios());
        return usuariosequipo;
    }

    @Transactional(readOnly = true)
    public List<Equipo> equiposUsuario(Long idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if( usuario == null){
            throw new EquipoServiceException("Usuario " + idUsuario + " no existe al listar sus equipos");
        }

        List<Equipo> equiposusuario = new ArrayList<>(usuario.getEquipos());
        return equiposusuario;
    }

    @Transactional
    public Equipo crearEquipo(String nombre, Usuario admin){
        Equipo equipo = new Equipo(nombre, admin);
        equipoRepository.save(equipo);
        return equipo;
    }

    @Transactional
    public Equipo aniadirUsuarioAEquipo(Long idUsuario, Long idEquipo){
        Equipo equip = equipoRepository.findById(idEquipo).orElse(null);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if( equip == null){
            throw new EquipoServiceException("No existe equipo con id " + idEquipo);
        }

        if( usuario == null){
            throw new EquipoServiceException("Usuario " + idUsuario + " no existe al añadirlo a un equipo");
        }

        usuario.getEquipos().add(equip);
        equip.getUsuarios().add(usuario);

        return equip;
    }

    @Transactional
    public Equipo borrarUsuarioDeEquipo(Long idUsuario, Long idEquipo){
        Equipo equip = equipoRepository.findById(idEquipo).orElse(null);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if( equip == null){
            throw new EquipoServiceException("No existe equipo con id " + idEquipo);
        }

        if( usuario == null){
            throw new EquipoServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }


        if(equip.getAdmin() != null && usuario.getId() ==  equip.getAdmin().getId()){
            equip.setAdmin(null);
        }

        usuario.getEquipos().remove(equip);
        equip.getUsuarios().remove(usuario);

        return equip;
    }


    @Transactional
    public void borrarEquipo(Long idEquipo){
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) {
            throw new EquipoServiceException("No existe equipo con id " + idEquipo);
        }

        Set<Tarea> tareasEquipo = new HashSet<>(equipo.getAdmin().getTareas());

        for(Tarea tarea: tareasEquipo){
            if(tarea.getEquipo() != null && tarea.getEquipo().getId() == idEquipo)
            {
                tareaRepository.delete(tarea);
            }
        }
        
        Set<Usuario> usuariosEquipo = new HashSet<>(equipo.getUsuarios());
        for(Usuario usu : usuariosEquipo){
            borrarUsuarioDeEquipo(usu.getId(), equipo.getId());
        }
        
        equipoRepository.delete(equipo);
    }

    @Transactional
    public void cambiarNombreEquipo(Long idEquipo, String nuevonombre){
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) {
            throw new EquipoServiceException("No existe equipo con id " + idEquipo);
        }
        equipo.setNombre(nuevonombre);
        equipoRepository.save(equipo);
    }

    @Transactional
    public Usuario getAdministradorEquipo(Long idEquipo){
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        Usuario admin = equipo.getAdmin();

        return admin;
    }
}
