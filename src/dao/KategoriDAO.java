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

    public void addKategori(Kategori kategori) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.save(conn);
        }
    }

    public void updateKategori(Kategori kategori) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.update(conn);
        }
    }

    public void deleteKategori(Kategori kategori) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.delete(conn);
        }
    }

    public Kategori getKategoriById(int id) throws SQLException {
        Kategori kategori = new Kategori();
        try (Connection conn = DatabaseManager.getConnection()) {
            kategori.load(conn, id);
            return kategori;
        } catch (SQLException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return null;
            }
            throw e;
        }
    }

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