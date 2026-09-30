package server;

import shared.protocol.Request;
import shared.protocol.Response;
import shared.protocol.Operation;
import shared.model.SearchWorkerCriteria;
import shared.model.Worker;
import server.service.WorkerService;
import server.service.ProvinceService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

// ===== LUỒNG SERVER XỬ LÝ REQUEST =====
public class ClientHandler extends Thread {
    // 1. Nhận Socket của Client.
    private Socket socket;
    private WorkerService workerService;
    private ProvinceService provinceService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.workerService = new WorkerService();
        this.provinceService = new ProvinceService();
    }

    @Override
    public void run() {
        try (
            // 2. Tạo ObjectOutputStream.
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            // 3. Tạo ObjectInputStream.
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            // 4. Chờ nhận Request từ Client.
            while (true) {
                // 5. Đọc Request.
                // Server đọc Request từ Socket
                Request request = (Request) in.readObject();
                
                // 6. Gọi processRequest.
                Response response = processRequest(request);
                
                // 18. Gửi Response về Client.
                // Gửi kết quả từ Server về Client
                out.writeObject(response);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("Client disconnected or error: " + e.getMessage());
        } finally {
            try {
                // 21. Đóng Socket khi Client ngắt kết nối.
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Response processRequest(Request request) {
        try {
            // 7. Kiểm tra Operation.
            // Xác định Client đang yêu cầu chức năng nào
            switch (request.getOperation()) {
                
                // 8. GET_PROVINCES.
                case GET_PROVINCES:
                    // Chuyển yêu cầu sang ProvinceService
                    return new Response(true, "Lấy danh sách tỉnh thành công.", provinceService.getAllProvinces());
                
                // 9. ADD_WORKER.
                case ADD_WORKER:
                    Worker newWorker = (Worker) request.getData();
                    // Chuyển yêu cầu sang WorkerService
                    // 15. Gọi Service tương ứng.
                    // 16. Nhận kết quả từ Service.
                    boolean added = workerService.addWorker(newWorker);
                    // 17. Tạo Response.
                    if (added) {
                        return new Response(true, "Thêm công nhân thành công.", null);
                    } else {
                        return new Response(false, "Thêm công nhân thất bại.", null);
                    }
                
                // 10. GET_WORKERS.
                case GET_WORKERS:
                    return new Response(true, "Lấy danh sách công nhân thành công.", workerService.getAllWorkers());
                
                // 11. UPDATE_WORKER.
                case UPDATE_WORKER:
                    Worker updateWorker = (Worker) request.getData();
                    boolean updated = workerService.updateWorker(updateWorker);
                    if (updated) {
                        return new Response(true, "Cập nhật công nhân thành công.", null);
                    } else {
                        return new Response(false, "Cập nhật công nhân thất bại.", null);
                    }
                
                // 12. SEARCH_WORKERS.
                case SEARCH_WORKERS:
                    SearchWorkerCriteria criteria = (SearchWorkerCriteria) request.getData();
                    List<Worker> result = workerService.searchWorkers(criteria);
                    return new Response(true, "Tìm kiếm thành công.", result);
                
                // 13. DELETE_WORKER.
                case DELETE_WORKER:
                    int workerId = (int) request.getData();
                    boolean deleted = workerService.deleteWorker(workerId);
                    if (deleted) {
                        return new Response(true, "Xóa công nhân thành công.", null);
                    } else {
                        return new Response(false, "Xóa công nhân thất bại.", null);
                    }
                
                // 14. DELETE_PROVINCE.
                case DELETE_PROVINCE:
                    int provinceId = (int) request.getData();
                    boolean pDeleted = provinceService.deleteProvince(provinceId);
                    if (pDeleted) {
                        return new Response(true, "Xóa tỉnh thành công.", null);
                    } else {
                        return new Response(false, "Xóa tỉnh thất bại.", null);
                    }
                
                default:
                    return new Response(false, "Unknown operation", null);
            }
        } catch (IllegalArgumentException e) {
            // 19. Xử lý lỗi validation.
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            // 20. Xử lý lỗi database/server.
            e.printStackTrace();
            return new Response(false, "Lỗi Server: " + e.getMessage(), null);
        }
    }
}
