// ===============================
// Interface Recordable, biar semua class bisa save, update, delete, load
package model;

import java.sql.Connection;
import java.sql.SQLException;

public interface Recordable {
    void save(Connection conn) throws SQLException;
    void update(Connection conn) throws SQLException;
    void delete(Connection conn) throws SQLException;
    void load(Connection conn, int id) throws SQLException;
    int getId();
    void setId(int id);
}