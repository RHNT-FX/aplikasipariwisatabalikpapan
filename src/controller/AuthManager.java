// src/controller/AuthManager.java
package controller;

import dao.UserDAO;
import model.User;
import java.sql.SQLException;

/**
 * Kelas ini bertanggung jawab untuk mengelola otentikasi pengguna.
 */
public class AuthManager {
    private static User currentUser; // Menyimpan user yang sedang login

    /**
     * Mencoba melakukan login pengguna.
     * @param email Email pengguna.
     * @param password Password pengguna.
     * @return User yang berhasil login (bisa User atau Admin), null jika gagal.
     * @throws SQLException Jika terjadi masalah koneksi atau query database.
     */
    public static User login(String email, String password) throws SQLException {
        UserDAO userDAO = new UserDAO();
        currentUser = userDAO.authenticateUser(email, password);
        return currentUser;
    }

    /**
     * Mendapatkan user yang sedang login saat ini.
     * @return Objek User yang sedang login, null jika belum ada yang login.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Memeriksa apakah user yang sedang login adalah Admin.
     * @return true jika user adalah Admin, false jika bukan atau belum login.
     */
    public static boolean isAdminLoggedIn() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Melakukan logout user saat ini.
     */
    public static void logout() {
        currentUser = null;
    }
}