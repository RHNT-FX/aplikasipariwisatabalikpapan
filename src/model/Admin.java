package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Admin extends User {

    // default admin, role-nya admin
    public Admin() {
        super();
        this.setRole("admin");
    }

    // kalo mau langsung isi semua
    public Admin(int id, String nama, String email, String password) {
        super(id, nama, email, password, "admin");
    }

    // method2 buat ngatur wisata, kategori, fasilitas, dll
    public void tambahWisata(Wisata wisata, Connection conn) throws SQLException {
        wisata.save(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menambah wisata: " + wisata.getNama());
    }

    public void ubahWisata(Wisata wisata, Connection conn) throws SQLException {
        wisata.update(conn);
        System.out.println("Admin " + this.getNama() + " berhasil mengubah wisata: " + wisata.getNama());
    }

    public void hapusWisata(Wisata wisata, Connection conn) throws SQLException {
        wisata.delete(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menghapus wisata: " + wisata.getNama());
    }

    public void tambahKategori(Kategori kategori, Connection conn) throws SQLException {
        kategori.save(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menambah kategori: " + kategori.getNama());
    }

    public void tambahFasilitas(Fasilitas fasilitas, Connection conn) throws SQLException {
        fasilitas.save(conn);
        System.out.println("Admin " + this.getNama() + " berhasil menambah fasilitas: " + fasilitas.getNama());
    }

    public void printDaftarWisata(List<Wisata> daftarWisata) {
        System.out.println("Daftar Wisata:");
        for (Wisata w : daftarWisata) {
            System.out.println("- " + w.getNama());
        }
    }

    // biar kalo di-print ga aneh
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