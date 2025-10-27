package com.mycompany.proyectojv_postventa.controller;

import com.mycompany.proyectojv_postventa.model.UsuarioModel;
import com.mycompany.proyectojv_postventa.view.UsuarioView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class UsuarioController {
    private UsuarioView view;

    public UsuarioController(UsuarioView view) {
        this.view = view;
        init();
    }

    private void init() {
        cargarTabla();
        view.addRefrescarListener(e -> cargarTabla());
        view.addAgregarListener(e -> agregarUsuario());
        view.addEditarListener(e -> editarUsuario());
        view.addEliminarListener(e -> eliminarUsuario());
        view.addCerrarListener(e -> view.dispose());
    }

    private void cargarTabla() {
        List<UsuarioModel> lista = UsuarioModel.obtenerTodos();
        String[] cols = {"ID","Nombre","Usuario","PasswordHash","ID_Rol","Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (UsuarioModel u : lista) {
            model.addRow(new Object[]{
                    u.getId_usuario(), u.getNombre(), u.getUsuario(),
                    u.getPassword_hash(), u.getId_rol(), u.getEstado()
            });
        }
        view.setTableModel(model);
    }

    private void agregarUsuario() {
        UsuarioFormDialog dlg = new UsuarioFormDialog(null);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;
        UsuarioModel u = new UsuarioModel(0, dlg.getNombre(), dlg.getUsuario(), dlg.getPassword(), dlg.getIdRol(), dlg.getEstado());
        if (u.guardar()) {
            JOptionPane.showMessageDialog(view, "Usuario agregado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al agregar usuario.");
    }

    private void editarUsuario() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un usuario."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        String nombre = (String) view.getTabla().getValueAt(fila, 1);
        String usuario = (String) view.getTabla().getValueAt(fila, 2);
        String password = (String) view.getTabla().getValueAt(fila, 3);
        int idRol = (int) view.getTabla().getValueAt(fila, 4);
        String estado = (String) view.getTabla().getValueAt(fila, 5);

        UsuarioFormDialog dlg = new UsuarioFormDialog(view);
        dlg.setForm(nombre, usuario, password, idRol, estado);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        UsuarioModel u = new UsuarioModel(id, dlg.getNombre(), dlg.getUsuario(), dlg.getPassword(), dlg.getIdRol(), dlg.getEstado());
        if (u.actualizar()) {
            JOptionPane.showMessageDialog(view, "Usuario actualizado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al actualizar usuario.");
    }

    private void eliminarUsuario() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un usuario."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        int ok = JOptionPane.showConfirmDialog(view, "Eliminar usuario ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        UsuarioModel u = new UsuarioModel();
        u.setId_usuario(id);
        if (u.eliminar()) {
            JOptionPane.showMessageDialog(view, "Usuario eliminado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al eliminar usuario.");
    }

    // Diálogo de formulario para agregar/editar usuario
    private static class UsuarioFormDialog extends JDialog {
        private JTextField txtNombre, txtUsuario, txtPassword;
        private JComboBox<Integer> cbIdRol;
        private JComboBox<String> cbEstado;
        private boolean confirmed = false;

        UsuarioFormDialog(JFrame owner) {
            super(owner, "Formulario Usuario", true);
            setSize(420, 320);
            setLocationRelativeTo(owner);
            setLayout(new java.awt.BorderLayout(8,8));

            JPanel center = new JPanel(new java.awt.GridLayout(5,2,8,8));
            center.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

            center.add(new JLabel("Nombre:")); txtNombre = new JTextField(); center.add(txtNombre);
            center.add(new JLabel("Usuario:")); txtUsuario = new JTextField(); center.add(txtUsuario);
            center.add(new JLabel("Password:")); txtPassword = new JTextField(); center.add(txtPassword);
            center.add(new JLabel("ID Rol:")); cbIdRol = new JComboBox<>(new Integer[]{1,2,3}); center.add(cbIdRol);
            center.add(new JLabel("Estado:")); cbEstado = new JComboBox<>(new String[]{"activo","inactivo"}); center.add(cbEstado);

            add(center, java.awt.BorderLayout.CENTER);

            JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            JButton btnOk = new JButton("Guardar"); btnOk.addActionListener(e -> { confirmed = true; dispose(); });
            JButton btnCancel = new JButton("Cancelar"); btnCancel.addActionListener(e -> { confirmed = false; dispose(); });
            buttons.add(btnCancel); buttons.add(btnOk);
            add(buttons, java.awt.BorderLayout.SOUTH);
        }

        void setForm(String nombre, String usuario, String password, int idRol, String estado) {
            txtNombre.setText(nombre);
            txtUsuario.setText(usuario);
            txtPassword.setText(password);
            cbIdRol.setSelectedItem(idRol);
            cbEstado.setSelectedItem(estado);
        }

        String getNombre() { return txtNombre.getText().trim(); }
        String getUsuario() { return txtUsuario.getText().trim(); }
        String getPassword() { return txtPassword.getText().trim(); }
        int getIdRol() { return (Integer) cbIdRol.getSelectedItem(); }
        String getEstado() { return (String) cbEstado.getSelectedItem(); }
        boolean isConfirmed() { return confirmed; }
    }
}
