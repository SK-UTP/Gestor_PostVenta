package com.mycompany.proyectojv_postventa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_itc?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";      // tu usuario MySQL
    private static final String PASSWORD = "admin"; // tu contraseña MySQL

    public static Connection getConnection() {
        try {
            // Cargar driver MySQL (opcional en versiones recientes de JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
