package client;

import shared.model.Province;
import shared.model.SearchWorkerCriteria;
import shared.model.Worker;
import shared.protocol.Operation;
import shared.protocol.Request;
import shared.protocol.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientApp {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8888;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;

    public ClientApp() {
        // A. Khởi tạo Scanner
        scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            // B. Kết nối Server bằng Socket
            socket = new Socket(SERVER_IP, PORT);
            
            // C. ObjectOutputStream
            out = new ObjectOutputStream(socket.getOutputStream());
            
            // D. ObjectInputStream
            in = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("Kết nối tới Server thành công!");

            // E. Vòng lặp menu
            while (true) {
                printMenu();
                
                // F. Đọc lựa chọn người dùng
                String input = scanner.nextLine();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    // AJ. Xử lý input sai
                    System.out.println("Lỗi: Vui lòng nhập số hợp lệ.");
                    continue;
                }

                switch (choice) {
                    case 1: addWorker(); break;
                    case 2: displayAllWorkers(); break;
                    case 3: updateWorker(); break;
                    case 4: searchWorkersMenu(); break;
                    case 5: deleteWorker(); break;
                    case 6: deleteProvince(); break;
                    case 0:
                        System.out.println("Đang thoát...");
                        socket.close();
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            }
        } catch (Exception e) {
            // AK. Xử lý lỗi kết nối
            System.out.println("Lỗi mạng/Kết nối Server thất bại: " + e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println("\n============================================================");
        System.out.println("             QUẢN LÝ CÔNG NHÂN");
        System.out.println("============================================================");
        System.out.println("1. Thêm công nhân");
        System.out.println("2. Hiển thị danh sách công nhân");
        System.out.println("3. Cập nhật công nhân");
        System.out.println("4. Tìm kiếm công nhân");
        System.out.println("5. Xóa công nhân");
        System.out.println("6. Xóa tỉnh");
        System.out.println("0. Thoát");
        System.out.println("------------------------------------------------------------");
        System.out.print("Chọn: ");
    }

    // ===== LUỒNG TCP CLIENT → SERVER =====
    private Response sendRequest(Operation op, Object data) throws IOException, ClassNotFoundException {
        // G. Gửi Request
        // Client tạo Request
        // Request đi qua Socket TCP
        out.writeObject(new Request(op, data));
        out.flush();
        
        // H. Nhận Response
        return (Response) in.readObject();
    }

    // I. Lấy danh sách Province
    private List<Province> getProvinces() {
        try {
            Response response = sendRequest(Operation.GET_PROVINCES, null);
            if (response.isSuccess()) {
                return (List<Province>) response.getData();
            } else {
                System.out.println("Lỗi lấy danh sách tỉnh: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        return null;
    }

    // J. Hiển thị Province
    private void printProvinces(List<Province> provinces) {
        System.out.println("--- Danh sách tỉnh/thành phố ---");
        for (Province p : provinces) {
            System.out.println(p.getId() + ". " + p.getName());
        }
    }

    // K. Hiển thị Worker
    private void printWorkers(List<Worker> workers) {
        if (workers == null || workers.isEmpty()) {
            System.out.println("Không có dữ liệu công nhân.");
            return;
        }
        System.out.println("=========================================================================================");
        System.out.printf("%-5s | %-25s | %-10s | %-20s | %-15s\n", "ID", "HỌ TÊN", "NĂM SINH", "QUÊ QUÁN", "LƯƠNG");
        System.out.println("=========================================================================================");
        for (Worker w : workers) {
            System.out.printf("%-5d | %-25s | %-10d | %-20s | %,15.0f\n", 
                w.getId(), w.getFullName(), w.getBirthYear(), w.getProvinceName(), w.getSalary());
        }
        System.out.println("=========================================================================================");
    }

    private void displayAllWorkers() {
        try {
            Response response = sendRequest(Operation.GET_WORKERS, null);
            if (response.isSuccess()) {
                printWorkers((List<Worker>) response.getData());
            } else {
                System.out.println("Lỗi: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // L. Thêm Worker
    private void addWorker() {
        // M. Lấy danh sách Province trước khi thêm
        List<Province> provinces = getProvinces();
        if (provinces == null || provinces.isEmpty()) return;

        try {
            System.out.print("Nhập họ tên: ");
            String name = scanner.nextLine();
            
            System.out.print("Nhập năm sinh: ");
            int year = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Nhập mức lương: ");
            BigDecimal salary = new BigDecimal(scanner.nextLine());
            
            printProvinces(provinces);
            System.out.print("Chọn ID Tỉnh/Thành phố: ");
            int provinceId = Integer.parseInt(scanner.nextLine());

            // N. Tạo Worker
            Worker worker = new Worker(0, name, year, salary, provinceId);
            
            // O. Gửi ADD_WORKER
            Response response = sendRequest(Operation.ADD_WORKER, worker);
            
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.out.println("Thêm thất bại: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi nhập liệu: Sai định dạng số.");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // P. Cập nhật Worker
    private void updateWorker() {
        try {
            System.out.print("Nhập ID công nhân cần cập nhật: ");
            int id = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Nhập họ tên mới: ");
            String name = scanner.nextLine();
            
            System.out.print("Nhập năm sinh mới: ");
            int year = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Nhập mức lương mới: ");
            BigDecimal salary = new BigDecimal(scanner.nextLine());
            
            // Q. Lấy danh sách Province khi đổi quê quán
            List<Province> provinces = getProvinces();
            if (provinces == null || provinces.isEmpty()) return;
            printProvinces(provinces);
            
            System.out.print("Chọn ID Tỉnh/Thành phố mới: ");
            int provinceId = Integer.parseInt(scanner.nextLine());

            Worker worker = new Worker(id, name, year, salary, provinceId);
            
            // R. Gửi UPDATE_WORKER
            Response response = sendRequest(Operation.UPDATE_WORKER, worker);
            
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.out.println("Cập nhật thất bại: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi nhập liệu: Sai định dạng số.");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // S. Tìm kiếm Worker
    private void searchWorkersMenu() {
        // T. Tạo SearchWorkerCriteria
        SearchWorkerCriteria criteria = new SearchWorkerCriteria();
        
        System.out.println("\n--- Tìm kiếm công nhân ---");
        System.out.println("Lưu ý: Bỏ trống (nhấn Enter) các tiêu chí không muốn lọc");
        
        try {
            // U. Keyword
            System.out.print("Nhập từ khóa họ tên: ");
            String keyword = scanner.nextLine();
            if (!keyword.trim().isEmpty()) criteria.setKeyword(keyword);

            // V. Province
            List<Province> provinces = getProvinces();
            if (provinces != null) {
                printProvinces(provinces);
                System.out.print("Chọn ID quê quán (nhấn Enter để bỏ qua): ");
                String pId = scanner.nextLine();
                if (!pId.trim().isEmpty()) criteria.setProvinceId(Integer.parseInt(pId));
            }

            // W. Khoảng năm sinh
            System.out.print("Nhập năm sinh TỪ (nhấn Enter để bỏ qua): ");
            String minYear = scanner.nextLine();
            if (!minYear.trim().isEmpty()) criteria.setMinBirthYear(Integer.parseInt(minYear));
            
            System.out.print("Nhập năm sinh ĐẾN (nhấn Enter để bỏ qua): ");
            String maxYear = scanner.nextLine();
            if (!maxYear.trim().isEmpty()) criteria.setMaxBirthYear(Integer.parseInt(maxYear));
            
            // X. Khoảng lương
            System.out.print("Nhập mức lương TỪ (nhấn Enter để bỏ qua): ");
            String minSal = scanner.nextLine();
            if (!minSal.trim().isEmpty()) criteria.setMinSalary(new BigDecimal(minSal));
            
            System.out.print("Nhập mức lương ĐẾN (nhấn Enter để bỏ qua): ");
            String maxSal = scanner.nextLine();
            if (!maxSal.trim().isEmpty()) criteria.setMaxSalary(new BigDecimal(maxSal));
            
            // Y. Gửi SEARCH_WORKERS
            Response response = sendRequest(Operation.SEARCH_WORKERS, criteria);
            if (response.isSuccess()) {
                printWorkers((List<Worker>) response.getData());
            } else {
                System.out.println("Lỗi tìm kiếm: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi nhập liệu: Vui lòng nhập đúng định dạng số.");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // ===== LUỒNG XÓA CÔNG NHÂN =====
    // Z. Xóa Worker
    private void deleteWorker() {
        System.out.println("\n--- Xóa công nhân theo từ khóa ---");
        // Nhập từ khóa để tìm công nhân
        System.out.print("Nhập từ khóa tên công nhân (nhấn Enter để lấy tất cả): ");
        String keyword = scanner.nextLine();
        
        SearchWorkerCriteria criteria = new SearchWorkerCriteria();
        if (!keyword.trim().isEmpty()) criteria.setKeyword(keyword);
        
        try {
            // AA. Tìm theo keyword trước
            // Tìm worker theo keyword trước
            // Gửi SEARCH_WORKERS lên Server
            Response searchRes = sendRequest(Operation.SEARCH_WORKERS, criteria);
            if (!searchRes.isSuccess()) {
                System.out.println("Lỗi tìm kiếm: " + searchRes.getMessage());
                return;
            }
            // Client nhận danh sách worker phù hợp
            List<Worker> workers = (List<Worker>) searchRes.getData();
            if (workers == null || workers.isEmpty()) {
                System.out.println("Không tìm thấy công nhân nào khớp với từ khóa.");
                return;
            }
            
            // AB. Hiển thị các Worker tìm được
            printWorkers(workers);
            
            // AC. Chọn ID Worker cần xóa
            // Sau đó người dùng chọn ID worker cần xóa
            // Người dùng chọn ID công nhân cần xóa
            System.out.print("Nhập ID công nhân cần xóa (Nhập 0 để hủy): ");
            int id = Integer.parseInt(scanner.nextLine());
            
            if (id == 0) return;
            
            // AD. Gửi DELETE_WORKER
            // Gửi DELETE_WORKER với ID đã chọn
            // Server xóa worker trong database
            Response delRes = sendRequest(Operation.DELETE_WORKER, id);
            if (delRes.isSuccess()) {
                System.out.println(delRes.getMessage());
                // AE. Sau khi xóa gọi lại displayAllWorkers()
                // Xóa thành công thì lấy lại danh sách worker mới
                // ===== LUỒNG HIỂN THỊ LẠI SAU KHI XÓA =====
                displayAllWorkers();
            } else {
                System.out.println("Lỗi: " + delRes.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi nhập liệu: Phải là số.");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // ===== LUỒNG XÓA TỈNH =====
    // AF. Xóa Province
    private void deleteProvince() {
        // AG. Lấy danh sách Province
        // Lấy danh sách tỉnh từ Server
        List<Province> provinces = getProvinces();
        if (provinces == null || provinces.isEmpty()) return;
        
        printProvinces(provinces);
        // Người dùng chọn tỉnh cần xóa
        System.out.print("Nhập ID tỉnh cần xóa: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            
            // AH. Gửi DELETE_PROVINCE
            // Gửi DELETE_PROVINCE lên Server
            Response response = sendRequest(Operation.DELETE_PROVINCE, id);
            
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
                // AI. Sau khi xóa thành công load lại danh sách Province
                // Nếu xóa được thì Client tải lại danh sách tỉnh
                // ===== LUỒNG HIỂN THỊ LẠI SAU KHI XÓA =====
                printProvinces(getProvinces());
            } else {
                System.out.println("Xóa thất bại: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi nhập liệu: Phải là số.");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ClientApp().start();
    }
}
