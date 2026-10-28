package com.ticliahuanca.denunciasapp.model;

public class Denuncia {

    private Integer id;
    private String dni;
    private String fecha;
    private String tipo;
    private String direccion;
    private String descripcion;
    private Integer estado;

    // Constructor vacío
    public Denuncia() {
    }

    // Constructor para registrar (POST: solo los 4 campos del formulario)
    public Denuncia(String dni, String tipo, String direccion, String descripcion) {
        this.dni = dni;
        this.tipo = tipo;
        this.direccion = direccion;
        this.descripcion = descripcion;
    }

    // Constructor completo
    public Denuncia(Integer id, String dni, String fecha, String tipo, String direccion, String descripcion, Integer estado) {
        this.id = id;
        this.dni = dni;
        this.fecha = fecha;
        this.tipo = tipo;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}
