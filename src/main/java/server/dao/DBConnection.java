package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Thông tin kết nối tới MySQL
    
    // URL database
    private static final String URL = "jdbc:mysql://localhost:3306/ltm_btl";
    
    // username kết nối database
    private static final String USER = "root";
    
    // password kết nối database
    private static final String PASSWORD = "12345678";

    // Tạo kết nối từ Server tới database
    public static Connection getConnection() throws SQLException {
        // DriverManager.getConnection() dùng để thiết lập kết nối JDBC với tham số url, username, password
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
