package com.mycompany.proyectojv_postventa.controller;

import com.mycompany.proyectojv_postventa.model.Cotizacion;
import com.mycompany.proyectojv_postventa.model.DetalleCotizacion;
import com.mycompany.proyectojv_postventa.model.Cliente; // asumo que ya tienes este modelo
import com.mycompany.proyectojv_postventa.view.CotizacionView;
import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CotizacionController {
    private CotizacionView view;

    public CotizacionController(CotizacionView view) {
        this.view = view;
        init();
    }

    private void init() {
        cargarTabla();
        view.addRefrescarListener(e -> cargarTabla());
        view.addAgregarListener(e -> agregarCotizacion());
        view.addEditarListener(e -> editarCotizacion());
        view.addEliminarListener(e -> eliminarCotizacion());
        view.addVerDetalleListener(e -> verDetalle());
        view.addCerrarListener(e -> view.dispose());
    }

    private void cargarTabla() {
        List<Cotizacion> lista = Cotizacion.obtenerTodos();
        String[] cols = {"ID","ID Cliente","Cliente","Fecha","Estado","Total"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
        };
        try {
            for (Cotizacion c : lista) {
                String clienteNombre = "";
                try {
                    Cliente cl = Cliente.obtenerPorId(c.getId_cliente());
                    if (cl != null) clienteNombre = cl.getNombre();
                } catch (Exception ex) { ex.printStackTrace(); }
                model.addRow(new Object[]{
                        c.getId_cotizacion(), c.getId_cliente(), clienteNombre,
                        c.getFecha() == null ? "" : c.getFecha().toString(),
                        c.getEstado(), c.getTotal()
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        view.setTableModel(model);
    }

    private void agregarCotizacion() {
        CotizacionFormDialog dlg = new CotizacionFormDialog(null);
        dlg.cargarClientes();
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        try {
            Cotizacion c = new Cotizacion();
            c.setId_cliente(dlg.getIdCliente());
            c.setEstado(dlg.getEstado());
            // compute total from lines
            double total = 0.0;
            for (DetalleLinea ln : dlg.getLineas()) {
                total += ln.cantidad * ln.precio;
            }
            c.setTotal(total);
            if (!c.guardar()) { JOptionPane.showMessageDialog(view, "Error al guardar cotización."); return; }

            // guardar detalles
            for (DetalleLinea ln : dlg.getLineas()) {
                DetalleCotizacion d = new DetalleCotizacion();
                d.setId_cotizacion(c.getId_cotizacion());
                d.setConcepto(ln.concepto);
                d.setCantidad(ln.cantidad);
                d.setPrecio_unitario(ln.precio);
                d.guardar();
            }

            JOptionPane.showMessageDialog(view, "Cotización agregada.");
            cargarTabla();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error añadiendo cotización: " + ex.getMessage());
        }
    }

    private void editarCotizacion() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione una cotización."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);

        // cargar cotizacion actual
        List<Cotizacion> lista = Cotizacion.obtenerTodos();
        Cotizacion actual = null;
        for (Cotizacion c : lista) if (c.getId_cotizacion() == id) { actual = c; break; }
        if (actual == null) { JOptionPane.showMessageDialog(view, "Cotización no encontrada."); return; }

        CotizacionFormDialog dlg = new CotizacionFormDialog(view);
        dlg.cargarClientes();
        // rellenar datos
        dlg.setCliente(actual.getId_cliente());
        dlg.setEstado(actual.getEstado());
        // cargar detalles
        List<DetalleCotizacion> dets = Cotizacion.obtenerDetalle(id);
        StringBuilder sb = new StringBuilder();
        for (DetalleCotizacion d : dets) {
            sb.append(d.getConcepto()).append(";").append(d.getCantidad()).append(";").append(d.getPrecio_unitario()).append("\n");
        }
        dlg.setDetalleText(sb.toString());
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        try {
            // actualizar campos y total
            actual.setId_cliente(dlg.getIdCliente());
            actual.setEstado(dlg.getEstado());
            double total = 0;
            for (DetalleLinea ln : dlg.getLineas()) total += ln.cantidad * ln.precio;
            actual.setTotal(total);
            if (!actual.actualizar()) { JOptionPane.showMessageDialog(view, "Error al actualizar cotización."); return; }

            // eliminar detalles viejos y guardar nuevos
            DetalleCotizacion.eliminarPorCotizacion(actual.getId_cotizacion());
            for (DetalleLinea ln : dlg.getLineas()) {
                DetalleCotizacion d = new DetalleCotizacion();
                d.setId_cotizacion(actual.getId_cotizacion());
                d.setConcepto(ln.concepto);
                d.setCantidad(ln.cantidad);
                d.setPrecio_unitario(ln.precio);
                d.guardar();
            }

            JOptionPane.showMessageDialog(view, "Cotización actualizada.");
            cargarTabla();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error editando cotización: " + ex.getMessage());
        }
    }

    private void eliminarCotizacion() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione una cotización."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        int ok = JOptionPane.showConfirmDialog(view, "Eliminar cotización ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            // eliminar detalles y luego cabecera
            DetalleCotizacion.eliminarPorCotizacion(id);
            Cotizacion c = new Cotizacion();
            c.setId_cotizacion(id);
            if (c.eliminar()) {
                JOptionPane.showMessageDialog(view, "Cotización eliminada.");
                cargarTabla();
            } else JOptionPane.showMessageDialog(view, "Error al eliminar cotización.");
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage()); }
    }

    private void verDetalle() {
        int fila = view.getTabla().getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(view, "Seleccione una cotización."); return; }
        int id = (int) view.getTabla().getValueAt(fila, 0);
        List<DetalleCotizacion> dets = Cotizacion.obtenerDetalle(id);
        StringBuilder sb = new StringBuilder();
        for (DetalleCotizacion d : dets) {
            sb.append(d.getConcepto()).append(" | Cant: ").append(d.getCantidad())
              .append(" | Precio: ").append(d.getPrecio_unitario()).append("\n");
        }
        JOptionPane.showMessageDialog(view, sb.length()==0 ? "Sin detalles." : sb.toString(), "Detalle cotización " + id, JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------
    // Dialog interno para agregar/editar
    // ---------------------------
    private static class CotizacionFormDialog extends JDialog {
        private JComboBox<String> cbClientes;
        private JComboBox<String> cbEstado;
        private JTextArea taDetalles;
        private boolean confirmed = false;

        CotizacionFormDialog(JFrame owner) {
            super(owner, "Formulario Cotización", true);
            setSize(560, 420);
            setLocationRelativeTo(owner);
            setLayout(new java.awt.BorderLayout(8,8));

            JPanel form = new JPanel(new java.awt.GridLayout(3,2,8,8));
            form.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
            form.add(new JLabel("Cliente:")); cbClientes = new JComboBox<>(); form.add(cbClientes);
            form.add(new JLabel("Estado:")); cbEstado = new JComboBox<>(new String[]{"pendiente","aprobada","rechazada"}); form.add(cbEstado);

            add(form, java.awt.BorderLayout.NORTH);

            JPanel center = new JPanel(new BorderLayout());
            center.setBorder(BorderFactory.createTitledBorder("Detalle (una línea por item: concepto;cantidad;precio_unitario)"));
            taDetalles = new JTextArea();
            center.add(new JScrollPane(taDetalles), BorderLayout.CENTER);
            add(center, java.awt.BorderLayout.CENTER);

            JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            JButton ok = new JButton("Guardar"); ok.addActionListener(e -> { confirmed = true; dispose(); });
            JButton cancel = new JButton("Cancelar"); cancel.addActionListener(e -> { confirmed = false; dispose(); });
            buttons.add(cancel); buttons.add(ok);
            add(buttons, java.awt.BorderLayout.SOUTH);
        }

        void cargarClientes() {
            cbClientes.removeAllItems();
            try {
                for (Cliente c : Cliente.obtenerTodos()) {
                    cbClientes.addItem(c.getId_cliente() + " - " + c.getNombre());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        void setCliente(int idCliente) {
            String pref = idCliente + " - ";
            for (int i = 0; i < cbClientes.getItemCount(); i++) {
                if (cbClientes.getItemAt(i).startsWith(pref)) { cbClientes.setSelectedIndex(i); break; }
            }
        }

        int getIdCliente() {
            String s = (String) cbClientes.getSelectedItem();
            if (s == null) return 0;
            return Integer.parseInt(s.split(" - ",2)[0]);
        }

        void setEstado(String estado) { cbEstado.setSelectedItem(estado); }
        String getEstado() { return (String) cbEstado.getSelectedItem(); }

        void setDetalleText(String text) { taDetalles.setText(text); }

        // parsea las líneas y retorna lista de DetalleLinea
        java.util.List<DetalleLinea> getLineas() {
            java.util.List<DetalleLinea> res = new java.util.ArrayList<>();
            String[] lines = taDetalles.getText().split("\\r?\\n");
            for (String ln : lines) {
                if (ln.trim().isEmpty()) continue;
                String[] parts = ln.split(";");
                if (parts.length < 3) continue;
                String concepto = parts[0].trim();
                int cantidad = 1;
                double precio = 0.0;
                try { cantidad = Integer.parseInt(parts[1].trim()); } catch (Exception e) {}
                try { precio = Double.parseDouble(parts[2].trim()); } catch (Exception e) {}
                res.add(new DetalleLinea(concepto, cantidad, precio));
            }
            return res;
        }

        boolean isConfirmed() { return confirmed; }
    }

    // helper POJO para lineas
    private static class DetalleLinea {
        String concepto;
        int cantidad;
        double precio;
        DetalleLinea(String concepto,int cantidad,double precio){ this.concepto=concepto; this.cantidad=cantidad; this.precio=precio; }
    }
}
