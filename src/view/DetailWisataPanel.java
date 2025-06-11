package view;

import dao.RatingDAO;
import model.Rating;
import model.User;
import model.Wisata;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel ini bertanggung jawab untuk menampilkan detail lengkap dari satu objek Wisata,
 * termasuk menampilkan rating & komentar, serta form untuk menambahkan ulasan baru.
 */
public class DetailWisataPanel extends JPanel {

    // Data utama yang ditampilkan di panel ini
    private Wisata wisata;
    private User currentUser;

    // Komponen untuk menampilkan detail wisata (Contoh)
    private JLabel lblNamaWisata;
    private JTextArea areaDeskripsi;

    // Komponen untuk menampilkan rating
    private JLabel lblAverageRating;
    private JPanel commentsPanel; // Panel untuk menampung semua komentar

    // Komponen untuk form input rating
    private JComboBox<Integer> ratingComboBox;
    private JTextArea commentTextArea;
    private JButton submitRatingButton;
    private JPanel formPanel; // Panel yang menampung form input

    private RatingDAO ratingDAO;

    /**
     * Constructor untuk DetailWisataPanel.
     * @param wisata Objek Wisata yang akan ditampilkan detailnya.
     * @param currentUser Objek User yang sedang login, untuk menentukan hak akses.
     * @param mainFrame Referensi ke MainFrame untuk fungsionalitas kembali.
     */
    public DetailWisataPanel(Wisata wisata, User currentUser, MainFrame mainFrame) {
        this.wisata = wisata;
        this.currentUser = currentUser;
        this.ratingDAO = new RatingDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PANEL UTAMA UNTUK DETAIL & RATING ---
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));

        // 1. Panel Detail Wisata (Bagian Atas)
        JPanel detailPanel = new JPanel(new BorderLayout());
        lblNamaWisata = new JLabel(wisata.getNama());
        lblNamaWisata.setFont(new Font("Segoe UI", Font.BOLD, 24));
        areaDeskripsi = new JTextArea(wisata.getDeskripsi());
        areaDeskripsi.setWrapStyleWord(true);
        areaDeskripsi.setLineWrap(true);
        areaDeskripsi.setEditable(false);
        detailPanel.add(lblNamaWisata, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(areaDeskripsi), BorderLayout.CENTER);

        // 2. Panel Rating (Bagian Bawah pada mainContentPanel)
        JPanel ratingDisplayPanel = new JPanel(new BorderLayout(5, 5));
        lblAverageRating = new JLabel("Rating: - / 5");
        lblAverageRating.setFont(new Font("Segoe UI", Font.BOLD, 16));
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        JScrollPane commentsScrollPane = new JScrollPane(commentsPanel);
        ratingDisplayPanel.add(lblAverageRating, BorderLayout.NORTH);
        ratingDisplayPanel.add(commentsScrollPane, BorderLayout.CENTER);

        mainContentPanel.add(detailPanel, BorderLayout.NORTH);
        mainContentPanel.add(ratingDisplayPanel, BorderLayout.CENTER);

        // 3. Panel Form Input Rating (akan ditempatkan di kanan atau bawah)
        formPanel = createFormPanel();
        
        // Hanya tampilkan form jika yang login adalah 'user'
        if (currentUser != null && "user".equals(currentUser.getRole())) {
            formPanel.setVisible(true);
        } else {
            formPanel.setVisible(false);
        }

        // 4. Tombol Kembali
        JButton backButton = new JButton("Kembali ke Daftar Wisata");
        backButton.addActionListener(e -> mainFrame.showWisataPanel());

        // Menambahkan semua panel ke layout utama
        add(mainContentPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);
        add(backButton, BorderLayout.SOUTH);

        // Muat data rating dari database
        loadRatings();
    }

    /**
     * Membuat panel untuk form input rating dan komentar.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Beri Ulasan Anda"));

        ratingComboBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        commentTextArea = new JTextArea(5, 20);
        submitRatingButton = new JButton("Kirim Ulasan");
        addSubmitButtonListener();

        JPanel ratingLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingLine.add(new JLabel("Beri Bintang:"));
        ratingLine.add(ratingComboBox);

        panel.add(ratingLine, BorderLayout.NORTH);
        panel.add(new JScrollPane(commentTextArea), BorderLayout.CENTER);
        panel.add(submitRatingButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Mengambil data dari DAO dan memperbarui komponen UI.
     */
    private void loadRatings() {
        // Muat dan tampilkan rata-rata rating
        double avgRating = ratingDAO.getAverageRating(wisata.getId());
        String formattedRating = String.format("%.1f", avgRating);
        lblAverageRating.setText("⭐ Rating Rata-rata: " + formattedRating + " / 5.0 (" + ratingDAO.getRatingsByWisataId(wisata.getId()).size() + " ulasan)");

        // Muat dan tampilkan semua komentar
        commentsPanel.removeAll();
        List<Rating> ratings = ratingDAO.getRatingsByWisataId(wisata.getId());

        if (ratings.isEmpty()) {
            commentsPanel.add(new JLabel("  Belum ada ulasan untuk lokasi ini. Jadilah yang pertama!"));
        } else {
            for (Rating r : ratings) {
                JPanel singleCommentPanel = new JPanel(new BorderLayout(5, 5));
                singleCommentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
                
                JLabel userAndRating = new JLabel(r.getNamaUser() + " - " + r.getRating() + " Bintang");
                userAndRating.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                JTextArea commentText = new JTextArea(r.getKomentar());
                commentText.setWrapStyleWord(true);
                commentText.setLineWrap(true);
                commentText.setEditable(false);
                commentText.setOpaque(false);
                commentText.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                singleCommentPanel.add(userAndRating, BorderLayout.NORTH);
                singleCommentPanel.add(commentText, BorderLayout.CENTER);
                
                commentsPanel.add(singleCommentPanel);
            }
        }
        commentsPanel.revalidate();
        commentsPanel.repaint();
    }

    /**
     * Menambahkan listener untuk tombol submit.
     */
    private void addSubmitButtonListener() {
        submitRatingButton.addActionListener(e -> {
            Integer selectedRating = (Integer) ratingComboBox.getSelectedItem();
            String comment = commentTextArea.getText().trim();

            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Komentar tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Rating newRating = new Rating(wisata.getId(), currentUser.getId(), selectedRating, comment);
            ratingDAO.addRating(newRating);

            JOptionPane.showMessageDialog(this, "Terima kasih atas ulasan Anda!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            commentTextArea.setText("");
            ratingComboBox.setSelectedIndex(0);
            loadRatings();
        });
    }
}