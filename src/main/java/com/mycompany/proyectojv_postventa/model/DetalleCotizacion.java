package com.mycompany.proyectojv_postventa.model;

import com.mycompany.proyectojv_postventa.conexion;
import java.sql.*;

public class DetalleCotizacion {
    private int id_detalle;
    private int id_cotizacion;
    private String concepto;
    private int cantidad;
    private double precio_unitario;

    public DetalleCotizacion() {}

    public DetalleCotizacion(int id_detalle, int id_cotizacion, String concepto, int cantidad, double precio_unitario) {
        this.id_detalle = id_detalle;
        this.id_cotizacion = id_cotizacion;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
    }

    // getters/setters
    public int getId_detalle() { return id_detalle; }
    public void setId_detalle(int id_detalle) { this.id_detalle = id_detalle; }
    public int getId_cotizacion() { return id_cotizacion; }
    public void setId_cotizacion(int id_cotizacion) { this.id_cotizacion = id_cotizacion; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecio_unitario() { return precio_unitario; }
    public void setPrecio_unitario(double precio_unitario) { this.precio_unitario = precio_unitario; }

    public boolean guardar() {
        String sql = "INSERT INTO detalle_cotizacion(id_cotizacion, concepto, cantidad, precio_unitario) VALUES(?,?,?,?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, id_cotizacion);
            pst.setString(2, concepto);
            pst.setInt(3, cantidad);
            pst.setDouble(4, precio_unitario);
            int f = pst.executeUpdate();
            if (f > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) this.id_detalle = keys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean eliminarPorCotizacion(int idCot) {
        String sql = "DELETE FROM detalle_cotizacion WHERE id_cotizacion=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCot);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
