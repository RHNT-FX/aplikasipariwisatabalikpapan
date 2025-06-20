package dao;

import database.DatabaseManager;
import model.Kategori;
import model.Wisata;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WisataDAO {

    // DAO buat wisata, biar gampang ngambil data dari db
    public List<Wisata> getAllWisata() {
        List<Wisata> wisataList = new ArrayList<>();
        String sql = "SELECT w.*, k.id AS kategori_id, k.nama AS kategori_nama " +
                     "FROM Wisata w LEFT JOIN Kategori k ON w.kategori_id = k.id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // ambil kategori, kalo ada
                Kategori kategori = null;
                int kategoriId = rs.getInt("kategori_id");
                if (!rs.wasNull()) {
                    kategori = new Kategori(kategoriId, rs.getString("kategori_nama"));
                }

                // bikin objek wisata, masukin ke list
                Wisata wisata = new Wisata(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("deskripsi"),
                    rs.getString("lokasi"),
                    rs.getDouble("harga_tiket"),
                    rs.getString("jam_operasional"),
                    kategori,
                    rs.getString("foto_path")
                );
                wisataList.add(wisata);
            }
        } catch (SQLException e) {
            // kalo error ya print aja
            System.err.println("Error saat mengambil semua data wisata: " + e.getMessage());
            e.printStackTrace();
        }
        return wisataList;
    }
    
    public List<Wisata> searchWisataByName(String keyword) {
        List<Wisata> wisataList = new ArrayList<>();
        String sql = "SELECT w.*, k.id AS kategori_id, k.nama AS kategori_nama " +
                     "FROM Wisata w LEFT JOIN Kategori k ON w.kategori_id = k.id " +
                     "WHERE w.nama LIKE ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Kategori kategori = null;
                    int kategoriId = rs.getInt("kategori_id");
                    if (!rs.wasNull()) {
                        kategori = new Kategori(kategoriId, rs.getString("kategori_nama"));
                    }

                    Wisata wisata = new Wisata(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("deskripsi"),
                        rs.getString("lokasi"),
                        rs.getDouble("harga_tiket"),
                        rs.getString("jam_operasional"),
                        kategori,
                        rs.getString("foto_path")
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


    
        public void deleteWisata(Wisata selectedWisata) throws SQLException {
            try (Connection conn = database.DatabaseManager.getConnection()) {
                selectedWisata.delete(conn);
            }
        }
    
        public void addWisata(Wisata wisata) throws SQLException {
            try (Connection conn = database.DatabaseManager.getConnection()) {
                wisata.save(conn); // Pastikan Wisata punya method save(Connection)
            }
        }
    
        public void updateWisata(Wisata wisata) throws SQLException {
            try (Connection conn = database.DatabaseManager.getConnection()) {
                wisata.update(conn); // Pastikan Wisata punya method update(Connection)
            }
        }
        
    }
