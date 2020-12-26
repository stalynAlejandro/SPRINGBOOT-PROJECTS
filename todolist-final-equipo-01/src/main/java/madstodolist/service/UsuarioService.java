package madstodolist.service;

import madstodolist.controller.RegistroData;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    private UsuarioRepository usuarioRepository;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private TareaService tareaService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    // Se añade un usuario en la aplicación.
    // El email y password del usuario deben ser distinto de null
    // El email no debe estar registrado en la base de datos
    @Transactional
    public Usuario registrar(Usuario usuario) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else if (usuario.getEmail() == null)
            throw new UsuarioServiceException("El usuario no tiene email");
        else if (usuario.getPassword() == null)
            throw new UsuarioServiceException("El usuario no tiene password");
        else return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario modificar(Long idUsuario, Usuario usuarioModificado) {
        Usuario usuario = findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioServiceException("El usuario con id: " + idUsuario + " no existe");
        }
        usuario.setNombre(usuarioModificado.getNombre());
        usuario.setEmail(usuarioModificado.getEmail());
        usuario.setPassword(usuarioModificado.getPassword());
        usuario.setFechaNacimiento(usuarioModificado.getFechaNacimiento());

        usuarioRepository.save(usuario);
        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getAllUsuarios(){
        List<Usuario> lista = new ArrayList<>();
        Iterable<Usuario> it = usuarioRepository.findAll();
        for ( Usuario usuario : it ) {
            lista.add(usuario);
        }

        return lista;

    }

    @Transactional(readOnly = true)
    public Boolean existeAdmin(){
        Boolean existeAdmin = false;
        Iterable<Usuario> it = usuarioRepository.findAll();
        for ( Usuario usuario : it ) {
            if(usuario.getesAdmin()){
                existeAdmin = true;
            }
        }
        return existeAdmin;
    }

    @Transactional
    public String bloquearDesbloquearUsuario(Long idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if(usuario == null){
            throw new UsuarioServiceException("No existe el usuario con id " + idUsuario);
        }
        String mensaje="";
        if(usuario.getBloqueado()) {
            usuario.setBloqueado(false);
            mensaje = "Usuario desbloqueado!";
        }else {
            usuario.setBloqueado(true);
            mensaje = "Usuario bloqueado!";
        }
        usuarioRepository.save(usuario);

        return mensaje;
    }

    @Transactional
    public void borrarCuenta(Long idUsuario) {
        Usuario usuario = findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioServiceException("El usuario con id: " + idUsuario + " no existe");
        }
        List<Equipo> equipos = new ArrayList<>(usuario.getEquipos());
        List<Tarea> tareas = new ArrayList<>(usuario.getTareas());

        // Borramos referencias usuario equipo
        for(Equipo equipo: equipos){
            if(equipoService.getAdministradorEquipo(equipo.getId()).equals(usuario)){
                equipoService.borrarEquipo(equipo.getId());
            }
            else{
                equipoService.borrarUsuarioDeEquipo(usuario.getId(), equipo.getId());
            }

        }

        // Borramos las referencias de las tareas del usuario
        for(Tarea tarea: tareas){
            tareaService.borraTarea(tarea.getId());
        }
        usuarioRepository.delete(usuario);
    }




    @Transactional(readOnly = true)
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Transactional(readOnly = true)
    public Usuario findById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId).orElse(null);
    }
}
