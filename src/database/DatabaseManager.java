package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:pariwisata_balikpapan.db";

    /**
     * Mendapatkan koneksi ke database.
     * PERBAIKAN: Memuat driver secara manual untuk memastikan selalu ditemukan.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Gagal memuat driver SQLite JDBC. Pastikan file JAR ada di classpath.");
            e.printStackTrace();
            throw new SQLException("Driver SQLite tidak ditemukan", e);
        }
        return DriverManager.getConnection(URL);
    }

    /**
     * Membuat semua tabel yang diperlukan dalam database.
     * PERBAIKAN: Menggunakan transaksi untuk memastikan semua tabel dibuat atau tidak sama sekali.
     */
    public static void createTables() {
        try (Connection conn = getConnection()) {
            // Memulai mode transaksi
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {

                // PERBAIKAN: SQL menggunakan Text Blocks agar lebih mudah dibaca.
                stmt.execute("""
                CREATE TABLE IF NOT EXISTS Users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL DEFAULT 'user'
                );
                """);
                System.out.println("Tabel 'Users' berhasil dibuat atau sudah ada.");

                stmt.execute("""
                CREATE TABLE IF NOT EXISTS Kategori (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL UNIQUE
                );
                """);
                System.out.println("Tabel 'Kategori' berhasil dibuat atau sudah ada.");

                stmt.execute("""
                CREATE TABLE IF NOT EXISTS Wisata (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL,
                    deskripsi TEXT,
                    lokasi TEXT,
                    harga_tiket REAL DEFAULT 0.0,
                    jam_operasional TEXT,
                    kategori_id INTEGER,
                    FOREIGN KEY (kategori_id) REFERENCES Kategori(id) ON DELETE SET NULL
                );
                """);
                System.out.println("Tabel 'Wisata' berhasil dibuat atau sudah ada.");

                stmt.execute("""
                CREATE TABLE IF NOT EXISTS Fasilitas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL UNIQUE
                );
                """);
                System.out.println("Tabel 'Fasilitas' berhasil dibuat atau sudah ada.");

                stmt.execute("""
                CREATE TABLE IF NOT EXISTS Wisata_Fasilitas (
                    wisata_id INTEGER,
                    fasilitas_id INTEGER,
                    PRIMARY KEY (wisata_id, fasilitas_id),
                    FOREIGN KEY (wisata_id) REFERENCES Wisata(id) ON DELETE CASCADE,
                    FOREIGN KEY (fasilitas_id) REFERENCES Fasilitas(id) ON DELETE CASCADE
                );
                """);
                System.out.println("Tabel 'Wisata_Fasilitas' berhasil dibuat atau sudah ada.");

                // KODE BARU: SQL untuk membuat tabel Ratings
                stmt.execute("""
                CREATE TABLE IF NOT EXISTS Ratings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    wisata_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5),
                    komentar TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (wisata_id) REFERENCES Wisata(id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
                );
                """);
                System.out.println("Tabel 'Ratings' berhasil dibuat atau sudah ada.");


                if (!isUserExist(conn, "admin@aplikasi.com")) {
                    stmt.executeUpdate("INSERT INTO Users (nama, email, password, role) VALUES ('Admin Utama', 'admin@aplikasi.com', 'admin123', 'admin');");
                    System.out.println("User Admin default berhasil ditambahkan.");
                }

                // Jika semua berhasil, simpan perubahan
                conn.commit();
                System.out.println("Transaksi pembuatan tabel berhasil.");

            } catch (SQLException e) {
                // Jika ada error, batalkan semua perubahan
                System.err.println("Terjadi error saat membuat tabel, transaksi di-rollback.");
                conn.rollback();
                throw e; // Lemparkan lagi errornya agar bisa terlihat
            }

        } catch (SQLException e) {
            System.err.println("Error pada DatabaseManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isUserExist(Connection conn, String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}