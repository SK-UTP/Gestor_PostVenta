package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Servicio {
    private int id_servicio;
    private int id_motor;
    private Integer id_mecanico; // puede ser NULL
    private String tipo;
    private Timestamp fecha_inicio;
    private Timestamp fecha_fin;
    private String estado;
    private String observaciones;

    public Servicio() {}

    public Servicio(int id_servicio, int id_motor, Integer id_mecanico, String tipo,
                    Timestamp fecha_inicio, Timestamp fecha_fin, String estado, String observaciones) {
        this.id_servicio = id_servicio;
        this.id_motor = id_motor;
        this.id_mecanico = id_mecanico;
        this.tipo = tipo;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // getters / setters
    public int getId_servicio() { return id_servicio; }
    public void setId_servicio(int id_servicio) { this.id_servicio = id_servicio; }
    public int getId_motor() { return id_motor; }
    public void setId_motor(int id_motor) { this.id_motor = id_motor; }
    public Integer getId_mecanico() { return id_mecanico; }
    public void setId_mecanico(Integer id_mecanico) { this.id_mecanico = id_mecanico; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Timestamp getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(Timestamp fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public Timestamp getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(Timestamp fecha_fin) { this.fecha_fin = fecha_fin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Obtener todos con JOIN para mostrar datos legibles
    public static List<Servicio> obtenerTodos() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id_servicio, id_motor, id_mecanico, tipo, fecha_inicio, fecha_fin, estado, observaciones " +
                     "FROM servicios ORDER BY id_servicio DESC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Servicio s = new Servicio();
                s.id_servicio = rs.getInt("id_servicio");
                s.id_motor = rs.getInt("id_motor");
                int mec = rs.getInt("id_mecanico");
                s.id_mecanico = rs.wasNull() ? null : mec;
                s.tipo = rs.getString("tipo");
                s.fecha_inicio = rs.getTimestamp("fecha_inicio");
                s.fecha_fin = rs.getTimestamp("fecha_fin");
                s.estado = rs.getString("estado");
                s.observaciones = rs.getString("observaciones");
                lista.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean guardar() {
        String sql = "INSERT INTO servicios(id_motor, id_mecanico, tipo, fecha_inicio, fecha_fin, estado, observaciones) " +
                     "VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, id_motor);
            if (id_mecanico == null) pst.setNull(2, Types.INTEGER); else pst.setInt(2, id_mecanico);
            pst.setString(3, tipo);
            pst.setTimestamp(4, fecha_inicio);
            pst.setTimestamp(5, fecha_fin);
            pst.setString(6, estado == null ? "planificado" : estado);
            pst.setString(7, observaciones);
            int filas = pst.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) this.id_servicio = keys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean actualizar() {
        String sql = "UPDATE servicios SET id_motor=?, id_mecanico=?, tipo=?, fecha_inicio=?, fecha_fin=?, estado=?, observaciones=? WHERE id_servicio=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_motor);
            if (id_mecanico == null) pst.setNull(2, Types.INTEGER); else pst.setInt(2, id_mecanico);
            pst.setString(3, tipo);
            pst.setTimestamp(4, fecha_inicio);
            pst.setTimestamp(5, fecha_fin);
            pst.setString(6, estado);
            pst.setString(7, observaciones);
            pst.setInt(8, id_servicio);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean eliminar() {
        String sql = "DELETE FROM servicios WHERE id_servicio=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_servicio);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
