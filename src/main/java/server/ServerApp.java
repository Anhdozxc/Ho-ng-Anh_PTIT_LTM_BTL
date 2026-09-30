package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    // khai báo PORT
    private static final int PORT = 8888;

    public static void main(String[] args) {
        // tạo ServerSocket
        // Mở cổng 8888 để chờ Client kết nối
        // ServerSocket = nơi Server chờ Client.
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT + "...");
            
            // bắt đầu lắng nghe
            // vòng lặp tiếp tục chờ Client khác
            while (true) {
                // accept Client
                // Nhận kết nối từ Client
                // lấy Socket của Client
                // Socket = kết nối cụ thể với một Client.
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                
                // tạo ClientHandler
                // start Thread
                // Mỗi Client được xử lý bằng một Thread riêng
                new ClientHandler(socket).start();
            }
        } catch (Exception e) {
            // xử lý lỗi Server
            e.printStackTrace();
        }
    }
}
