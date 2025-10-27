package com.mycompany.proyectojv_postventa;

import com.mycompany.proyectojv_postventa.model.usuario;
import com.mycompany.proyectojv_postventa.view.LoginView;
import com.mycompany.proyectojv_postventa.controller.LoginController;

public class Main {
    public static void main(String[] args) {
        LoginView view = new LoginView();
        usuario model = new usuario();
        LoginController controller = new LoginController(view, model);
    }
}
