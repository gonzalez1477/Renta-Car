package com.example.sistema_rentacar.Modelos;

import java.sql.Date;
import java.sql.Timestamp;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private String dui;
    private String licencia;
    private Date fechaNacimiento;
    private String usuario;
    private String contrasena;
    private Timestamp fechaRegistro;
    private boolean activo;

    // Constructor vacío
    public Cliente() {}

    // Constructor completo
    public Cliente(int idCliente, String nombre, String apellido, String email, String telefono,
                   String direccion, String dui, String licencia, Date fechaNacimiento,
                   String usuario, String contrasena, boolean activo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.dui = dui;
        this.licencia = licencia;
        this.fechaNacimiento = fechaNacimiento;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.activo = activo;
    }

    // Getters y Setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    //Obtener nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + usuario + ")";
    }
}
