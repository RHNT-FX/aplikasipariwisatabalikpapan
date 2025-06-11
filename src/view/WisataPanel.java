package view;

import dao.WisataDAO;
import model.Wisata;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException; // Import SQLException
import java.util.List;

public class WisataPanel extends JPanel {

    private JPanel wisataListPanel;
    private WisataDAO wisataDAO;
    private MainFrame mainFrame;

    public WisataPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.wisataDAO = new WisataDAO();
        
        setLayout(new BorderLayout());
        
        wisataListPanel = new JPanel();
        wisataListPanel.setLayout(new GridLayout(0, 3, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(wisataListPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadWisata();
    }

    private void loadWisata() {
        wisataListPanel.removeAll();
        
        // PERBAIKAN: Membungkus logika dalam try-catch block
        try {
            List<Wisata> allWisata = wisataDAO.getAllWisata();

            if (allWisata.isEmpty()) {
                wisataListPanel.setLayout(new BorderLayout());
                wisataListPanel.add(new JLabel("Belum ada data destinasi wisata.", SwingConstants.CENTER));
            } else {
                wisataListPanel.setLayout(new GridLayout(0, 3, 10, 10));
                for (Wisata wisata : allWisata) {
                    WisataCardPanel cardPanel = new WisataCardPanel(wisata);
                    cardPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            mainFrame.showDetailWisata(wisata);
                        }
                    });
                    wisataListPanel.add(cardPanel);
                }
            }
        } catch (Exception e) { // Menangkap semua jenis exception, termasuk SQLException
            // Jika terjadi error saat memuat data, tampilkan pesan error
            wisataListPanel.setLayout(new BorderLayout());
            wisataListPanel.add(new JLabel("Gagal memuat data dari database.", SwingConstants.CENTER));
            e.printStackTrace(); // Penting untuk debugging di konsol
            JOptionPane.showMessageDialog(this, 
                "Tidak dapat mengambil data dari database.\nPastikan database terhubung dengan benar.", 
                "Error Database", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Memastikan panel di-refresh setelah try-catch selesai
        wisataListPanel.revalidate();
        wisataListPanel.repaint();
    }
}