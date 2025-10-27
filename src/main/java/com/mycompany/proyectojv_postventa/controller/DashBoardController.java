package com.mycompany.proyectojv_postventa.controller;

import com.mycompany.proyectojv_postventa.view.DashboardView;
import com.mycompany.proyectojv_postventa.view.ClienteView;
import com.mycompany.proyectojv_postventa.controller.ClienteController;
import javax.swing.*;

/**
 * DashboardController: gestiona la apertura de módulos (Clientes y Motores).
 */
public class DashBoardController {
    private DashboardView view;

    public DashBoardController(DashboardView view) {
        this.view = view;
        initListeners();
    }

    private void initListeners() {
        System.out.println("DashboardController: initListeners() llamado");

        view.addClientesListener(e -> {
            System.out.println("DashboardController: click en Clientes");
            abrirClientes();
        });

        view.addMotoresListener(e -> {
            System.out.println("DashboardController: click en Motores");
            abrirMotores();
        });

        view.addUsuariosListener(e -> mostrarNoImplementado("Usuarios"));
        view.addServiciosListener(e -> mostrarNoImplementado("Servicios"));
        view.addCotizacionesListener(e -> mostrarNoImplementado("Cotizaciones"));
        view.addSalirListener(e -> salir());
    }

    private void abrirClientes() {
        System.out.println("DashboardController: abrirClientes() iniciado");
        try {
            JOptionPane.showMessageDialog(view, "Intentando abrir módulo Clientes...", "DEBUG", JOptionPane.INFORMATION_MESSAGE);
            ClienteView cv = new ClienteView();
            new ClienteController(cv);
            System.out.println("DashboardController: ClienteView y ClienteController creados OK");
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(view, "Error al abrir Clientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void abrirMotores() {
        System.out.println("DashboardController: abrirMotores() iniciado");
        try {
            JOptionPane.showMessageDialog(view, "Intentando abrir módulo Motores...", "DEBUG", JOptionPane.INFORMATION_MESSAGE);
            // usamos nombres fully-qualified para evitar errores de import
            com.mycompany.proyectojv_postventa.view.MotorView mv = new com.mycompany.proyectojv_postventa.view.MotorView();
            new com.mycompany.proyectojv_postventa.controller.MotorController(mv);
            System.out.println("DashboardController: MotorView y MotorController creados OK");
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(view, "Error al abrir Motores: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void mostrarNoImplementado(String nombreModulo) {
        JOptionPane.showMessageDialog(view,
                "El módulo \"" + nombreModulo + "\" está en desarrollo.\nSe habilitará en próximas entregas.",
                "Módulo en desarrollo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void salir() {
        int ok = JOptionPane.showConfirmDialog(view, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            view.dispose();
        }
    }
}
