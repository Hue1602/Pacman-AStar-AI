# Pacman Java với A* Algorithm

## Mô tả dự án
Đây là một phiên bản game Pacman được triển khai bằng ngôn ngữ Java sử dụng thư viện AWT/Swing cho giao diện đồ họa. Game sử dụng thuật toán A* (A-star) để điều khiển các con ma đuổi theo Pacman một cách thông minh, thay vì di chuyển ngẫu nhiên như trong phiên bản cơ bản.

## Tính năng chính
- **Di chuyển Pacman**: Sử dụng phím mũi tên để điều khiển Pacman di chuyển trong mê cung.
- **AI Ghost thông minh**: Các con ma sử dụng thuật toán A* để tìm đường ngắn nhất đến Pacman.
- **Ăn pellet**: Pacman ăn các chấm trắng để tăng điểm số.
- **Va chạm**: Nếu Pacman chạm vào ma, mất một mạng. Hết mạng thì game over.
- **Điểm số và mạng**: Hiển thị điểm số và số mạng còn lại.
- **Thắng game**: Ăn hết tất cả pellet để thắng và chơi lại.

## Yêu cầu hệ thống
- Java JDK (phiên bản 8 trở lên)
- Hệ điều hành: Windows, macOS, hoặc Linux

## Cách chạy
1. Đảm bảo bạn đã cài đặt Java JDK. Kiểm tra bằng lệnh:
   ```
   java -version
   javac -version
   ```

2. Clone hoặc tải xuống dự án này.

3. Mở terminal/command prompt và điều hướng đến thư mục chứa file `App.java` và `PacMan.java`.

4. Biên dịch các file Java:
   ```
   javac App.java PacMan.java
   ```

5. Chạy game:
   ```
   java App
   ```

6. Game sẽ mở ra cửa sổ mới. Sử dụng phím mũi tên để điều khiển Pacman.

## Cấu trúc file
- `App.java`: Class chính khởi tạo cửa sổ game (JFrame).
- `PacMan.java`: Class chứa logic game chính, kế thừa JPanel.
- `SystemOverview.md`: Tài liệu tổng quan về code và chức năng (tự tạo).

## Đề xuất phát triển
- **Thiết kế map tùy chỉnh**: Thay đổi `tileMap` trong `PacMan.java` để tạo mê cung mới.
- **Thêm power pellet**: Thêm pellet đặc biệt cho phép Pacman ăn ma trong thời gian ngắn.
- **Wrap-around**: Khi Pacman đi qua cạnh trái/phải, xuất hiện ở bên kia.
- **Cải thiện AI**: Làm cho ma thông minh hơn, có thể đi tắt hoặc hợp tác.



