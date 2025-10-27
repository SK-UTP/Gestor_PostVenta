package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class usuario {
    private String usuario;
    private String password;

    public usuario() {}

    public usuario(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    // Getters y setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Validación en la base de datos
    public boolean validar() {
        String sql = "SELECT * FROM usuarios WHERE usuario=? AND password_hash=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, usuario);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            return rs.next(); // true si existe
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
