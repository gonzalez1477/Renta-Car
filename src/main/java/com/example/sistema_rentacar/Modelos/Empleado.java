package com.example.sistema_rentacar.Modelos;

import java.sql.Date;
import java.sql.Timestamp;

public class Empleado {
    private int idEmpleado;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String usuario;
    private String contrasena;
    private String cargo;
    private Date fechaContratacion;
    private boolean activo;
    private Timestamp createdAt;

    // Constructor vacío
    public Empleado() {}

    // Constructor completo
    public Empleado(int idEmpleado, String nombre, String apellido, String email,
                    String telefono, String usuario, String contrasena, String cargo,
                    Date fechaContratacion, boolean activo) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.cargo = cargo;
        this.fechaContratacion = fechaContratacion;
        this.activo = activo;
    }

    // Getters y Setters
    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Date getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(Date fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " - " + cargo;
    }
}