package com.mycompany.proyectojv_postventa.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * CotizacionView — versión estilizada (consistente con ClienteView / UsuarioView).
 */
public class CotizacionView extends JFrame {
    private JTable tabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnVerDetalle, btnRefrescar, btnCerrar, btnExportar;

    public CotizacionView() {
        System.out.println("CotizacionView.<init>() — versión NEW cargada");
        setTitle("Cotizaciones — Sistema ITC");
        setSize(980, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12,12));

        // Top
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(34,49,63));
        JLabel title = new JLabel("Gestión de Cotizaciones");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(12,16,12,12));
        top.add(title, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        // Center - tabla
        tabla = new JTable();
        tabla.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Bottom - botones
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btnRefrescar = stylishButton("Refrescar", new Color(149,165,166));
        btnAgregar = stylishButton("Agregar", new Color(52,152,219));
        btnEditar = stylishButton("Editar", new Color(46,204,113));
        btnEliminar = stylishButton("Eliminar", new Color(231,76,60));
        btnVerDetalle = stylishButton("Ver Detalle", new Color(52,73,94));
        btnExportar = stylishButton("Exportar Excel", new Color(121,85,72));
        btnCerrar = stylishButton("Cerrar", new Color(109,110,114));

        bottom.add(btnRefrescar);
        bottom.add(btnAgregar);
        bottom.add(btnEditar);
        bottom.add(btnEliminar);
        bottom.add(btnVerDetalle);
        bottom.add(btnExportar);
        bottom.add(btnCerrar);

        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton stylishButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 36));
        return b;
    }

    // API para controller
    public JTable getTabla() { return tabla; }
    public void setTableModel(DefaultTableModel model) { tabla.setModel(model); }

    public void addRefrescarListener(ActionListener l) { btnRefrescar.addActionListener(l); }
    public void addAgregarListener(ActionListener l) { btnAgregar.addActionListener(l); }
    public void addEditarListener(ActionListener l) { btnEditar.addActionListener(l); }
    public void addEliminarListener(ActionListener l) { btnEliminar.addActionListener(l); }
    public void addVerDetalleListener(ActionListener l) { btnVerDetalle.addActionListener(l); }
    public void addCerrarListener(ActionListener l) { btnCerrar.addActionListener(l); }
    public void addExportarListener(ActionListener l) { btnExportar.addActionListener(l); }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
