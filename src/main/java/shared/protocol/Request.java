package shared.protocol;

import java.io.Serializable;

// Request là yêu cầu Client gửi lên Server thông qua luồng TCP
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // operation cho biết Client muốn làm gì (VD: ADD_WORKER, DELETE_WORKER)
    private Operation operation;
    
    // data là dữ liệu đi kèm yêu cầu (VD: đối tượng Worker cần thêm, hoặc ID cần xóa)
    private Object data;
    
    public Request(Operation operation, Object data) {
        this.operation = operation;
        this.data = data;
    }
    
    public Operation getOperation() { return operation; }
    public void setOperation(Operation operation) { this.operation = operation; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
