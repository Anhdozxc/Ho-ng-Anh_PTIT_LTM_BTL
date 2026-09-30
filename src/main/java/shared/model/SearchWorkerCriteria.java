package shared.model;

import java.io.Serializable;
import java.math.BigDecimal;

// Các điều kiện dùng để tìm kiếm công nhân, đóng gói gửi từ Client lên Server
public class SearchWorkerCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    // keyword dùng tìm theo tên
    private String keyword; 
    
    // provinceId dùng lọc theo quê quán
    private Integer provinceId;
    
    // min/maxBirthYear dùng lọc theo khoảng năm sinh
    private Integer minBirthYear;
    private Integer maxBirthYear;
    
    // min/maxSalary dùng lọc theo khoảng lương
    private BigDecimal minSalary;
    private BigDecimal maxSalary;

    public SearchWorkerCriteria() {}

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public Integer getProvinceId() { return provinceId; }
    public void setProvinceId(Integer provinceId) { this.provinceId = provinceId; }
    
    public Integer getMinBirthYear() { return minBirthYear; }
    public void setMinBirthYear(Integer minBirthYear) { this.minBirthYear = minBirthYear; }
    
    public Integer getMaxBirthYear() { return maxBirthYear; }
    public void setMaxBirthYear(Integer maxBirthYear) { this.maxBirthYear = maxBirthYear; }
    
    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }
    
    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }
}
