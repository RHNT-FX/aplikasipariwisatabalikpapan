// src/dao/UserDAO.java
package dao;

import database.DatabaseManager;
import model.User;
import model.Admin; // Import Admin agar bisa mengembalikan objek Admin jika role-nya admin

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Menambahkan pengguna baru ke database.
     * @param user Objek User (atau Admin) yang akan ditambahkan.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void addUser(User user) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            user.save(conn);
        }
    }

    /**
     * Memperbarui data pengguna yang sudah ada di database.
     * @param user Objek User (atau Admin) yang akan diperbarui (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void updateUser(User user) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            user.update(conn);
        }
    }

    /**
     * Menghapus pengguna dari database berdasarkan ID.
     * @param user Objek User (atau Admin) yang akan dihapus (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void deleteUser(User user) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            user.delete(conn);
        }
    }

    /**
     * Mengambil pengguna dari database berdasarkan ID-nya.
     * Mengembalikan objek User atau Admin berdasarkan role-nya.
     * @param id ID pengguna yang dicari.
     * @return Objek User (atau Admin) jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public User getUserById(int id) throws SQLException {
        User user = null;
        String sql = "SELECT id, nama, email, password, role FROM Users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("admin".equalsIgnoreCase(role)) {
                        user = new Admin(
                            rs.getInt("id"),
                            rs.getString("nama"),
                            rs.getString("email"),
                            rs.getString("password")
                        );
                    } else {
                        user = new User(
                            rs.getInt("id"),
                            rs.getString("nama"),
                            rs.getString("email"),
                            rs.getString("password"),
                            role
                        );
                    }
                }
            }
        }
        return user;
    }

    /**
     * Mengambil semua pengguna dari database.
     * @return List objek User (termasuk Admin).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> daftarUsers = new ArrayList<>();
        String sql = "SELECT id, nama, email, password, role FROM Users ORDER BY nama";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String role = rs.getString("role");
                User user;
                if ("admin".equalsIgnoreCase(role)) {
                    user = new Admin(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                } else {
                    user = new User(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("password"),
                        role
                    );
                }
                daftarUsers.add(user);
            }
        }
        return daftarUsers;
    }

    /**
     * Mengotentikasi pengguna berdasarkan email dan password.
     * @param email Email pengguna.
     * @param password Password pengguna.
     * @return Objek User (atau Admin) yang berhasil login, null jika otentikasi gagal.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public User authenticateUser(String email, String password) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            User authenticatedUser = User.authenticate(conn, email, password);
            if (authenticatedUser != null && "admin".equalsIgnoreCase(authenticatedUser.getRole())) {
                // Jika user adalah admin, kembalikan objek Admin
                return new Admin(authenticatedUser.getId(), authenticatedUser.getNama(), authenticatedUser.getEmail(), authenticatedUser.getPassword());
            }
            return authenticatedUser; // Kembalikan objek User biasa atau null
        }
    }
        /**
     * Mendaftarkan pengguna baru ke database.
     * @param user Objek User yang berisi data registrasi (nama, email, password, role).
     * @return true jika pendaftaran berhasil, false jika gagal.
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO Users (nama, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getNama());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.executeUpdate();
        return true;
        } catch (SQLException e) {
            e.printStackTrace();
        return false;
        }
    }
}