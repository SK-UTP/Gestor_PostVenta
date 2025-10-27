package com.mycompany.proyectojv_postventa.view;

import javax.swing.*;
import java.awt.*;

public class usuarioviewold extends JFrame {
    private JTable tablaUsuarios;
    private JButton btnAgregar, btnEditar, btnEliminar;

    public usuarioviewold() {
        setTitle("Gestión de Usuarios");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabla de usuarios
        tablaUsuarios = new JTable();
        add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Getters para el controlador
    public JTable getTablaUsuarios() { return tablaUsuarios; }
    public JButton getBtnAgregar() { return btnAgregar; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnEliminar() { return btnEliminar; }
}
