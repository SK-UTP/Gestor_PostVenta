package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id_usuario;
    private String nombre;
    private String usuario;
    private String password_hash;
    private int id_rol;
    private String estado;

    public Usuario() {}

    public Usuario(int id_usuario, String nombre, String usuario, String password_hash, int id_rol, String estado) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password_hash = password_hash;
        this.id_rol = id_rol;
        this.estado = estado;
    }

    // Getters / Setters
    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getPassword_hash() { return password_hash; }
    public void setPassword_hash(String password_hash) { this.password_hash = password_hash; }
    public int getId_rol() { return id_rol; }
    public void setId_rol(int id_rol) { this.id_rol = id_rol; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // --- Compatibilidad para login antiguo ---
    // permite que antiguos controladores que llamaban setPassword(...) sigan funcionando
    public void setPassword(String pass) { this.password_hash = pass; }
    // permite que antiguos controladores que llamaban validar() sigan funcionando
    public boolean validar() { return validarCredenciales(); }

    // -----------------------
    // LOGIN (validar credenciales)
    // -----------------------
    public boolean validarCredenciales() {
        String sql = "SELECT id_usuario, nombre, usuario, password_hash, id_rol, estado FROM usuarios WHERE usuario=? AND password_hash=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, usuario);
            pst.setString(2, password_hash); // texto plano por ahora
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    this.id_usuario = rs.getInt("id_usuario");
                    this.nombre = rs.getString("nombre");
                    this.usuario = rs.getString("usuario");
                    this.password_hash = rs.getString("password_hash");
                    this.id_rol = rs.getInt("id_rol");
                    this.estado = rs.getString("estado");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -----------------------
    // CRUD
    // -----------------------
    public static List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, usuario, password_hash, id_rol, estado FROM usuarios ORDER BY id_usuario";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("password_hash"),
                        rs.getInt("id_rol"),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean guardar() {
        String sql = "INSERT INTO usuarios(nombre, usuario, password_hash, id_rol, estado) VALUES(?,?,?,?,?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nombre);
            pst.setString(2, usuario);
            pst.setString(3, password_hash);
            pst.setInt(4, id_rol);
            pst.setString(5, estado == null ? "activo" : estado);
            int filas = pst.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) this.id_usuario = keys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean actualizar() {
        String sql = "UPDATE usuarios SET nombre=?, usuario=?, password_hash=?, id_rol=?, estado=? WHERE id_usuario=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, usuario);
            pst.setString(3, password_hash);
            pst.setInt(4, id_rol);
            pst.setString(5, estado);
            pst.setInt(6, id_usuario);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean eliminar() {
        String sql = "DELETE FROM usuarios WHERE id_usuario=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_usuario);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
