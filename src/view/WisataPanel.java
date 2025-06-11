package view;

import dao.WisataDAO;
import model.Wisata;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class WisataPanel extends JPanel {

    private JPanel wisataListPanel;
    private WisataDAO wisataDAO;
    private MainFrame mainFrame;
    
    // Komponen untuk pencarian
    private JTextField searchField;
    private JButton searchButton;

    public WisataPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.wisataDAO = new WisataDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Membuat panel pencarian
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        searchButton = new JButton("Cari");
        
        searchPanel.add(new JLabel("Cari Destinasi:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Menambahkan panel pencarian ke bagian atas (NORTH)
        add(searchPanel, BorderLayout.NORTH);

        wisataListPanel = new JPanel();
        wisataListPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 kolom, jarak 10px
        
        JScrollPane scrollPane = new JScrollPane(wisataListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Menambahkan listener untuk tombol cari
        searchButton.addActionListener(e -> performSearch());
        
        // Juga cari saat menekan Enter di kolom search
        searchField.addActionListener(e -> performSearch());

        loadAllWisata(); // Memuat semua wisata saat panel pertama kali dibuka
    }

    private void performSearch() {
        String keyword = searchField.getText();
        try {
            List<Wisata> searchResult = wisataDAO.searchWisataByName(keyword);
            updateWisataDisplay(searchResult);
        } catch (Exception ex) {
            handleDatabaseError(ex);
        }
    }

    private void loadAllWisata() {
        try {
            List<Wisata> allWisata = wisataDAO.getAllWisata();
            updateWisataDisplay(allWisata);
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }

    // Metode baru untuk menampilkan data (agar tidak duplikat kode)
    private void updateWisataDisplay(List<Wisata> wisataList) {
        wisataListPanel.removeAll();

        if (wisataList.isEmpty()) {
            wisataListPanel.setLayout(new BorderLayout());
            wisataListPanel.add(new JLabel("Data wisata tidak ditemukan.", SwingConstants.CENTER));
        } else {
            wisataListPanel.setLayout(new GridLayout(0, 3, 10, 10));
            for (Wisata wisata : wisataList) {
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
        wisataListPanel.revalidate();
        wisataListPanel.repaint();
    }
    
    // Metode baru untuk menangani error database
    private void handleDatabaseError(Exception e) {
        wisataListPanel.removeAll();
        wisataListPanel.setLayout(new BorderLayout());
        wisataListPanel.add(new JLabel("Gagal memuat data dari database.", SwingConstants.CENTER));
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Tidak dapat mengambil data dari database.\nPastikan database terhubung dengan benar.", 
            "Error Database", 
            JOptionPane.ERROR_MESSAGE);
        wisataListPanel.revalidate();
        wisataListPanel.repaint();
    }
}