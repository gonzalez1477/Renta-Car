package com.example.sistema_rentacar.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración de la base de datos
    private static final String URL = "jdbc:postgresql://localhost:5432/rentacar_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1976";

    // Retorna una nueva conexión cada vez que se llama
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de PostgreSQL no encontrado");
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    //probar conexion
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión: " + e.getMessage());
            return false;
        }
    }
}

