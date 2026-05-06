# Tổng Quan Hệ Thống Pacman Java với A* Algorithm

## Mô Tả Tổng Quan
Đây là một dự án game Pacman được triển khai bằng Java sử dụng thư viện AWT/Swing cho giao diện đồ họa. Game sử dụng thuật toán A* để điều khiển các con ma đuổi theo Pacman một cách thông minh. Dự án bao gồm các file chính: `App.java` (điểm vào chính), `PacMan.java` (logic game), và `README.md` (hướng dẫn).

## Cấu Trúc Dự Án
- **App.java**: Class chính khởi tạo JFrame và thêm panel game.
- **PacMan.java**: Class chính chứa toàn bộ logic game, kế thừa JPanel và implement các interface cho event handling.
- **README.md**: Tài liệu hướng dẫn và mô tả tính năng.

## Các Thành Phần Chính

### 1. Class App
Class này chịu trách nhiệm khởi tạo cửa sổ game.

#### Biến Chính:
- `rowCount`: Số hàng của map (21)
- `columnCount`: Số cột của map (19)
- `tileSize`: Kích thước mỗi ô (32 pixel)
- `boardWidth`: Chiều rộng bảng (columnCount * tileSize)
- `boardHeight`: Chiều cao bảng (rowCount * tileSize)

#### Hàm Chính:
- `main(String[] args)`: Tạo JFrame, thiết lập kích thước, vị trí, và thêm PacMan panel vào frame.

### 2. Class PacMan
Class chính chứa toàn bộ logic game.

#### Inner Class: Node
Đại diện cho một node trong thuật toán A*.

##### Biến:
- `r, c`: Tọa độ hàng, cột
- `g`: Chi phí từ điểm bắt đầu
- `h`: Heuristic (ước tính chi phí đến đích)
- `f`: Tổng chi phí (f = g + h)
- `parent`: Node cha

##### Hàm:
- `compareTo(Node other)`: So sánh dựa trên f để sử dụng trong PriorityQueue.

#### Inner Class: Block
Đại diện cho các đối tượng trong game (Pacman, ghost, wall, food).

##### Biến:
- `x, y`: Tọa độ pixel
- `width, height`: Kích thước
- `image`: Hình ảnh
- `speed`: Tốc độ di chuyển
- `ghostType`: Loại ghost (0: đuổi trực diện, 1: đón đầu)
- `nextDirection`: Hướng tiếp theo (cho Pacman buffer input)
- `startX, startY`: Vị trí ban đầu
- `direction`: Hướng hiện tại ('U', 'D', 'L', 'R')
- `velocityX, velocityY`: Vận tốc theo trục X, Y

##### Hàm:
- `Block(Image, int, int, int, int)`: Constructor
- `updateDirection(char)`: Cập nhật hướng và di chuyển, kiểm tra va chạm tường
- `updateVelocity()`: Cập nhật velocityX, velocityY dựa trên direction
- `reset()`: Reset về vị trí ban đầu

#### Biến Chính:
- `rowCount, columnCount, tileSize, boardWidth, boardHeight`: Như trong App
- `wallImage, redGhostImage, pinkGhostImage`: Hình ảnh tường và ma
- `pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage`: Hình ảnh Pacman theo hướng
- `tileMap`: Mảng string đại diện map game
- `walls, foods, ghosts`: HashSet chứa các Block tương ứng
- `pacman`: Block đại diện Pacman
- `gameLoop`: Timer cho game loop
- `directions`: Mảng hướng có thể ('U', 'D', 'L', 'R')
- `random`: Đối tượng Random
- `score`: Điểm số
- `lives`: Số mạng (3)
- `gameOver`: Trạng thái game over

#### Hàm Chính:

##### `PacMan()` (Constructor)
- Thiết lập kích thước panel, background đen
- Thêm KeyListener
- Load hình ảnh từ resources
- Gọi `loadMap()` để khởi tạo map
- Khởi tạo hướng ngẫu nhiên cho ghost
- Khởi động Timer với 50ms delay (20 FPS)

##### `loadMap()`
- Khởi tạo HashSet cho walls, foods, ghosts
- Duyệt qua tileMap để tạo các Block:
  - 'X': Tường
  - 'r': Ma đỏ (ghostType 0)
  - 'p': Ma hồng (ghostType 1)
  - 'P': Pacman
  - ' ': Đồ ăn

##### `paintComponent(Graphics g)`
- Gọi `draw(g)` để vẽ

##### `draw(Graphics g)`
- Vẽ Pacman, ghost, tường, đồ ăn
- Hiển thị điểm số và số mạng

##### `move()`
- Xử lý buffer input cho Pacman (cho phép rẽ khi đứng giữa ô)
- Di chuyển Pacman, kiểm tra va chạm tường
- Logic ghost: Kiểm tra va chạm với Pacman, sử dụng A* để tìm đường
- Xử lý ăn đồ ăn, thắng game

##### `getNextDirectionAStar(Block ghost, int goalR, int goalC)`
- Triển khai thuật toán A* để tìm hướng tốt nhất từ vị trí ghost đến đích (goalR, goalC)
- Sử dụng PriorityQueue cho open list
- Heuristic: Khoảng cách Manhattan
- Trả về hướng đầu tiên trong đường đi

##### `collision(Block a, Block b)`
- Kiểm tra va chạm giữa hai Block bằng AABB collision detection

##### `resetPositions()`
- Reset vị trí Pacman và ghost về ban đầu
- Đặt hướng ngẫu nhiên cho ghost

##### `actionPerformed(ActionEvent e)`
- Gọi `move()` và `repaint()`
- Dừng timer nếu game over

##### `keyReleased(KeyEvent e)`
- Xử lý phím mũi tên để thay đổi hướng Pacman
- Reset game nếu game over

##### `updatePacmanImage(char direction)`
- Cập nhật hình ảnh Pacman dựa trên hướng

## Thuật Toán A*
- Sử dụng để điều khiển ghost đuổi theo Pacman
- Heuristic: Khoảng cách Manhattan (|dr| + |dc|)
- PriorityQueue để chọn node có f nhỏ nhất
- Trả về hướng đầu tiên trong đường đi tối ưu

## AI Ghost
- **Ghost Type 0**: Đuổi trực diện Pacman
- **Ghost Type 1**: Đón đầu Pacman (dự đoán vị trí tương lai)
- Khi xa (>5 ô): Dự đoán vị trí Pacman sẽ đến dựa trên hướng di chuyển
- Khi gần: Đuổi trực diện

## Tính Năng Game
- Di chuyển Pacman bằng phím mũi tên
- Ghost di chuyển thông minh bằng A*
- Va chạm: Pacman ăn đồ ăn (+10 điểm), chạm ghost (-1 mạng)
- Wrap-around: Chưa implement (homework)
- Power pellet: Chưa implement (homework)
- Thắng khi ăn hết đồ ăn

