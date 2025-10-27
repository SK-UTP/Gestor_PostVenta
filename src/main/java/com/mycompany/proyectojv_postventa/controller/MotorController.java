package com.mycompany.proyectojv_postventa.controller;

import com.mycompany.proyectojv_postventa.model.Cliente;
import com.mycompany.proyectojv_postventa.model.Motor;
import com.mycompany.proyectojv_postventa.view.MotorView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MotorController {
    private MotorView view;

    public MotorController(MotorView view) {
        this.view = view;
        init();
    }

    private void init() {
        cargarTabla();
        view.addRefrescarListener(e -> cargarTabla());
        view.addAgregarListener(e -> agregarMotor());
        view.addEditarListener(e -> editarMotor());
        view.addEliminarListener(e -> eliminarMotor());
        view.addCerrarListener(e -> view.dispose());
    }

    private void cargarTabla() {
        List<Motor> lista = Motor.obtenerTodos();
        String[] cols = {"ID","ID Cliente","Marca","Modelo","Nro Serie","Estado","Creado","Actualizado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Motor m : lista) {
            model.addRow(new Object[]{
                m.getId_motor(), m.getId_cliente(), m.getMarca(), m.getModelo(),
                m.getNumero_serie(), m.getEstado(),
                m.getCreated_at(), m.getUpdated_at()
            });
        }
        view.setTableModel(model);
    }

    private void agregarMotor() {
        MotorFormDialog dlg = new MotorFormDialog(null);
        dlg.cargarClientes(); // llena combo con clientes
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;
        Motor m = new Motor(0, dlg.getIdCliente(), dlg.getMarca(), dlg.getModelo(), dlg.getNumeroSerie(), dlg.getEstado());
        if (m.guardar()) {
            JOptionPane.showMessageDialog(view, "Motor agregado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al agregar motor.");
    }

    private void editarMotor() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un motor."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        int idCliente = (int) view.getTabla().getValueAt(fila, 1);
        String marca = (String) view.getTabla().getValueAt(fila, 2);
        String modelo = (String) view.getTabla().getValueAt(fila, 3);
        String nroSerie = (String) view.getTabla().getValueAt(fila, 4);
        String estado = (String) view.getTabla().getValueAt(fila, 5);

        MotorFormDialog dlg = new MotorFormDialog(view);
        dlg.cargarClientes();
        dlg.setForm(idCliente, marca, modelo, nroSerie, estado);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        Motor m = new Motor(id, dlg.getIdCliente(), dlg.getMarca(), dlg.getModelo(), dlg.getNumeroSerie(), dlg.getEstado());
        if (m.actualizar()) {
            JOptionPane.showMessageDialog(view, "Motor actualizado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al actualizar motor.");
    }

    private void eliminarMotor() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un motor."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        int ok = JOptionPane.showConfirmDialog(view, "Eliminar motor ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        Motor m = new Motor();
        m.setId_motor(id);
        if (m.eliminar()) {
            JOptionPane.showMessageDialog(view, "Motor eliminado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al eliminar motor.");
    }

    // Diálogo de formulario para agregar/editar motor
    private static class MotorFormDialog extends JDialog {
        private JComboBox<String> cbClientes; // formato "id - nombre"
        private JTextField txtMarca, txtModelo, txtNroSerie;
        private JComboBox<String> cbEstado;
        private boolean confirmed = false;

        MotorFormDialog(JFrame owner) {
            super(owner, "Formulario Motor", true);
            setSize(420, 300);
            setLocationRelativeTo(owner);
            setLayout(new java.awt.BorderLayout(8,8));

            JPanel center = new JPanel(new java.awt.GridLayout(5,2,8,8));
            center.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

            center.add(new JLabel("Cliente:")); cbClientes = new JComboBox<>(); center.add(cbClientes);
            center.add(new JLabel("Marca:")); txtMarca = new JTextField(); center.add(txtMarca);
            center.add(new JLabel("Modelo:")); txtModelo = new JTextField(); center.add(txtModelo);
            center.add(new JLabel("Nro Serie:")); txtNroSerie = new JTextField(); center.add(txtNroSerie);
            center.add(new JLabel("Estado:")); cbEstado = new JComboBox<>(new String[]{"activo","en_reparacion","dado_de_baja"}); center.add(cbEstado);

            add(center, java.awt.BorderLayout.CENTER);

            JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            JButton btnOk = new JButton("Guardar"); btnOk.addActionListener(e -> { confirmed = true; dispose(); });
            JButton btnCancel = new JButton("Cancelar"); btnCancel.addActionListener(e -> { confirmed = false; dispose(); });
            buttons.add(btnCancel); buttons.add(btnOk);
            add(buttons, java.awt.BorderLayout.SOUTH);
        }

        void cargarClientes() {
            cbClientes.removeAllItems();
            try {
                java.util.List<Cliente> lista = Cliente.obtenerTodos();
                for (Cliente c : lista) {
                    cbClientes.addItem(c.getId_cliente() + " - " + c.getNombre());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        void setForm(int idCliente, String marca, String modelo, String nroSerie, String estado) {
            // seleccionar cliente en combo
            String pref = idCliente + " - ";
            for (int i = 0; i < cbClientes.getItemCount(); i++) {
                String item = cbClientes.getItemAt(i);
                if (item.startsWith(pref)) { cbClientes.setSelectedIndex(i); break; }
            }
            txtMarca.setText(marca);
            txtModelo.setText(modelo);
            txtNroSerie.setText(nroSerie);
            cbEstado.setSelectedItem(estado);
        }

        int getIdCliente() {
            String sel = (String) cbClientes.getSelectedItem();
            if (sel == null) return 0;
            String[] parts = sel.split(" - ", 2);
            try { return Integer.parseInt(parts[0]); } catch (Exception e) { return 0; }
        }
        String getMarca() { return txtMarca.getText().trim(); }
        String getModelo() { return txtModelo.getText().trim(); }
        String getNumeroSerie() { return txtNroSerie.getText().trim(); }
        String getEstado() { return (String) cbEstado.getSelectedItem(); }
        boolean isConfirmed() { return confirmed; }
    }
}

