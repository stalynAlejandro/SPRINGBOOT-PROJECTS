package madstodolist.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipos")
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    @ManyToMany
    @JoinTable(name = "equipo_usuario",
            joinColumns = { @JoinColumn(name = "fk_equipo") },
            inverseJoinColumns = { @JoinColumn(name = "fk_usuario")})
    Set<Usuario> usuarios = new HashSet<>();

    @NotNull
    @ManyToOne
    @JoinColumn( name = "administrador_id")
    private Usuario administrador;


    private Equipo(){}

    public Equipo(String nombre, Usuario admin){

        this.nombre = nombre;
        this.administrador = admin;
    }

    public String getNombre(){
        return this.nombre;
    }

    public void setNombre(String nuevonombre){
        this.nombre = nuevonombre;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;

    }

    public Set<Usuario> getUsuarios(){ return usuarios; }

    public void setUsuarios(Set<Usuario> usuarios){ this.usuarios = usuarios; }

    public Usuario getAdmin(){ return this.administrador; }

    public void setAdmin(Usuario admin){ this.administrador = admin; }


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if ( o == null || getClass() != o.getClass()) return false;
        Equipo equipo = (Equipo) o;
        if (this.id != null && equipo.id != null) {
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, equipo.id);
        }
        // sino comparamos por campos obligatorios
        return Objects.equals(nombre, equipo.nombre);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(nombre);
    }

}
