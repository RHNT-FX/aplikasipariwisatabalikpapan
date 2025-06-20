package controller;

import dao.UserDAO;
import model.User;
import java.sql.SQLException;

//kelas buat ngelola autentikasi user//
public class AuthManager {
    private static User currentUser; // user yang lagi login

    //login user dengan email dan password//
    public static User login(String email, String password) throws SQLException {
        UserDAO userDAO = new UserDAO();
        currentUser = userDAO.authenticateUser(email, password);
        return currentUser;
    }

    //tanda user lagi login//
    public static User getCurrentUser() {
        return currentUser;
    }

     //Meriksa user yang lagi login adalah Admin//
    public static boolean isAdminLoggedIn() {
        return currentUser != null && currentUser.isAdmin();
    }

    //ngelakuin logout user sekarang//
    public static void logout() {
        currentUser = null;
    }
}