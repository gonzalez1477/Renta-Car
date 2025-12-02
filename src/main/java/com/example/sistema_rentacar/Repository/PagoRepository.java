package com.example.sistema_rentacar.Repository;

import com.example.sistema_rentacar.Conexion.ConexionDB;
import com.example.sistema_rentacar.Modelos.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoRepository {

    // Registrar pago en efectivo
    public boolean registrarPagoEfectivo(Pago pago) {
        String sql = "INSERT INTO tbl_pagos (" +
                "id_alquiler, monto, metodo_pago, referencia, estado_pago, id_empleado, fecha_pago" +
                ") VALUES (?, ?, 'Efectivo', ?, 'Completado', ?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, pago.getIdAlquiler());
            pstmt.setDouble(2, pago.getMonto());
            pstmt.setString(3, pago.getReferencia());

            if (pago.getIdEmpleado() != null) {
                pstmt.setInt(4, pago.getIdEmpleado());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }


            pstmt.setTimestamp(5, pago.getFechaPago() != null ?
                    pago.getFechaPago() : new Timestamp(System.currentTimeMillis()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pago.setIdPago(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar pago en efectivo: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Registrar pago con tarjeta
    public boolean registrarPagoTarjeta(Pago pago) {
        String sql = "INSERT INTO tbl_pagos (" +
                "id_alquiler, monto, metodo_pago, tipo_tarjeta, ultimos_digitos_tarjeta, " +
                "nombre_titular, referencia, estado_pago, id_empleado, fecha_pago" +
                ") VALUES (?, ?, 'Tarjeta', ?, ?, ?, ?, 'Completado', ?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, pago.getIdAlquiler());
            pstmt.setDouble(2, pago.getMonto());
            pstmt.setString(3, pago.getTipoTarjeta());
            pstmt.setString(4, pago.getUltimosDigitosTarjeta());
            pstmt.setString(5, pago.getNombreTitular());
            pstmt.setString(6, pago.getReferencia());

            if (pago.getIdEmpleado() != null) {
                pstmt.setInt(7, pago.getIdEmpleado());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            // Establecer fecha_pago
            pstmt.setTimestamp(8, pago.getFechaPago() != null ?
                    pago.getFechaPago() : new Timestamp(System.currentTimeMillis()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pago.setIdPago(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar pago con tarjeta: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Obtener pagos de un alquiler
    public List<Pago> obtenerPorAlquiler(int idAlquiler) {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT * FROM tbl_pagos WHERE id_alquiler = ? ORDER BY fecha_pago DESC";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idAlquiler);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                pagos.add(mapearPago(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pagos del alquiler: " + e.getMessage());
        }

        return pagos;
    }

    // Historial con detalles
    public List<Pago> obtenerHistorialPagos() {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT * FROM vista_historial_pagos";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pagos.add(mapearPagoVista(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historial de pagos: " + e.getMessage());
        }

        return pagos;
    }

    // Total pagado
    public double obtenerTotalPagado(int idAlquiler) {
        String sql = "SELECT COALESCE(SUM(monto), 0) AS total " +
                "FROM tbl_pagos WHERE id_alquiler = ? AND estado_pago = 'Completado'";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idAlquiler);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total pagado: " + e.getMessage());
        }

        return 0.0;
    }

    // Verificar pago completo
    public boolean estaPagadoCompleto(int idAlquiler, double costoTotal) {
        return obtenerTotalPagado(idAlquiler) >= costoTotal;
    }

    // Obtener pago por ID
    public Pago obtenerPorId(int idPago) {
        String sql = "SELECT * FROM tbl_pagos WHERE id_pago = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idPago);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearPago(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pago: " + e.getMessage());
        }

        return null;
    }

    // Mapeo general
    private Pago mapearPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();

        pago.setIdPago(rs.getInt("id_pago"));
        pago.setIdAlquiler(rs.getInt("id_alquiler"));
        pago.setMonto(rs.getDouble("monto"));

        Timestamp fecha = rs.getTimestamp("fecha_pago");
        pago.setFechaPago(fecha != null ? fecha : new Timestamp(System.currentTimeMillis()));

        pago.setMetodoPago(rs.getString("metodo_pago"));
        pago.setReferencia(rs.getString("referencia"));
        pago.setEstadoPago(rs.getString("estado_pago"));

        pago.setTipoTarjeta(rs.getString("tipo_tarjeta"));
        pago.setUltimosDigitosTarjeta(rs.getString("ultimos_digitos_tarjeta"));
        pago.setNombreTitular(rs.getString("nombre_titular"));

        int idEmp = rs.getInt("id_empleado");
        if (!rs.wasNull()) {
            pago.setIdEmpleado(idEmp);
        }

        return pago;
    }

    // Mapeo vista
    private Pago mapearPagoVista(ResultSet rs) throws SQLException {
        Pago pago = mapearPago(rs);
        pago.setNombreCliente(rs.getString("cliente"));
        pago.setVehiculo(rs.getString("vehiculo"));
        pago.setPlaca(rs.getString("placa"));
        return pago;

    }


}
