// src/dao/WisataDAO.java
package dao;

import database.DatabaseManager;
import model.Fasilitas;
import model.Kategori;
import model.Wisata;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WisataDAO {

    /**
     * Menambahkan destinasi wisata baru ke database.
     * @param wisata Objek Wisata yang akan ditambahkan.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void addWisata(Wisata wisata) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Mulai transaksi
            try {
                wisata.save(conn); // Menggunakan metode save dari Recordable/Wisata
                conn.commit(); // Commit transaksi jika berhasil
            } catch (SQLException e) {
                conn.rollback(); // Rollback jika ada kesalahan
                throw e;
            } finally {
                conn.setAutoCommit(true); // Kembalikan ke auto-commit
            }
        }
    }

    /**
     * Memperbarui data destinasi wisata yang sudah ada di database.
     * @param wisata Objek Wisata yang akan diperbarui (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void updateWisata(Wisata wisata) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Mulai transaksi
            try {
                wisata.update(conn); // Menggunakan metode update dari Recordable/Wisata
                conn.commit(); // Commit transaksi jika berhasil
            } catch (SQLException e) {
                conn.rollback(); // Rollback jika ada kesalahan
                throw e;
            } finally {
                conn.setAutoCommit(true); // Kembalikan ke auto-commit
            }
        }
    }

    /**
     * Menghapus destinasi wisata dari database berdasarkan ID.
     * @param wisata Objek Wisata yang akan dihapus (ID harus sudah ada).
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public void deleteWisata(Wisata wisata) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Mulai transaksi
            try {
                wisata.delete(conn); // Menggunakan metode delete dari Recordable/Wisata
                conn.commit(); // Commit transaksi jika berhasil
            } catch (SQLException e) {
                conn.rollback(); // Rollback jika ada kesalahan
                throw e;
            } finally {
                conn.setAutoCommit(true); // Kembalikan ke auto-commit
            }
        }
    }

    /**
     * Mengambil destinasi wisata dari database berdasarkan ID-nya.
     * Memuat juga Kategori dan Fasilitas terkait.
     * @param id ID wisata yang dicari.
     * @return Objek Wisata jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public Wisata getWisataById(int id) throws SQLException {
        Wisata wisata = new Wisata();
        try (Connection conn = DatabaseManager.getConnection()) {
            wisata.load(conn, id); // Menggunakan metode load dari Recordable/Wisata

            // Penggunaan langsung kelas Fasilitas
            List<Fasilitas> daftarFasilitas = wisata.getDaftarFasilitas(); // Pastikan Wisata punya getter ini
            for (Fasilitas fasilitas :daftarFasilitas) {
                System.out.println("Fasilitas: " + fasilitas.getNama());
            }
            

            return wisata;
        } catch (SQLException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Mengambil semua destinasi wisata dari database.
     * Memuat juga Kategori dan Fasilitas terkait untuk setiap wisata.
     * @return List objek Wisata.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public List<Wisata> getAllWisata() throws SQLException {
        List<Wisata> daftarWisata = new ArrayList<>();
        String sql = "SELECT w.id, w.nama, w.deskripsi, w.lokasi, w.harga_tiket, w.jam_operasional, " +
                     "k.id AS kategori_id, k.nama AS kategori_nama " +
                     "FROM Wisata w LEFT JOIN Kategori k ON w.kategori_id = k.id " +
                     "ORDER BY w.nama";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kategori kategori = null;
                int kategoriId = rs.getInt("kategori_id");
                if (!rs.wasNull()) { // Cek apakah kategori_id tidak NULL
                    kategori = new Kategori(kategoriId, rs.getString("kategori_nama"));
                }

                Wisata wisata = new Wisata(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("deskripsi"),
                    rs.getString("lokasi"),
                    rs.getDouble("harga_tiket"),
                    rs.getString("jam_operasional"),
                    kategori
                );
                // Muat fasilitas untuk setiap wisata
                wisata.load(conn, wisata.getId()); // Menggunakan metode load untuk melengkapi fasilitas
                daftarWisata.add(wisata);
            }
        }
        return daftarWisata;
    }

    /**
     * Mencari destinasi wisata berdasarkan nama atau deskripsi.
     * @param keyword Kata kunci pencarian.
     * @return List objek Wisata yang cocok.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    public List<Wisata> searchWisata(String keyword) throws SQLException {
        List<Wisata> hasilPencarian = new ArrayList<>();
        String sql = "SELECT w.id, w.nama, w.deskripsi, w.lokasi, w.harga_tiket, w.jam_operasional, " +
                     "k.id AS kategori_id, k.nama AS kategori_nama " +
                     "FROM Wisata w LEFT JOIN Kategori k ON w.kategori_id = k.id " +
                     "WHERE w.nama LIKE ? OR w.deskripsi LIKE ? OR w.lokasi LIKE ? " +
                     "ORDER BY w.nama";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

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
                        kategori
                    );
                    wisata.load(conn, wisata.getId()); // Muat fasilitas
                    hasilPencarian.add(wisata);
                }
            }
        }
        return hasilPencarian;
    }
}