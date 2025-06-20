package dao;

import database.DatabaseManager;
import model.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

        public boolean hasUserRated(int wisataId, int userId) {
        String sql = "SELECT COUNT(*) FROM Ratings WHERE wisata_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, wisataId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat cek rating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void addRating(Rating rating) {
        String sql = "INSERT INTO Ratings (wisata_id, user_id, rating, komentar) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rating.getWisataId());
            pstmt.setInt(2, rating.getUserId());
            pstmt.setInt(3, rating.getRating());
            pstmt.setString(4, rating.getKomentar());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saat menambahkan rating: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Rating> getRatingsByWisataId(int wisataId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = """
                     SELECT r.id, r.wisata_id, r.user_id, r.rating, r.komentar, r.created_at, u.nama 
                     FROM Ratings r
                     JOIN Users u ON r.user_id = u.id
                     WHERE r.wisata_id = ?
                     ORDER BY r.created_at DESC
                     """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, wisataId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Rating rating = new Rating(
                        rs.getInt("id"),
                        rs.getInt("wisata_id"),
                        rs.getInt("user_id"),
                        rs.getInt("rating"),
                        rs.getString("komentar"),
                        rs.getTimestamp("created_at")
                    );
                    rating.setNamaUser(rs.getString("nama"));
                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            // kalo error ya print aja
            System.err.println("Error saat mengambil ratings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ratings;
    }

    public double getAverageRating(int wisataId) {
        String sql = "SELECT AVG(rating) FROM Ratings WHERE wisata_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, wisataId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat menghitung rata-rata rating: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
}