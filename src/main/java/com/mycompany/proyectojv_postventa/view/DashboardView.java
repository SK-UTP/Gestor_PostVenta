package com.mycompany.proyectojv_postventa.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * DashboardView moderno (tarjetas). Exponer listeners para cada módulo.
 */
public class DashboardView extends JFrame {
    private JButton btnUsuarios, btnClientes, btnMotores, btnServicios, btnCotizaciones, btnSalir;

    public DashboardView() {
        setTitle("Sistema ITC — Dashboard");
        setSize(960, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12,12));

        // Top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(34,49,63));
        top.setPreferredSize(new Dimension(0, 72));
        JLabel title = new JLabel("Panel Principal — Sistema ITC");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        top.add(title, BorderLayout.WEST);

        // Optional: user info area on top-right (placeholder)
        JLabel userInfo = new JLabel("Usuario: admin");
        userInfo.setForeground(Color.WHITE);
        userInfo.setBorder(BorderFactory.createEmptyBorder(0,0,0,16));
        top.add(userInfo, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // Center - grid of cards
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(245,247,249));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(18,18,18,18);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1; c.weighty = 1;

        btnUsuarios = makeCard("Usuarios", "Gestiona cuentas y roles", new Color(52,152,219));
        btnClientes = makeCard("Clientes", "Gestión de clientes y contactos", new Color(46,204,113));
        btnMotores = makeCard("Motores", "Registro y búsqueda de motores", new Color(155,89,182));
        btnServicios = makeCard("Servicios", "Registrar y controlar servicios", new Color(241,196,15));
        btnCotizaciones = makeCard("Cotizaciones", "Administrar cotizaciones", new Color(230,126,34));

        c.gridx = 0; c.gridy = 0;
        center.add(wrapCard(btnUsuarios), c);
        c.gridx = 1; c.gridy = 0;
        center.add(wrapCard(btnClientes), c);
        c.gridx = 0; c.gridy = 1;
        center.add(wrapCard(btnMotores), c);
        c.gridx = 1; c.gridy = 1;
        center.add(wrapCard(btnServicios), c);
        // add cotizaciones spanning full width below
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.weighty = 0.6;
        center.add(wrapCard(btnCotizaciones), c);
        c.gridwidth = 1; // reset

        add(center, BorderLayout.CENTER);

        // Bottom - logout / small footer
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(245,247,249));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(new Color(245,247,249));
        btnSalir = new JButton("Cerrar sesión");
        btnSalir.setBackground(new Color(231,76,60));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.setFocusPainted(false);
        right.add(btnSalir);
        bottom.add(right, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton makeCard(String title, String subtitle, Color color) {
        JButton b = new JButton("<html><div style='text-align:left'>"
                + "<span style='font-size:16px; font-weight:bold;'>" + title + "</span><br>"
                + "<span style='font-size:12px;'>" + subtitle + "</span></div></html>");
        b.setVerticalAlignment(SwingConstants.CENTER);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        b.setFocusPainted(false);
        return b;
    }

    private JPanel wrapCard(JButton cardBtn) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245,247,249));
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBtn.getBackground());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200), 0),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        card.add(cardBtn, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(420, 160));
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // API de listeners para que el controlador conecte
    public void addUsuariosListener(ActionListener l) { btnUsuarios.addActionListener(l); }
    public void addClientesListener(ActionListener l) { btnClientes.addActionListener(l); }
    public void addMotoresListener(ActionListener l) { btnMotores.addActionListener(l); }
    public void addServiciosListener(ActionListener l) { btnServicios.addActionListener(l); }
    public void addCotizacionesListener(ActionListener l) { btnCotizaciones.addActionListener(l); }
    public void addSalirListener(ActionListener l) { btnSalir.addActionListener(l); }
}
