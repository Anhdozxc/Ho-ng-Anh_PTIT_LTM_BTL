package shared.model;

import java.io.Serializable;

// Lớp chứa thông tin Tỉnh/TP, dùng chung cho cả Client và Server
// Cần implements Serializable để có thể gửi qua mạng bằng ObjectOutputStream
public class Province implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Thuộc tính id của tỉnh
    private int id;
    
    // Thuộc tính tên của tỉnh
    private String name;
    
    // Thuộc tính mô tả của tỉnh
    private String description;
    
    // Hàm khởi tạo không tham số
    public Province() {}
    
    // Hàm khởi tạo có tham số
    public Province(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return id + " - " + name + " (" + description + ")";
    }
}
