package server.dao;

import shared.model.Province;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO là nơi trực tiếp làm việc với MySQL
public class ProvinceDAO {
    
    // Lấy toàn bộ danh sách tỉnh
    public List<Province> getAll() throws SQLException {
        List<Province> list = new ArrayList<>();
        // SELECT province
        String sql = "SELECT * FROM province ORDER BY name ASC";
        
        // Mở Connection từ DBConnection
        // Tạo PreparedStatement để chuẩn bị câu lệnh
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { // executeQuery() để chạy lệnh SELECT và lấy ResultSet
            
            // Lặp qua từng dòng kết quả trong ResultSet
            while (rs.next()) {
                Province p = new Province();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                list.add(p);
            }
        }
        return list;
    }

    // DELETE province
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM province WHERE id = ?";
        
        // Mở Connection và tạo PreparedStatement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Truyền tham số ID vào dấu ?
            ps.setInt(1, id);
            
            // executeUpdate() chạy lệnh DELETE, trả về số dòng bị ảnh hưởng
            return ps.executeUpdate() > 0;
        }
    }

    // exists province
    // Kiểm tra tỉnh có tồn tại trước khi thêm/sửa công nhân
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT 1 FROM province WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            // executeQuery để kiểm tra xem có dòng nào trả về không
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu có next() tức là tồn tại
            }
        }
    }
}
