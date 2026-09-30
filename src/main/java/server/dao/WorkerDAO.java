package server.dao;

import shared.model.Worker;
import shared.model.SearchWorkerCriteria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO là nơi trực tiếp làm việc với MySQL. Không chứa logic kiểm tra tính hợp lệ dữ liệu.
public class WorkerDAO {
    
    // A. INSERT
    public boolean insert(Worker worker) throws SQLException {
        // câu SQL insert
        String sql = "INSERT INTO worker (full_name, birth_year, salary, province_id) VALUES (?, ?, ?, ?)";
        
        // mở Connection
        // tạo PreparedStatement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // truyền dữ liệu vào dấu ?
            ps.setString(1, worker.getFullName());
            ps.setInt(2, worker.getBirthYear());
            ps.setBigDecimal(3, worker.getSalary());
            ps.setInt(4, worker.getProvinceId());
            
            // executeUpdate
            // kết quả true/false
            return ps.executeUpdate() > 0;
        }
    }

    // B. SELECT ALL
    public List<Worker> getAll() throws SQLException {
        List<Worker> list = new ArrayList<>();
        // câu SQL
        // JOIN province để lấy tên quê quán hiển thị ra Client
        String sql = "SELECT w.*, p.name AS province_name FROM worker w " +
                     "JOIN province p ON w.province_id = p.id ORDER BY w.id ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { // ResultSet chứa danh sách lấy từ DB
            
            // đọc từng dòng
            while (rs.next()) {
                // tạo Worker thông qua hàm phụ trợ
                // thêm vào List
                list.add(mapResultSetToWorker(rs));
            }
        }
        return list;
    }

    // C. UPDATE
    public boolean update(Worker worker) throws SQLException {
        // câu SQL update
        String sql = "UPDATE worker SET full_name = ?, birth_year = ?, salary = ?, province_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // set dữ liệu
            ps.setString(1, worker.getFullName());
            ps.setInt(2, worker.getBirthYear());
            ps.setBigDecimal(3, worker.getSalary());
            ps.setInt(4, worker.getProvinceId());
            
            // WHERE id
            ps.setInt(5, worker.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    // D. SEARCH
    public List<Worker> search(SearchWorkerCriteria criteria) throws SQLException {
        List<Worker> list = new ArrayList<>();
        
        // tạo SQL ban đầu
        // dùng StringBuilder để dễ dàng nối chuỗi câu lệnh SQL
        StringBuilder sql = new StringBuilder(
            "SELECT w.*, p.name AS province_name FROM worker w " +
            "JOIN province p ON w.province_id = p.id " +
            "WHERE 1=1" // Mẹo WHERE 1=1 để nối tiếp các điều kiện AND phía sau dễ dàng
        );
        
        List<Object> params = new ArrayList<>();

        // thêm điều kiện keyword nếu có
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            sql.append(" AND w.full_name LIKE ?");
            params.add("%" + criteria.getKeyword().trim() + "%");
        }
        
        // thêm province nếu có
        if (criteria.getProvinceId() != null) {
            sql.append(" AND w.province_id = ?");
            params.add(criteria.getProvinceId());
        }
        
        // thêm min/max year
        if (criteria.getMinBirthYear() != null) {
            sql.append(" AND w.birth_year >= ?");
            params.add(criteria.getMinBirthYear());
        }
        if (criteria.getMaxBirthYear() != null) {
            sql.append(" AND w.birth_year <= ?");
            params.add(criteria.getMaxBirthYear());
        }
        
        // thêm min/max salary
        if (criteria.getMinSalary() != null) {
            sql.append(" AND w.salary >= ?");
            params.add(criteria.getMinSalary());
        }
        if (criteria.getMaxSalary() != null) {
            sql.append(" AND w.salary <= ?");
            params.add(criteria.getMaxSalary());
        }
        
        sql.append(" ORDER BY w.id ASC");

        // tạo PreparedStatement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // truyền parameter
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            // executeQuery
            // đọc ResultSet
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToWorker(rs));
                }
            }
        }
        return list;
    }

    // E. DELETE
    public boolean delete(int id) throws SQLException {
        // câu SQL
        String sql = "DELETE FROM worker WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // truyền ID
            ps.setInt(1, id);
            
            // executeUpdate
            return ps.executeUpdate() > 0;
        }
    }
    
    // F. mapResultSetToWorker()
    private Worker mapResultSetToWorker(ResultSet rs) throws SQLException {
        // tạo Worker
        Worker w = new Worker();
        
        // lấy từng cột từ ResultSet
        w.setId(rs.getInt("id"));
        w.setFullName(rs.getString("full_name"));
        w.setBirthYear(rs.getInt("birth_year"));
        w.setSalary(rs.getBigDecimal("salary"));
        w.setProvinceId(rs.getInt("province_id"));
        
        // gán provinceName (lấy ra từ phép JOIN p.name AS province_name)
        w.setProvinceName(rs.getString("province_name"));
        
        return w;
    }
}
