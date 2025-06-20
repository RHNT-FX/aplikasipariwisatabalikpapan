package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class Kategori implements Recordable {
    // id sama nama, simple aja
    private int id;
    private String nama;

    // konstruktor default, biar aman
    public Kategori() {}

    // konstruktor kalo mau langsung isi id & nama
    public Kategori(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    // konstruktor kalo cuma nama doang
    public Kategori(String nama) {
        this.nama = nama;
    }

    // getter id, ya gitu
    @Override
    public int getId() {
        return id;
    }

    // setter id, biar bisa diubah
    @Override
    public void setId(int id) {
        this.id = id;
    }

    // getter nama
    public String getNama() {
        return nama;
    }

    // setter nama
    public void setNama(String nama) {
        this.nama = nama;
    }

    // simpen ke db, biar ga ilang
    @Override
    public void save(Connection conn) throws SQLException {
        String sql = "INSERT INTO Kategori (nama) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, this.nama);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    // update data di db
    @Override
    public void update(Connection conn) throws SQLException {
        String sql = "UPDATE Kategori SET nama = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.nama);
            pstmt.setInt(2, this.id);
            pstmt.executeUpdate();
        }
    }

    // hapus dari db
    @Override
    public void delete(Connection conn) throws SQLException {
        String sql = "DELETE FROM Kategori WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }
    }

    // ambil data dari db
    @Override
    public void load(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, nama FROM Kategori WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.nama = rs.getString("nama");
                } else {
                    throw new SQLException("Kategori dengan ID " + id + " tidak ditemukan.");
                }
            }
        }
    }

    // biar kalo di-print ga aneh
    @Override
    public String toString() {
        return nama;
    }

    // biar equals-nya ga ngaco
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kategori kategori = (Kategori) o;
        return id == kategori.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}