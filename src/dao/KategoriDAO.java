// src/dao/KategoriDAO.java
package dao;

import database.DatabaseManager;
import model.Kategori;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    /**
     * Menambahkan kategori baru ke database.
     * @param kategori Objek Kategori yang akan ditambahkan.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void addKategori(Kategori kategori) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.save(conn); // Menggunakan metode save dari Recordable/Kategori
        }
    }

    /**
     * Memperbarui data kategori yang sudah ada di database.
     * @param kategori Objek Kategori yang akan diperbarui (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void updateKategori(Kategori kategori) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.update(conn); // Menggunakan metode update dari Recordable/Kategori
        }
    }

    /**
     * Menghapus kategori dari database berdasarkan ID.
     * @param kategori Objek Kategori yang akan dihapus (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void deleteKategori(Kategori kategori) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.delete(conn); // Menggunakan metode delete dari Recordable/Kategori
        }
    }

    /**
     * Mengambil kategori dari database berdasarkan ID-nya.
     * @param id ID kategori yang dicari.
     * @return Objek Kategori jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public Kategori getKategoriById(int id) throws SQLException {
        Kategori kategori = new Kategori();
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.load(conn, id); // Menggunakan metode load dari Recordable/Kategori
            return kategori;
        } catch (SQLException e) {
            if (e.getMessage().contains("tidak ditemukan")) { // Menangkap pesan spesifik dari metode load
                return null;
            }
            throw e; // Lemparkan exception jika bukan karena tidak ditemukan
        }
    }

    /**
     * Mengambil semua kategori dari database.
     * @return List objek Kategori.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public List<Kategori> getAllKategori() throws SQLException {
        List<Kategori> daftarKategori = new ArrayList<>();
        String sql = "SELECT id, nama FROM Kategori ORDER BY nama";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kategori kategori = new Kategori(
                    rs.getInt("id"),
                    rs.getString("nama")
                );
                daftarKategori.add(kategori);
            }
        }
        return daftarKategori;
    }

    /**
     * Mengambil kategori dari database berdasarkan namanya.
     * Berguna untuk pengecekan duplikasi atau pencarian.
     * @param nama Nama kategori yang dicari.
     * @return Objek Kategori jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public Kategori getKategoriByNama(String nama) throws SQLException {
        String sql = "SELECT id, nama FROM Kategori WHERE nama = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Kategori(rs.getInt("id"), rs.getString("nama"));
                }
                return null;
            }
        }
    }
}