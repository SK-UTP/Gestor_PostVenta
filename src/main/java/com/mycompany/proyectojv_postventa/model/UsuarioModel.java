package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para CRUD de la tabla usuarios.
 * Nombre distinto (UsuarioModel) para no confundir con la clase 'usuario' que usas en login.
 */
public class UsuarioModel {
    private int id_usuario;
    private String nombre;
    private String usuario;
    private String password_hash;
    private int id_rol;
    private String estado;

    public UsuarioModel() {}

    public UsuarioModel(int id_usuario, String nombre, String usuario, String password_hash, int id_rol, String estado) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password_hash = password_hash;
        this.id_rol = id_rol;
        this.estado = estado;
    }

    // getters / setters
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

    // Obtener todos
    public static List<UsuarioModel> obtenerTodos() {
        List<UsuarioModel> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, usuario, password_hash, id_rol, estado FROM usuarios ORDER BY id_usuario";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                lista.add(new UsuarioModel(
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

    // Guardar
    public boolean guardar() {
        String sql = "INSERT INTO usuarios(nombre, usuario, password_hash, id_rol, estado) VALUES(?,?,?,?,?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nombre);
            pst.setString(2, usuario);
            pst.setString(3, password_hash); // hoy guardamos tal cual; más adelante puede usarse hashing.
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

    // Actualizar
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

    // Eliminar
    public boolean eliminar() {
        String sql = "DELETE FROM usuarios WHERE id_usuario=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_usuario);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
