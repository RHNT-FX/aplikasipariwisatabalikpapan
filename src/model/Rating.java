package model;

import java.sql.Timestamp;

/**
 * Kelas Model (POJO - Plain Old Java Object) yang merepresentasikan
 * satu entitas rating dan komentar dalam database.
 */
public class Rating {

    private int id;
    private int wisataId;
    private int userId;
    private int rating;
    private String komentar;
    private Timestamp createdAt;
    private String namaUser; // Digunakan untuk menampilkan nama pemberi komentar

    // Constructor untuk membuat objek Rating baru sebelum disimpan ke DB
    public Rating(int wisataId, int userId, int rating, String komentar) {
        this.wisataId = wisataId;
        this.userId = userId;
        this.rating = rating;
        this.komentar = komentar;
    }

    // Constructor untuk membuat objek Rating saat data diambil dari DB
    public Rating(int id, int wisataId, int userId, int rating, String komentar, Timestamp createdAt) {
        this.id = id;
        this.wisataId = wisataId;
        this.userId = userId;
        this.rating = rating;
        this.komentar = komentar;
        this.createdAt = createdAt;
    }
    
    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWisataId() {
        return wisataId;
    }

    public void setWisataId(int wisataId) {
        this.wisataId = wisataId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
}