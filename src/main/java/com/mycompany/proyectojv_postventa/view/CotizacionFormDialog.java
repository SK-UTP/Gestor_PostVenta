package com.mycompany.proyectojv_postventa.view;

import com.mycompany.proyectojv_postventa.model.Cliente;
import com.mycompany.proyectojv_postventa.model.Cotizacion;
import com.mycompany.proyectojv_postventa.model.DetalleCotizacion;
import com.mycompany.proyectojv_postventa.model.Motor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.text.DecimalFormat;

/**
 * Formulario para crear / editar cotizaciones.
 * Añadido: selección de Estado (pendiente, aprobada, rechazada, etc.)
 */
public class CotizacionFormDialog extends JDialog {
    private JComboBox<String> cbClientes;
    private JComboBox<String> cbMotores;
    private JTextField txtMarca, txtModelo;
    private JComboBox<String> cbEstado;             // <-- nuevo: estado
    private JTable tablaDetalles;
    private DefaultTableModel detallesModel;
    private JButton btnAgregarLinea, btnEliminarLinea, btnGuardar, btnCancelar;
    private JLabel lblTotal;
    private boolean confirmed = false;
    private int editingCotizacionId = 0;
    private static final DecimalFormat DF = new DecimalFormat("#0.00");

    public CotizacionFormDialog(JFrame owner) {
        super(owner, "Formulario de Cotización", true);
        setSize(920, 640);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8,8));

        // Top: cliente / motor / marca / modelo / estado
        JPanel top = new JPanel(new GridLayout(3,4,8,8));
        top.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        cbClientes = new JComboBox<>();
        cbMotores = new JComboBox<>();
        txtMarca = new JTextField(); txtMarca.setEditable(false);
        txtModelo = new JTextField(); txtModelo.setEditable(false);
        cbEstado = new JComboBox<>(new String[] {"pendiente","aprobada","rechazada","cancelada","borrador"}); // opciones
        top.add(new JLabel("Cliente:")); top.add(cbClientes);
        top.add(new JLabel("Motor:")); top.add(cbMotores);
        top.add(new JLabel("Marca:")); top.add(txtMarca);
        top.add(new JLabel("Modelo:")); top.add(txtModelo);
        top.add(new JLabel("Estado:")); top.add(cbEstado);
        add(top, BorderLayout.NORTH);

        // Center - tabla detalles (modelo con tipos)
        String[] cols = {"Concepto","Cantidad","Precio unitario","Subtotal"};
        detallesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 1 || column == 2;
            }
            @Override public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 1: return Integer.class;
                    case 2: return Double.class;
                    case 3: return Double.class;
                    default: return String.class;
                }
            }
        };
        tablaDetalles = new JTable(detallesModel);
        tablaDetalles.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tablaDetalles.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override public void setValue(Object value) {
                if (value instanceof Number) setText(DF.format(((Number)value).doubleValue()));
                else setText(value == null ? "" : value.toString());
            }
        };
        tablaDetalles.getColumnModel().getColumn(2).setCellRenderer(moneyRenderer);
        tablaDetalles.getColumnModel().getColumn(3).setCellRenderer(moneyRenderer);

        JScrollPane sc = new JScrollPane(tablaDetalles);
        sc.setBorder(BorderFactory.createTitledBorder("Detalle: concepto, cantidad, precio_unitario"));
        add(sc, BorderLayout.CENTER);

        // Right: botones líneas & total
        JPanel right = new JPanel(new BorderLayout());
        JPanel btnsLineas = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        btnAgregarLinea = new JButton("Agregar línea");
        btnEliminarLinea = new JButton("Eliminar línea");
        btnsLineas.add(btnAgregarLinea); btnsLineas.add(btnEliminarLinea);
        right.add(btnsLineas, BorderLayout.NORTH);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPanel.add(lblTotal);
        right.add(totalPanel, BorderLayout.SOUTH);
        add(right, BorderLayout.EAST);

        // Bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        btnCancelar = new JButton("Cancelar");
        btnGuardar = new JButton("Guardar");
        bottom.add(btnCancelar); bottom.add(btnGuardar);
        add(bottom, BorderLayout.SOUTH);

        // Listeners
        btnAgregarLinea.addActionListener(e -> agregarLineaVacia());
        btnEliminarLinea.addActionListener(e -> eliminarLineaSeleccionada());
        btnCancelar.addActionListener(e -> { confirmed = false; dispose(); });
        btnGuardar.addActionListener(e -> { if (validarYGuardar()) { confirmed = true; dispose(); } });

        cbClientes.addActionListener(e -> cargarMotoresDelCliente());
        cbMotores.addActionListener(e -> aplicarMotorSeleccionado());

        detallesModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int col = e.getColumn();
                if (col == TableModelEvent.ALL_COLUMNS || col == -1 || col <= 2) {
                    SwingUtilities.invokeLater(this::recalcularSubtotales);
                }
            }
        });

        tablaDetalles.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (tablaDetalles.isEditing()) tablaDetalles.getCellEditor().stopCellEditing();
                    else agregarLineaVacia();
                    e.consume();
                }
            }
        });

        cargarClientes();
    }

    public boolean isConfirmed() { return confirmed; }

    public void setFormForEdit(Cotizacion c) {
        if (c == null) return;
        this.editingCotizacionId = c.getId_cotizacion();
        seleccionarClienteEnCombo(c.getId_cliente());
        if (c.getId_motor() > 0) seleccionarMotorEnCombo(c.getId_motor());
        detallesModel.setRowCount(0);
        List<DetalleCotizacion> detalles = DetalleCotizacion.obtenerPorCotizacion(c.getId_cotizacion());
        for (DetalleCotizacion d : detalles) {
            detallesModel.addRow(new Object[]{ d.getConcepto(), Integer.valueOf(d.getCantidad()), Double.valueOf(d.getPrecio_unitario()), Double.valueOf(d.getSubtotal()) });
        }
        // set estado en combo si viene
        if (c.getEstado() != null) {
            try { cbEstado.setSelectedItem(c.getEstado()); } catch (Exception ex) { /* ignore */ }
        }
        recalcularSubtotales();
    }

    // Helpers (igual que antes)
    private void cargarClientes() {
        cbClientes.removeAllItems();
        List<Cliente> lista = Cliente.obtenerTodos();
        for (Cliente c : lista) cbClientes.addItem(c.getId_cliente() + " - " + c.getNombre());
        if (cbClientes.getItemCount() > 0) cbClientes.setSelectedIndex(0);
        cargarMotoresDelCliente();
    }

    private void cargarMotoresDelCliente() {
        cbMotores.removeAllItems();
        String sel = (String) cbClientes.getSelectedItem();
        if (sel == null) return;
        int idCliente = parseIdFromCombo(sel);
        List<Motor> motores = Motor.obtenerPorCliente(idCliente);
        if (motores == null || motores.isEmpty()) {
            cbMotores.addItem("0 - (sin motores)");
            txtMarca.setText(""); txtModelo.setText("");
            return;
        }
        for (Motor m : motores) cbMotores.addItem(m.getId_motor() + " - " + m.getNumero_serie());
        cbMotores.setSelectedIndex(0);
        aplicarMotorSeleccionado();
    }

    private void aplicarMotorSeleccionado() {
        String sel = (String) cbMotores.getSelectedItem();
        if (sel == null) return;
        int idMotor = parseIdFromCombo(sel);
        if (idMotor <= 0) { txtMarca.setText(""); txtModelo.setText(""); return; }
        Motor m = Motor.obtenerPorId(idMotor);
        if (m != null) {
            txtMarca.setText(m.getMarca() == null ? "" : m.getMarca());
            txtModelo.setText(m.getModelo() == null ? "" : m.getModelo());
        }
    }

    private int parseIdFromCombo(String s) {
        if (s == null) return 0;
        try { return Integer.parseInt(s.split(" - ",2)[0].trim()); } catch (Exception e) { return 0; }
    }

    private void agregarLineaVacia() {
        detallesModel.addRow(new Object[]{"", Integer.valueOf(1), Double.valueOf(0.0), Double.valueOf(0.0)});
        int row = detallesModel.getRowCount()-1;
        tablaDetalles.changeSelection(row, 0, false, false);
        tablaDetalles.requestFocus();
    }

    private void eliminarLineaSeleccionada() {
        int fila = tablaDetalles.getSelectedRow();
        if (fila >= 0) { detallesModel.removeRow(fila); recalcularSubtotales(); }
        else JOptionPane.showMessageDialog(this, "Seleccione la línea a eliminar.");
    }

    private void recalcularSubtotales() {
        double total = 0d;
        for (int r = 0; r < detallesModel.getRowCount(); r++) {
            try {
                Object conceptoObj = detallesModel.getValueAt(r,0);
                Object cantObj = detallesModel.getValueAt(r,1);
                Object puObj = detallesModel.getValueAt(r,2);

                if (conceptoObj == null || String.valueOf(conceptoObj).trim().isEmpty()) {
                    detallesModel.setValueAt(Double.valueOf(0.0), r, 3);
                    continue;
                }

                int cantidad = 1;
                double precio = 0.0;

                if (cantObj instanceof Number) cantidad = ((Number)cantObj).intValue();
                else if (cantObj != null) cantidad = (int)Math.round(Double.parseDouble(String.valueOf(cantObj).replace(",", ".")));

                if (puObj instanceof Number) precio = ((Number)puObj).doubleValue();
                else if (puObj != null) precio = Double.parseDouble(String.valueOf(puObj).replace(",", "."));

                double subtotal = cantidad * precio;
                detallesModel.setValueAt(Double.valueOf(subtotal), r, 3);
                total += subtotal;
            } catch (Exception ex) {
                try { detallesModel.setValueAt(Double.valueOf(0.0), r, 3); } catch (Exception ignored) {}
            }
        }
        lblTotal.setText("Total: " + DF.format(total));
    }

    private boolean validarYGuardar() {
        String selCliente = (String) cbClientes.getSelectedItem();
        if (selCliente == null || selCliente.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione un cliente."); return false; }
        int idCliente = parseIdFromCombo(selCliente);
        int idMotor = parseIdFromCombo((String) cbMotores.getSelectedItem());

        if (detallesModel.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Agregue al menos una línea."); return false; }

        Cotizacion c = new Cotizacion();
        c.setId_cliente(idCliente);
        c.setId_motor(idMotor);
        c.setTotal(parseTotalFromLabel());
        // LEER ESTADO del combo
        c.setEstado((String) cbEstado.getSelectedItem());

        if (editingCotizacionId > 0) {
            c.setId_cotizacion(editingCotizacionId);
            if (!c.actualizar()) { JOptionPane.showMessageDialog(this, "Error al actualizar cotización."); return false; }
            DetalleCotizacion.eliminarPorCotizacion(editingCotizacionId);
            insertarDetallesParaCotizacion(editingCotizacionId);
        } else {
            if (!c.guardar()) { JOptionPane.showMessageDialog(this, "Error al guardar cotización."); return false; }
            insertarDetallesParaCotizacion(c.getId_cotizacion());
        }
        return true;
    }

    private void insertarDetallesParaCotizacion(int idCotizacion) {
        for (int r = 0; r < detallesModel.getRowCount(); r++) {
            Object conceptoObj = detallesModel.getValueAt(r,0);
            if (conceptoObj == null || String.valueOf(conceptoObj).trim().isEmpty()) continue;
            String concepto = String.valueOf(conceptoObj).trim();

            Object cantObj = detallesModel.getValueAt(r,1);
            Object puObj = detallesModel.getValueAt(r,2);
            int cantidad = 1;
            double precio = 0d;
            try { cantidad = (cantObj instanceof Number) ? ((Number)cantObj).intValue() : (int)Math.round(Double.parseDouble(String.valueOf(cantObj))); } catch (Exception ex) {}
            try { precio = (puObj instanceof Number) ? ((Number)puObj).doubleValue() : Double.parseDouble(String.valueOf(puObj).replace(",",".").replace(" ", "")); } catch (Exception ex) {}
            DetalleCotizacion d = new DetalleCotizacion();
            d.setId_cotizacion(idCotizacion);
            d.setConcepto(concepto);
            d.setCantidad(cantidad);
            d.setPrecio_unitario(precio);
            d.guardar();
        }
    }

    private double parseTotalFromLabel() {
        try {
            String t = lblTotal.getText().replace("Total:", "").trim().replace(",", "");
            return Double.parseDouble(t);
        } catch (Exception e) { return 0d; }
    }

    private void seleccionarClienteEnCombo(int idCliente) {
        String pref = idCliente + " - ";
        for (int i = 0; i < cbClientes.getItemCount(); i++) {
            if (cbClientes.getItemAt(i).startsWith(pref)) { cbClientes.setSelectedIndex(i); cargarMotoresDelCliente(); break; }
        }
    }

    private void seleccionarMotorEnCombo(int idMotor) {
        String pref = idMotor + " - ";
        for (int i = 0; i < cbMotores.getItemCount(); i++) {
            if (cbMotores.getItemAt(i).startsWith(pref)) { cbMotores.setSelectedIndex(i); aplicarMotorSeleccionado(); break; }
        }
    }
}
