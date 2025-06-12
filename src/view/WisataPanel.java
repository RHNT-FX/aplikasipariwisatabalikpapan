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
    private JTextField searchField;
    private JButton searchButton;

    public WisataPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.wisataDAO = new WisataDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        searchButton = new JButton("Cari");
        
        searchPanel.add(new JLabel("Cari Destinasi:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        add(searchPanel, BorderLayout.NORTH);

        wisataListPanel = new JPanel();
        wisataListPanel.setLayout(new GridLayout(0, 3, 10, 10)); 

        JScrollPane scrollPane = new JScrollPane(wisataListPanel);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> performSearch());
        
        searchField.addActionListener(e -> performSearch());

        loadAllWisata(); 
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