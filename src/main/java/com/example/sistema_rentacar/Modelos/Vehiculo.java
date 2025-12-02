package com.example.sistema_rentacar.Modelos;

import java.sql.Timestamp;

public class Vehiculo {
    private int idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private String color;
    private int numeroPassajeros;
    private String transmision;
    private String tipoCombustible;
    private boolean tieneAireAcondicionado;
    private int idTipo;
    private String nombreTipo;
    private double tarifaPorDia;
    private String estado;
    private String imagenUrl;
    private String descripcion;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor vacío
    public Vehiculo() {}

    // Constructor completo
    public Vehiculo(int idVehiculo, String placa, String marca, String modelo, int anio,
                    String color, int numeroPassajeros, String transmision, String tipoCombustible,
                    boolean tieneAireAcondicionado, int idTipo, String nombreTipo,
                    double tarifaPorDia, String estado, String imagenUrl, String descripcion) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.numeroPassajeros = numeroPassajeros;
        this.transmision = transmision;
        this.tipoCombustible = tipoCombustible;
        this.tieneAireAcondicionado = tieneAireAcondicionado;
        this.idTipo = idTipo;
        this.nombreTipo = nombreTipo;
        this.tarifaPorDia = tarifaPorDia;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getNumeroPassajeros() { return numeroPassajeros; }
    public void setNumeroPassajeros(int numeroPassajeros) { this.numeroPassajeros = numeroPassajeros; }

    public String getTransmision() { return transmision; }
    public void setTransmision(String transmision) { this.transmision = transmision; }

    public String getTipoCombustible() { return tipoCombustible; }
    public void setTipoCombustible(String tipoCombustible) { this.tipoCombustible = tipoCombustible; }

    public boolean isTieneAireAcondicionado() { return tieneAireAcondicionado; }
    public void setTieneAireAcondicionado(boolean tieneAireAcondicionado) {
        this.tieneAireAcondicionado = tieneAireAcondicionado;
    }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public double getTarifaPorDia() { return tarifaPorDia; }
    public void setTarifaPorDia(double tarifaPorDia) { this.tarifaPorDia = tarifaPorDia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    //obtener el nombre completo del vehiculo
    public String getNombreCompleto() {
        return marca + " " + modelo + " " + anio;
    }

    //verificar si esta disponible
    public boolean isDisponible() {
        return "Disponible".equals(estado);
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + placa + ")";
    }
}