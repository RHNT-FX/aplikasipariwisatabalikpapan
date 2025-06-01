// src/view/LoginFrame.java
package view;

import controller.AuthManager;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Jendela login untuk otentikasi pengguna dengan desain yang diperbarui.
 */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel signUpLabel; // Tautan "Sign up"

    public LoginFrame() {
        setTitle("WISATA INDAH BALIKPAPAN - Login");
        setSize(800, 600); // Ukuran lebih besar agar desain terlihat jelas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Menempatkan frame di tengah layar
        setResizable(false); // Agar desain tidak rusak saat diresize

        initComponents();
        addListeners();
    }

    private void initComponents() {
        // Mengatur latar belakang frame menjadi putih
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // --- Panel Header (Logo dan Nama Aplikasi) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS)); // Vertikal
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0)); // Padding atas
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoWIB = new JLabel("WIB");
        logoWIB.setFont(new Font("Arial", Font.BOLD, 70)); // Font tebal, ukuran besar
        logoWIB.setForeground(Color.BLACK);
        logoWIB.setAlignmentX(Component.CENTER_ALIGNMENT); // Pusatkan

        JLabel appTagline = new JLabel("WISATA INDAH BALIKPAPAN");
        appTagline.setFont(new Font("Arial", Font.BOLD, 20)); // Font tebal, ukuran sedang
        appTagline.setForeground(Color.BLACK);
        appTagline.setAlignmentX(Component.CENTER_ALIGNMENT); // Pusatkan

        headerPanel.add(logoWIB);
        headerPanel.add(appTagline);
        add(headerPanel, BorderLayout.NORTH);

        // --- Panel Tengah (Input Fields dan Tombol) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout()); // GridBagLayout untuk kontrol posisi
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50)); // Padding horizontal

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Jarak antar komponen
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER; // Pusatkan komponen di sel

        // Placeholder untuk menempatkan input field dan tombol di tengah secara vertikal
        // Ini adalah trik GridBagLayout: gunakan komponen kosong dengan weighty untuk mendorong
        gbc.weighty = 0.5; // Mengambil ruang vertikal di atas
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(Box.createVerticalGlue(), gbc); // Komponen kosong di atas

        // Email Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 15; // Padding internal vertikal untuk tinggi field
        emailField = new JTextField("User"); // Placeholder text "User"
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        emailField.setForeground(Color.GRAY); // Warna teks placeholder
        emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Border tipis
        centerPanel.add(emailField, gbc);
        gbc.ipady = 0; // Reset ipady

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipady = 15; // Padding internal vertikal
        passwordField = new JPasswordField("Password"); // Placeholder text "Password"
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setForeground(Color.GRAY); // Warna teks placeholder
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        passwordField.setEchoChar((char) 0); // Tampilkan teks "Password" awalnya
        centerPanel.add(passwordField, gbc);
        gbc.ipady = 0; // Reset ipady

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 0, 10, 0); // Lebih banyak jarak di atas tombol
        gbc.ipady = 20; // Tinggi tombol
        loginButton = new JButton("Login");
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.setBorderPainted(false); // Hapus border default
        loginButton.setFocusPainted(false); // Hapus highlight fokus
        centerPanel.add(loginButton, gbc);
        gbc.ipady = 0; // Reset ipady
        gbc.insets = new Insets(10, 0, 10, 0); // Reset insets

        // Tautan Sign Up
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 0, 0); // Jarak kecil di bawah tombol
        signUpLabel = new JLabel("<html>Don't you have an account? <a href=\"#\">Sign up</a></html>");
        signUpLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signUpLabel.setForeground(Color.BLACK);
        signUpLabel.setHorizontalAlignment(SwingConstants.CENTER); // Pusatkan teks
        centerPanel.add(signUpLabel, gbc);

        // Placeholder untuk menempatkan input field dan tombol di tengah secara vertikal
        gbc.weighty = 0.5; // Mengambil ruang vertikal di bawah
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(Box.createVerticalGlue(), gbc); // Komponen kosong di bawah

        add(centerPanel, BorderLayout.CENTER);

        // --- Panel Kaki (Gambar Pohon Palem) ---
        // Jika kamu memiliki gambar pohon palem transparan, letakkan di src/resources/palm_trees.png
        ImageIcon palmIcon = null;
        try {
            // Memastikan gambar dimuat dari classpath, penting di VS Code
            palmIcon = new ImageIcon(getClass().getResource("/resources/palm.png"));
            // Skalakan gambar agar sesuai, jika perlu
            if (palmIcon.getImage() != null) {
                Image originalImage = palmIcon.getImage();
                // Atur skala sesuai keinginan, misalnya 30% dari ukuran aslinya
                int scaledWidth = (int) (originalImage.getWidth(null) * 1);
                int scaledHeight = (int) (originalImage.getHeight(null) * 2);
                Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                palmIcon = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar pohon palem: " + e.getMessage());
            // Gunakan gambar placeholder atau tidak tampilkan sama sekali jika gambar tidak ada
        }

        JLabel palmLabel = new JLabel(palmIcon);
        palmLabel.setHorizontalAlignment(SwingConstants.LEFT); // Posisikan di kiri bawah
        palmLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        // Gunakan JLayeredPane untuk menempatkan pohon palem di atas konten lain
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600)); // Ukuran layered pane sama dengan frame

        // Tambahkan panel header dan center ke layered pane pada layer default
        headerPanel.setBounds(0, 0, 800, 150); // Sesuaikan batas
        layeredPane.add(headerPanel, JLayeredPane.DEFAULT_LAYER);

        centerPanel.setBounds(0, 150, 800, 450); // Sesuaikan batas
        layeredPane.add(centerPanel, JLayeredPane.DEFAULT_LAYER);

        // Tambahkan palmLabel ke layered pane pada layer di atasnya
        if (palmIcon != null) {
            // Sesuaikan posisi dan ukuran label pohon palem
            // Misalnya, letakkan di kiri bawah, ukurannya sesuai gambar
            palmLabel.setBounds(-100, 200, palmIcon.getIconWidth(), palmIcon.getIconHeight()); // X, Y, Width, Height
            layeredPane.add(palmLabel, JLayeredPane.PALETTE_LAYER); // Letakkan di layer yang lebih tinggi
        }
        
        add(layeredPane, BorderLayout.CENTER); // Tambahkan layeredPane ke frame

        // Placeholder text behavior
        setupPlaceholder(emailField, "User");
        setupPlaceholder(passwordField, "Password");
    }

    private void addListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Listener untuk tautan "Sign up" (saat ini hanya placeholder)
        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Fungsionalitas Sign Up belum diimplementasikan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                // Di sini nanti bisa membuka dialog atau frame registrasi baru
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR)); // Ubah kursor menjadi tangan
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Kembali ke kursor default
            }
        });
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Hapus teks placeholder jika masih ada
        if (email.equals("User")) {
            email = "";
        }
        if (password.equals("Password")) {
            password = "";
        }

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email dan Password tidak boleh kosong.", "Validasi Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User loggedInUser = AuthManager.login(email, password);

            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(this, "Login Berhasil sebagai " + loggedInUser.getRole() + "!", "Login Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Tutup jendela login
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Email atau password salah. Silakan coba lagi.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan database: " + ex.getMessage() + "\nMohon coba lagi nanti.", "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Metode bantuan untuk placeholder text behavior
    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('*'); // Tampilkan karakter password
                    }
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0); // Sembunyikan karakter password
                    }
                }
            }
        });
    }

    // Overload untuk JPasswordField
    private void setupPlaceholder(JPasswordField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('*'); // Tampilkan karakter password
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (new String(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0); // Sembunyikan karakter password
                }
            }
        });
    }
}