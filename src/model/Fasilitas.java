package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class Fasilitas implements Recordable {
    private int id;
    private String nama;

    public Fasilitas() {}

    public Fasilitas(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public Fasilitas(String nama) {
        this.nama = nama;
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

    @Override
    public void save(Connection conn) throws SQLException {
        String sql = "INSERT INTO Fasilitas (nama) VALUES (?)";
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

    @Override
    public void update(Connection conn) throws SQLException {
        String sql = "UPDATE Fasilitas SET nama = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.nama);
            pstmt.setInt(2, this.id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Connection conn) throws SQLException {
        String sql = "DELETE FROM Fasilitas WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void load(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, nama FROM Fasilitas WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.nama = rs.getString("nama");
                } else {
                    throw new SQLException("Fasilitas dengan ID " + id + " tidak ditemukan.");
                }
            }
        }
    }

    @Override
    public String toString() {
        return nama;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fasilitas fasilitas = (Fasilitas) o;
        return id == fasilitas.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}