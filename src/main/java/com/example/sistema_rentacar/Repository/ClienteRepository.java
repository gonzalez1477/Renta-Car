package com.example.sistema_rentacar.Repository;

import com.example.sistema_rentacar.Conexion.ConexionDB;
import com.example.sistema_rentacar.Modelos.Cliente;
import com.example.sistema_rentacar.Utilidades.EncriptarContraseña;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    public ClienteRepository() {
    }

    // Autenticación de clientes
    public Cliente autenticar(String usuario, String contrasena) {
        String sql = "SELECT * FROM tbl_clientes WHERE usuario = ? AND activo = TRUE";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("contrasena");

                if (EncriptarContraseña.verifyPassword(contrasena, hashedPassword)) {
                    return mapearCliente(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al autenticar cliente: " + e.getMessage());
        }

        return null;
    }

    // Registrar cliente
    public boolean registrar(Cliente cliente) {
        String sql = """
                INSERT INTO tbl_clientes (nombre, apellido, email, telefono, direccion, dui,
                licencia, fecha_nacimiento, usuario, contrasena)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDireccion());
            pstmt.setString(6, cliente.getDui());
            pstmt.setString(7, cliente.getLicencia());
            pstmt.setDate(8, cliente.getFechaNacimiento());
            pstmt.setString(9, cliente.getUsuario());

            String hashedPassword = EncriptarContraseña.encryptPassword(cliente.getContrasena());
            pstmt.setString(10, hashedPassword);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    // Verificar si usuario existe
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM tbl_clientes WHERE usuario = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
        }

        return false;
    }

    // Verificar si email existe
    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM tbl_clientes WHERE email = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar email: " + e.getMessage());
        }

        return false;
    }

    //  Verificar si DUI existe
    public boolean existeDui(String dui) {
        String sql = "SELECT COUNT(*) FROM tbl_clientes WHERE dui = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, dui);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar DUI: " + e.getMessage());
        }

        return false;
    }

    // Verificar si licencia existe
    public boolean existeLicencia(String licencia) {
        String sql = "SELECT COUNT(*) FROM tbl_clientes WHERE licencia = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, licencia);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar licencia: " + e.getMessage());
        }

        return false;
    }

    // Obtener todos los clientes
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM tbl_clientes ORDER BY nombre, apellido";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }

        return clientes;
    }

    // Obtener cliente por ID
    public Cliente obtenerPorId(int idCliente) {
        String sql = "SELECT * FROM tbl_clientes WHERE id_cliente = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return mapearCliente(rs);

        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por ID: " + e.getMessage());
        }

        return null;
    }

    // Obtener cliente por email
    public Cliente obtenerPorEmail(String email) {
        String sql = "SELECT * FROM tbl_clientes WHERE email = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por email: " + e.getMessage());
        }

        return null;
    }

    // Búsqueda
    public List<Cliente> buscar(String criterio) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = """
                SELECT * FROM tbl_clientes
                WHERE LOWER(nombre) LIKE ? OR LOWER(apellido) LIKE ?
                OR LOWER(email) LIKE ? OR LOWER(dui) LIKE ?
                """;

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String busqueda = "%" + criterio.toLowerCase() + "%";

            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);
            pstmt.setString(4, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
        }

        return clientes;
    }

    // Actualizar datos del cliente
    public boolean actualizar(Cliente cliente) {
        String sql = """
                UPDATE tbl_clientes SET nombre = ?, apellido = ?, email = ?, telefono = ?,
                direccion = ?, dui = ?, licencia = ?, fecha_nacimiento = ?, activo = ?
                WHERE id_cliente = ?
                """;

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDireccion());
            pstmt.setString(6, cliente.getDui());
            pstmt.setString(7, cliente.getLicencia());
            pstmt.setDate(8, cliente.getFechaNacimiento());
            pstmt.setBoolean(9, cliente.isActivo());
            pstmt.setInt(10, cliente.getIdCliente());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    //  Actualizar contraseña (ahora encripta automáticamente)
    // Actualizar contraseña (recibe contraseña YA encriptada)
    public boolean actualizarContrasena(int idCliente, String contrasenaEncriptada) {
        String sql = "UPDATE tbl_clientes SET contrasena = ? WHERE id_cliente = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // NO encriptar - ya viene encriptada
            pstmt.setString(1, contrasenaEncriptada);
            pstmt.setInt(2, idCliente);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Contraseña actualizada para cliente ID: " + idCliente);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Verificar contraseña
    public boolean verificarContrasena(int idCliente, String contrasenaPlana) {
        String sql = "SELECT contrasena FROM tbl_clientes WHERE id_cliente = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("contrasena");
                return EncriptarContraseña.verifyPassword(contrasenaPlana, hash);
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
        }

        return false;
    }

    // Mapear ResultSet a objeto Cliente
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();

        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellido(rs.getString("apellido"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setDui(rs.getString("dui"));
        cliente.setLicencia(rs.getString("licencia"));
        cliente.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        cliente.setUsuario(rs.getString("usuario"));
        cliente.setContrasena(rs.getString("contrasena"));
        cliente.setFechaRegistro(rs.getTimestamp("created_at"));
        cliente.setActivo(rs.getBoolean("activo"));

        return cliente;
    }
}