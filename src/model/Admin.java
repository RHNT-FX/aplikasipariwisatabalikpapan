// src/model/Admin.java
package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Kelas Admin yang mewarisi dari User.
 * Merepresentasikan bahwa seorang Admin pada dasarnya adalah seorang User dengan hak akses dan fungsionalitas tambahan[cite: 39].
 */
public class Admin extends User {

    public Admin() {
        super();
        this.setRole("admin"); // Override default role menjadi 'admin'
    }

    public Admin(int id, String nama, String email, String password) {
        super(id, nama, email, password, "admin");
    }

    // --- Fungsionalitas tambahan khusus Admin (Manajemen Data Utama) ---
    // (Dalam implementasi ini, operasi CRUD akan dilakukan melalui kelas-kelas DAO)
    // Namun, kita bisa tambahkan placeholder metode di sini untuk menunjukkan fungsionalitas admin

    /**
     * Menambah destinasi wisata baru (Contoh fungsionalitas admin).
     * @param wisata Objek Wisata yang akan ditambahkan.
     * @param conn Koneksi database.
     * @throws SQLException jika ada kesalahan DB.
     */
    public void tambahWisata(Wisata wisata, Connection conn) throws SQLException {
        wisata.save(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menambah wisata: " + wisata.getNama());
    }

    /**
     * Mengubah detail destinasi wisata (Contoh fungsionalitas admin).
     * @param wisata Objek Wisata yang akan diubah.
     * @param conn Koneksi database.
     * @throws SQLException jika ada kesalahan DB.
     */
    public void ubahWisata(Wisata wisata, Connection conn) throws SQLException {
        wisata.update(conn);
        System.out.println("Admin " + this.getNama() + " berhasil mengubah wisata: " + wisata.getNama());
    }

    /**
     * Menghapus destinasi wisata (Contoh fungsionalitas admin).
     * @param wisata Objek Wisata yang akan dihapus.
     * @param conn Koneksi database.
     * @throws SQLException jika ada kesalahan DB.
     */
    public void hapusWisata(Wisata wisata, Connection conn) throws SQLException {
        wisata.delete(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menghapus wisata: " + wisata.getNama());
    }

    /**
     * Menambah kategori baru (Contoh fungsionalitas admin).
     */
    public void tambahKategori(Kategori kategori, Connection conn) throws SQLException {
        kategori.save(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menambah kategori: " + kategori.getNama());
    }

    /**
     * Menambah fasilitas baru (Contoh fungsionalitas admin).
     */
    public void tambahFasilitas(Fasilitas fasilitas, Connection conn) throws SQLException {
        fasilitas.save(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menambah fasilitas: " + fasilitas.getNama());
    }

    /**
     * Contoh: Mendapatkan daftar nama destinasi wisata.
     */
    public void printDaftarWisata(List<Wisata> daftarWisata) {
        System.out.println("Daftar Wisata:");
        for (Wisata w : daftarWisata) {
            System.out.println("- " + w.getNama());
        }
    }

    @Override
    public String toString() {
        return "Admin{" +
               "id=" + id +
               ", nama='" + nama + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}