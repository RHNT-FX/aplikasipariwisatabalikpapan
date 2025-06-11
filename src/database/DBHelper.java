package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:wisata.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("❌ Koneksi database gagal: " + e.getMessage());
            return null;
        }
    }

    public static void initDB() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            // 1. Tabel Users
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL CHECK(role IN ('admin', 'user'))
                );
            """);

            // 2. Tabel Kategori
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS kategori (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL
                );
            """);

            // 3. Tabel Wisata
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS wisata (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL,
                    lokasi TEXT NOT NULL,
                    deskripsi TEXT,
                    hargaTiket REAL,
                    jamOperasional TEXT,
                    kategoriId INTEGER,
                    FOREIGN KEY (kategoriId) REFERENCES kategori(id)
                );
            """);

            // 4. Tabel Fasilitas
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fasilitas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL
                );
            """);

            // 5. Tabel Relasi Wisata-Fasilitas
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS wisata_fasilitas (
                    wisataId INTEGER,
                    fasilitasId INTEGER,
                    PRIMARY KEY (wisataId, fasilitasId),
                    FOREIGN KEY (wisataId) REFERENCES wisata(id),
                    FOREIGN KEY (fasilitasId) REFERENCES fasilitas(id)
                );
            """);

            // Seed data kategori
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO kategori (id, nama) VALUES
                (1, 'Alam'),
                (2, 'Budaya'),
                (3, 'Kuliner');
            """);

            // Seed data fasilitas
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO fasilitas (id, nama) VALUES
                (1, 'Parkir'),
                (2, 'Toilet'),
                (3, 'Restoran'),
                (4, 'Wi-Fi');
            """);

            // Seed data user
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO users (id, nama, email, password, role) VALUES
                (1, 'Admin Balikpapan', 'admin@balikpapan.go.id', 'admin123', 'admin'),
                (2, 'Pengunjung 1', 'user1@gmail.com', 'user123', 'user');
            """);

            // Seed data wisata
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO wisata (id, nama, lokasi, deskripsi, hargaTiket, jamOperasional, kategoriId) VALUES
                (1, 'Pantai Kemala', 'Jl. Jendral Sudirman', 'Pantai dengan pasir putih dan sunset yang indah.', 10000, '08:00 - 18:00', 1),
                (2, 'Hutan Sungai Wain', 'Km 15 Balikpapan', 'Hutan tropis alami dengan flora dan fauna langka.', 15000, '07:00 - 17:00', 1),
                (3, 'Kampung Atas Air', 'Margasari', 'Pemukiman terapung khas Balikpapan.', 5000, '09:00 - 17:00', 2);
            """);

            // Seed data relasi wisata-fasilitas
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO wisata_fasilitas (wisataId, fasilitasId) VALUES
                (1, 1), (1, 2), (1, 3),
                (2, 1), (2, 2),
                (3, 1), (3, 2);
            """);

            System.out.println("✅ Database berhasil diinisialisasi dan diisi dengan data awal.");

        } catch (SQLException e) {
            System.err.println("❌ Gagal inisialisasi DB: " + e.getMessage());
        }
    }
}