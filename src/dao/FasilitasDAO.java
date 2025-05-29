// src/dao/FasilitasDAO.java
package dao;

import database.DatabaseManager;
import model.Fasilitas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FasilitasDAO {

    /**
     * Menambahkan fasilitas baru ke database.
     * @param fasilitas Objek Fasilitas yang akan ditambahkan.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void addFasilitas(Fasilitas fasilitas) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            fasilitas.save(conn);
        }
    }

    /**
     * Memperbarui data fasilitas yang sudah ada di database.
     * @param fasilitas Objek Fasilitas yang akan diperbarui (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void updateFasilitas(Fasilitas fasilitas) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            fasilitas.update(conn);
        }
    }

    /**
     * Menghapus fasilitas dari database berdasarkan ID.
     * @param fasilitas Objek Fasilitas yang akan dihapus (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void deleteFasilitas(Fasilitas fasilitas) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            fasilitas.delete(conn);
        }
    }

    /**
     * Mengambil fasilitas dari database berdasarkan ID-nya.
     * @param id ID fasilitas yang dicari.
     * @return Objek Fasilitas jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public Fasilitas getFasilitasById(int id) throws SQLException {
        Fasilitas fasilitas = new Fasilitas();
        try (Connection conn = DatabaseManager.getConnection()) {
            fasilitas.load(conn, id);
            return fasilitas;
        } catch (SQLException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Mengambil semua fasilitas dari database.
     * @return List objek Fasilitas.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public List<Fasilitas> getAllFasilitas() throws SQLException {
        List<Fasilitas> daftarFasilitas = new ArrayList<>();
        String sql = "SELECT id, nama FROM Fasilitas ORDER BY nama";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Fasilitas fasilitas = new Fasilitas(
                    rs.getInt("id"),
                    rs.getString("nama")
                );
                daftarFasilitas.add(fasilitas);
            }
        }
        return daftarFasilitas;
    }

    /**
     * Mengambil fasilitas dari database berdasarkan namanya.
     * Berguna untuk pengecekan duplikasi atau pencarian.
     * @param nama Nama fasilitas yang dicari.
     * @return Objek Fasilitas jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public Fasilitas getFasilitasByNama(String nama) throws SQLException {
        String sql = "SELECT id, nama FROM Fasilitas WHERE nama = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Fasilitas(rs.getInt("id"), rs.getString("nama"));
                }
                return null;
            }
        }
    }
}