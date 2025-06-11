package view;

import controller.AuthManager;
import model.User;
import model.Wisata; // Import kelas Wisata

import javax.swing.*;
import java.awt.*;

/**
 * Jendela utama aplikasi setelah pengguna berhasil login.
 * Bertindak sebagai controller utama untuk navigasi antar panel.
 */
public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private User currentUser;

    public MainFrame() {
        currentUser = AuthManager.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Sesi pengguna tidak valid. Silakan login kembali.", "Error Sesi", JOptionPane.ERROR_MESSAGE);
            new LoginFrame().setVisible(true); // Asumsi LoginFrame ada
            dispose();
            return;
        }

        setTitle("Aplikasi Pariwisata Balikpapan - " + (currentUser.isAdmin() ? "Admin" : "Pengguna"));
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        createMenus();
        showWisataPanel(); // Menampilkan panel default saat start
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createMenus() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

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

        JMenu dataMenu = new JMenu("Navigasi");
        JMenuItem wisataMenuItem = new JMenuItem("Daftar Wisata");
        dataMenu.add(wisataMenuItem);

        // Menu ini akan membawa pengguna kembali ke daftar wisata utama
        wisataMenuItem.addActionListener(e -> showWisataPanel());

        if (currentUser.isAdmin()) {
            JMenuItem kategoriMenuItem = new JMenuItem("Kelola Kategori");
            JMenuItem fasilitasMenuItem = new JMenuItem("Kelola Fasilitas");
            dataMenu.add(kategoriMenuItem);
            dataMenu.add(fasilitasMenuItem);

            kategoriMenuItem.addActionListener(e -> showPanel(new KategoriPanel()));
            fasilitasMenuItem.addActionListener(e -> showPanel(new FasilitasPanel()));
        }
        menuBar.add(dataMenu);
    }

    /**
     * Metode inti untuk mengganti panel yang ditampilkan di contentPanel.
     * Dibuat private karena hanya akan dipanggil oleh metode navigasi publik.
     * @param panel Panel yang akan ditampilkan.
     */
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- METODE NAVIGASI PUBLIK ---

    /**
     * KODE BARU
     * Menampilkan panel daftar wisata utama. Ini adalah "halaman utama" untuk pengguna.
     */
    public void showWisataPanel() {
        // Kita teruskan 'this' (instance MainFrame) ke WisataPanel
        // agar WisataPanel bisa memanggil kembali MainFrame untuk navigasi.
        showPanel(new WisataPanel(this));
    }

    /**
     * KODE BARU
     * Menampilkan panel detail untuk wisata yang dipilih.
     * @param wisata Objek Wisata yang akan ditampilkan.
     */
    public void showDetailWisata(Wisata wisata) {
        // Teruskan objek wisata, user yang login, dan instance MainFrame
        DetailWisataPanel detailPanel = new DetailWisataPanel(wisata, this.currentUser, this);
        showPanel(detailPanel);
    }
}