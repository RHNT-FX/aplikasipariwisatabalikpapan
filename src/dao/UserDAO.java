package dao;

import database.DatabaseManager;
import model.User;
import model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void addUser(User user) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            user.save(conn);
        }
    }

    public void updateUser(User user) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            user.update(conn);
        }
    }

    public void deleteUser(User user) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            user.delete(conn);
        }
    }

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

    public User authenticateUser(String email, String password) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            User authenticatedUser = User.authenticate(conn, email, password);
            if (authenticatedUser != null && "admin".equalsIgnoreCase(authenticatedUser.getRole())) {
                return new Admin(authenticatedUser.getId(), authenticatedUser.getNama(), authenticatedUser.getEmail(), authenticatedUser.getPassword());
            }
            return authenticatedUser;
        }
    }

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