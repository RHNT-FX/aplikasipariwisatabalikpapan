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

    public void addFasilitas(Fasilitas fasilitas) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            fasilitas.save(conn);
        }
    }

    public void updateFasilitas(Fasilitas fasilitas) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            fasilitas.update(conn);
        }
    }

    public void deleteFasilitas(Fasilitas fasilitas) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            fasilitas.delete(conn);
        }
    }

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