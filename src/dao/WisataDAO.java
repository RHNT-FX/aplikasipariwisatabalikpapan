package dao;

import database.DatabaseManager;
import model.Kategori; // Import kelas Kategori
import model.Wisata;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk mengelola operasi CRUD (Create, Read, Update, Delete)
 * yang berkaitan dengan entitas Wisata.
 */
public class WisataDAO {

    /**
     * Mengambil semua data wisata dari database.
     * PERBAIKAN: Melakukan JOIN dengan tabel Kategori untuk mendapatkan data kategori secara lengkap.
     * @return Sebuah List yang berisi semua objek Wisata.
     */
    public List<Wisata> getAllWisata() {
        List<Wisata> wisataList = new ArrayList<>();
        // PERBAIKAN: SQL diubah untuk JOIN dengan tabel Kategori
        String sql = "SELECT w.*, k.id AS kategori_id, k.nama AS kategori_nama " +
                     "FROM Wisata w LEFT JOIN Kategori k ON w.kategori_id = k.id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // PERBAIKAN: Membuat objek Kategori terlebih dahulu
                Kategori kategori = null;
                int kategoriId = rs.getInt("kategori_id");
                if (!rs.wasNull()) { // Cek jika wisata punya kategori
                    kategori = new Kategori(kategoriId, rs.getString("kategori_nama"));
                }

                // PERBAIKAN: Membuat objek Wisata dengan objek Kategori, bukan int
                Wisata wisata = new Wisata(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("deskripsi"),
                    rs.getString("lokasi"),
                    rs.getDouble("harga_tiket"),
                    rs.getString("jam_operasional"),
                    kategori // Menggunakan objek Kategori yang sudah dibuat
                );
                wisataList.add(wisata);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua data wisata: " + e.getMessage());
            e.printStackTrace();
        }
        return wisataList;
    }
    
    /**
     * Mencari data wisata berdasarkan kata kunci pada nama.
     * Sama seperti di atas, metode ini juga diubah untuk menyertakan data Kategori.
     * @param keyword Kata kunci untuk pencarian.
     * @return Sebuah List yang berisi objek Wisata yang cocok dengan kriteria.
     */
    public List<Wisata> searchWisataByName(String keyword) {
        List<Wisata> wisataList = new ArrayList<>();
        // PERBAIKAN: SQL diubah untuk JOIN dengan tabel Kategori
        String sql = "SELECT w.*, k.id AS kategori_id, k.nama AS kategori_nama " +
                     "FROM Wisata w LEFT JOIN Kategori k ON w.kategori_id = k.id " +
                     "WHERE w.nama LIKE ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                     // PERBAIKAN: Membuat objek Kategori terlebih dahulu
                    Kategori kategori = null;
                    int kategoriId = rs.getInt("kategori_id");
                    if (!rs.wasNull()) {
                        kategori = new Kategori(kategoriId, rs.getString("kategori_nama"));
                    }

                    // PERBAIKAN: Membuat objek Wisata dengan objek Kategori
                    Wisata wisata = new Wisata(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("deskripsi"),
                        rs.getString("lokasi"),
                        rs.getDouble("harga_tiket"),
                        rs.getString("jam_operasional"),
                        kategori
                    );
                    wisataList.add(wisata);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mencari data wisata: " + e.getMessage());
            e.printStackTrace();
        }
        return wisataList;
    }
}