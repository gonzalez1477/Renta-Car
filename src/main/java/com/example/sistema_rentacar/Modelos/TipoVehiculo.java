package com.example.sistema_rentacar.Modelos;

import java.sql.Timestamp;

public class TipoVehiculo {
    private int idTipo;
    private String nombreTipo;
    private double tarifaPorDia;
    private String descripcion;
    private Timestamp createdAt;

    // Constructor vacío
    public TipoVehiculo() {}

    // Constructor completo
    public TipoVehiculo(int idTipo, String nombreTipo, double tarifaPorDia, String descripcion) {
        this.idTipo = idTipo;
        this.nombreTipo = nombreTipo;
        this.tarifaPorDia = tarifaPorDia;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public double getTarifaPorDia() { return tarifaPorDia; }
    public void setTarifaPorDia(double tarifaPorDia) { this.tarifaPorDia = tarifaPorDia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return nombreTipo + " ($" + String.format("%.2f", tarifaPorDia) + "/día)";
    }
}
