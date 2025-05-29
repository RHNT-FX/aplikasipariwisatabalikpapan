// src/view/FasilitasPanel.java
package view;

import dao.FasilitasDAO;
import model.Fasilitas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FasilitasPanel extends JPanel {

    private JTable fasilitasTable;
    private DefaultTableModel tableModel;
    private JTextField namaFasilitasField;
    private JButton addButton, editButton, deleteButton;

    private FasilitasDAO fasilitasDAO;

    public FasilitasPanel() {
        fasilitasDAO = new FasilitasDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        addListeners();
        loadFasilitasData();
    }

    private void initComponents() {
        // Input form untuk tambah/edit
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputPanel.add(new JLabel("Nama Fasilitas:"));
        namaFasilitasField = new JTextField(20);
        inputPanel.add(namaFasilitasField);

        addButton = new JButton("Tambah");
        editButton = new JButton("Ubah");
        deleteButton = new JButton("Hapus");

        inputPanel.add(addButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // Tabel untuk menampilkan fasilitas
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Fasilitas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fasilitasTable = new JTable(tableModel);
        fasilitasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(fasilitasTable), BorderLayout.CENTER);
    }

    private void addListeners() {
        addButton.addActionListener(e -> addFasilitas());
        editButton.addActionListener(e -> updateFasilitas());
        deleteButton.addActionListener(e -> deleteFasilitas());

        fasilitasTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = fasilitasTable.getSelectedRow();
                if (selectedRow != -1) {
                    namaFasilitasField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                } else {
                    namaFasilitasField.setText("");
                }
            }
        });
    }

    private void loadFasilitasData() {
        tableModel.setRowCount(0);
        try {
            List<Fasilitas> daftarFasilitas = fasilitasDAO.getAllFasilitas();
            for (Fasilitas fasilitas : daftarFasilitas) {
                tableModel.addRow(new Object[]{fasilitas.getId(), fasilitas.getNama()});
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex, "memuat data fasilitas");
        }
    }

    private void addFasilitas() {
        String nama = namaFasilitasField.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama fasilitas tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (fasilitasDAO.getFasilitasByNama(nama) != null) {
                JOptionPane.showMessageDialog(this, "Fasilitas dengan nama tersebut sudah ada.", "Duplikasi Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Fasilitas newFasilitas = new Fasilitas(nama);
            fasilitasDAO.addFasilitas(newFasilitas);
            JOptionPane.showMessageDialog(this, "Fasilitas berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            namaFasilitasField.setText("");
            loadFasilitasData();
        } catch (SQLException ex) {
            handleDatabaseError(ex, "menambah fasilitas");
        }
    }

    private void updateFasilitas() {
        int selectedRow = fasilitasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih fasilitas yang akan diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String namaBaru = namaFasilitasField.getText().trim();

        if (namaBaru.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama fasilitas tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Fasilitas fasilitasToUpdate = fasilitasDAO.getFasilitasById(id);
            if (fasilitasToUpdate != null) {
                // Cek duplikasi nama jika nama berubah dan nama baru sudah ada untuk fasilitas lain
                Fasilitas existingFasilitas = fasilitasDAO.getFasilitasByNama(namaBaru);
                if (existingFasilitas != null && existingFasilitas.getId() != id) {
                    JOptionPane.showMessageDialog(this, "Fasilitas dengan nama tersebut sudah ada.", "Duplikasi Data", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fasilitasToUpdate.setNama(namaBaru);
                fasilitasDAO.updateFasilitas(fasilitasToUpdate);
                JOptionPane.showMessageDialog(this, "Fasilitas berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                namaFasilitasField.setText("");
                loadFasilitasData();
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex, "mengubah fasilitas");
        }
    }

    private void deleteFasilitas() {
        int selectedRow = fasilitasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih fasilitas yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Menghapus fasilitas juga akan menghapus referensinya dari destinasi wisata. Lanjutkan?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Fasilitas fasilitasToDelete = fasilitasDAO.getFasilitasById(id);
                if (fasilitasToDelete != null) {
                    fasilitasDAO.deleteFasilitas(fasilitasToDelete);
                    JOptionPane.showMessageDialog(this, "Fasilitas berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    namaFasilitasField.setText("");
                    loadFasilitasData();
                }
            } catch (SQLException ex) {
                handleDatabaseError(ex, "menghapus fasilitas");
            }
        }
    }

    private void handleDatabaseError(SQLException ex, String operation) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat " + operation + ".\nDetail: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}