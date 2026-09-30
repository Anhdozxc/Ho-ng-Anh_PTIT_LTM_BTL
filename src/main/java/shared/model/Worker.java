package shared.model;

import java.io.Serializable;
import java.math.BigDecimal;

// Worker chứa thông tin về Công nhân
// Implements Serializable để gửi qua TCP Socket
public class Worker implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // id của công nhân
    private int id;
    
    // fullName họ tên công nhân
    private String fullName;
    
    // birthYear năm sinh công nhân
    private int birthYear;
    
    // salary mức lương công nhân
    private BigDecimal salary;
    
    // provinceId dùng để liên kết với bảng province (Lưu ID của quê quán)
    private int provinceId;
    
    // provinceName dùng để hiển thị tên quê quán khi SELECT JOIN (chỉ dùng cho hiển thị)
    private String provinceName;
    
    public Worker() {}
    
    public Worker(int id, String fullName, int birthYear, BigDecimal salary, int provinceId) {
        this.id = id;
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.salary = salary;
        this.provinceId = provinceId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
    
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    
    public int getProvinceId() { return provinceId; }
    public void setProvinceId(int provinceId) { this.provinceId = provinceId; }
    
    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", birthYear=" + birthYear +
                ", salary=" + salary +
                ", provinceId=" + provinceId +
                (provinceName != null ? ", provinceName='" + provinceName + '\'' : "") +
                '}';
    }
}
