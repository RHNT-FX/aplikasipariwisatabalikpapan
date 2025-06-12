package view;

import controller.AuthManager;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel signUpLabel;

    public LoginFrame() {
        setTitle("WISATA INDAH BALIKPAPAN - Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        addListeners();
    }

    private void initComponents() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoWIB = new JLabel("WIB");
        logoWIB.setFont(new Font("Arial", Font.BOLD, 70));
        logoWIB.setForeground(Color.BLACK);
        logoWIB.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appTagline = new JLabel("WISATA INDAH BALIKPAPAN");
        appTagline.setFont(new Font("Arial", Font.BOLD, 20));
        appTagline.setForeground(Color.BLACK);
        appTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoWIB);
        headerPanel.add(appTagline);
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(Box.createVerticalGlue(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 15;
        emailField = new JTextField("User");
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        emailField.setForeground(Color.GRAY);
        emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        centerPanel.add(emailField, gbc);
        gbc.ipady = 0;

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipady = 15;
        passwordField = new JPasswordField("Password");
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setForeground(Color.GRAY);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        passwordField.setEchoChar((char) 0);
        centerPanel.add(passwordField, gbc);
        gbc.ipady = 0;

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 0, 10, 0);
        gbc.ipady = 20;
        loginButton = new JButton("Login");
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        centerPanel.add(loginButton, gbc);
        gbc.ipady = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 0, 0);
        signUpLabel = new JLabel("<html>Don't you have an account? <a href=\"#\">Sign up</a></html>");
        signUpLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signUpLabel.setForeground(Color.BLACK);
        signUpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(signUpLabel, gbc);

        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(Box.createVerticalGlue(), gbc);

        add(centerPanel, BorderLayout.CENTER);

        ImageIcon palmIcon = null;
        try {
            palmIcon = new ImageIcon(getClass().getResource("/resources/palm.png"));
            if (palmIcon.getImage() != null) {
                Image originalImage = palmIcon.getImage();
                int scaledWidth = (int) (originalImage.getWidth(null) * 1);
                int scaledHeight = (int) (originalImage.getHeight(null) * 2);
                Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                palmIcon = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar pohon palem: " + e.getMessage());
        }

        JLabel palmLabel = new JLabel(palmIcon);
        palmLabel.setHorizontalAlignment(SwingConstants.LEFT);
        palmLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));

        headerPanel.setBounds(0, 0, 800, 150);
        layeredPane.add(headerPanel, JLayeredPane.DEFAULT_LAYER);

        centerPanel.setBounds(0, 150, 800, 450);
        layeredPane.add(centerPanel, JLayeredPane.DEFAULT_LAYER);

        if (palmIcon != null) {
            palmLabel.setBounds(-100, 200, palmIcon.getIconWidth(), palmIcon.getIconHeight());
            layeredPane.add(palmLabel, JLayeredPane.PALETTE_LAYER);
        }
        
        add(layeredPane, BorderLayout.CENTER);

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

        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new RegisterPanel().setVisible(true);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

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
                dispose();
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

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('*');
                    }
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

    private void setupPlaceholder(JPasswordField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('*');
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (new String(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }
}