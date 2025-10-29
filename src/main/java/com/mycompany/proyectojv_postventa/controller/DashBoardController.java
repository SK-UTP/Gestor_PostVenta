package com.mycompany.proyectojv_postventa.controller;

import com.mycompany.proyectojv_postventa.view.DashboardView;
import com.mycompany.proyectojv_postventa.view.ClienteView;
import com.mycompany.proyectojv_postventa.controller.ClienteController;

import javax.swing.*;
import java.lang.reflect.Constructor;

/**
 * DashboardController robusto: abre Clientes, Motores, Usuarios, Servicios y Cotizaciones.
 * Si la apertura directa falla, intenta varias alternativas por reflexión
 * para diagnosticar problemas de nombres/compilación.
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

        view.addUsuariosListener(e -> {
            System.out.println("DashboardController: click en Usuarios");
            abrirUsuarios();
        });

        view.addServiciosListener(e -> {
            System.out.println("DashboardController: click en Servicios");
            abrirServicios();
        });

        // <-- cambiado: ahora abre Cotizaciones
        view.addCotizacionesListener(e -> {
            System.out.println("DashboardController: click en Cotizaciones");
            abrirCotizaciones();
        });

        view.addSalirListener(e -> salir());
    }

    private void abrirClientes() {
        try {
            System.out.println("abrirClientes(): creando ClienteView/Controller");
            JOptionPane.showMessageDialog(view, "Intentando abrir módulo Clientes...", "DEBUG", JOptionPane.INFORMATION_MESSAGE);
            ClienteView cv = new ClienteView();
            new ClienteController(cv);
            System.out.println("abrirClientes(): OK");
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error al abrir Clientes:\n" + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirMotores() {
        try {
            System.out.println("abrirMotores(): creando MotorView/Controller");
            JOptionPane.showMessageDialog(view, "Intentando abrir módulo Motores...", "DEBUG", JOptionPane.INFORMATION_MESSAGE);
            com.mycompany.proyectojv_postventa.view.MotorView mv = new com.mycompany.proyectojv_postventa.view.MotorView();
            new com.mycompany.proyectojv_postventa.controller.MotorController(mv);
            System.out.println("abrirMotores(): OK");
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error al abrir Motores:\n" + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirUsuarios() {
        // 1) Intento directo (lo normal)
        try {
            System.out.println("abrirUsuarios(): intento directo");
            JOptionPane.showMessageDialog(view, "Intentando abrir módulo Usuarios...", "DEBUG", JOptionPane.INFORMATION_MESSAGE);
            com.mycompany.proyectojv_postventa.view.UsuarioView uv = new com.mycompany.proyectojv_postventa.view.UsuarioView();
            new com.mycompany.proyectojv_postventa.controller.UsuarioController(uv);
            System.out.println("abrirUsuarios(): apertura directa OK");
            return;
        } catch (Throwable t) {
            System.out.println("abrirUsuarios(): apertura directa FALLÓ -> " + t.toString());
            t.printStackTrace();
        }

        // 2) Intento por reflexión (varias variantes)
        String[] posiblesViews = new String[] {
            "com.mycompany.proyectojv_postventa.view.UsuarioView",
            "com.mycompany.proyectojv_postventa.view.usuarioview",
            "com.mycompany.proyectojv_postventa.view.usuarioviewold",
            "com.mycompany.proyectojv_postventa.view.UsuarioviewOld",
            "com.mycompany.proyectojv_postventa.view.Usuarioview"
        };
        String[] posiblesControllers = new String[] {
            "com.mycompany.proyectojv_postventa.controller.UsuarioController",
            "com.mycompany.proyectojv_postventa.controller.usuarioController",
            "com.mycompany.proyectojv_postventa.controller.Usuariocontroller"
        };

        for (String vname : posiblesViews) {
            try {
                System.out.println("abrirUsuarios(): probando clase view -> " + vname);
                Class<?> vclass = Class.forName(vname);
                Constructor<?> vctor = vclass.getDeclaredConstructor();
                vctor.setAccessible(true);
                Object vobj = vctor.newInstance();

                for (String cname : posiblesControllers) {
                    try {
                        System.out.println("abrirUsuarios(): probando controller -> " + cname);
                        Class<?> cclass = Class.forName(cname);
                        for (Constructor<?> cc : cclass.getConstructors()) {
                            Class<?>[] params = cc.getParameterTypes();
                            if (params.length == 1 && params[0].isAssignableFrom(vclass)) {
                                cc.newInstance(vobj);
                                JOptionPane.showMessageDialog(view, "Abierto módulo Usuarios (reflexión) con " + vname + " / " + cname, "OK", JOptionPane.INFORMATION_MESSAGE);
                                System.out.println("abrirUsuarios(): OK via reflexión con " + vname + " / " + cname);
                                return;
                            }
                        }
                    } catch (ClassNotFoundException cnf) {
                        System.out.println("abrirUsuarios(): controller no encontrado: " + cname);
                    }
                }

                JOptionPane.showMessageDialog(view, "Se creó la vista " + vname + " pero no se encontró controlador compatible.\nComprueba clases.", "Aviso", JOptionPane.WARNING_MESSAGE);
                System.out.println("abrirUsuarios(): vista creada sin controller para " + vname);
                return;
            } catch (ClassNotFoundException cnf) {
                System.out.println("abrirUsuarios(): clase view no encontrada: " + vname);
            } catch (Throwable ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error al intentar abrir (reflexión) " + vname + ":\n" + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String mensaje =
            "No se pudo abrir el módulo Usuarios.\n" +
            "Comprueba:\n" +
            " - Que exista la clase com.mycompany.proyectojv_postventa.view.UsuarioView\n" +
            " - Que exista la clase com.mycompany.proyectojv_postventa.controller.UsuarioController\n" +
            " - Que hayas hecho Run → Clean and Build en NetBeans\n" +
            " - Que no queden archivos duplicados (usuarioview/UsuarioView con distinto case)\n\n" +
            "Mira la consola (Output) para la traza completa.";
        JOptionPane.showMessageDialog(view, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * abrirServicios() — intenta abrir ServicioView/ServicioController
     */
    private void abrirServicios() {
        try {
            System.out.println("abrirServicios(): intento directo");
            JOptionPane.showMessageDialog(view, "Intentando abrir módulo Servicios...", "DEBUG", JOptionPane.INFORMATION_MESSAGE);
            com.mycompany.proyectojv_postventa.view.ServicioView sv = new com.mycompany.proyectojv_postventa.view.ServicioView();
            new com.mycompany.proyectojv_postventa.controller.ServicioController(sv);
            System.out.println("abrirServicios(): apertura directa OK");
            return;
        } catch (Throwable t) {
            System.out.println("abrirServicios(): apertura directa FALLÓ -> " + t.toString());
            t.printStackTrace();
        }

        String[] posiblesViews = new String[] {
            "com.mycompany.proyectojv_postventa.view.ServicioView",
            "com.mycompany.proyectojv_postventa.view.servicioview",
            "com.mycompany.proyectojv_postventa.view.ServicioViewOld",
            "com.mycompany.proyectojv_postventa.view.servicioviewold"
        };
        String[] posiblesControllers = new String[] {
            "com.mycompany.proyectojv_postventa.controller.ServicioController",
            "com.mycompany.proyectojv_postventa.controller.serviciocontroller"
        };

        for (String vname : posiblesViews) {
            try {
                System.out.println("abrirServicios(): probando clase view -> " + vname);
                Class<?> vclass = Class.forName(vname);
                Constructor<?> vctor = vclass.getDeclaredConstructor();
                vctor.setAccessible(true);
                Object vobj = vctor.newInstance();

                for (String cname : posiblesControllers) {
                    try {
                        System.out.println("abrirServicios(): probando controller -> " + cname);
                        Class<?> cclass = Class.forName(cname);
                        for (Constructor<?> cc : cclass.getConstructors()) {
                            Class<?>[] params = cc.getParameterTypes();
                            if (params.length == 1 && params[0].isAssignableFrom(vclass)) {
                                cc.newInstance(vobj);
                                JOptionPane.showMessageDialog(view, "Abierto módulo Servicios (reflexión) con " + vname + " / " + cname, "OK", JOptionPane.INFORMATION_MESSAGE);
                                System.out.println("abrirServicios(): OK via reflexión con " + vname + " / " + cname);
                                return;
                            }
                        }
                    } catch (ClassNotFoundException cnf) {
                        System.out.println("abrirServicios(): controller no encontrado: " + cname);
                    }
                }

                JOptionPane.showMessageDialog(view, "Se creó la vista " + vname + " pero no se encontró controlador compatible.\nComprueba clases.", "Aviso", JOptionPane.WARNING_MESSAGE);
                System.out.println("abrirServicios(): vista creada sin controller para " + vname);
                return;
            } catch (ClassNotFoundException cnf) {
                System.out.println("abrirServicios(): clase view no encontrada: " + vname);
            } catch (Throwable ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error al intentar abrir (reflexión) " + vname + ":\n" + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String mensaje =
            "No se pudo abrir el módulo Servicios.\n" +
            "Comprueba:\n" +
            " - Que exista la clase com.mycompany.proyectojv_postventa.view.ServicioView\n" +
            " - Que exista la clase com.mycompany.proyectojv_postventa.controller.ServicioController\n" +
            " - Que hayas hecho Run → Clean and Build en NetBeans\n" +
            " - Que no queden archivos duplicados (servicioview/ServicioView con distinto case)\n\n" +
            "Mira la consola (Output) para la traza completa.";
        JOptionPane.showMessageDialog(view, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Nuevo: abrirCotizaciones() — intenta abrir CotizacionView/CotizacionController
     */
    private void abrirCotizaciones() {
        // intento directo
        try {
            System.out.println("abrirCotizaciones(): intento directo");
            JOptionPane.showMessageDialog(view, "Intentando abrir módulo Cotizaciones...", "DEBUG", JOptionPane.INFORMATION_MESSAGE);
            com.mycompany.proyectojv_postventa.view.CotizacionView cv = new com.mycompany.proyectojv_postventa.view.CotizacionView();
            new com.mycompany.proyectojv_postventa.controller.CotizacionController(cv);
            System.out.println("abrirCotizaciones(): apertura directa OK");
            return;
        } catch (Throwable t) {
            System.out.println("abrirCotizaciones(): apertura directa FALLÓ -> " + t.toString());
            t.printStackTrace();
            // seguir con reflexión
        }

        // intentos por reflexión
        String[] posiblesViews = new String[] {
            "com.mycompany.proyectojv_postventa.view.CotizacionView",
            "com.mycompany.proyectojv_postventa.view.cotizacionview",
            "com.mycompany.proyectojv_postventa.view.CotizacionViewOld",
            "com.mycompany.proyectojv_postventa.view.cotizacionviewold"
        };
        String[] posiblesControllers = new String[] {
            "com.mycompany.proyectojv_postventa.controller.CotizacionController",
            "com.mycompany.proyectojv_postventa.controller.cotizacioncontroller"
        };

        for (String vname : posiblesViews) {
            try {
                System.out.println("abrirCotizaciones(): probando clase view -> " + vname);
                Class<?> vclass = Class.forName(vname);
                Constructor<?> vctor = vclass.getDeclaredConstructor();
                vctor.setAccessible(true);
                Object vobj = vctor.newInstance();

                for (String cname : posiblesControllers) {
                    try {
                        System.out.println("abrirCotizaciones(): probando controller -> " + cname);
                        Class<?> cclass = Class.forName(cname);
                        for (Constructor<?> cc : cclass.getConstructors()) {
                            Class<?>[] params = cc.getParameterTypes();
                            if (params.length == 1 && params[0].isAssignableFrom(vclass)) {
                                cc.newInstance(vobj);
                                JOptionPane.showMessageDialog(view, "Abierto módulo Cotizaciones (reflexión) con " + vname + " / " + cname, "OK", JOptionPane.INFORMATION_MESSAGE);
                                System.out.println("abrirCotizaciones(): OK via reflexión con " + vname + " / " + cname);
                                return;
                            }
                        }
                    } catch (ClassNotFoundException cnf) {
                        System.out.println("abrirCotizaciones(): controller no encontrado: " + cname);
                    }
                }

                // si la view se instanció pero no hay controller compatible
                JOptionPane.showMessageDialog(view, "Se creó la vista " + vname + " pero no se encontró controlador compatible.\nComprueba clases.", "Aviso", JOptionPane.WARNING_MESSAGE);
                System.out.println("abrirCotizaciones(): vista creada sin controller para " + vname);
                return;
            } catch (ClassNotFoundException cnf) {
                System.out.println("abrirCotizaciones(): clase view no encontrada: " + vname);
            } catch (Throwable ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error al intentar abrir (reflexión) " + vname + ":\n" + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String mensaje =
            "No se pudo abrir el módulo Cotizaciones.\n" +
            "Comprueba:\n" +
            " - Que exista la clase com.mycompany.proyectojv_postventa.view.CotizacionView\n" +
            " - Que exista la clase com.mycompany.proyectojv_postventa.controller.CotizacionController\n" +
            " - Que hayas hecho Run → Clean and Build en NetBeans\n" +
            " - Que no queden archivos duplicados (cotizacionview/CotizacionView con distinto case)\n\n" +
            "Mira la consola (Output) para la traza completa.";
        JOptionPane.showMessageDialog(view, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
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
