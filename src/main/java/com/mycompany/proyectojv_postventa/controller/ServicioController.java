package com.mycompany.proyectojv_postventa.controller;

import com.mycompany.proyectojv_postventa.model.Servicio;
import com.mycompany.proyectojv_postventa.model.Motor;
import com.mycompany.proyectojv_postventa.model.Usuario;
import com.mycompany.proyectojv_postventa.view.ServicioView;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ServicioController {
    private ServicioView view;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ServicioController(ServicioView view) {
        this.view = view;
        init();
    }

    private void init() {
        cargarTabla();
        view.addRefrescarListener(e -> cargarTabla());
        view.addAgregarListener(e -> agregarServicio());
        view.addEditarListener(e -> editarServicio());
        view.addEliminarListener(e -> eliminarServicio());
        view.addCerrarListener(e -> view.dispose());
    }

    private void cargarTabla() {
        List<Servicio> lista = Servicio.obtenerTodos();
        String[] cols = {"ID","ID Motor","Nro Serie Motor","ID Mecánico","Mecánico","Tipo","Fecha Inicio","Fecha Fin","Estado","Observaciones"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Para obtener datos legibles (motor.numero_serie, mecanico.nombre) hacemos consultas auxiliares
        for (Servicio s : lista) {
            String nroSerie = "";
            String mecanicoNombre = "";
            try {
                // buscar motor por id (reutiliza Motor.obtenerTodos para buscar — si tienes método mejor, reemplaza)
                for (Motor m : Motor.obtenerTodos()) {
                    if (m.getId_motor() == s.getId_motor()) {
                        nroSerie = m.getNumero_serie();
                        break;
                    }
                }
                if (s.getId_mecanico() != null) {
                    for (Usuario u : Usuario.obtenerTodos()) {
                        if (u.getId_usuario() == s.getId_mecanico()) {
                            mecanicoNombre = u.getNombre();
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            model.addRow(new Object[]{
                s.getId_servicio(), s.getId_motor(), nroSerie, s.getId_mecanico(), mecanicoNombre,
                s.getTipo(),
                s.getFecha_inicio() == null ? "" : fmt.format(s.getFecha_inicio()),
                s.getFecha_fin() == null ? "" : fmt.format(s.getFecha_fin()),
                s.getEstado(), s.getObservaciones()
            });
        }

        view.setTableModel(model);
    }

    private void agregarServicio() {
        ServicioFormDialog dlg = new ServicioFormDialog(null);
        dlg.cargarCombos(); // llena combo de motores y mecanicos
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        Servicio s = new Servicio();
        s.setId_motor(dlg.getIdMotor());
        s.setId_mecanico(dlg.getIdMecanico());
        s.setTipo(dlg.getTipo());
        s.setFecha_inicio(parseTimestamp(dlg.getFechaInicio()));
        s.setFecha_fin(parseTimestamp(dlg.getFechaFin()));
        s.setEstado(dlg.getEstado());
        s.setObservaciones(dlg.getObservaciones());

        if (s.guardar()) {
            JOptionPane.showMessageDialog(view, "Servicio agregado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al agregar servicio.");
    }

    private void editarServicio() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un servicio."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        int idMotor = (int) view.getTabla().getValueAt(fila, 1);
        Integer idMecanico = (Integer) view.getTabla().getValueAt(fila, 3);
        String tipo = (String) view.getTabla().getValueAt(fila, 5);
        String fechaInicio = (String) view.getTabla().getValueAt(fila, 6);
        String fechaFin = (String) view.getTabla().getValueAt(fila, 7);
        String estado = (String) view.getTabla().getValueAt(fila, 8);
        String obs = (String) view.getTabla().getValueAt(fila, 9);

        ServicioFormDialog dlg = new ServicioFormDialog(view);
        dlg.cargarCombos();
        dlg.setForm(idMotor, idMecanico, tipo, fechaInicio, fechaFin, estado, obs);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        Servicio s = new Servicio(id, dlg.getIdMotor(), dlg.getIdMecanico(), dlg.getTipo(),
                                  parseTimestamp(dlg.getFechaInicio()), parseTimestamp(dlg.getFechaFin()),
                                  dlg.getEstado(), dlg.getObservaciones());
        if (s.actualizar()) {
            JOptionPane.showMessageDialog(view, "Servicio actualizado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al actualizar servicio.");
    }

    private void eliminarServicio() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione un servicio."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        int ok = JOptionPane.showConfirmDialog(view, "Eliminar servicio ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        Servicio s = new Servicio();
        s.setId_servicio(id);
        if (s.eliminar()) {
            JOptionPane.showMessageDialog(view, "Servicio eliminado.");
            cargarTabla();
        } else JOptionPane.showMessageDialog(view, "Error al eliminar servicio.");
    }

    // parse helper
    private Timestamp parseTimestamp(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            java.util.Date d = fmt.parse(s.trim());
            return new Timestamp(d.getTime());
        } catch (ParseException e) {
            // if fails, try ISO-friendly parse
            try {
                return Timestamp.valueOf(s.trim());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    // Diálogo del formulario
    private static class ServicioFormDialog extends JDialog {
        private JComboBox<String> cbMotores; // "id - nroSerie"
        private JComboBox<String> cbMecanicos; // "id - nombre"
        private JComboBox<String> cbTipo;
        private JTextField txtFechaInicio, txtFechaFin;
        private JComboBox<String> cbEstado;
        private JTextArea taObservaciones;
        private boolean confirmed = false;

        ServicioFormDialog(JFrame owner) {
            super(owner, "Formulario Servicio", true);
            setSize(520, 420);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(8,8));

            JPanel center = new JPanel(new GridLayout(6,2,8,8));
            center.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

            center.add(new JLabel("Motor:")); cbMotores = new JComboBox<>(); center.add(cbMotores);
            center.add(new JLabel("Mecánico (opcional):")); cbMecanicos = new JComboBox<>(); center.add(cbMecanicos);
            center.add(new JLabel("Tipo:")); cbTipo = new JComboBox<>(new String[]{"mantenimiento","reparacion","inspeccion"}); center.add(cbTipo);
            center.add(new JLabel("Fecha Inicio (yyyy-MM-dd HH:mm:ss):")); txtFechaInicio = new JTextField(); center.add(txtFechaInicio);
            center.add(new JLabel("Fecha Fin (yyyy-MM-dd HH:mm:ss):")); txtFechaFin = new JTextField(); center.add(txtFechaFin);
            center.add(new JLabel("Estado:")); cbEstado = new JComboBox<>(new String[]{"planificado","en_curso","finalizado","cancelado"}); center.add(cbEstado);

            add(center, BorderLayout.NORTH);

            JPanel obsPanel = new JPanel(new BorderLayout());
            obsPanel.setBorder(BorderFactory.createTitledBorder("Observaciones"));
            taObservaciones = new JTextArea(6,40);
            obsPanel.add(new JScrollPane(taObservaciones), BorderLayout.CENTER);
            add(obsPanel, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnOk = new JButton("Guardar"); btnOk.addActionListener(e -> { confirmed = true; dispose(); });
            JButton btnCancel = new JButton("Cancelar"); btnCancel.addActionListener(e -> { confirmed = false; dispose(); });
            buttons.add(btnCancel); buttons.add(btnOk);
            add(buttons, BorderLayout.SOUTH);
        }

        void cargarCombos() {
            cbMotores.removeAllItems();
            cbMecanicos.removeAllItems();
            try {
                for (Motor m : Motor.obtenerTodos()) {
                    cbMotores.addItem(m.getId_motor() + " - " + m.getNumero_serie());
                }
                for (Usuario u : Usuario.obtenerTodos()) {
                    cbMecanicos.addItem(u.getId_usuario() + " - " + u.getNombre());
                }
                // allow empty mecánico
                cbMecanicos.insertItemAt("0 - Ninguno", 0);
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        void setForm(int idMotor, Integer idMecanico, String tipo, String fechaInicio, String fechaFin, String estado, String obs) {
            String pref = idMotor + " - ";
            for (int i = 0; i < cbMotores.getItemCount(); i++) {
                if (cbMotores.getItemAt(i).startsWith(pref)) { cbMotores.setSelectedIndex(i); break; }
            }
            if (idMecanico == null) cbMecanicos.setSelectedIndex(0);
            else {
                String prefM = idMecanico + " - ";
                for (int i = 0; i < cbMecanicos.getItemCount(); i++) {
                    if (cbMecanicos.getItemAt(i).startsWith(prefM)) { cbMecanicos.setSelectedIndex(i); break; }
                }
            }
            cbTipo.setSelectedItem(tipo);
            txtFechaInicio.setText(fechaInicio);
            txtFechaFin.setText(fechaFin);
            cbEstado.setSelectedItem(estado);
            taObservaciones.setText(obs);
        }

        int getIdMotor() {
            String s = (String) cbMotores.getSelectedItem();
            if (s == null) return 0;
            return Integer.parseInt(s.split(" - ",2)[0]);
        }
        Integer getIdMecanico() {
            String s = (String) cbMecanicos.getSelectedItem();
            if (s == null) return null;
            int id = Integer.parseInt(s.split(" - ",2)[0]);
            return id == 0 ? null : id;
        }
        String getTipo() { return (String) cbTipo.getSelectedItem(); }
        String getFechaInicio() { return txtFechaInicio.getText().trim(); }
        String getFechaFin() { return txtFechaFin.getText().trim(); }
        String getEstado() { return (String) cbEstado.getSelectedItem(); }
        String getObservaciones() { return taObservaciones.getText().trim(); }
        boolean isConfirmed() { return confirmed; }
    }
}
