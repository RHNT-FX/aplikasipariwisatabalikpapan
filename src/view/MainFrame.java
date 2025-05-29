// src/view/MainFrame.java
package view;

import controller.AuthManager;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Jendela utama aplikasi setelah pengguna berhasil login.
 * Menampilkan konten berdasarkan peran pengguna (User/Admin).
 */
public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private User currentUser;

    public MainFrame() {
        currentUser = AuthManager.getCurrentUser();
        if (currentUser == null) {
            // Seharusnya tidak terjadi jika login berhasil
            JOptionPane.showMessageDialog(this, "Sesi pengguna tidak valid. Silakan login kembali.", "Error Sesi", JOptionPane.ERROR_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Aplikasi Pendataan Pariwisata Balikpapan - " + (currentUser.isAdmin() ? "Admin" : "Pengguna"));
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Tengah layar

        initComponents();
        createMenus(); // Membuat menu bar
        showDefaultPanel(); // Menampilkan panel default
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout()); // Panel untuk menampung konten dinamis
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createMenus() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menu Aplikasi
        JMenu appMenu = new JMenu("Aplikasi");
        JMenuItem logoutMenuItem = new JMenuItem("Logout");
        JMenuItem exitMenuItem = new JMenuItem("Keluar");

        logoutMenuItem.addActionListener(e -> {
            AuthManager.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });

        exitMenuItem.addActionListener(e -> System.exit(0));

        appMenu.add(logoutMenuItem);
        appMenu.addSeparator();
        appMenu.add(exitMenuItem);
        menuBar.add(appMenu);

        // Menu Data
        JMenu dataMenu = new JMenu("Data");
        JMenuItem wisataMenuItem = new JMenuItem("Destinasi Wisata");
        dataMenu.add(wisataMenuItem);

        wisataMenuItem.addActionListener(e -> showPanel(new WisataPanel()));

        // Hanya tampilkan menu admin jika user adalah admin
        if (currentUser.isAdmin()) {
            JMenuItem kategoriMenuItem = new JMenuItem("Kategori");
            JMenuItem fasilitasMenuItem = new JMenuItem("Fasilitas");
            dataMenu.add(kategoriMenuItem);
            dataMenu.add(fasilitasMenuItem);

            kategoriMenuItem.addActionListener(e -> showPanel(new KategoriPanel())); // Akan kita buat
            fasilitasMenuItem.addActionListener(e -> showPanel(new FasilitasPanel())); // Akan kita buat
        }

        menuBar.add(dataMenu);
    }

    private void showDefaultPanel() {
        // Tampilkan panel destinasi wisata sebagai default
        showPanel(new WisataPanel());
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll(); // Hapus panel sebelumnya
        contentPanel.add(panel, BorderLayout.CENTER); // Tambahkan panel baru
        contentPanel.revalidate(); // Validasi ulang layout
        contentPanel.repaint(); // Gambar ulang komponen
    }
}