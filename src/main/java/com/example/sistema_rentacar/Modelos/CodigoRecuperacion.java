package com.example.sistema_rentacar.Modelos;

import java.sql.Timestamp;

public class CodigoRecuperacion {
    private int idCodigo;
    private int idCliente;
    private String codigo;
    private Timestamp fechaGeneracion;
    private Timestamp fechaExpiracion;
    private boolean usado;
    private Timestamp fechaUso;

    // Constructor vacío
    public CodigoRecuperacion() {}

    // Constructor con campos principales
    public CodigoRecuperacion(int idCliente, String codigo, Timestamp fechaExpiracion) {
        this.idCliente = idCliente;
        this.codigo = codigo;
        this.fechaGeneracion = new Timestamp(System.currentTimeMillis());
        this.fechaExpiracion = fechaExpiracion;
        this.usado = false;
    }

    // Getters y Setters
    public int getIdCodigo() {
        return idCodigo;
    }

    public void setIdCodigo(int idCodigo) {
        this.idCodigo = idCodigo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Timestamp getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Timestamp fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Timestamp getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Timestamp fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    public Timestamp getFechaUso() {
        return fechaUso;
    }

    public void setFechaUso(Timestamp fechaUso) {
        this.fechaUso = fechaUso;
    }

    // Métodos de utilidad
    public boolean estaVigente() {
        if (usado) {
            return false;
        }
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        return ahora.before(fechaExpiracion);
    }

    public long minutosRestantes() {
        if (!estaVigente()) {
            return 0;
        }
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        long diferencia = fechaExpiracion.getTime() - ahora.getTime();
        return diferencia / (60 * 1000); // Convertir a minutos
    }

    @Override
    public String toString() {
        return "CodigoRecuperacion{" +
                "idCodigo=" + idCodigo +
                ", codigo='" + codigo + '\'' +
                ", usado=" + usado +
                ", vigente=" + estaVigente() +
                '}';
    }
}
