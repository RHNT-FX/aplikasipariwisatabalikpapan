package app;

import database.DatabaseManager;
import view.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Inisialisasi database dan buat tabel kalo belum ada
        DatabaseManager.createTables();

    // ngejalanin GUI LoginFrame di thread Swing//
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        System.out.println("Aplikasi Pariwisata Balikpapan dimulai!");
    }
}