package view;

import dao.FasilitasDAO;
import dao.KategoriDAO;
import dao.WisataDAO;
import model.Fasilitas;
import model.Kategori;
import model.Wisata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WisataFormDialog extends JDialog {

    private Wisata wisata; // Objek wisata yang sedang diedit (null jika baru)
    private boolean dataSaved = false;

    private JTextField namaField, lokasiField, jamOperasionalField, fotoFileNameField;
    private JTextArea deskripsiArea;
    private JTextField hargaTiketField;
    private JComboBox<Kategori> kategoriComboBox;
    private JList<Fasilitas> fasilitasList;
    private DefaultListModel<Fasilitas> fasilitasListModel; // Model untuk JList
    private JButton saveButton, cancelButton;

    private KategoriDAO kategoriDAO;
    private FasilitasDAO fasilitasDAO;
    private WisataDAO wisataDAO;

    public WisataFormDialog(Window owner, boolean modal, Wisata wisataToEdit) {
        super(owner instanceof Frame ? (Frame) owner : null, modal);
        this.wisata = wisataToEdit; // Jika null, ini adalah operasi tambah
        kategoriDAO = new KategoriDAO();
        fasilitasDAO = new FasilitasDAO();
        wisataDAO = new WisataDAO();

        setTitle(wisata == null ? "Tambah Destinasi Wisata Baru" : "Ubah Destinasi Wisata");
        setSize(600, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        populateForm(); // Isi form jika sedang mode edit
        addListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Baris 0: Nama
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Destinasi:"), gbc);
        gbc.gridx = 1;
        namaField = new JTextField(30);
        formPanel.add(namaField, gbc);

        // Baris 1: Lokasi
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Lokasi:"), gbc);
        gbc.gridx = 1;
        lokasiField = new JTextField(30);
        formPanel.add(lokasiField, gbc);

        // Baris 2: Harga Tiket
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Harga Tiket:"), gbc);
        gbc.gridx = 1;
        hargaTiketField = new JTextField(30);
        formPanel.add(hargaTiketField, gbc);

        // Baris 3: Jam Operasional
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Jam Operasional:"), gbc);
        gbc.gridx = 1;
        jamOperasionalField = new JTextField(30);
        formPanel.add(jamOperasionalField, gbc);

        // Baris 4: Kategori (ComboBox)
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 1;
        kategoriComboBox = new JComboBox<>();
        loadKategoriIntoComboBox(); // Muat kategori dari DB
        formPanel.add(kategoriComboBox, gbc);

        // Baris 5: Deskripsi (TextArea)
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1;
        gbc.weighty = 1.0; // Membuat text area membesar secara vertikal
        deskripsiArea = new JTextArea(5, 30); // 5 baris, 30 kolom
        deskripsiArea.setLineWrap(true);
        deskripsiArea.setWrapStyleWord(true);
        JScrollPane deskripsiScrollPane = new JScrollPane(deskripsiArea);
        formPanel.add(deskripsiScrollPane, gbc);
        gbc.weighty = 0; // Reset weighty

        // Baris 6: Fasilitas (JList dengan multiple selection)
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Anchor ke kiri atas
        formPanel.add(new JLabel("Fasilitas:"), gbc);
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        fasilitasListModel = new DefaultListModel<>();
        fasilitasList = new JList<>(fasilitasListModel);
        fasilitasList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        loadFasilitasIntoList(); // Muat fasilitas dari DB
        JScrollPane fasilitasScrollPane = new JScrollPane(fasilitasList);
        formPanel.add(fasilitasScrollPane, gbc);
        gbc.weighty = 0; // Reset weighty

        // Baris 7: Nama File Foto
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Nama File Foto:"), gbc);
        gbc.gridx = 1;
        fotoFileNameField = new JTextField(30);
        formPanel.add(fotoFileNameField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Tombol Aksi ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        saveButton = new JButton("Simpan");
        cancelButton = new JButton("Batal");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadKategoriIntoComboBox() {
        try {
            List<Kategori> kategoriList = kategoriDAO.getAllKategori();
            kategoriComboBox.addItem(null); // Opsi "Tidak Ada Kategori"
            for (Kategori kat : kategoriList) {
                kategoriComboBox.addItem(kat);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadFasilitasIntoList() {
        try {
            List<Fasilitas> fasilitasData = fasilitasDAO.getAllFasilitas();
            for (Fasilitas fas : fasilitasData) {
                fasilitasListModel.addElement(fas);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat fasilitas: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void populateForm() {
        if (wisata != null) { // Jika dalam mode edit
            namaField.setText(wisata.getNama());
            lokasiField.setText(wisata.getLokasi());
            hargaTiketField.setText(String.valueOf(wisata.getHargaTiket()));
            jamOperasionalField.setText(wisata.getJamOperasional());
            deskripsiArea.setText(wisata.getDeskripsi());

            // Pilih kategori di ComboBox
            if (wisata.getKategori() != null) {
                for (int i = 0; i < kategoriComboBox.getItemCount(); i++) {
                    Kategori item = kategoriComboBox.getItemAt(i);
                    if (item != null && item.getId() == wisata.getKategori().getId()) {
                        kategoriComboBox.setSelectedItem(item);
                        break;
                    }
                }
            }

            // Pilih fasilitas di JList
            List<Integer> selectedFasilitasIndices = new ArrayList<>();
            for (int i = 0; i < fasilitasListModel.getSize(); i++) {
                Fasilitas currentFasilitasInList = fasilitasListModel.getElementAt(i);
                for (Fasilitas wisataFasilitas : wisata.getDaftarFasilitas()) {
                    if (currentFasilitasInList.getId() == wisataFasilitas.getId()) {
                        selectedFasilitasIndices.add(i);
                        break;
                    }
                }
            }
            int[] indices = selectedFasilitasIndices.stream().mapToInt(Integer::intValue).toArray();
            fasilitasList.setSelectedIndices(indices);

            // Nama file foto
            if (wisata.getFotoPath() != null && !wisata.getFotoPath().isEmpty()) {
                fotoFileNameField.setText(wisata.getFotoPath());
            } else {
                fotoFileNameField.setText("");
            }
        } else {
            fotoFileNameField.setText("");
        }
    }

    private void addListeners() {
        saveButton.addActionListener(e -> saveWisata());
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveWisata() {
        // --- Validasi Input ---
        String nama = namaField.getText().trim();
        String lokasi = lokasiField.getText().trim();
        String jamOperasional = jamOperasionalField.getText().trim();
        String deskripsi = deskripsiArea.getText().trim();
        double hargaTiket;

        if (nama.isEmpty() || lokasi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Lokasi tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            hargaTiket = Double.parseDouble(hargaTiketField.getText());
            if (hargaTiket < 0) {
                JOptionPane.showMessageDialog(this, "Harga tiket harus angka positif.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga Tiket harus berupa angka yang valid.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Kategori selectedKategori = (Kategori) kategoriComboBox.getSelectedItem();
        List<Fasilitas> selectedFasilitas = fasilitasList.getSelectedValuesList();
        String namaFileFoto = fotoFileNameField.getText().trim();

        // --- Proses Simpan/Update ---
        if (wisata == null) { // Mode Tambah
            wisata = new Wisata();
        }
        wisata.setNama(nama);
        wisata.setLokasi(lokasi);
        wisata.setHargaTiket(hargaTiket);
        wisata.setJamOperasional(jamOperasional);
        wisata.setDeskripsi(deskripsi);
        wisata.setKategori(selectedKategori);
        wisata.setDaftarFasilitas(selectedFasilitas);
        wisata.setFotoPath(namaFileFoto); // <-- hanya nama file!

        try {
            if (wisata.getId() == 0) { // Baru
                wisataDAO.addWisata(wisata);
                JOptionPane.showMessageDialog(this, "Destinasi wisata berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else { // Update
                wisataDAO.updateWisata(wisata);
                JOptionPane.showMessageDialog(this, "Destinasi wisata berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
            dataSaved = true;
            dispose(); // Tutup dialog setelah berhasil
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isDataSaved() {
        return dataSaved;
    }
}