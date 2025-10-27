package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Motor {
    private int id_motor;
    private int id_cliente;
    private String marca;
    private String modelo;
    private String numero_serie;
    private String estado;
    private Timestamp created_at;
    private Timestamp updated_at;

    public Motor() {}

    public Motor(int id_motor, int id_cliente, String marca, String modelo, String numero_serie, String estado) {
        this.id_motor = id_motor;
        this.id_cliente = id_cliente;
        this.marca = marca;
        this.modelo = modelo;
        this.numero_serie = numero_serie;
        this.estado = estado;
    }

    // getters / setters
    public int getId_motor() { return id_motor; }
    public void setId_motor(int id_motor) { this.id_motor = id_motor; }
    public int getId_cliente() { return id_cliente; }
    public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getNumero_serie() { return numero_serie; }
    public void setNumero_serie(String numero_serie) { this.numero_serie = numero_serie; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Timestamp getCreated_at() { return created_at; }
    public Timestamp getUpdated_at() { return updated_at; }

    // Obtener todos (con nombre de cliente para mostrar)
    public static List<Motor> obtenerTodos() {
        List<Motor> lista = new ArrayList<>();
        String sql = "SELECT id_motor, id_cliente, marca, modelo, numero_serie, estado, created_at, updated_at FROM motores ORDER BY id_motor";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Motor m = new Motor();
                m.id_motor = rs.getInt("id_motor");
                m.id_cliente = rs.getInt("id_cliente");
                m.marca = rs.getString("marca");
                m.modelo = rs.getString("modelo");
                m.numero_serie = rs.getString("numero_serie");
                m.estado = rs.getString("estado");
                m.created_at = rs.getTimestamp("created_at");
                m.updated_at = rs.getTimestamp("updated_at");
                lista.add(m);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // Guardar
    public boolean guardar() {
        String sql = "INSERT INTO motores(id_cliente, marca, modelo, numero_serie, estado) VALUES(?,?,?,?,?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, id_cliente);
            pst.setString(2, marca);
            pst.setString(3, modelo);
            pst.setString(4, numero_serie);
            pst.setString(5, estado == null ? "activo" : estado);
            int filas = pst.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) this.id_motor = keys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Actualizar
    public boolean actualizar() {
        String sql = "UPDATE motores SET id_cliente=?, marca=?, modelo=?, numero_serie=?, estado=? WHERE id_motor=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_cliente);
            pst.setString(2, marca);
            pst.setString(3, modelo);
            pst.setString(4, numero_serie);
            pst.setString(5, estado);
            pst.setInt(6, id_motor);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Eliminar
    public boolean eliminar() {
        String sql = "DELETE FROM motores WHERE id_motor=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_motor);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
