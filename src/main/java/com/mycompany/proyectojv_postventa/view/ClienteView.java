package com.mycompany.proyectojv_postventa.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vista para Clientes con estilo parecido al Dashboard.
 */
public class ClienteView extends JFrame {
    private JTable tabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnRefrescar, btnCerrar;

    public ClienteView() {
        setTitle("Clientes — Sistema ITC");
        setSize(920, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12,12));

        // Top
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(34,49,63));
        JLabel title = new JLabel("Gestión de Clientes");
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
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnAgregar = stylishButton("Agregar", new Color(52,152,219));
        btnEditar = stylishButton("Editar", new Color(46,204,113));
        btnEliminar = stylishButton("Eliminar", new Color(231,76,60));
        btnRefrescar = stylishButton("Refrescar", new Color(149,165,166));
        btnCerrar = stylishButton("Cerrar", new Color(109,110,114));

        bottom.add(btnRefrescar);
        bottom.add(btnAgregar);
        bottom.add(btnEditar);
        bottom.add(btnEliminar);
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
        b.setPreferredSize(new Dimension(120, 36));
        return b;
    }

    // API para el controlador
    public JTable getTabla() { return tabla; }
    public void setTableModel(DefaultTableModel model) { tabla.setModel(model); }

    public void addAgregarListener(ActionListener l) { btnAgregar.addActionListener(l); }
    public void addEditarListener(ActionListener l) { btnEditar.addActionListener(l); }
    public void addEliminarListener(ActionListener l) { btnEliminar.addActionListener(l); }
    public void addRefrescarListener(ActionListener l) { btnRefrescar.addActionListener(l); }
    public void addCerrarListener(ActionListener l) { btnCerrar.addActionListener(l); }
}
