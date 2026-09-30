package shared.protocol;

// Các Operation (thao tác) mà Client có thể yêu cầu Server thực hiện
public enum Operation {
    // Lấy danh sách tỉnh
    GET_PROVINCES,
    
    // Thêm công nhân
    ADD_WORKER,
    
    // Lấy danh sách công nhân
    GET_WORKERS,
    
    // Cập nhật công nhân
    UPDATE_WORKER,
    
    // Tìm kiếm công nhân
    SEARCH_WORKERS,
    
    // Xóa công nhân
    DELETE_WORKER,
    
    // Xóa tỉnh
    DELETE_PROVINCE
}
