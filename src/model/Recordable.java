// src/model/Recordable.java
package model;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface ini mendefinisikan kontrak untuk objek yang datanya dapat disimpan dan dimuat dari database.
 * Diinspirasi dari "Recordable interface" dalam proposal[cite: 35].
 */
public interface Recordable {
    // Metode untuk menyimpan data objek ke database
    void save(Connection conn) throws SQLException;

    // Metode untuk memperbarui data objek yang sudah ada di database
    void update(Connection conn) throws SQLException;

    // Metode untuk menghapus data objek dari database
    void delete(Connection conn) throws SQLException;

    // Metode untuk memuat data objek dari database berdasarkan ID-nya
    void load(Connection conn, int id) throws SQLException;

    // Metode untuk mendapatkan ID objek (jika ada)
    int getId();

    // Metode untuk mengatur ID objek (misalnya setelah disimpan ke DB dan mendapatkan auto-generated ID)
    void setId(int id);
}