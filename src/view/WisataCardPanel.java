// src/view/WisataCardPanel.java
package view;

import model.Wisata;
import javax.swing.*;
import java.awt.*;

/**
 * Panel kustom untuk menampilkan satu kartu destinasi wisata.
 * Berisi gambar placeholder, judul wisata, dan lokasi.
 */
public class WisataCardPanel extends JPanel {

    private Wisata wisata;
    private JLabel imageLabel;
    private JLabel titleLocationLabel;

    public WisataCardPanel(Wisata wisata) {
        this.wisata = wisata;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // Border tipis
        setPreferredSize(new Dimension(250, 250)); // Ukuran kartu yang konsisten
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Ubah kursor menjadi tangan saat hover

        initComponents();
        populateCard();
    }

    private void initComponents() {
        // --- Gambar Wisata (Placeholder) ---
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBackground(Color.LIGHT_GRAY); // Warna abu-abu seperti di desain
        imageLabel.setOpaque(true); // Pastikan latar belakang terlihat
        imageLabel.setPreferredSize(new Dimension(250, 180)); // Proporsi gambar
        
        // Coba muat gambar default jika ada
        ImageIcon defaultImage = null;
        try {
            // Asumsikan ada gambar placeholder di src/resources/placeholder_wisata.png
            defaultImage = new ImageIcon(getClass().getResource("/resources/placeholder_wisata.png"));
            if (defaultImage.getImage() != null) {
                // Skalakan gambar agar pas di label (jika terlalu besar)
                Image scaledImage = defaultImage.getImage().getScaledInstance(imageLabel.getPreferredSize().width, imageLabel.getPreferredSize().height, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar placeholder wisata: " + e.getMessage());
            imageLabel.setText("Gambar Wisata"); // Jika gagal, tampilkan teks
            imageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        }

        add(imageLabel, BorderLayout.CENTER);

        // --- Judul dan Lokasi ---
        titleLocationLabel = new JLabel();
        titleLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLocationLabel.setBackground(new Color(200, 200, 200)); // Warna abu-abu gelap untuk background teks
        titleLocationLabel.setOpaque(true);
        titleLocationLabel.setForeground(Color.BLACK); // Warna teks hitam
        titleLocationLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding teks

        add(titleLocationLabel, BorderLayout.SOUTH);
    }

    private void populateCard() {
        if (wisata != null) {
            // Menampilkan nama dan lokasi
            titleLocationLabel.setText("<html><center><b>" + wisata.getNama() + "</b><br>" + wisata.getLokasi() + "</center></html>");
            
            // TODO: Jika ada gambar spesifik untuk wisata, muat di sini
            // Misalnya: if (wisata.getImagePath() != null) { ... }
        }
    }

    public Wisata getWisata() {
        return wisata;
    }
}