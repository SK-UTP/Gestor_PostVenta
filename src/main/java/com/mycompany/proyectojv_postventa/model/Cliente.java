package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cliente {
    private int id_cliente;
    private String nombre;
    private String empresa;
    private String telefono;
    private String correo;
    private String estado;

    public Cliente() {}

    public Cliente(int id_cliente, String nombre, String empresa, String telefono, String correo, String estado) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.empresa = empresa;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
    }

    // Getters / Setters
    public int getId_cliente() { return id_cliente; }
    public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Obtener todos
    public static List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, empresa, telefono, correo, estado FROM clientes ORDER BY id_cliente";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("empresa"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // Guardar
    public boolean guardar() {
        String sql = "INSERT INTO clientes(nombre, empresa, telefono, correo, estado) VALUES(?,?,?,?,?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nombre);
            pst.setString(2, empresa);
            pst.setString(3, telefono);
            pst.setString(4, correo);
            pst.setString(5, estado == null ? "activo" : estado);
            int filas = pst.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) this.id_cliente = keys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Actualizar
    public boolean actualizar() {
        String sql = "UPDATE clientes SET nombre=?, empresa=?, telefono=?, correo=?, estado=? WHERE id_cliente=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, empresa);
            pst.setString(3, telefono);
            pst.setString(4, correo);
            pst.setString(5, estado);
            pst.setInt(6, id_cliente);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Eliminar
    public boolean eliminar() {
        String sql = "DELETE FROM clientes WHERE id_cliente=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_cliente);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    // dentro de la clase Cliente (asegúrate del package correcto y de tener conexion importada)
public static Cliente obtenerPorId(int id) {
    String sql = "SELECT id_cliente, nombre, empresa, telefono, correo, estado, created_at, updated_at FROM clientes WHERE id_cliente = ?";
    try (Connection conn = conexion.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setInt(1, id);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                Cliente c = new Cliente();
                c.setId_cliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setEmpresa(rs.getString("empresa"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                c.setEstado(rs.getString("estado"));
                // si tu clase Cliente tiene campos createdAt / updatedAt, parsearlos:
                // c.setCreatedAt(rs.getTimestamp("created_at"));
                // c.setUpdatedAt(rs.getTimestamp("updated_at"));
                return c;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

}
