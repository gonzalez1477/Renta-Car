package com.example.sistema_rentacar.Repository;

import com.example.sistema_rentacar.Conexion.ConexionDB;
import com.example.sistema_rentacar.Modelos.Alquiler;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlquilerRepository {

    // Crear nuevo alquiler
    public boolean crear(Alquiler alquiler) {
        String sql = "INSERT INTO tbl_alquileres (id_cliente, id_vehiculo, id_empleado, fecha_inicio, fecha_fin_estimada, " +
                "dias_alquiler, tarifa_diaria, costo_total, deposito, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, alquiler.getIdCliente());
            pstmt.setInt(2, alquiler.getIdVehiculo());

            if (alquiler.getIdEmpleado() != null) {
                pstmt.setInt(3, alquiler.getIdEmpleado());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setTimestamp(4, alquiler.getFechaInicio());
            pstmt.setTimestamp(5, alquiler.getFechaFinEstimada());
            pstmt.setInt(6, alquiler.getDiasAlquiler());
            pstmt.setDouble(7, alquiler.getTarifaDiaria());
            pstmt.setDouble(8, alquiler.getCostoTotal());
            pstmt.setDouble(9, alquiler.getDeposito());
            pstmt.setString(10, alquiler.getObservaciones());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        alquiler.setIdAlquiler(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al crear alquiler: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return false;
    }

    // Obtener alquileres activos
    public List<Alquiler> obtenerActivos() {
        List<Alquiler> alquileres = new ArrayList<>();
        String sql = "SELECT * FROM vista_alquileres_activos ORDER BY fecha_inicio DESC";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alquileres.add(mapearAlquilerVista(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alquileres activos: " + e.getMessage());
        }

        return alquileres;
    }

    // historial de alquileres de un cliente
    public List<Alquiler> obtenerPorCliente(int idCliente) {
        List<Alquiler> alquileres = new ArrayList<>();
        String sql = "SELECT * FROM vista_historial_cliente WHERE id_cliente = ? ORDER BY fecha_inicio DESC";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                alquileres.add(mapearHistorialCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historial del cliente: " + e.getMessage());
        }

        return alquileres;
    }

    // Finalizar alquiler
    public boolean finalizar(int idAlquiler) {
        String sql = "UPDATE tbl_alquileres SET estado = 'Finalizado', fecha_fin_real = CURRENT_TIMESTAMP " +
                "WHERE id_alquiler = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idAlquiler);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al finalizar alquiler: " + e.getMessage());
            return false;
        }
    }

    // Verificar si cliente tiene alquileres activos
    public boolean tieneAlquileresActivos(int idCliente) {
        String sql = "SELECT COUNT(*) FROM tbl_alquileres WHERE id_cliente = ? AND estado = 'Activo'";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar alquileres activos: " + e.getMessage());
        }

        return false;
    }

    // Obtener alquiler activo del cliente
    public Alquiler obtenerAlquilerActivoCliente(int idCliente) {
        String sql = "SELECT a.*, v.marca || ' ' || v.modelo as vehiculo, v.placa " +
                "FROM tbl_alquileres a " +
                "INNER JOIN tbl_vehiculos v ON a.id_vehiculo = v.id_vehiculo " +
                "WHERE a.id_cliente = ? AND a.estado = 'Activo' " +
                "ORDER BY a.fecha_inicio DESC LIMIT 1";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Alquiler alquiler = mapearAlquiler(rs);
                alquiler.setVehiculo(rs.getString("vehiculo"));
                alquiler.setPlaca(rs.getString("placa"));
                return alquiler;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alquiler activo: " + e.getMessage());
        }

        return null;
    }

    // Verificar si el vehiculo tiene alquileres
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


    public boolean tieneAlquileresEnCurso(int idCliente) {
        String sql = "SELECT COUNT(*) FROM tbl_alquileres " +
                "WHERE id_cliente = ? " +
                "AND estado IN ('Activo', 'Por Vencer', 'Retrasado')";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar alquileres en curso: " + e.getMessage());
        }

        return false;
    }

    public Alquiler obtenerAlquilerEnCursoCliente(int idCliente) {
        String sql = "SELECT a.*, v.marca || ' ' || v.modelo as vehiculo, v.placa " +
                "FROM tbl_alquileres a " +
                "INNER JOIN tbl_vehiculos v ON a.id_vehiculo = v.id_vehiculo " +
                "WHERE a.id_cliente = ? " +
                "AND a.estado IN ('Activo', 'Por Vencer', 'Retrasado') " +
                "ORDER BY a.fecha_inicio DESC LIMIT 1";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Alquiler alquiler = mapearAlquiler(rs);
                alquiler.setVehiculo(rs.getString("vehiculo"));
                alquiler.setPlaca(rs.getString("placa"));
                return alquiler;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alquiler en curso: " + e.getMessage());
        }

        return null;
    }

    public boolean actualizarObservaciones(int idAlquiler, String observaciones) {
        String sql = "UPDATE tbl_alquileres SET observaciones = ? WHERE id_alquiler = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, observaciones);
            pstmt.setInt(2, idAlquiler);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar observaciones: " + e.getMessage());
            return false;
        }
    }


    public boolean cambiarEstado(int idAlquiler, String nuevoEstado) {
        String sql = "UPDATE tbl_alquileres SET estado = ? WHERE id_alquiler = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idAlquiler);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }


    public boolean actualizarPenalizacion(int idAlquiler, double penalizacion, int diasRetraso) {
        String sql = "UPDATE tbl_alquileres SET penalizacion = ?, dias_retraso = ? WHERE id_alquiler = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setDouble(1, penalizacion);
            pstmt.setInt(2, diasRetraso);
            pstmt.setInt(3, idAlquiler);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar penalización: " + e.getMessage());
            return false;
        }
    }


    public List<Alquiler> obtenerActivosYRetrasados() {
        List<Alquiler> alquileres = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "c.nombre || ' ' || c.apellido as nombre_cliente, " +
                "c.telefono as telefono_cliente, " +
                "c.email as email_cliente, " +
                "v.marca || ' ' || v.modelo as vehiculo, " +
                "v.placa, " +
                "e.nombre || ' ' || e.apellido as empleado_responsable " +
                "FROM tbl_alquileres a " +
                "INNER JOIN tbl_clientes c ON a.id_cliente = c.id_cliente " +
                "INNER JOIN tbl_vehiculos v ON a.id_vehiculo = v.id_vehiculo " +
                "LEFT JOIN tbl_empleados e ON a.id_empleado = e.id_empleado " +
                "WHERE a.estado IN ('Activo', 'Por Vencer', 'Retrasado') " +
                "ORDER BY " +
                "  CASE " +
                "    WHEN CURRENT_DATE > a.fecha_fin_estimada THEN 1 " +  // Retrasados primero
                "    WHEN CURRENT_DATE = a.fecha_fin_estimada OR " +
                "         CURRENT_DATE = (a.fecha_fin_estimada - INTERVAL '1 day') THEN 2 " + // Por vencer segundo
                "    ELSE 3 " + // Activos al final
                "  END, " +
                "  a.fecha_fin_estimada ASC";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Alquiler alquiler = mapearAlquiler(rs);
                alquiler.setNombreCliente(rs.getString("nombre_cliente"));
                alquiler.setTelefonoCliente(rs.getString("telefono_cliente"));
                alquiler.setVehiculo(rs.getString("vehiculo"));
                alquiler.setPlaca(rs.getString("placa"));

                String empleado = rs.getString("empleado_responsable");
                if (empleado != null) {
                    alquiler.setNombreEmpleado(empleado);
                }

                alquileres.add(alquiler);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alquileres activos y retrasados: " + e.getMessage());
            e.printStackTrace();
        }

        return alquileres;
    }


    public List<Alquiler> obtenerAlquileresVencidos() {
        List<Alquiler> alquileres = new ArrayList<>();
        String sql = "SELECT * FROM tbl_alquileres " +
                "WHERE estado = 'Activo' " +
                "AND fecha_fin_estimada < CURRENT_DATE " +
                "ORDER BY fecha_fin_estimada ASC";

        try (Connection connection = ConexionDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alquileres.add(mapearAlquiler(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alquileres vencidos: " + e.getMessage());
        }

        return alquileres;
    }


    public boolean estaVencido(int idAlquiler) {
        String sql = "SELECT fecha_fin_estimada FROM tbl_alquileres " +
                "WHERE id_alquiler = ? AND estado = 'Activo'";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idAlquiler);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Date fechaFin = rs.getDate("fecha_fin_estimada");
                LocalDate fechaFinLocal = fechaFin.toLocalDate();
                return LocalDate.now().isAfter(fechaFinLocal);
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar vencimiento: " + e.getMessage());
        }

        return false;
    }


    public double obtenerIngresosDelMesActual() {
        String sql = "SELECT COALESCE(SUM(costo_total), 0) as ingresos " +
                "FROM tbl_alquileres " +
                "WHERE estado = 'Finalizado' " +
                "AND fecha_fin_real IS NOT NULL " +
                "AND EXTRACT(MONTH FROM fecha_fin_real) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                "AND EXTRACT(YEAR FROM fecha_fin_real) = EXTRACT(YEAR FROM CURRENT_DATE)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("ingresos");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ingresos del mes: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }


    public double obtenerIngresosProyectadosDelMes() {
        String sql = "SELECT COALESCE(SUM(costo_total), 0) as ingresos " +
                "FROM tbl_alquileres " +
                "WHERE EXTRACT(MONTH FROM fecha_inicio) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                "AND EXTRACT(YEAR FROM fecha_inicio) = EXTRACT(YEAR FROM CURRENT_DATE)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("ingresos");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ingresos proyectados: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    // Buscar alquileres activos por nombre de cliente
    public List<Alquiler> buscarActivosPorCliente(String criterio) {
        List<Alquiler> alquileres = new ArrayList<>();

        String sql = "SELECT a.*, " +
                "c.nombre || ' ' || c.apellido as nombre_cliente, " +
                "c.telefono as telefono_cliente, " +
                "c.email as email_cliente, " +
                "v.marca || ' ' || v.modelo as vehiculo, " +
                "v.placa, " +
                "e.nombre || ' ' || e.apellido as empleado_responsable " +
                "FROM tbl_alquileres a " +
                "INNER JOIN tbl_clientes c ON a.id_cliente = c.id_cliente " +
                "INNER JOIN tbl_vehiculos v ON a.id_vehiculo = v.id_vehiculo " +
                "LEFT JOIN tbl_empleados e ON a.id_empleado = e.id_empleado " +
                "WHERE a.estado IN ('Activo', 'Por Vencer', 'Retrasado') " +
                "AND (LOWER(c.nombre) LIKE ? " +
                "     OR LOWER(c.apellido) LIKE ? " +
                "     OR LOWER(c.nombre || ' ' || c.apellido) LIKE ?) " +
                "ORDER BY " +
                "  CASE " +
                "    WHEN CURRENT_DATE > a.fecha_fin_estimada THEN 1 " +
                "    WHEN CURRENT_DATE = a.fecha_fin_estimada OR " +
                "         CURRENT_DATE = (a.fecha_fin_estimada - INTERVAL '1 day') THEN 2 " +
                "    ELSE 3 " +
                "  END, " +
                "  a.fecha_fin_estimada ASC";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String busqueda = "%" + criterio.toLowerCase() + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Alquiler alquiler = mapearAlquiler(rs);
                alquiler.setNombreCliente(rs.getString("nombre_cliente"));
                alquiler.setTelefonoCliente(rs.getString("telefono_cliente"));
                alquiler.setVehiculo(rs.getString("vehiculo"));
                alquiler.setPlaca(rs.getString("placa"));

                String empleado = rs.getString("empleado_responsable");
                if (empleado != null) {
                    alquiler.setNombreEmpleado(empleado);
                }

                alquileres.add(alquiler);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar alquileres por cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return alquileres;
    }

    // Métodos de mapeo
    private Alquiler mapearAlquiler(ResultSet rs) throws SQLException {
        Alquiler alquiler = new Alquiler();
        alquiler.setIdAlquiler(rs.getInt("id_alquiler"));
        alquiler.setIdCliente(rs.getInt("id_cliente"));
        alquiler.setIdVehiculo(rs.getInt("id_vehiculo"));

        int idEmp = rs.getInt("id_empleado");
        if (!rs.wasNull()) alquiler.setIdEmpleado(idEmp);

        alquiler.setFechaInicio(rs.getTimestamp("fecha_inicio"));
        alquiler.setFechaFinEstimada(rs.getTimestamp("fecha_fin_estimada"));
        alquiler.setFechaFinReal(rs.getTimestamp("fecha_fin_real"));
        alquiler.setDiasAlquiler(rs.getInt("dias_alquiler"));
        alquiler.setTarifaDiaria(rs.getDouble("tarifa_diaria"));
        alquiler.setCostoTotal(rs.getDouble("costo_total"));
        alquiler.setDeposito(rs.getDouble("deposito"));
        alquiler.setEstado(rs.getString("estado"));
        alquiler.setObservaciones(rs.getString("observaciones"));
        alquiler.setCreatedAt(rs.getTimestamp("created_at"));
        return alquiler;
    }

    private Alquiler mapearAlquilerVista(ResultSet rs) throws SQLException {
        Alquiler alquiler = new Alquiler();
        alquiler.setIdAlquiler(rs.getInt("id_alquiler"));
        alquiler.setFechaInicio(rs.getTimestamp("fecha_inicio"));
        alquiler.setFechaFinEstimada(rs.getTimestamp("fecha_fin_estimada"));
        alquiler.setDiasAlquiler(rs.getInt("dias_alquiler"));
        alquiler.setCostoTotal(rs.getDouble("costo_total"));
        alquiler.setEstado(rs.getString("estado"));
        alquiler.setNombreCliente(rs.getString("nombre_cliente"));
        alquiler.setTelefonoCliente(rs.getString("telefono_cliente"));
        alquiler.setVehiculo(rs.getString("vehiculo"));
        alquiler.setPlaca(rs.getString("placa"));
        alquiler.setNombreEmpleado(rs.getString("empleado_responsable"));
        return alquiler;
    }

    private Alquiler mapearHistorialCliente(ResultSet rs) throws SQLException {
        Alquiler alquiler = new Alquiler();

        // IDs
        alquiler.setIdAlquiler(rs.getInt("id_alquiler"));
        alquiler.setIdCliente(rs.getInt("id_cliente"));

        // Información del cliente (desde la vista)
        alquiler.setNombreCliente(rs.getString("nombre_cliente"));

        // Información del vehículo
        alquiler.setVehiculo(rs.getString("vehiculo"));
        alquiler.setPlaca(rs.getString("placa"));

        // Fechas
        alquiler.setFechaInicio(rs.getTimestamp("fecha_inicio"));
        alquiler.setFechaFinEstimada(rs.getTimestamp("fecha_fin_estimada"));
        alquiler.setFechaFinReal(rs.getTimestamp("fecha_fin_real"));

        // Detalles económicos
        alquiler.setDiasAlquiler(rs.getInt("dias_alquiler"));
        alquiler.setTarifaDiaria(rs.getDouble("tarifa_diaria"));
        alquiler.setCostoTotal(rs.getDouble("costo_total"));

        // Estado
        alquiler.setEstado(rs.getString("estado"));

        return alquiler;
    }
}