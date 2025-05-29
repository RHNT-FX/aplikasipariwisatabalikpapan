// src/view/KategoriPanel.java
package view;

import dao.KategoriDAO;
import model.Kategori;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KategoriPanel extends JPanel {

    private JTable kategoriTable;
    private DefaultTableModel tableModel;
    private JTextField namaKategoriField;
    private JButton addButton, editButton, deleteButton;

    private KategoriDAO kategoriDAO;

    public KategoriPanel() {
        kategoriDAO = new KategoriDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        addListeners();
        loadKategoriData();
    }

    private void initComponents() {
        // Input form untuk tambah/edit
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputPanel.add(new JLabel("Nama Kategori:"));
        namaKategoriField = new JTextField(20);
        inputPanel.add(namaKategoriField);

        addButton = new JButton("Tambah");
        editButton = new JButton("Ubah");
        deleteButton = new JButton("Hapus");

        inputPanel.add(addButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // Tabel untuk menampilkan kategori
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Kategori"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        kategoriTable = new JTable(tableModel);
        kategoriTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(kategoriTable), BorderLayout.CENTER);
    }

    private void addListeners() {
        addButton.addActionListener(e -> addKategori());
        editButton.addActionListener(e -> updateKategori());
        deleteButton.addActionListener(e -> deleteKategori());

        kategoriTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = kategoriTable.getSelectedRow();
                if (selectedRow != -1) {
                    namaKategoriField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                } else {
                    namaKategoriField.setText("");
                }
            }
        });
    }

    private void loadKategoriData() {
        tableModel.setRowCount(0);
        try {
            List<Kategori> daftarKategori = kategoriDAO.getAllKategori();
            for (Kategori kategori : daftarKategori) {
                tableModel.addRow(new Object[]{kategori.getId(), kategori.getNama()});
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex, "memuat data kategori");
        }
    }

    private void addKategori() {
        String nama = namaKategoriField.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (kategoriDAO.getKategoriByNama(nama) != null) {
                JOptionPane.showMessageDialog(this, "Kategori dengan nama tersebut sudah ada.", "Duplikasi Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Kategori newKategori = new Kategori(nama);
            kategoriDAO.addKategori(newKategori);
            JOptionPane.showMessageDialog(this, "Kategori berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            namaKategoriField.setText("");
            loadKategoriData();
        } catch (SQLException ex) {
            handleDatabaseError(ex, "menambah kategori");
        }
    }

    private void updateKategori() {
        int selectedRow = kategoriTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang akan diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String namaBaru = namaKategoriField.getText().trim();

        if (namaBaru.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Kategori kategoriToUpdate = kategoriDAO.getKategoriById(id);
            if (kategoriToUpdate != null) {
                // Cek duplikasi nama jika nama berubah dan nama baru sudah ada untuk kategori lain
                Kategori existingKategori = kategoriDAO.getKategoriByNama(namaBaru);
                if (existingKategori != null && existingKategori.getId() != id) {
                    JOptionPane.showMessageDialog(this, "Kategori dengan nama tersebut sudah ada.", "Duplikasi Data", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                kategoriToUpdate.setNama(namaBaru);
                kategoriDAO.updateKategori(kategoriToUpdate);
                JOptionPane.showMessageDialog(this, "Kategori berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                namaKategoriField.setText("");
                loadKategoriData();
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex, "mengubah kategori");
        }
    }

    private void deleteKategori() {
        int selectedRow = kategoriTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Menghapus kategori juga akan menghapus referensinya dari destinasi wisata. Lanjutkan?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Kategori kategoriToDelete = kategoriDAO.getKategoriById(id);
                if (kategoriToDelete != null) {
                    kategoriDAO.deleteKategori(kategoriToDelete);
                    JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    namaKategoriField.setText("");
                    loadKategoriData();
                }
            } catch (SQLException ex) {
                handleDatabaseError(ex, "menghapus kategori");
            }
        }
    }

    private void handleDatabaseError(SQLException ex, String operation) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat " + operation + ".\nDetail: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}