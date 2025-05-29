// src/app/Main.java
package app;

import database.DatabaseManager;
import view.LoginFrame; // Sekarang ini sudah ada
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Inisialisasi database dan buat tabel jika belum ada
        DatabaseManager.createTables();

        // Menjalankan aplikasi GUI di Event Dispatch Thread (EDT)
        // Ini adalah praktik terbaik untuk aplikasi Swing
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        System.out.println("Aplikasi Pariwisata Balikpapan dimulai!");
    }
}