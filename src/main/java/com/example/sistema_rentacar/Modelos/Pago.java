package com.example.sistema_rentacar.Modelos;

import java.sql.Timestamp;

public class Pago {
    private int idPago;
    private int idAlquiler;
    private double monto;
    private Timestamp fechaPago;
    private String metodoPago; // "Efectivo", "Tarjeta", "Transferencia"
    private String referencia;
    private Integer idEmpleado;

    // Campos específicos para tarjeta
    private String tipoTarjeta; // "Visa", "Mastercard", "American Express", etc.
    private String ultimosDigitosTarjeta; // Solo últimos 4 dígitos
    private String nombreTitular;
    private String estadoPago; // "Completado", "Pendiente", "Rechazado"

    // Datos adicionales para vistas
    private String nombreCliente;
    private String vehiculo;
    private String placa;

    // Constructor vacío
    public Pago() {}

    // Constructor para pago en efectivo
    public Pago(int idAlquiler, double monto, String metodoPago, String referencia) {
        this.idAlquiler = idAlquiler;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
        this.estadoPago = "Completado";
        this.fechaPago = new Timestamp(System.currentTimeMillis());
    }

    // Constructor para pago con tarjeta
    public Pago(int idAlquiler, double monto, String tipoTarjeta,
                String ultimosDigitosTarjeta, String nombreTitular, String referencia) {
        this.idAlquiler = idAlquiler;
        this.monto = monto;
        this.metodoPago = "Tarjeta";
        this.tipoTarjeta = tipoTarjeta;
        this.ultimosDigitosTarjeta = ultimosDigitosTarjeta;
        this.nombreTitular = nombreTitular;
        this.referencia = referencia;
        this.estadoPago = "Completado";
        this.fechaPago = new Timestamp(System.currentTimeMillis());
    }

    // Getters y Setters
    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdAlquiler() {
        return idAlquiler;
    }

    public void setIdAlquiler(int idAlquiler) {
        this.idAlquiler = idAlquiler;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Timestamp getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Timestamp fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getUltimosDigitosTarjeta() {
        return ultimosDigitosTarjeta;
    }

    public void setUltimosDigitosTarjeta(String ultimosDigitosTarjeta) {
        this.ultimosDigitosTarjeta = ultimosDigitosTarjeta;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    // Métodos de utilidad
    public boolean esPagoConTarjeta() {
        return "Tarjeta".equals(metodoPago);
    }

    public boolean esPagoEnEfectivo() {
        return "Efectivo".equals(metodoPago);
    }

    public String getTarjetaEnmascarada() {
        if (ultimosDigitosTarjeta != null && !ultimosDigitosTarjeta.isEmpty()) {
            return "**** **** **** " + ultimosDigitosTarjeta;
        }
        return "";
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", estadoPago='" + estadoPago + '\'' +
                '}';
    }
}