package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Cotizacion {
    private int id_cotizacion;
    private int id_cliente;
    private Timestamp fecha;
    private String estado;
    private double total;

    public Cotizacion() {}

    public Cotizacion(int id_cotizacion, int id_cliente, Timestamp fecha, String estado, double total) {
        this.id_cotizacion = id_cotizacion;
        this.id_cliente = id_cliente;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
    }

    // getters / setters
    public int getId_cotizacion() { return id_cotizacion; }
    public void setId_cotizacion(int id_cotizacion) { this.id_cotizacion = id_cotizacion; }
    public int getId_cliente() { return id_cliente; }
    public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // Obtener todas las cotizaciones
    public static List<Cotizacion> obtenerTodos() {
        List<Cotizacion> lista = new ArrayList<>();
        String sql = "SELECT id_cotizacion, id_cliente, fecha, estado, total FROM cotizaciones ORDER BY fecha DESC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cotizacion(
                        rs.getInt("id_cotizacion"),
                        rs.getInt("id_cliente"),
                        rs.getTimestamp("fecha"),
                        rs.getString("estado"),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // Guardar cotizacion (sin detalles) -> retorna true y setea id_cotizacion
    public boolean guardar() {
        String sql = "INSERT INTO cotizaciones(id_cliente, fecha, estado, total) VALUES(?,?,?,?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, id_cliente);
            pst.setTimestamp(2, fecha == null ? new Timestamp(System.currentTimeMillis()) : fecha);
            pst.setString(3, estado == null ? "pendiente" : estado);
            pst.setDouble(4, total);
            int filas = pst.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) this.id_cotizacion = keys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean actualizar() {
        String sql = "UPDATE cotizaciones SET id_cliente=?, fecha=?, estado=?, total=? WHERE id_cotizacion=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_cliente);
            pst.setTimestamp(2, fecha);
            pst.setString(3, estado);
            pst.setDouble(4, total);
            pst.setInt(5, id_cotizacion);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean eliminar() {
        String sql = "DELETE FROM cotizaciones WHERE id_cotizacion=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id_cotizacion);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Obtener detalles de una cotizacion (helper, puede usarse desde controller)
    public static List<DetalleCotizacion> obtenerDetalle(int idCot) {
        List<DetalleCotizacion> lista = new ArrayList<>();
        String sql = "SELECT id_detalle, id_cotizacion, concepto, cantidad, precio_unitario, subtotal FROM detalle_cotizacion WHERE id_cotizacion = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCot);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    DetalleCotizacion d = new DetalleCotizacion();
                    d.setId_detalle(rs.getInt("id_detalle"));
                    d.setId_cotizacion(rs.getInt("id_cotizacion"));
                    d.setConcepto(rs.getString("concepto"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecio_unitario(rs.getDouble("precio_unitario"));
                    // subtotal lo calcula la BD, pero también lo podemos obtener:
                    lista.add(d);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
