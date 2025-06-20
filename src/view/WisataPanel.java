package view;

import dao.WisataDAO;
import model.Wisata;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class WisataPanel extends JPanel {

    private JPanel wisataListPanel;
    private WisataDAO wisataDAO;
    private MainFrame mainFrame;
    private User user;

    private JTextField searchField;
    private JButton searchButton;

    private JButton addButton, editButton, deleteButton, detailButton;

    private Wisata selectedWisata;

    public WisataPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.user = user;
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        detailButton = new JButton("Lihat Detail");
        buttonPanel.add(detailButton);

        if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
            addButton = new JButton("Tambah Destinasi");
            editButton = new JButton("Ubah Destinasi");
            deleteButton = new JButton("Hapus Destinasi");

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            addButton.addActionListener(e -> showAddWisataDialog());
            editButton.addActionListener(e -> showEditWisataDialog());
            deleteButton.addActionListener(e -> showDeleteWisataDialog());
        }

        detailButton.addActionListener(e -> showDetailWisataDialog());

        add(buttonPanel, BorderLayout.SOUTH);

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
        selectedWisata = null;

        if (wisataList.isEmpty()) {
            wisataListPanel.setLayout(new BorderLayout());
            wisataListPanel.add(new JLabel("Data wisata tidak ditemukan.", SwingConstants.CENTER));
        } else {
            wisataListPanel.setLayout(new GridLayout(0, 3, 10, 10));
            for (Wisata wisata : wisataList) {
                WisataCardPanel cardPanel = new WisataCardPanel(wisata);
                cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                cardPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedWisata = wisata;
                    }
                });
                wisataListPanel.add(cardPanel);
            }
        }
        wisataListPanel.revalidate();
        wisataListPanel.repaint();
    }

    private void showAddWisataDialog() {
        WisataFormDialog dialog = new WisataFormDialog(SwingUtilities.getWindowAncestor(this), true, null);
        dialog.setVisible(true);
        if (dialog.isDataSaved()) {
            loadAllWisata();
        }
    }

    private void showEditWisataDialog() {
        if (selectedWisata == null) {
            JOptionPane.showMessageDialog(this, "Pilih destinasi yang ingin diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        WisataFormDialog dialog = new WisataFormDialog(SwingUtilities.getWindowAncestor(this), true, selectedWisata);
        dialog.setVisible(true);
        if (dialog.isDataSaved()) {
            loadAllWisata();
        }
    }

    private void showDeleteWisataDialog() {
        if (selectedWisata == null) {
            JOptionPane.showMessageDialog(this, "Pilih destinasi yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus destinasi ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                wisataDAO.deleteWisata(selectedWisata);
                loadAllWisata();
                JOptionPane.showMessageDialog(this, "Destinasi berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                handleDatabaseError(ex);
            }
        }
    }

    private void showDetailWisataDialog() {
        if (selectedWisata == null) {
            JOptionPane.showMessageDialog(this, "Pilih destinasi yang ingin dilihat detailnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainFrame.showDetailWisata(selectedWisata);
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