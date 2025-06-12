package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Wisata implements Recordable {
    private int id;
    private String nama;
    private String deskripsi;
    private String lokasi;
    private double hargaTiket;
    private String jamOperasional;
    private Kategori kategori;
    private List<Fasilitas> daftarFasilitas;

    public Wisata() {
        this.daftarFasilitas = new ArrayList<>();
    }

    public Wisata(int id, String nama, String deskripsi, String lokasi,
                  double hargaTiket, String jamOperasional, Kategori kategori) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.hargaTiket = hargaTiket;
        this.jamOperasional = jamOperasional;
        this.kategori = kategori;
        this.daftarFasilitas = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public double getHargaTiket() {
        return hargaTiket;
    }

    public void setHargaTiket(double hargaTiket) {
        this.hargaTiket = hargaTiket;
    }

    public String getJamOperasional() {
        return jamOperasional;
    }

    public void setJamOperasional(String jamOperasional) {
        this.jamOperasional = jamOperasional;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public List<Fasilitas> getDaftarFasilitas() {
        return daftarFasilitas;
    }

    public void setDaftarFasilitas(List<Fasilitas> daftarFasilitas) {
        this.daftarFasilitas = daftarFasilitas;
    }

    public void addFasilitas(Fasilitas fasilitas) {
        if (!this.daftarFasilitas.contains(fasilitas)) {
            this.daftarFasilitas.add(fasilitas);
        }
    }

    public void removeFasilitas(Fasilitas fasilitas) {
        this.daftarFasilitas.remove(fasilitas);
    }

    @Override
    public void save(Connection conn) throws SQLException {
        String sql = "INSERT INTO Wisata (nama, deskripsi, lokasi, harga_tiket, jam_operasional, kategori_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, this.nama);
            pstmt.setString(2, this.deskripsi);
            pstmt.setString(3, this.lokasi);
            pstmt.setDouble(4, this.hargaTiket);
            pstmt.setString(5, this.jamOperasional);
            pstmt.setInt(6, (this.kategori != null) ? this.kategori.getId() : null);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
            saveFasilitasRelasi(conn);
        }
    }

    @Override
    public void update(Connection conn) throws SQLException {
        String sql = "UPDATE Wisata SET nama = ?, deskripsi = ?, lokasi = ?, harga_tiket = ?, jam_operasional = ?, kategori_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.nama);
            pstmt.setString(2, this.deskripsi);
            pstmt.setString(3, this.lokasi);
            pstmt.setDouble(4, this.hargaTiket);
            pstmt.setString(5, this.jamOperasional);
            pstmt.setInt(6, (this.kategori != null) ? this.kategori.getId() : null);
            pstmt.setInt(7, this.id);
            pstmt.executeUpdate();
            updateFasilitasRelasi(conn);
        }
    }

    @Override
    public void delete(Connection conn) throws SQLException {
        String deleteFasilitasSql = "DELETE FROM Wisata_Fasilitas WHERE wisata_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteFasilitasSql)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }

        String deleteWisataSql = "DELETE FROM Wisata WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteWisataSql)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void load(Connection conn, int id) throws SQLException {
        String sql = "SELECT w.id, w.nama, w.deskripsi, w.lokasi, w.harga_tiket, w.jam_operasional, " +
                     "k.id AS kategori_id, k.nama AS kategori_nama " +
                     "FROM Wisata w LEFT JOIN Kategori k ON w.kategori_id = k.id " +
                     "WHERE w.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.nama = rs.getString("nama");
                    this.deskripsi = rs.getString("deskripsi");
                    this.lokasi = rs.getString("lokasi");
                    this.hargaTiket = rs.getDouble("harga_tiket");
                    this.jamOperasional = rs.getString("jam_operasional");

                    int kategoriId = rs.getInt("kategori_id");
                    if (!rs.wasNull()) {
                        String kategoriNama = rs.getString("kategori_nama");
                        this.kategori = new Kategori(kategoriId, kategoriNama);
                    } else {
                        this.kategori = null;
                    }

                    loadFasilitasRelasi(conn);
                } else {
                    throw new SQLException("Wisata dengan ID " + id + " tidak ditemukan.");
                }
            }
        }
    }

    private void saveFasilitasRelasi(Connection conn) throws SQLException {
        if (this.id == 0) {
            throw new IllegalStateException("Wisata harus disimpan terlebih dahulu sebelum menyimpan fasilitas relasi.");
        }
        String deleteSql = "DELETE FROM Wisata_Fasilitas WHERE wisata_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }

        String insertSql = "INSERT INTO Wisata_Fasilitas (wisata_id, fasilitas_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Fasilitas fasilitas : daftarFasilitas) {
                pstmt.setInt(1, this.id);
                pstmt.setInt(2, fasilitas.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void updateFasilitasRelasi(Connection conn) throws SQLException {
        saveFasilitasRelasi(conn);
    }

    private void loadFasilitasRelasi(Connection conn) throws SQLException {
        this.daftarFasilitas.clear();
        String sql = "SELECT f.id, f.nama FROM Fasilitas f " +
                     "JOIN Wisata_Fasilitas wf ON f.id = wf.fasilitas_id " +
                     "WHERE wf.wisata_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Fasilitas fasilitas = new Fasilitas(rs.getInt("id"), rs.getString("nama"));
                    this.daftarFasilitas.add(fasilitas);
                }
            }
        }
    }

    public void tampilkanInfo() {
        System.out.println("--- Detail Wisata ---");
        System.out.println("Nama: " + this.nama);
        System.out.println("Deskripsi: " + this.deskripsi);
        System.out.println("Lokasi: " + this.lokasi);
        System.out.println("Harga Tiket: " + this.hargaTiket);
        System.out.println("Jam Operasional: " + this.jamOperasional);
        System.out.println("Kategori: " + (this.kategori != null ? this.kategori.getNama() : "Tidak ada"));
        System.out.print("Fasilitas: ");
        if (this.daftarFasilitas.isEmpty()) {
            System.out.println("Tidak ada fasilitas.");
        } else {
            for (int i = 0; i < this.daftarFasilitas.size(); i++) {
                System.out.print(this.daftarFasilitas.get(i).getNama());
                if (i < this.daftarFasilitas.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
        System.out.println("---------------------");
    }

    @Override
    public String toString() {
        return nama + " - " + lokasi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wisata wisata = (Wisata) o;
        return id == wisata.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}