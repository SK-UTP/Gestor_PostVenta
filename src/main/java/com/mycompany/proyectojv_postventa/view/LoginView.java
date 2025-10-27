package com.mycompany.proyectojv_postventa.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * LoginView moderno (Swing puro).
 * - Mantiene: getUsuario(), getPassword(), addLoginListener(ActionListener)
 * - Presionar Enter activa el botón (setDefaultButton).
 */
public class LoginView extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginView() {
        setTitle("Sistema ITC — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel fondo con degradado
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Color c1 = new Color(20, 132, 194);
                Color c2 = new Color(44, 62, 80);
                g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // Tarjeta blanca centro
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(340, 220));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 8, 8, 8);

        // Logo opcional: si tienes logo en resources/logo.png, descomenta
        /*
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/logo.png"));
            JLabel lblLogo = new JLabel(icon);
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
            c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
            card.add(lblLogo, c);
        } catch (Exception ex) {
            // no hay logo
        }
        */

        // Título
        JLabel lblTitle = new JLabel("ACCESO SISTEMA ITC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(34, 49, 63));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(lblTitle, c);

        // Usuario
        JLabel lUser = new JLabel("Usuario:");
        lUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lUser.setForeground(new Color(70, 80, 90));
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1; c.weightx = 0.3;
        card.add(lUser, c);

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        c.gridx = 1; c.gridy = 1; c.weightx = 0.7;
        card.add(txtUsuario, c);

        // Contraseña
        JLabel lPass = new JLabel("Contraseña:");
        lPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lPass.setForeground(new Color(70, 80, 90));
        c.gridx = 0; c.gridy = 2; c.weightx = 0.3;
        card.add(lPass, c);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        c.gridx = 1; c.gridy = 2; c.weightx = 0.7;
        card.add(txtPassword, c);

        // Botón ingresar (redondeado)
        btnIngresar = new RoundedButton("Ingresar");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setPreferredSize(new Dimension(120, 36));
        btnIngresar.setBackground(new Color(42, 183, 122));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.weightx = 1; c.anchor = GridBagConstraints.CENTER;
        card.add(btnIngresar, c);

        background.add(card);

        // Enter activa boton
        getRootPane().setDefaultButton(btnIngresar);

        // mostrar
        setVisible(true);
    }

    // Getters y API para controlador (se mantienen)
    public String getUsuario() { return txtUsuario.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }

    public void addLoginListener(ActionListener listener) { btnIngresar.addActionListener(listener); }

    public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }

    // Clase interna: boton redondeado simple
    private static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            int width = getWidth(); int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // fondo
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, width, height, 18, 18);

            // texto
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {
            // sin borde
        }
    }
}
