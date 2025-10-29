package com.mycompany.proyectojv_postventa.controller;

import com.mycompany.proyectojv_postventa.model.Usuario;
import com.mycompany.proyectojv_postventa.view.LoginView;
import com.mycompany.proyectojv_postventa.view.DashboardView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// importar el controlador del dashboard
import com.mycompany.proyectojv_postventa.controller.DashBoardController;


public class LoginController {
    private LoginView view;
    private Usuario model;

    public LoginController(LoginView view, Usuario model) {
        this.view = view;
        this.model = model;

        // Conectar botón y Enter
        this.view.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        String usuarioInput = view.getUsuario();
        String passwordInput = view.getPassword();

        model.setUsuario(usuarioInput);
        model.setPassword(passwordInput);

        if (model.validar()) {
            view.mostrarMensaje("¡Login correcto!");
            view.dispose(); // cerrar ventana login

            // Crear vista + controlador del dashboard (así los botones responden)
           DashboardView dashboard = new DashboardView();
            new DashBoardController(dashboard);
            // la vista ya se muestra en su propio constructor; si no, puedes forzar:
            // dashboard.setVisible(true);
        } else {
            view.mostrarMensaje("Usuario o contraseña incorrectos");
        }
    }
}
