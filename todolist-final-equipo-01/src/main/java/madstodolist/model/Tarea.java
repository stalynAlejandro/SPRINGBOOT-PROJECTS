package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Date;

@Entity
@Table(name = "tareas")
public class Tarea implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String titulo;
    private String descripcion;
    @NotNull
    private Boolean finalizada;
    @NotNull
    private Integer duracion;
    @Column(name="fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name="fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    //Si es una tarea de equipo este valor es diferente de null y representa al equipo al que pertenece
    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;


    //Si es una tarea de equipo este valor es diferente de null y representa a quien se le ha asignado
    @ManyToOne
    @JoinColumn(name = "asignado_id")
    private Usuario asignadoA;

    // Constructor vacío necesario para JPA/Hibernate.
    // Lo hacemos privado para que no se pueda usar desde el código de la aplicación. Para crear un
    // usuario en la aplicación habrá que llamar al constructor público. Hibernate sí que lo puede usar, a pesar
    // de ser privado.
    private Tarea() {}

    // Al crear una tarea la asociamos automáticamente a un
    // usuario. Actualizamos por tanto la lista de tareas del
    // usuario.
    public Tarea(Usuario usuario, String titulo) {
        this.usuario = usuario;
        this.titulo = titulo;
        finalizada = false;
        duracion = 0;
        usuario.getTareas().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getDuracion(){
        return this.duracion;
    }

    public void setDuracion(Integer duracion){
        this.duracion = duracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Equipo getEquipo(){ 
        return equipo;
    }

    public void setEquipo(Equipo equipo){ 
        this.equipo = equipo; 
    }

    public Usuario getAsignadoA(){ 
        return asignadoA;
    }

    public void setAsignadoA(Usuario asignadoA) { 
        this.asignadoA = asignadoA; 
    }

    public String getDescripcion(){
        return this.descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public Date getFechaInicio(){
        return this.fechaInicio;
    }   

    public void setFechaInicio(Date fechaInicio){
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin(){
        return this.fechaFin;
    }

    public void setFechaFin(Date fechaFin){
        this.fechaFin = fechaFin;
    }

    public void setFinalizada(Boolean finalizada) { 
        this.finalizada = finalizada; 
    }

    public Boolean getFinalizada() { 
        return this.finalizada; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        if (id != null && tarea.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, tarea.id);
        // sino comparamos por campos obligatorios
        return titulo.equals(tarea.titulo) &&
                usuario.equals(tarea.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, usuario);
    }
}
