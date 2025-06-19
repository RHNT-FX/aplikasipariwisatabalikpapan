package view;

import model.Wisata;
import javax.swing.*;
import java.awt.*;

public class WisataCardPanel extends JPanel {

    private Wisata wisata;
    private JLabel imageLabel;
    private JLabel titleLocationLabel;

    public WisataCardPanel(Wisata wisata) {
        this.wisata = wisata;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setPreferredSize(new Dimension(250, 250));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        initComponents();
        populateCard();
    }

    private void initComponents() {
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBackground(Color.LIGHT_GRAY);
        imageLabel.setOpaque(true);
        imageLabel.setPreferredSize(new Dimension(250, 180));

        ImageIcon defaultImage = null;
        try {
            defaultImage = new ImageIcon(getClass().getResource("/resources/placeholder_wisata.png"));
            if (defaultImage.getImage() != null) {
                Image scaledImage = defaultImage.getImage().getScaledInstance(imageLabel.getPreferredSize().width, imageLabel.getPreferredSize().height, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar placeholder wisata: " + e.getMessage());
            imageLabel.setText("Gambar Wisata");
            imageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        }

        add(imageLabel, BorderLayout.CENTER);

        titleLocationLabel = new JLabel();
        titleLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLocationLabel.setBackground(new Color(200, 200, 200));
        titleLocationLabel.setOpaque(true);
        titleLocationLabel.setForeground(Color.BLACK);
        titleLocationLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(titleLocationLabel, BorderLayout.SOUTH);
    }

    private void populateCard() {
        if (wisata != null) {
            titleLocationLabel.setText("<html><center><b>" + wisata.getNama() + "</b><br>" + wisata.getLokasi() + "</center></html>");
        }
    }

    public Wisata getWisata() {
        return wisata;
    }
}