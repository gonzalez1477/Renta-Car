package com.example.sistema_rentacar.Repository;

import com.example.sistema_rentacar.Conexion.ConexionDB;
import com.example.sistema_rentacar.Modelos.CodigoRecuperacion;

import java.sql.*;
import java.util.Random;

public class RecuperacionRepository {


    public String generarCodigoAleatorio() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000); // Número entre 100000 y 999999
        return String.valueOf(codigo);
    }


    public CodigoRecuperacion crearCodigoRecuperacion(int idCliente) {
        // Invalidar códigos anteriores del mismo cliente
        invalidarCodigosAnteriores(idCliente);

        String codigo = generarCodigoAleatorio();

        // Calcular fecha de expiración de 15 minutos
        long tiempoExpiracion = System.currentTimeMillis() + (15 * 60 * 1000);
        Timestamp fechaExpiracion = new Timestamp(tiempoExpiracion);

        String sql = "INSERT INTO tbl_codigos_recuperacion " +
                "(id_cliente, codigo, fecha_expiracion) VALUES (?, ?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, idCliente);
            pstmt.setString(2, codigo);
            pstmt.setTimestamp(3, fechaExpiracion);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        CodigoRecuperacion codigoRec = new CodigoRecuperacion(
                                idCliente, codigo, fechaExpiracion);
                        codigoRec.setIdCodigo(generatedKeys.getInt(1));
                        codigoRec.setFechaGeneracion(new Timestamp(System.currentTimeMillis()));

                        System.out.println("Código generado: " + codigo + " para cliente ID: " + idCliente);
                        return codigoRec;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al crear código de recuperación: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private void invalidarCodigosAnteriores(int idCliente) {
        String sql = "UPDATE tbl_codigos_recuperacion SET usado = TRUE " +
                "WHERE id_cliente = ? AND usado = FALSE";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al invalidar códigos anteriores: " + e.getMessage());
        }
    }


    public int validarCodigo(String codigo) {
        String sql = "SELECT * FROM tbl_codigos_recuperacion " +
                "WHERE codigo = ? AND usado = FALSE " +
                "ORDER BY fecha_generacion DESC LIMIT 1";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CodigoRecuperacion codigoRec = mapearCodigo(rs);

                // Verificar si está vigente
                if (codigoRec.estaVigente()) {
                    return codigoRec.getIdCliente();
                } else {
                    System.out.println("Código expirado: " + codigo);
                }
            } else {
                System.out.println("Código no encontrado o ya usado: " + codigo);
            }

        } catch (SQLException e) {
            System.err.println("Error al validar código: " + e.getMessage());
        }

        return -1; // Código inválido
    }


    public boolean marcarComoUsado(String codigo) {
        String sql = "UPDATE tbl_codigos_recuperacion " +
                "SET usado = TRUE, fecha_uso = CURRENT_TIMESTAMP " +
                "WHERE codigo = ? AND usado = FALSE";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Código marcado como usado: " + codigo);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al marcar código como usado: " + e.getMessage());
        }

        return false;
    }


    public CodigoRecuperacion obtenerPorCodigo(String codigo) {
        String sql = "SELECT * FROM tbl_codigos_recuperacion " +
                "WHERE codigo = ? ORDER BY fecha_generacion DESC LIMIT 1";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearCodigo(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener código: " + e.getMessage());
        }

        return null;
    }


    public int limpiarCodigosExpirados() {
        String sql = "DELETE FROM tbl_codigos_recuperacion " +
                "WHERE fecha_expiracion < CURRENT_TIMESTAMP";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            int eliminados = pstmt.executeUpdate();
            System.out.println("🧹 Códigos expirados eliminados: " + eliminados);
            return eliminados;

        } catch (SQLException e) {
            System.err.println("Error al limpiar códigos expirados: " + e.getMessage());
        }

        return 0;
    }

    private CodigoRecuperacion mapearCodigo(ResultSet rs) throws SQLException {
        CodigoRecuperacion codigo = new CodigoRecuperacion();
        codigo.setIdCodigo(rs.getInt("id_codigo"));
        codigo.setIdCliente(rs.getInt("id_cliente"));
        codigo.setCodigo(rs.getString("codigo"));
        codigo.setFechaGeneracion(rs.getTimestamp("fecha_generacion"));
        codigo.setFechaExpiracion(rs.getTimestamp("fecha_expiracion"));
        codigo.setUsado(rs.getBoolean("usado"));
        codigo.setFechaUso(rs.getTimestamp("fecha_uso"));
        return codigo;
    }
}
