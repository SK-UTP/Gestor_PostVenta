package com.mycompany.proyectojv_postventa.controller;
import com.mycompany.proyectojv_postventa.model.Cliente;
import com.mycompany.proyectojv_postventa.view.ClienteView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*; // 🔹 ESTE ES CLAVE para GridLayout y BorderLayout
import java.util.List;
public class ClienteController {
    private ClienteView view;

    public ClienteController(ClienteView view) {
        this.view = view;
        init();
    }

    private void init() {
        cargarTabla();
        view.addRefrescarListener(e -> cargarTabla());
        view.addAgregarListener(e -> agregarCliente());
        view.addEditarListener(e -> editarCliente());
        view.addEliminarListener(e -> eliminarCliente());
        view.addCerrarListener(e -> view.dispose());
    }

    private void cargarTabla() {
        List<Cliente> lista = Cliente.obtenerTodos();
        String[] cols = {"ID", "Nombre", "Empresa", "Teléfono", "Correo", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Cliente c : lista) {
            model.addRow(new Object[]{
                c.getId_cliente(), c.getNombre(), c.getEmpresa(),
                c.getTelefono(), c.getCorreo(), c.getEstado()
            });
        }
        view.setTableModel(model);
    }

    private void agregarCliente() {
        ClienteFormDialog dlg = new ClienteFormDialog(null);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;
        Cliente c = new Cliente(0, dlg.getNombre(), dlg.getEmpresa(), dlg.getTelefono(), dlg.getCorreo(), dlg.getEstado());
        if (c.guardar()) {
            JOptionPane.showMessageDialog(view, "Cliente agregado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al agregar cliente.");
    }

    private void editarCliente() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un cliente."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        String nombre = (String) view.getTabla().getValueAt(fila, 1);
        String empresa = (String) view.getTabla().getValueAt(fila, 2);
        String telefono = (String) view.getTabla().getValueAt(fila, 3);
        String correo = (String) view.getTabla().getValueAt(fila, 4);
        String estado = (String) view.getTabla().getValueAt(fila, 5);

        ClienteFormDialog dlg = new ClienteFormDialog(view);
        dlg.setForm(nombre, empresa, telefono, correo, estado);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        Cliente c = new Cliente(id, dlg.getNombre(), dlg.getEmpresa(), dlg.getTelefono(), dlg.getCorreo(), dlg.getEstado());
        if (c.actualizar()) {
            JOptionPane.showMessageDialog(view, "Cliente actualizado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al actualizar cliente.");
    }

    private void eliminarCliente() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un cliente."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        int ok = JOptionPane.showConfirmDialog(view, "Eliminar cliente ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        Cliente c = new Cliente();
        c.setId_cliente(id);
        if (c.eliminar()) {
            JOptionPane.showMessageDialog(view, "Cliente eliminado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al eliminar cliente.");
    }

    // Diálogo de formulario (interno al controlador para mantener simple el ejemplo)
    private static class ClienteFormDialog extends JDialog {
        private JTextField txtNombre, txtEmpresa, txtTelefono, txtCorreo;
        private JComboBox<String> cbEstado;
        private boolean confirmed = false;

        ClienteFormDialog(JFrame owner) {
            super(owner, "Formulario Cliente", true);
            setSize(420, 320);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(8,8));

            JPanel center = new JPanel(new GridLayout(5,2,8,8));
            center.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

            center.add(new JLabel("Nombre:")); txtNombre = new JTextField(); center.add(txtNombre);
            center.add(new JLabel("Empresa:")); txtEmpresa = new JTextField(); center.add(txtEmpresa);
            center.add(new JLabel("Teléfono:")); txtTelefono = new JTextField(); center.add(txtTelefono);
            center.add(new JLabel("Correo:")); txtCorreo = new JTextField(); center.add(txtCorreo);
            center.add(new JLabel("Estado:")); cbEstado = new JComboBox<>(new String[]{"activo","inactivo"}); center.add(cbEstado);

            add(center, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnOk = new JButton("Guardar"); btnOk.addActionListener(e -> { confirmed = true; dispose(); });
            JButton btnCancel = new JButton("Cancelar"); btnCancel.addActionListener(e -> { confirmed = false; dispose(); });
            buttons.add(btnCancel); buttons.add(btnOk);
            add(buttons, BorderLayout.SOUTH);
        }

        void setForm(String nombre, String empresa, String tel, String correo, String estado) {
            txtNombre.setText(nombre);
            txtEmpresa.setText(empresa);
            txtTelefono.setText(tel);
            txtCorreo.setText(correo);
            cbEstado.setSelectedItem(estado);
        }

        String getNombre() { return txtNombre.getText().trim(); }
        String getEmpresa() { return txtEmpresa.getText().trim(); }
        String getTelefono() { return txtTelefono.getText().trim(); }
        String getCorreo() { return txtCorreo.getText().trim(); }
        String getEstado() { return (String) cbEstado.getSelectedItem(); }
        boolean isConfirmed() { return confirmed; }
    }
}
