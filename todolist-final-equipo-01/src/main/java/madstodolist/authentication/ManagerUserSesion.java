package madstodolist.authentication;

import madstodolist.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import madstodolist.service.UsuarioService;
import javax.servlet.http.HttpSession;

@Component
public class ManagerUserSesion {


    // Añadimos el id de usuario en la sesión HTTP para hacer
    // una autorización sencilla. En los métodos de controllers
    // comprobamos si el id del usuario logeado coincide con el obtenido
    // desde la URL
    public void logearUsuario(HttpSession session, Long idUsuario) {
        session.setAttribute("idUsuarioLogeado", idUsuario);
    }

    public void comprobarUsuarioLogeado(HttpSession session, Long idUsuario) {
        Long idUsuarioLogeado = (Long) session.getAttribute("idUsuarioLogeado");
        if (idUsuario == null || !idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    public void comprobarUsuarioAdminPagina(Long idUsuarioLogeado, UsuarioService usuarioService){
        if ( idUsuarioLogeado == null || !usuarioService.findById(idUsuarioLogeado).getesAdmin() ){
            throw new UsuarioNoLogeadoException();
        }
    }

    public void comprobarUsuarioAdmin(Long idUsuarioLogeado, Long idEquipo, UsuarioService usuarioService, EquipoService equipoService){
        if ( (idUsuarioLogeado == null || !usuarioService.findById(idUsuarioLogeado).getesAdmin()) && ( idUsuarioLogeado == null || equipoService == null) ){
            throw new UsuarioNoLogeadoException();
        }

        //si se trata de la comprobacion del administrador del equipo
        if( equipoService.getAdministradorEquipo(idEquipo).getId() != idUsuarioLogeado && !usuarioService.findById(idUsuarioLogeado).getesAdmin() ){
            throw new UsuarioNoLogeadoException();
        }
    }

    public void comprobarUsuarioBloqueado(HttpSession session, Long usuarioID, UsuarioService usuarioService){
        if ( usuarioService.findById(usuarioID).getBloqueado() ){
            throw new UsuarioNoLogeadoException();
        }
    }
}
