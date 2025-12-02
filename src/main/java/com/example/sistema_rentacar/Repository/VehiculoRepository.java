package com.example.sistema_rentacar.Repository;

import com.example.sistema_rentacar.Conexion.ConexionDB;
import com.example.sistema_rentacar.Modelos.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoRepository {

    public VehiculoRepository() {}

    // Obtener todos los vehículos

    public List<Vehiculo> obtenerTodos() {
        List<Vehiculo> vehiculos = new ArrayList<>();

        String sql = "SELECT * FROM vista_tbl_vehiculos_completa ORDER BY nombre_tipo, marca, modelo";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(mapearVehiculo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener vehículos: " + e.getMessage());
        }

        return vehiculos;
    }

    public List<Vehiculo> obtenerDisponibles() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vista_tbl_vehiculos_completa WHERE estado = 'Disponible' ORDER BY nombre_tipo, marca";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(mapearVehiculo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener vehículos disponibles: " + e.getMessage());
        }

        return vehiculos;
    }

    // Obtener vehículos por categoría
    public List<Vehiculo> obtenerPorCategoria(String categoria) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vista_tbl_vehiculos_completa WHERE nombre_tipo = ? AND estado = 'Disponible'";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                vehiculos.add(mapearVehiculo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener vehículos por categoría: " + e.getMessage());
        }

        return vehiculos;
    }

    // Buscar vehículos por texto
    public List<Vehiculo> buscar(String criterio) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vista_tbl_vehiculos_completa " +
                "WHERE LOWER(placa) LIKE ? OR LOWER(marca) LIKE ? OR LOWER(modelo) LIKE ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String busqueda = "%" + criterio.toLowerCase() + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                vehiculos.add(mapearVehiculo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar vehículos: " + e.getMessage());
        }

        return vehiculos;
    }

    // Obtener vehículo por ID
    public Vehiculo obtenerPorId(int id) {
        String sql = "SELECT * FROM vista_tbl_vehiculos_completa WHERE id_vehiculo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearVehiculo(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener vehículo: " + e.getMessage());
        }

        return null;
    }

    // Insertar vehículo
    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO tbl_vehiculos (placa, marca, modelo, anio, color, numero_pasajeros, " +
                "transmision, tipo_combustible, tiene_aire_acondicionado, id_tipo, imagen_url, descripcion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, vehiculo.getPlaca());
            pstmt.setString(2, vehiculo.getMarca());
            pstmt.setString(3, vehiculo.getModelo());
            pstmt.setInt(4, vehiculo.getAnio());
            pstmt.setString(5, vehiculo.getColor());
            pstmt.setInt(6, vehiculo.getNumeroPassajeros());
            pstmt.setString(7, vehiculo.getTransmision());
            pstmt.setString(8, vehiculo.getTipoCombustible());
            pstmt.setBoolean(9, vehiculo.isTieneAireAcondicionado());
            pstmt.setInt(10, vehiculo.getIdTipo());
            pstmt.setString(11, vehiculo.getImagenUrl());
            pstmt.setString(12, vehiculo.getDescripcion());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar vehículo: " + e.getMessage());
            return false;
        }
    }

    // Actualizar vehículo
    public boolean actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE tbl_vehiculos SET placa = ?, marca = ?, modelo = ?, anio = ?, color = ?, " +
                "numero_pasajeros = ?, transmision = ?, tipo_combustible = ?, tiene_aire_acondicionado = ?, " +
                "id_tipo = ?, estado = ?, imagen_url = ?, descripcion = ? WHERE id_vehiculo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, vehiculo.getPlaca());
            pstmt.setString(2, vehiculo.getMarca());
            pstmt.setString(3, vehiculo.getModelo());
            pstmt.setInt(4, vehiculo.getAnio());
            pstmt.setString(5, vehiculo.getColor());
            pstmt.setInt(6, vehiculo.getNumeroPassajeros());
            pstmt.setString(7, vehiculo.getTransmision());
            pstmt.setString(8, vehiculo.getTipoCombustible());
            pstmt.setBoolean(9, vehiculo.isTieneAireAcondicionado());
            pstmt.setInt(10, vehiculo.getIdTipo());
            pstmt.setString(11, vehiculo.getEstado());
            pstmt.setString(12, vehiculo.getImagenUrl());
            pstmt.setString(13, vehiculo.getDescripcion());
            pstmt.setInt(14, vehiculo.getIdVehiculo());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar vehículo: " + e.getMessage());
            return false;
        }
    }

    // Eliminar vehículo
    public boolean eliminar(int id) {
        String sql = "DELETE FROM tbl_vehiculos WHERE id_vehiculo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
            return false;
        }
    }

    // Verificar si un vehículo tiene alquileres activos
    public boolean tieneAlquileresPorVehiculo(int idVehiculo) {
        String sql = "SELECT COUNT(*) FROM tbl_alquileres WHERE id_vehiculo = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idVehiculo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error verificando alquileres del vehículo: " + e.getMessage());
        }

        return false;
    }

    public boolean existePlaca(String placa) {
        String sql = "SELECT COUNT(*) FROM tbl_vehiculos WHERE placa = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, placa.toUpperCase().trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de placa: " + e.getMessage());
        }

        return false;
    }

    public Vehiculo obtenerPorPlaca(String placa) {
        String sql = "SELECT * FROM vista_tbl_vehiculos_completa WHERE placa = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, placa.toUpperCase().trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearVehiculo(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener vehículo por placa: " + e.getMessage());
        }

        return null;
    }

    // Mapear ResultSet a Vehiculo
    private Vehiculo mapearVehiculo(ResultSet rs) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();

        vehiculo.setIdVehiculo(rs.getInt("id_vehiculo"));
        vehiculo.setPlaca(rs.getString("placa"));
        vehiculo.setMarca(rs.getString("marca"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setAnio(rs.getInt("anio"));
        vehiculo.setColor(rs.getString("color"));
        vehiculo.setNumeroPassajeros(rs.getInt("numero_pasajeros"));
        vehiculo.setTransmision(rs.getString("transmision"));
        vehiculo.setTipoCombustible(rs.getString("tipo_combustible"));
        vehiculo.setTieneAireAcondicionado(rs.getBoolean("tiene_aire_acondicionado"));
        vehiculo.setIdTipo(rs.getInt("id_tipo"));
        vehiculo.setNombreTipo(rs.getString("nombre_tipo"));
        vehiculo.setTarifaPorDia(rs.getDouble("tarifa_por_dia"));
        vehiculo.setEstado(rs.getString("estado"));
        vehiculo.setImagenUrl(rs.getString("imagen_url"));
        vehiculo.setDescripcion(rs.getString("tipo_descripcion"));

        return vehiculo;
    }
}

