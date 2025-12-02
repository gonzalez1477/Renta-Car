package com.example.sistema_rentacar.Modelos;

import java.sql.Date;
import java.sql.Timestamp;

public class Alquiler {
    private int idAlquiler;
    private int idCliente;
    private int idVehiculo;
    private Integer idEmpleado;
    private Timestamp fechaInicio;
    private Timestamp fechaFinEstimada;
    private Timestamp fechaFinReal;
    private int diasAlquiler;
    private double tarifaDiaria;
    private double costoTotal;
    private double deposito;
    private String estado;
    private String observaciones;
    private Timestamp createdAt;

    // Datos adicionales para vistas
    private String nombreCliente;
    private String telefonoCliente;
    private String vehiculo;
    private String placa;
    private String nombreEmpleado;

    // Constructor vacío
    public Alquiler() {}

    // Constructor completo
    public Alquiler(int idCliente, int idVehiculo, Timestamp fechaInicio, Timestamp fechaFinEstimada,
                    int diasAlquiler, double tarifaDiaria, double costoTotal, double deposito) {
        this.idCliente = idCliente;
        this.idVehiculo = idVehiculo;
        this.fechaInicio = fechaInicio;
        this.fechaFinEstimada = fechaFinEstimada;
        this.diasAlquiler = diasAlquiler;
        this.tarifaDiaria = tarifaDiaria;
        this.costoTotal = costoTotal;
        this.deposito = deposito;
        this.estado = "Activo";
    }

    // Getters y Setters
    public int getIdAlquiler() { return idAlquiler; }
    public void setIdAlquiler(int idAlquiler) { this.idAlquiler = idAlquiler; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public Integer getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }

    public Timestamp getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Timestamp fechaInicio) { this.fechaInicio = fechaInicio; }

    public Timestamp getFechaFinEstimada() { return fechaFinEstimada; }
    public void setFechaFinEstimada(Timestamp fechaFinEstimada) {
        this.fechaFinEstimada = fechaFinEstimada;
    }

    public Timestamp getFechaFinReal() { return fechaFinReal; }
    public void setFechaFinReal(Timestamp fechaFinReal) { this.fechaFinReal = fechaFinReal; }

    public int getDiasAlquiler() { return diasAlquiler; }
    public void setDiasAlquiler(int diasAlquiler) { this.diasAlquiler = diasAlquiler; }

    public double getTarifaDiaria() { return tarifaDiaria; }
    public void setTarifaDiaria(double tarifaDiaria) { this.tarifaDiaria = tarifaDiaria; }

    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    public double getDeposito() { return deposito; }
    public void setDeposito(double deposito) { this.deposito = deposito; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getVehiculo() { return vehiculo; }
    public void setVehiculo(String vehiculo) { this.vehiculo = vehiculo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public boolean isActivo() {
        return "Activo".equals(estado);
    }


    public boolean puedeFinalizarse() {
        return "Activo".equals(estado) ||
                "Por Vencer".equals(estado) ||
                "Retrasado".equals(estado);
    }


    public boolean estaRetrasado() {
        return "Retrasado".equals(estado);
    }

    public boolean estaPorVencer() {
        return "Por Vencer".equals(estado);
    }

}
