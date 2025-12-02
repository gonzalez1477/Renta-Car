package com.example.sistema_rentacar.Repository;

import com.example.sistema_rentacar.Conexion.ConexionDB;
import com.example.sistema_rentacar.Modelos.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {

    public EmpleadoRepository() {
    }

    // Autenticar empleado
    public Empleado autenticar(String usuario, String contrasena) {
        String sql = "SELECT * FROM tbl_empleados WHERE usuario = ? AND contrasena = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasena);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapearEmpleado(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al autenticar empleado: " + e.getMessage());
        }
        return null;
    }

    // Obtener todos
    public List<Empleado> obtenerTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM tbl_empleados ORDER BY activo DESC, nombre, apellido";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
        }
        return lista;
    }

    // Obtener por ID
    public Empleado obtenerPorId(int id) {
        String sql = "SELECT * FROM tbl_empleados WHERE id_empleado = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearEmpleado(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener empleado: " + e.getMessage());
        }
        return null;
    }

    // Búsqueda general
    public List<Empleado> buscar(String criterio) {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM tbl_empleados WHERE " +
                "LOWER(nombre) LIKE ? OR LOWER(apellido) LIKE ? OR LOWER(email) LIKE ? " +
                "OR LOWER(usuario) LIKE ? OR LOWER(cargo) LIKE ? " +
                "ORDER BY activo DESC, nombre";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String filtro = "%" + criterio.toLowerCase() + "%";
            pstmt.setString(1, filtro);
            pstmt.setString(2, filtro);
            pstmt.setString(3, filtro);
            pstmt.setString(4, filtro);
            pstmt.setString(5, filtro);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar empleados: " + e.getMessage());
        }
        return lista;
    }

    // Verificar usuario
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM tbl_empleados WHERE usuario = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
        }
        return false;
    }

    // Verificar email
    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM tbl_empleados WHERE email = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar email: " + e.getMessage());
        }
        return false;
    }

    // Insertar
    public boolean insertar(Empleado e) {
        String sql = "INSERT INTO tbl_empleados " +
                "(nombre, apellido, email, telefono, usuario, contrasena, cargo, fecha_contratacion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, e.getNombre());
            pstmt.setString(2, e.getApellido());
            pstmt.setString(3, e.getEmail());
            pstmt.setString(4, e.getTelefono());
            pstmt.setString(5, e.getUsuario());
            pstmt.setString(6, e.getContrasena());
            pstmt.setString(7, e.getCargo());
            pstmt.setDate(8, e.getFechaContratacion());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error al insertar empleado: " + ex.getMessage());
        }
        return false;
    }

    // Actualizar
    public boolean actualizar(Empleado e) {
        String sql = "UPDATE tbl_empleados SET nombre = ?, apellido = ?, email = ?, telefono = ?, " +
                "cargo = ?, fecha_contratacion = ?, activo = ? WHERE id_empleado = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, e.getNombre());
            pstmt.setString(2, e.getApellido());
            pstmt.setString(3, e.getEmail());
            pstmt.setString(4, e.getTelefono());
            pstmt.setString(5, e.getCargo());
            pstmt.setDate(6, e.getFechaContratacion());
            pstmt.setBoolean(7, e.isActivo());
            pstmt.setInt(8, e.getIdEmpleado());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error al actualizar empleado: " + ex.getMessage());
        }
        return false;
    }

    // Actualizar contraseña
    public boolean actualizarContrasena(int idEmpleado, String nuevaContrasena) {
        String sql = "UPDATE tbl_empleados SET contrasena = ? WHERE id_empleado = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nuevaContrasena);
            pstmt.setInt(2, idEmpleado);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
        }
        return false;
    }

    // Desactivar empleado
    public boolean desactivar(int id) {
        String sql = "UPDATE tbl_empleados SET activo = false WHERE id_empleado = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar empleado: " + e.getMessage());
        }
        return false;
    }

    // Reactivar empleado
    public boolean reactivar(int id) {
        String sql = "UPDATE tbl_empleados SET activo = true WHERE id_empleado = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al reactivar empleado: " + e.getMessage());
        }
        return false;
    }

    // Eliminar definitivo
    public boolean eliminarPermanente(int id) {
        String sql = "DELETE FROM tbl_empleados WHERE id_empleado = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
        }
        return false;
    }

    // Verificar relaciones con alquileres
    public boolean tieneAlquileresAsociados(int id) {
        String sql = "SELECT COUNT(*) FROM tbl_alquileres WHERE id_empleado = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar alquileres asociados: " + e.getMessage());
        }
        return false;
    }

    // Mapear empleados
    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();

        e.setIdEmpleado(rs.getInt("id_empleado"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setEmail(rs.getString("email"));
        e.setTelefono(rs.getString("telefono"));
        e.setUsuario(rs.getString("usuario"));
        e.setContrasena(rs.getString("contrasena"));
        e.setCargo(rs.getString("cargo"));
        e.setFechaContratacion(rs.getDate("fecha_contratacion"));
        e.setActivo(rs.getBoolean("activo"));
        e.setCreatedAt(rs.getTimestamp("created_at")); // si lo renombraste

        return e;
    }
}

