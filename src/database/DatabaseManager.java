// src/database/DatabaseManager.java
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // URL koneksi ke database SQLite. File database akan dibuat di root proyek.
    private static final String URL = "jdbc:sqlite:pariwisata_balikpapan.db";

    /**
     * Mendapatkan objek Connection ke database SQLite.
     *
     * @return Objek Connection yang terhubung ke database.
     * @throws SQLException Jika terjadi kesalahan saat mencoba mendapatkan koneksi database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Membuat tabel-tabel yang diperlukan dalam database jika belum ada.
     * Ini termasuk tabel Users, Kategori, Wisata, Fasilitas, dan Wisata_Fasilitas.
     * Juga menambahkan user Admin default jika belum ada.
     */
    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // SQL untuk membuat tabel Users
            // Menyimpan data pengguna aplikasi, baik pengguna biasa maupun admin[cite: 46].
            // Memiliki kolom id (Primary Key), nama, email, password, dan potensi kolom role atau type[cite: 47].
            String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS Users (" +
                                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                         "nama TEXT NOT NULL," +
                                         "email TEXT NOT NULL UNIQUE," +
                                         "password TEXT NOT NULL," +
                                         "role TEXT NOT NULL DEFAULT 'user'" + // 'admin' atau 'user'
                                         ");";
            stmt.execute(createUsersTableSQL);
            System.out.println("Tabel 'Users' berhasil dibuat atau sudah ada.");

            // SQL untuk membuat tabel Kategori
            // Menyimpan data kategori wisata (misalnya, Alam, Budaya, Kuliner)[cite: 48].
            // Memiliki kolom id (Primary Key) dan nama[cite: 49].
            String createKategoriTableSQL = "CREATE TABLE IF NOT EXISTS Kategori (" +
                                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                            "nama TEXT NOT NULL UNIQUE" +
                                            ");";
            stmt.execute(createKategoriTableSQL);
            System.out.println("Tabel 'Kategori' berhasil dibuat atau sudah ada.");

            // SQL untuk membuat tabel Wisata
            // Menyimpan detail data setiap destinasi wisata[cite: 50].
            // Memiliki kolom id (Primary Key), nama, deskripsi, lokasi, harga_tiket, jam_operasional,
            // dan kategori_id (Foreign Key yang merujuk ke Kategori.id)[cite: 50].
            String createWisataTableSQL = "CREATE TABLE IF NOT EXISTS Wisata (" +
                                          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                          "nama TEXT NOT NULL," +
                                          "deskripsi TEXT," +
                                          "lokasi TEXT," +
                                          "harga_tiket REAL DEFAULT 0.0," +
                                          "jam_operasional TEXT," +
                                          "kategori_id INTEGER," +
                                          "FOREIGN KEY (kategori_id) REFERENCES Kategori(id) ON DELETE SET NULL" +
                                          ");";
            stmt.execute(createWisataTableSQL);
            System.out.println("Tabel 'Wisata' berhasil dibuat atau sudah ada.");

            // SQL untuk membuat tabel Fasilitas
            // Menyimpan daftar fasilitas umum yang bisa ada di berbagai tempat wisata (misalnya, Parkir, Toilet, Restoran)[cite: 51].
            // Memiliki kolom id (Primary Key) dan nama[cite: 52].
            String createFasilitasTableSQL = "CREATE TABLE IF NOT EXISTS Fasilitas (" +
                                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                             "nama TEXT NOT NULL UNIQUE" +
                                             ");";
            stmt.execute(createFasilitasTableSQL);
            System.out.println("Tabel 'Fasilitas' berhasil dibuat atau sudah ada.");

            // SQL untuk membuat tabel Wisata_Fasilitas (Tabel Penghubung Many-to-Many)
            // Ini adalah tabel penghubung (linking table) yang diperlukan untuk merepresentasikan hubungan banyak-ke-banyak antara Wisata dan Fasilitas[cite: 53].
            // Memiliki kolom wisata_id (Foreign Key merujuk ke Wisata.id) dan fasilitas_id (Foreign Key merujuk ke Fasilitas.id)[cite: 55].
            String createWisataFasilitasTableSQL = "CREATE TABLE IF NOT EXISTS Wisata_Fasilitas (" +
                                                   "wisata_id INTEGER," +
                                                   "fasilitas_id INTEGER," +
                                                   "PRIMARY KEY (wisata_id, fasilitas_id)," +
                                                   "FOREIGN KEY (wisata_id) REFERENCES Wisata(id) ON DELETE CASCADE," +
                                                   "FOREIGN KEY (fasilitas_id) REFERENCES Fasilitas(id) ON DELETE CASCADE" +
                                                   ");";
            stmt.execute(createWisataFasilitasTableSQL);
            System.out.println("Tabel 'Wisata_Fasilitas' berhasil dibuat atau sudah ada.");

            // Menambahkan user admin default jika belum ada
            if (!isUserExist(conn, "admin@aplikasi.com")) {
                String insertAdminSQL = "INSERT INTO Users (nama, email, password, role) VALUES ('Admin Utama', 'admin@aplikasi.com', 'admin123', 'admin');";
                stmt.executeUpdate(insertAdminSQL);
                System.out.println("User Admin default berhasil ditambahkan.");
            }

        } catch (SQLException e) {
            System.err.println("Error saat membuat tabel atau menghubungkan ke database: " + e.getMessage());
            // Ini adalah penanganan awal untuk "Skenario Error Operasi Basis Data"[cite: 77].
            // Dalam aplikasi GUI, kita akan menampilkannya dengan pesan yang lebih ramah pengguna.
        }
    }

    /**
     * Memeriksa apakah seorang pengguna dengan email tertentu sudah ada di database.
     * Digunakan untuk mencegah duplikasi user default.
     *
     * @param conn Objek Connection ke database.
     * @param email Email yang akan diperiksa.
     * @return true jika pengguna ada, false jika tidak.
     * @throws SQLException Jika terjadi kesalahan SQL.
     */
    private static boolean isUserExist(Connection conn, String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (var pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            var rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}