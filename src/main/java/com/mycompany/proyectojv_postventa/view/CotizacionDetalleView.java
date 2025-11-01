package com.mycompany.proyectojv_postventa.view;

import com.mycompany.proyectojv_postventa.model.DetalleCotizacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CotizacionDetalleView {

    /**
     * Muestra una ventana con la lista ordenada de detalles para una cotización.
     * @param idCotizacion id de la cotización
     */
    public static void mostrarDetalle(int idCotizacion) {
        // Obtener datos
        List<DetalleCotizacion> lista = DetalleCotizacion.obtenerPorCotizacion(idCotizacion);

        // Columnas
        String[] cols = {"#", "Concepto", "Cantidad", "Precio unitario", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // Rellenar filas
        int n = 1;
        for (DetalleCotizacion d : lista) {
            model.addRow(new Object[]{
                n++,
                d.getConcepto(),
                d.getCantidad(),
                d.getPrecio_unitario(),
                d.getSubtotal()
            });
        }

        // Crear tabla y ajustes visuales
        JTable tabla = new JTable(model);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true); // permitir ordenar las columnas
        tabla.getTableHeader().setReorderingAllowed(false);

        // Ajustar anchos (opcional)
        if (tabla.getColumnModel().getColumnCount() >= 5) {
            tabla.getColumnModel().getColumn(0).setPreferredWidth(40);  // #
            tabla.getColumnModel().getColumn(1).setPreferredWidth(300); // concepto
            tabla.getColumnModel().getColumn(2).setPreferredWidth(80);  // cantidad
            tabla.getColumnModel().getColumn(3).setPreferredWidth(120); // precio unit
            tabla.getColumnModel().getColumn(4).setPreferredWidth(120); // subtotal
        }

        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(760, 380));

        // Panel inferior con total
        double total = lista.stream().mapToDouble(DetalleCotizacion::getSubtotal).sum();
        JPanel bottom = new JPanel(new BorderLayout());
        JLabel lblTotal = new JLabel("Total: " + String.format("%.2f", total));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        bottom.add(lblTotal, BorderLayout.EAST);

        // Frame
        JFrame frame = new JFrame("Detalle — Cotización #" + idCotizacion);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Detalle de la cotización:"));
        top.setBorder(BorderFactory.createEmptyBorder(8,12,0,12));
        frame.add(top, BorderLayout.NORTH);
        frame.add(sp, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
