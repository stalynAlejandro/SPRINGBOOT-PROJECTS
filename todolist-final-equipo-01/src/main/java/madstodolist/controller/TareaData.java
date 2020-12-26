package madstodolist.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class TareaData {
    private String titulo;
    private String descripcion;
    private Boolean finalizada;
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date fechaInicio;
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date fechaFin;
    private Long asignadoA;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
        return finalizada;
    }
    public Long getAsignadoA(){ return asignadoA; }

    public void setAsignadoA(Long asignar){ this.asignadoA = asignar; }
}
