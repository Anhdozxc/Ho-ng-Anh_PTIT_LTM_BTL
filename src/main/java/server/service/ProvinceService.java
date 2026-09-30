package server.service;

import server.dao.ProvinceDAO;
import shared.model.Province;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

// Service không trực tiếp viết SQL.
// Service kiểm tra dữ liệu và gọi DAO.
public class ProvinceService {
    // ProvinceDAO được gọi ở đây để giao tiếp DB
    private ProvinceDAO provinceDAO = new ProvinceDAO();

    // lấy danh sách province
    public List<Province> getAllProvinces() throws SQLException {
        return provinceDAO.getAll();
    }

    // ===== LUỒNG XÓA TỈNH =====
    // xóa province
    public boolean deleteProvince(int id) throws SQLException {
        try {
            // ProvinceService gọi ProvinceDAO
            // Database kiểm tra Foreign Key
            boolean success = provinceDAO.delete(id);
            
            // kiểm tra kết quả xóa
            // xử lý province không tồn tại
            if (!success) {
                throw new IllegalArgumentException("Không tìm thấy tỉnh có ID = " + id);
            }
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            // Nếu province vẫn còn worker tham chiếu thì database không cho xóa
            // tại sao xóa province có worker lại bị chặn: do đã set ON DELETE RESTRICT ở database
            // xử lý lỗi Foreign Key
            // Chuyển lỗi database thành thông báo dễ hiểu cho Client
            throw new IllegalArgumentException("Không thể xóa tỉnh vì vẫn còn công nhân thuộc tỉnh này.");
        }
    }
}
