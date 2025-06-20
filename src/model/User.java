package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class User implements Recordable {
    // field2 basic user, ya gitu lah
    protected int id;
    protected String nama;
    protected String email;
    protected String password;
    protected String role;

    // default user, role-nya user
    public User() {
        this.role = "user";
    }

    // kalo mau langsung isi semua
    public User(int id, String nama, String email, String password, String role) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // getter setter, yaa biar gampang
    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // cek admin apa bukan, biar gampang
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    // simpen ke db
    @Override
    public void save(Connection conn) throws SQLException {
        String sql = "INSERT INTO Users (nama, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, this.nama);
            pstmt.setString(2, this.email);
            pstmt.setString(3, this.password);
            pstmt.setString(4, this.role);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    // update ke db
    @Override
    public void update(Connection conn) throws SQLException {
        String sql = "UPDATE Users SET nama = ?, email = ?, password = ?, role = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.nama);
            pstmt.setString(2, this.email);
            pstmt.setString(3, this.password);
            pstmt.setString(4, this.role);
            pstmt.setInt(5, this.id);
            pstmt.executeUpdate();
        }
    }

    // hapus dari db
    @Override
    public void delete(Connection conn) throws SQLException {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }
    }

    // ambil data dari db
    @Override
    public void load(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, nama, email, password, role FROM Users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.nama = rs.getString("nama");
                    this.email = rs.getString("email");
                    this.password = rs.getString("password");
                    this.role = rs.getString("role");
                } else {
                    throw new SQLException("User dengan ID " + id + " tidak ditemukan.");
                }
            }
        }
    }

    // buat login, yaa gitu
    public static User authenticate(Connection conn, String email, String password) throws SQLException {
        String sql = "SELECT id, nama, email, password, role FROM Users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
                return null;
            }
        }
    }

    // buat liat detail wisata, biar keren aja
    public void lihatDetail(Wisata wisata) {
        System.out.println("Pengguna " + this.nama + " melihat detail wisata:");
        wisata.tampilkanInfo();
    }

    // biar kalo di-print ga aneh
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", nama='" + nama + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               '}';
    }

    // biar equals-nya ga ngaco
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}