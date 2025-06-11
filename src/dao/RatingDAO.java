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

    /**
     * Menyimpan objek Rating baru ke dalam tabel Ratings di database.
     * Menggunakan PreparedStatement untuk keamanan dari SQL Injection.
     * @param rating Objek Rating yang berisi data untuk disimpan.
     */
    public void addRating(Rating rating) {
        // SQL untuk memasukkan data baru
        String sql = "INSERT INTO Ratings (wisata_id, user_id, rating, komentar) VALUES (?, ?, ?, ?)";

        // Menggunakan try-with-resources untuk memastikan koneksi dan statement ditutup otomatis
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Mengatur parameter PreparedStatement
            pstmt.setInt(1, rating.getWisataId());
            pstmt.setInt(2, rating.getUserId());
            pstmt.setInt(3, rating.getRating());
            pstmt.setString(4, rating.getKomentar());
            
            // Menjalankan perintah SQL
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saat menambahkan rating: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mengambil semua rating dan komentar untuk suatu lokasi wisata tertentu.
     * Hasilnya diurutkan dari yang paling baru.
     * @param wisataId ID dari lokasi wisata.
     * @return Sebuah List yang berisi objek Rating.
     */
    public List<Rating> getRatingsByWisataId(int wisataId) {
        List<Rating> ratings = new ArrayList<>();
        // SQL untuk mengambil data rating dan menggabungkannya dengan tabel Users untuk mendapatkan nama user
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
                // Looping melalui setiap baris hasil query
                while (rs.next()) {
                    // Membuat objek Rating dari data di database
                    Rating rating = new Rating(
                        rs.getInt("id"),
                        rs.getInt("wisata_id"),
                        rs.getInt("user_id"),
                        rs.getInt("rating"),
                        rs.getString("komentar"),
                        rs.getTimestamp("created_at")
                    );
                    // Menambahkan nama user ke objek rating
                    rating.setNamaUser(rs.getString("nama"));
                    
                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil ratings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ratings; // Mengembalikan list (bisa kosong jika tidak ada rating)
    }

    /**
     * Menghitung rata-rata rating untuk suatu lokasi wisata.
     * @param wisataId ID dari lokasi wisata.
     * @return Nilai rata-rata rating dalam format double.
     */
    public double getAverageRating(int wisataId) {
        // SQL untuk menghitung rata-rata dari kolom 'rating'
        String sql = "SELECT AVG(rating) FROM Ratings WHERE wisata_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, wisataId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Jika ada hasilnya, kembalikan nilai rata-rata
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat menghitung rata-rata rating: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0; // Kembalikan 0 jika tidak ada rating atau terjadi error
    }
}