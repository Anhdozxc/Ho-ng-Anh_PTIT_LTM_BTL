package server.service;

import server.dao.ProvinceDAO;
import server.dao.WorkerDAO;
import shared.model.Worker;
import shared.model.SearchWorkerCriteria;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;


// Service kiểm tra dữ liệu và gọi DAO.
public class WorkerService {
    // WorkerDAO được gọi ở đâu: Được khởi tạo và gọi trong các hàm CRUD của Service
    private WorkerDAO workerDAO = new WorkerDAO();
    
    // ProvinceDAO được gọi ở đâu: Gọi trong validateWorker để kiểm tra Tỉnh có tồn tại không
    private ProvinceDAO provinceDAO = new ProvinceDAO();

    // validateWorker()
    // Service kiểm tra dữ liệu trước khi ghi vào database
    private void validateWorker(Worker worker) throws SQLException {
        // kiểm tra họ tên
        if (worker.getFullName() == null || worker.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        
        // kiểm tra năm sinh
        if (worker.getBirthYear() < 1900 || worker.getBirthYear() > 2026) {
            throw new IllegalArgumentException("Năm sinh không hợp lệ.");
        }
        
        // kiểm tra lương
        if (worker.getSalary() == null || worker.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Mức lương phải lớn hơn 0.");
        }
        
        // kiểm tra provinceId
        if (worker.getProvinceId() <= 0) {
            throw new IllegalArgumentException("ID tỉnh không hợp lệ.");
        }
        
        // kiểm tra provinceId tồn tại
        if (!provinceDAO.exists(worker.getProvinceId())) {
            throw new IllegalArgumentException("ID tỉnh không tồn tại trong hệ thống.");
        }
    }

    // addWorker
    public boolean addWorker(Worker worker) throws SQLException {
        validateWorker(worker);
        worker.setFullName(worker.getFullName().trim());
        return workerDAO.insert(worker);
    }

    public List<Worker> getAllWorkers() throws SQLException {
        return workerDAO.getAll();
    }

    // updateWorker
    public boolean updateWorker(Worker worker) throws SQLException {
        validateWorker(worker);
        worker.setFullName(worker.getFullName().trim());
        boolean success = workerDAO.update(worker);
        if (!success) {
            throw new IllegalArgumentException("Không tìm thấy công nhân với ID = " + worker.getId());
        }
        return success;
    }

    // searchWorkers
    public List<Worker> searchWorkers(SearchWorkerCriteria criteria) throws SQLException {
        if (criteria != null) {
            // kiểm tra province trong search
            if (criteria.getProvinceId() != null) {
                if (criteria.getProvinceId() <= 0) {
                    throw new IllegalArgumentException("ID tỉnh tìm kiếm phải lớn hơn 0.");
                }
                if (!provinceDAO.exists(criteria.getProvinceId())) {
                    throw new IllegalArgumentException("ID tỉnh tìm kiếm không tồn tại.");
                }
            }
            
            // kiểm tra min/max năm sinh
            if (criteria.getMinBirthYear() != null && (criteria.getMinBirthYear() < 1900 || criteria.getMinBirthYear() > 2026)) {
                throw new IllegalArgumentException("Năm sinh (từ) không hợp lệ.");
            }
            if (criteria.getMaxBirthYear() != null && (criteria.getMaxBirthYear() < 1900 || criteria.getMaxBirthYear() > 2026)) {
                throw new IllegalArgumentException("Năm sinh (đến) không hợp lệ.");
            }
            if (criteria.getMinBirthYear() != null && criteria.getMaxBirthYear() != null 
                && criteria.getMinBirthYear() > criteria.getMaxBirthYear()) {
                throw new IllegalArgumentException("Khoảng năm sinh: min không được lớn hơn max.");
            }
            
            // kiểm tra min/max lương
            if (criteria.getMinSalary() != null && criteria.getMinSalary().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Mức lương (từ) không được âm.");
            }
            if (criteria.getMaxSalary() != null && criteria.getMaxSalary().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Mức lương (đến) không được âm.");
            }
            if (criteria.getMinSalary() != null && criteria.getMaxSalary() != null 
                && criteria.getMinSalary().compareTo(criteria.getMaxSalary()) > 0) {
                throw new IllegalArgumentException("Khoảng lương: min không được lớn hơn max.");
            }
        }
        return workerDAO.search(criteria);
    }

    // deleteWorker
    public boolean deleteWorker(int id) throws SQLException {
        boolean success = workerDAO.delete(id);
        if (!success) {
            throw new IllegalArgumentException("Không tìm thấy công nhân với ID = " + id);
        }
        return success;
    }
}
