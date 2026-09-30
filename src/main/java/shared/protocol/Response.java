package shared.protocol;

import java.io.Serializable;

// Response là kết quả Server gửi lại Client sau khi xử lý xong Request
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // success cho biết thao tác thành công (true) hay thất bại (false)
    private boolean success;
    
    // message là thông báo trả về cho Client (VD: "Thêm thành công", hoặc "Lỗi CSDL")
    private String message;
    
    // data là dữ liệu Server trả về nếu có (VD: danh sách Worker)
    private Object data;
    
    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
