import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Node implements Comparable<Node> {
        int r, c, g, h, f;
        Node parent;

        Node(int r, int c, int g, int h, Node parent) {
            this.r = r;
            this.c = c;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    private void updatePacmanImage(char direction) {
        if (direction == 'U') {
            pacman.image = pacmanUpImage;
        } else if (direction == 'D') {
            pacman.image = pacmanDownImage;
        } else if (direction == 'L') {
            pacman.image = pacmanLeftImage;
        } else if (direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }

    // Sửa signature của hàm để nhận vào int goalR và int goalC
    public char getNextDirectionAStar(Block ghost, int goalR, int goalC) {
        int startR = ghost.y / tileSize;
        int startC = ghost.x / tileSize;

        java.util.PriorityQueue<Node> openList = new java.util.PriorityQueue<>();
        boolean[][] visited = new boolean[rowCount][columnCount];

        // Tính khoảng cách tới đích dựa trên goalR và goalC
        openList.add(new Node(startR, startC, 0, Math.abs(startR - goalR) + Math.abs(startC - goalC), null));

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            // Kiểm tra đích
            if (current.r == goalR && current.c == goalC) {
                Node firstStep = current;
                while (firstStep.parent != null && firstStep.parent.parent != null) {
                    firstStep = firstStep.parent;
                }
                if (firstStep.r < startR)
                    return 'U';
                if (firstStep.r > startR)
                    return 'D';
                if (firstStep.c < startC)
                    return 'L';
                return 'R';
            }

            visited[current.r][current.c] = true;

            int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
            for (int[] d : dirs) {
                int nr = current.r + d[0];
                int nc = current.c + d[1];

                if (nr >= 0 && nr < rowCount && nc >= 0 && nc < columnCount
                        && tileMap[nr].charAt(nc) != 'X' && !visited[nr][nc]) {

                    // Cập nhật heuristic dùng goalR và goalC
                    openList.add(new Node(nr, nc, current.g + 1, Math.abs(nr - goalR) + Math.abs(nc - goalC), current));
                }
            }
        }
        return ghost.direction;
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        // Thêm biến speed cho mỗi block
        int speed = tileSize / 8; // Tốc độ mặc định
        int ghostType = 0; // 0: đuổi trực diện, 1: đón đầu
        char nextDirection = ' '; // Biến này lưu lệnh rẽ tạm thời

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -speed;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = speed;
            } else if (this.direction == 'L') {
                this.velocityX = -speed;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = speed;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image redGhostImage;
    private Image pinkGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X                 X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X r  X    p  X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X                 X", // Chỉ để lại 1 con ghost ở đây
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X                 X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = { 'U', 'D', 'L', 'R' }; // up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
            ghost.speed = tileSize / 8;
        }
        // how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); // 20fps (1000/50)
        gameLoop.start();

    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                // Điều kiện mới: Chỉ thêm Ghost nếu chưa có con nào được thêm
                else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = 0; // Con này đuổi trực diện
                    ghost.speed = tileSize / 8;
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = 1; // Con này đón đầu
                    ghost.speed = tileSize / 8;
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    pacman.speed = tileSize / 4;
                } else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        // score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        } else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }
    }

    public void move() {
        // --- 1. XỬ LÝ RẼ (BUFFER INPUT) CHO PACMAN ---
        // Kiểm tra nếu Pacman đang đứng giữa ô lưới thì mới cho phép chuyển hướng
        if (pacman.x % tileSize == 0 && pacman.y % tileSize == 0 && pacman.nextDirection != ' ') {
            char tempDir = pacman.direction;
            pacman.direction = pacman.nextDirection;
            pacman.updateVelocity(); // Cập nhật velocityX/Y theo hướng mới

            // Kiểm tra xem hướng mới có bị đâm vào tường không
            pacman.x += pacman.velocityX;
            pacman.y += pacman.velocityY;
            boolean wallCollision = false;
            for (Block wall : walls) {
                if (collision(pacman, wall)) {
                    wallCollision = true;
                    break;
                }
            }
            pacman.x -= pacman.velocityX; // Lùi lại vị trí cũ
            pacman.y -= pacman.velocityY;

            if (!wallCollision) {
                // Rẽ thành công, cập nhật hình ảnh và xóa lệnh rẽ
                updatePacmanImage(pacman.direction);
                pacman.nextDirection = ' ';
            } else {
                // Rẽ thất bại (đâm tường), trả về hướng cũ
                pacman.direction = tempDir;
                pacman.updateVelocity();
            }
        }

        // --- 2. DI CHUYỂN PACMAN ---
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Va chạm tường Pacman
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // --- 3. LOGIC GHOST (AI & DI CHUYỂN) ---
        for (Block ghost : ghosts) {
            // Kiểm tra va chạm với Pacman
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            // Chỉ tính toán đường đi A* khi Ghost nằm khớp ô lưới
            if (ghost.x % tileSize == 0 && ghost.y % tileSize == 0) {
                int targetR = pacman.y / tileSize;
                int targetC = pacman.x / tileSize;

                // AI Nâng cấp: Nếu là Ghost đón đầu (ghostType == 1)
                if (ghost.ghostType == 1) {
                    // Tính khoảng cách để chuyển trạng thái từ "Đón đầu" sang "Đuổi trực diện"
                    int dist = Math.abs((ghost.y / tileSize) - targetR) + Math.abs((ghost.x / tileSize) - targetC);

                    if (dist > 5) { // Nếu còn xa: Đón đầu
                        int offset = 4;
                        if (pacman.direction == 'U')
                            targetR -= offset;
                        else if (pacman.direction == 'D')
                            targetR += offset;
                        else if (pacman.direction == 'L')
                            targetC -= offset;
                        else if (pacman.direction == 'R')
                            targetC += offset;

                        // Kiểm tra nếu điểm đón đầu bị tường chặn -> quay về đuổi trực diện
                        targetR = Math.max(0, Math.min(rowCount - 1, targetR));
                        targetC = Math.max(0, Math.min(columnCount - 1, targetC));
                        if (tileMap[targetR].charAt(targetC) == 'X') {
                            targetR = pacman.y / tileSize;
                            targetC = pacman.x / tileSize;
                        }
                    }
                }

                char bestDir = getNextDirectionAStar(ghost, targetR, targetC);
                ghost.updateDirection(bestDir);
            }

            // Di chuyển Ghost
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Va chạm tường Ghost (Nếu đâm tường thì chọn ngẫu nhiên hướng mới)
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                    break;
                }
            }
        }

        // --- 4. XỬ LÝ ĂN ĐIỂM & THẮNG ---
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        char keyDir = ' ';
        if (e.getKeyCode() == KeyEvent.VK_UP)
            keyDir = 'U';
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            keyDir = 'D';
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            keyDir = 'L';
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            keyDir = 'R';

        if (keyDir != ' ') {
            pacman.nextDirection = keyDir;
            updatePacmanImage(keyDir); // Gọi hàm cập nhật ảnh ở đây
        }
    }
}
