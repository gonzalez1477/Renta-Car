package com.example.sistema_rentacar.Repository;

import com.example.sistema_rentacar.Conexion.ConexionDB;
import com.example.sistema_rentacar.Modelos.TipoVehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoVehiculoRepository {

    public TipoVehiculoRepository() {
    }

    // Obtener todos los tipos de vehículo
    public List<TipoVehiculo> obtenerTodos() {
        List<TipoVehiculo> tipos = new ArrayList<>();
        String sql = "SELECT * FROM tbl_tipos_vehiculo ORDER BY nombre_tipo";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tipos.add(mapearTipoVehiculo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipos de vehículo: " + e.getMessage());
        }

        return tipos;
    }

    // Buscar tipos de vehículo por nombre o descripción
    public List<TipoVehiculo> buscar(String criterio) {
        List<TipoVehiculo> tipos = new ArrayList<>();
        String sql = "SELECT * FROM tbl_tipos_vehiculo " +
                "WHERE LOWER(nombre_tipo) LIKE ? " +
                "OR LOWER(descripcion) LIKE ? " +
                "ORDER BY nombre_tipo";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String busqueda = "%" + criterio.toLowerCase() + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tipos.add(mapearTipoVehiculo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar tipos de vehículo: " + e.getMessage());
        }

        return tipos;
    }
    // Obtener tipo por ID
    public TipoVehiculo obtenerPorId(int id) {
        String sql = "SELECT * FROM tbl_tipos_vehiculo WHERE id_tipo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearTipoVehiculo(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de vehículo: " + e.getMessage());
        }

        return null;
    }

    // Verificar si el nombre ya existe
    public boolean existeNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM tbl_tipos_vehiculo WHERE LOWER(nombre_tipo) = LOWER(?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar nombre de tipo de vehículo: " + e.getMessage());
        }

        return false;
    }

    // Insertar nuevo tipo
    public boolean insertar(TipoVehiculo tipo) {
        String sql = "INSERT INTO tbl_tipos_vehiculo (nombre_tipo, tarifa_por_dia, descripcion) VALUES (?, ?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, tipo.getNombreTipo());
            pstmt.setDouble(2, tipo.getTarifaPorDia());
            pstmt.setString(3, tipo.getDescripcion());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar tipo de vehículo: " + e.getMessage());
        }

        return false;
    }

    // Actualizar tipo
    public boolean actualizar(TipoVehiculo tipo) {
        String sql = "UPDATE tbl_tipos_vehiculo SET nombre_tipo = ?, tarifa_por_dia = ?, descripcion = ? WHERE id_tipo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, tipo.getNombreTipo());
            pstmt.setDouble(2, tipo.getTarifaPorDia());
            pstmt.setString(3, tipo.getDescripcion());
            pstmt.setInt(4, tipo.getIdTipo());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar tipo de vehículo: " + e.getMessage());
        }

        return false;
    }

    // Verificar si tiene vehículos asociados
    public boolean tieneVehiculosAsociados(int id) {
        String sql = "SELECT COUNT(*) FROM tbl_vehiculos WHERE id_tipo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar vehículos asociados: " + e.getMessage());
        }

        return false;
    }

    // Eliminar tipo
    public boolean eliminar(int id) {
        String sql = "DELETE FROM tbl_tipos_vehiculo WHERE id_tipo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar tipo de vehículo: " + e.getMessage());
        }

        return false;
    }

    // Normalizar nombre de carpeta para manejo de imágenes
    public String obtenerNombreCarpeta(String nombreTipo) {
        return nombreTipo.toLowerCase()
                .replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ú", "u")
                .replace("ñ", "n")
                .replace(" ", "_");
    }

    // Mapear TipoVehiculo
    private TipoVehiculo mapearTipoVehiculo(ResultSet rs) throws SQLException {
        TipoVehiculo tipo = new TipoVehiculo();

        tipo.setIdTipo(rs.getInt("id_tipo"));
        tipo.setNombreTipo(rs.getString("nombre_tipo"));
        tipo.setTarifaPorDia(rs.getDouble("tarifa_por_dia"));
        tipo.setDescripcion(rs.getString("descripcion"));
        tipo.setCreatedAt(rs.getTimestamp("created_at"));

        return tipo;
    }
}

