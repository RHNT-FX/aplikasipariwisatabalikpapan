// src/view/WisataPanel.java
package view;

import controller.AuthManager;
// import dao.KategoriDAO; // Tidak dipakai langsung di sini, tapi di WisataFormDialog
import dao.WisataDAO;
import model.Fasilitas; // Import ini penting agar Fasilitas dapat di-resolve
// import model.Kategori; // Tidak dipakai langsung di sini
import model.Wisata;
import model.User; // Untuk mendapatkan objek user yang sedang login

import javax.swing.*;
import javax.swing.border.Border; // Penting: import Border
import javax.swing.border.LineBorder; // Penting: import LineBorder

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
// import java.util.Vector; // Ini tidak diperlukan

/**
 * Panel yang menampilkan daftar destinasi wisata dalam format kartu
 * dan menyediakan tombol aksi untuk menambah, mengubah, menghapus, serta melihat detail.
 * Desain disesuaikan dengan image "MenuUtama (1).png".
 */
public class WisataPanel extends JPanel {

    private JPanel cardsContainerPanel; // Panel untuk menampung kartu-kartu wisata
    private JScrollPane scrollPane; // Untuk membuat cardsContainerPanel bisa di-scroll

    private JButton addButton, editButton, deleteButton, detailButton;
    private JTextField searchField;
    private JButton searchButton;

    private WisataDAO wisataDAO;

    public WisataPanel() {
        wisataDAO = new WisataDAO();
        setLayout(new BorderLayout(10, 10)); // Border layout dengan jarak 10px
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Penggunaan langsung kelas User
        User currentUser = AuthManager.getCurrentUser();
        if (currentUser != null) {
            System.out.println("User login: " + currentUser.getNama());
        }

        initComponents();
        addListeners();
        loadWisataData(); // Memuat data saat panel pertama kali dibuka
        updateButtonStates(); // Atur status tombol berdasarkan hak akses
    }

    private void initComponents() {
        // --- Search Panel ---
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS)); // Menggunakan BoxLayout
        searchPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Border abu-abu
        searchPanel.setBackground(Color.WHITE); // Latar belakang putih

        ImageIcon searchIcon = null;
        try {
            // Muat gambar asli
            java.net.URL iconURL = getClass().getResource("/resources/search_icon.png");
            if (iconURL != null) {
                searchIcon = new ImageIcon(iconURL);
                // Skalakan gambar ke ukuran yang diinginkan (misalnya 24x24 piksel)
                Image originalImage = searchIcon.getImage();
                int desiredWidth = 24; // Sesuaikan ukuran ini sesuai kebutuhan
                int desiredHeight = 24; // Sesuaikan ukuran ini sesuai kebutuhan
                Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
                searchIcon = new ImageIcon(scaledImage);
            } else {
                System.err.println("Gambar search_icon.png tidak ditemukan! Pastikan ada di src/resources/");
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat atau menskalakan gambar search_icon: " + e.getMessage());
        }

        JLabel searchIconLabel = new JLabel(searchIcon); // Gunakan ImageIcon yang sudah diskalakan
        searchIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        searchField = new JTextField("Search Here"); // Placeholder text
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding internal
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));

        searchButton = new JButton(); // Tombol search bisa jadi ikon atau tidak terlihat
        searchButton.setBorderPainted(false); // Hapus border tombol
        searchButton.setContentAreaFilled(false); // Hapus latar belakang tombol
        searchButton.setFocusPainted(false); // Hapus fokus
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Kursor tangan

        // Masukkan komponen ke searchPanel
        searchPanel.add(searchIconLabel);
        searchPanel.add(searchField);
        // searchPanel.add(searchButton); // Tombol search bisa tidak diperlukan jika pencarian by enter di textfield

        add(searchPanel, BorderLayout.NORTH);

        // --- Container untuk Kartu Wisata ---
        cardsContainerPanel = new JPanel();
        // Gunakan FlowLayout untuk menata kartu secara dinamis atau GridLayout jika ingin baris/kolom tetap
        // FlowLayout lebih cocok untuk tampilan kartu yang bisa wrapping
        cardsContainerPanel.setLayout(new GridLayout(0, 2, 20, 20)); // 2 kolom, jarak antar kartu
        cardsContainerPanel.setBackground(Color.WHITE); // Latar belakang putih
        cardsContainerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding keseluruhan

        scrollPane = new JScrollPane(cardsContainerPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Hanya scroll vertikal
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Hapus border scrollpane
        add(scrollPane, BorderLayout.CENTER);

        // --- Tombol Aksi ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Pusatkan tombol
        buttonPanel.setBackground(Color.WHITE); // Latar belakang putih
        
        addButton = new JButton("Tambah Destinasi");
        editButton = new JButton("Ubah Destinasi");
        deleteButton = new JButton("Hapus Destinasi");
        detailButton = new JButton("Lihat Detail"); // Tombol untuk melihat detail

        // Set gaya tombol agar terlihat bagus
        customizeButton(addButton);
        customizeButton(editButton);
        customizeButton(deleteButton);
        customizeButton(detailButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Placeholder text behavior for search field
        setupPlaceholder(searchField, "Search Here");
    }

    private void addListeners() {
        // Listener untuk tombol-tombol CRUD
        addButton.addActionListener(e -> {
            if (AuthManager.isAdminLoggedIn()) {
                WisataFormDialog dialog = new WisataFormDialog(null, true, null); // null untuk wisata baru
                dialog.setVisible(true);
                if (dialog.isDataSaved()) {
                    loadWisataData(); // Muat ulang data setelah penambahan
                }
            } else {
                JOptionPane.showMessageDialog(this, "Anda tidak memiliki izin untuk menambah destinasi wisata.", "Hak Akses Dibatasi", JOptionPane.WARNING_MESSAGE);
            }
        });

        editButton.addActionListener(e -> {
            if (AuthManager.isAdminLoggedIn()) {
                Wisata selectedWisata = getSelectedWisataFromCards();
                if (selectedWisata != null) {
                    try {
                        // Perlu memuat ulang wisata dari DB untuk memastikan fasilitas terload dengan benar
                        // jika objek di kartu tidak fully loaded atau ada perubahan eksternal
                        Wisata wisataToEdit = wisataDAO.getWisataById(selectedWisata.getId());
                        if (wisataToEdit != null) {
                            WisataFormDialog dialog = new WisataFormDialog(null, true, wisataToEdit);
                            dialog.setVisible(true);
                            if (dialog.isDataSaved()) {
                                loadWisataData(); // Muat ulang data setelah perubahan
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Destinasi tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        handleDatabaseError(ex, "mengambil data destinasi untuk diubah");
                    } catch (Exception ex) {
                        handleApplicationError(ex, "mengambil data destinasi untuk diubah");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Pilih destinasi yang akan diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Anda tidak memiliki izin untuk mengubah destinasi wisata.", "Hak Akses Dibatasi", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            if (AuthManager.isAdminLoggedIn()) {
                Wisata selectedWisata = getSelectedWisataFromCards();
                if (selectedWisata != null) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus destinasi ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            wisataDAO.deleteWisata(selectedWisata);
                            JOptionPane.showMessageDialog(this, "Destinasi berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            loadWisataData(); // Muat ulang data setelah penghapusan
                        } catch (SQLException ex) {
                            handleDatabaseError(ex, "menghapus destinasi");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Pilih destinasi yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Anda tidak memiliki izin untuk menghapus destinasi wisata.", "Hak Akses Dibatasi", JOptionPane.WARNING_MESSAGE);
            }
        });

        detailButton.addActionListener(e -> {
            Wisata selectedWisata = getSelectedWisataFromCards();
            if (selectedWisata != null) {
                // Memanggil metode tampilkanInfo() sebagai contoh polimorfisme
                // Untuk memastikan data fasilitas terload, kita bisa memuat ulang objek dari DB
                try {
                    Wisata detailedWisata = wisataDAO.getWisataById(selectedWisata.getId());
                    if (detailedWisata != null) {
                        String detailInfo = String.format(
                            "Nama: %s\nDeskripsi: %s\nLokasi: %s\nHarga Tiket: %.2f\nJam Operasional: %s\nKategori: %s\nFasilitas: %s",
                            detailedWisata.getNama(),
                            detailedWisata.getDeskripsi(),
                            detailedWisata.getLokasi(),
                            detailedWisata.getHargaTiket(),
                            detailedWisata.getJamOperasional(),
                            (detailedWisata.getKategori() != null ? detailedWisata.getKategori().getNama() : "Tidak ada"),
                            formatFasilitas(detailedWisata.getDaftarFasilitas())
                        );
                        JOptionPane.showMessageDialog(this, detailInfo, "Detail Destinasi Wisata", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                         JOptionPane.showMessageDialog(this, "Destinasi tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    handleDatabaseError(ex, "melihat detail destinasi");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih destinasi untuk melihat detail.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // Juga cari saat tekan enter di field

        // Listener untuk memilih kartu saat diklik
        cardsContainerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Component clickedComponent = e.getComponent().getComponentAt(e.getPoint());
                // Pastikan yang diklik adalah WisataCardPanel atau salah satu komponennya
                // Loop hingga menemukan WisataCardPanel atau mencapai cardsContainerPanel
                while (!(clickedComponent instanceof WisataCardPanel) && clickedComponent.getParent() != null && clickedComponent != cardsContainerPanel) {
                    clickedComponent = clickedComponent.getParent();
                }

                if (clickedComponent instanceof WisataCardPanel) {
                    // Hapus seleksi sebelumnya
                    clearSelection();
                    // Tandai kartu yang dipilih
                    ((WisataCardPanel) clickedComponent).setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); // Border biru untuk seleksi
                    updateButtonStates();
                } else {
                    // Jika mengklik di luar kartu, batalkan seleksi
                    clearSelection();
                    updateButtonStates();
                }
            }
        });

        // Pastikan tombol edit/delete/detail selalu update saat panel dimuat
        this.addPropertyChangeListener("ancestor", evt -> {
            if (evt.getNewValue() != null && evt.getOldValue() == null) {
                // Panel baru saja ditambahkan ke hierarki jendela
                clearSelection(); // Pastikan tidak ada seleksi saat awal
                updateButtonStates();
            }
        });
    }

    private void loadWisataData() {
        cardsContainerPanel.removeAll(); // Hapus semua kartu sebelumnya
        try {
            List<Wisata> daftarWisata = wisataDAO.getAllWisata();
            if (daftarWisata.isEmpty()) {
                JLabel noDataLabel = new JLabel("Tidak ada destinasi wisata yang tersedia. Silakan tambahkan beberapa!");
                noDataLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                noDataLabel.setForeground(Color.GRAY);
                cardsContainerPanel.add(noDataLabel);
            } else {
                for (Wisata wisata : daftarWisata) {
                    WisataCardPanel card = new WisataCardPanel(wisata);
                    cardsContainerPanel.add(card);
                }
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex, "memuat data destinasi");
        }
        cardsContainerPanel.revalidate(); // Validasi ulang layout
        cardsContainerPanel.repaint(); // Gambar ulang
        clearSelection(); // Hapus seleksi setelah muat ulang data
        updateButtonStates(); // Perbarui status tombol
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        // Hapus placeholder text sebelum melakukan pencarian
        if (keyword.equals("Search Here")) {
            keyword = "";
        }
        cardsContainerPanel.removeAll(); // Hapus semua kartu sebelumnya
        try {
            List<Wisata> hasilPencarian;
            if (keyword.isEmpty()) {
                hasilPencarian = wisataDAO.getAllWisata(); // Jika kosong, tampilkan semua
            } else {
                hasilPencarian = wisataDAO.searchWisata(keyword);
            }

            if (hasilPencarian.isEmpty()) {
                JLabel noResultLabel = new JLabel("Tidak ada destinasi wisata yang cocok dengan pencarian Anda.");
                noResultLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                noResultLabel.setForeground(Color.GRAY);
                cardsContainerPanel.add(noResultLabel);
            } else {
                for (Wisata wisata : hasilPencarian) {
                    WisataCardPanel card = new WisataCardPanel(wisata);
                    cardsContainerPanel.add(card);
                }
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex, "mencari destinasi");
        }
        cardsContainerPanel.revalidate();
        cardsContainerPanel.repaint();
        clearSelection();
        updateButtonStates();
    }

    /**
     * Mendapatkan objek Wisata dari kartu yang saat ini terpilih.
     * @return Objek Wisata yang terpilih, atau null jika tidak ada kartu yang dipilih.
     */
    private Wisata getSelectedWisataFromCards() {
        for (Component comp : cardsContainerPanel.getComponents()) {
            if (comp instanceof WisataCardPanel) {
                WisataCardPanel card = (WisataCardPanel) comp;
                // Periksa apakah border kartu adalah LineBorder dan ketebalannya 2 (untuk seleksi)
                Border border = card.getBorder();
                if (border instanceof LineBorder) {
                    LineBorder lb = (LineBorder) border;
                    if (lb.getThickness() == 2 && lb.getLineColor().equals(Color.BLUE)) {
                        return card.getWisata();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Menghapus status seleksi dari semua kartu.
     */
    private void clearSelection() {
        for (Component comp : cardsContainerPanel.getComponents()) {
            if (comp instanceof WisataCardPanel) {
                ((WisataCardPanel) comp).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        }
    }

    /**
     * Mengatur status enable/disable dan visibilitas tombol berdasarkan hak akses pengguna dan seleksi kartu.
     * Skenario Error Pelanggaran Hak Akses (GUI dinonaktifkan di awal)
     */
    private void updateButtonStates() {
        boolean isAdmin = AuthManager.isAdminLoggedIn();
        boolean isCardSelected = getSelectedWisataFromCards() != null;

        // Visibilitas tombol untuk peran admin
        addButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);

        // Keterangan (enable/disable) tombol berdasarkan seleksi dan peran
        editButton.setEnabled(isAdmin && isCardSelected);
        deleteButton.setEnabled(isAdmin && isCardSelected);
        detailButton.setEnabled(isCardSelected); // Tombol detail selalu aktif jika ada pilihan
    }

    /**
     * Menampilkan pesan error database yang ramah pengguna.
     * Skenario Error Operasi Basis Data
     */
    private void handleDatabaseError(SQLException ex, String operation) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat " + operation + ".\nDetail: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); // Cetak stack trace untuk debugging lebih lanjut
    }

    /**
     * Menampilkan pesan error aplikasi umum.
     */
    private void handleApplicationError(Exception ex, String operation) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan aplikasi saat " + operation + ".\nDetail: " + ex.getMessage(), "Error Aplikasi", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    /**
     * Helper method untuk memformat daftar fasilitas menjadi string.
     */
    private String formatFasilitas(List<Fasilitas> fasilitasList) {
        if (fasilitasList == null || fasilitasList.isEmpty()) {
            return "Tidak ada";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fasilitasList.size(); i++) {
            sb.append(fasilitasList.get(i).getNama());
            if (i < fasilitasList.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Metode bantuan untuk gaya tombol
    private void customizeButton(JButton button) {
        button.setBackground(new Color(0, 102, 204)); // Warna biru
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 80, 160), 1), // Border gelap
            BorderFactory.createEmptyBorder(8, 15, 8, 15) // Padding internal
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Metode bantuan untuk placeholder text behavior
    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
}