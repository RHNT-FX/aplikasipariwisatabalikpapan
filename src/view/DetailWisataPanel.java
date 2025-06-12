package view;

import dao.RatingDAO;
import model.Rating;
import model.User;
import model.Wisata;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DetailWisataPanel extends JPanel {

    private Wisata wisata;
    private User currentUser;

    private JLabel lblNamaWisata;
    private JTextArea areaDeskripsi;

    private JLabel lblAverageRating;
    private JPanel commentsPanel;

    private JComboBox<Integer> ratingComboBox;
    private JTextArea commentTextArea;
    private JButton submitRatingButton;
    private JPanel formPanel;

    private RatingDAO ratingDAO;

    public DetailWisataPanel(Wisata wisata, User currentUser, MainFrame mainFrame) {
        this.wisata = wisata;
        this.currentUser = currentUser;
        this.ratingDAO = new RatingDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));

        JPanel detailPanel = new JPanel(new BorderLayout());
        lblNamaWisata = new JLabel(wisata.getNama());
        lblNamaWisata.setFont(new Font("Segoe UI", Font.BOLD, 24));
        areaDeskripsi = new JTextArea(wisata.getDeskripsi());
        areaDeskripsi.setWrapStyleWord(true);
        areaDeskripsi.setLineWrap(true);
        areaDeskripsi.setEditable(false);
        detailPanel.add(lblNamaWisata, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(areaDeskripsi), BorderLayout.CENTER);

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

        formPanel = createFormPanel();
        
        if (currentUser != null && "user".equals(currentUser.getRole())) {
            formPanel.setVisible(true);
        } else {
            formPanel.setVisible(false);
        }

        JButton backButton = new JButton("Kembali ke Daftar Wisata");
        backButton.addActionListener(e -> mainFrame.showWisataPanel());

        add(mainContentPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);
        add(backButton, BorderLayout.SOUTH);

        loadRatings();
    }

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

    private void loadRatings() {
        double avgRating = ratingDAO.getAverageRating(wisata.getId());
        String formattedRating = String.format("%.1f", avgRating);
        lblAverageRating.setText("⭐ Rating Rata-rata: " + formattedRating + " / 5.0 (" + ratingDAO.getRatingsByWisataId(wisata.getId()).size() + " ulasan)");

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