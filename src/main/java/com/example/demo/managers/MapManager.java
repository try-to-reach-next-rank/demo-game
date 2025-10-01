package com.example.demo.managers;

import com.example.demo.core.Brick;
import com.example.demo.core.Wall;
import com.example.demo.core.VARIABLES;
import com.example.demo.core.MapData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {

    // Kích thước ô trong ma trận (tương đương 1 Brick/Wall + Padding)
    private final int CELL_W = VARIABLES.WIDTH_OF_BRICKS + VARIABLES.PADDING_X;
    private final int CELL_H = VARIABLES.HEIGHT_OF_BRICKS + VARIABLES.PADDING_Y;

    // Kích thước Ma trận (số lượng ô)
    private final int MATRIX_COLS = VARIABLES.BRICKS_PER_ROW; // 15
    // Ước tính số hàng có thể chứa trong khu vực chơi giữa tường biên trên và dưới
    private final int MATRIX_ROWS = (VARIABLES.HEIGHT - VARIABLES.HEIGHT_OF_WALLS * 2) / CELL_H; // ~35-36 hàng

    // Tọa độ bắt đầu của ma trận (sau tường biên)
    private final int MATRIX_START_X = VARIABLES.WIDTH_OF_WALLS;
    private final int MATRIX_START_Y = VARIABLES.HEIGHT_OF_WALLS;

    private final Random rand = new Random();

    /**
     * Tải map cho level chỉ định từ ma trận tương ứng.
     * @param level Số level (1, 2, 3...)
     */
    public MapData loadMap(int level) {
        int[][] matrix;
        if (level == 1) matrix = createMap1Matrix();
        else if (level == 2) matrix = createMap2Matrix();
        else if (level == 3) matrix = createMap3Matrix();
        else matrix = createRandomMatrix(); // Level mặc định ngẫu nhiên

        return loadMapFromMatrix(matrix);
    }

    /**
     * Chuyển đổi Ma trận (0, 1, 2) thành danh sách các đối tượng Brick và Wall.
     */
    public MapData loadMapFromMatrix(int[][] matrix) {
        List<Brick> bricks = new ArrayList<>();
        List<Wall> walls = createBoundaryWalls(); // Luôn thêm tường biên cố định

        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {

                // Tọa độ vật lý (pixel)
                double x = MATRIX_START_X + c * CELL_W;
                double y = MATRIX_START_Y + r * CELL_H;

                if (matrix[r][c] == 1) { // Gạch (có thể phá hủy)
                    bricks.add(new Brick((int)x, (int)y));
                } else if (matrix[r][c] == 2) { // Tường (không phá hủy)
                    // Dùng kích thước Brick/Cell cho tường được sinh từ Ma trận
                    walls.add(new Wall(Wall.Side.TOP, x, y, VARIABLES.WIDTH_OF_BRICKS, VARIABLES.HEIGHT_OF_BRICKS));
                }
            }
        }
        return new MapData(bricks, walls);
    }

    // --- Phương thức tạo Tường Biên (Tường cố định xung quanh màn hình) ---
    private List<Wall> createBoundaryWalls() {
        List<Wall> walls = new ArrayList<>();

        // Tường trái, phải, trên
        for (int i = 0; i < VARIABLES.N_OF_WALLS_LEFT_RIGHT; i++) {
            // Tường Trái
            walls.add(new Wall(Wall.Side.LEFT, 0, i * VARIABLES.HEIGHT_OF_WALLS, VARIABLES.WIDTH_OF_WALLS, VARIABLES.HEIGHT_OF_WALLS));
            // Tường Phải
            walls.add(new Wall(Wall.Side.RIGHT, VARIABLES.WIDTH - VARIABLES.WIDTH_OF_WALLS, i * VARIABLES.HEIGHT_OF_WALLS, VARIABLES.WIDTH_OF_WALLS, VARIABLES.HEIGHT_OF_WALLS));
        }

        // Tường trên
        for (int i = 0; i < VARIABLES.N_OF_WALLS_TOP; i++) {
            walls.add(new Wall(Wall.Side.TOP, i * VARIABLES.WIDTH_OF_WALLS, 0, VARIABLES.WIDTH_OF_WALLS, VARIABLES.HEIGHT_OF_WALLS));
        }
        return walls;
    }

    // --- Các hàm khởi tạo Ma trận cho từng Level (0: Trống, 1: Gạch, 2: Tường) ---

    // 1. Level 1: Nửa trên là Gạch (1)
    public int[][] createMap1Matrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];

        // Số hàng tương ứng với Y <= 400 (nửa trên)
        final int HALF_SCREEN_ROWS = (VARIABLES.HEIGHT / 2 - MATRIX_START_Y) / CELL_H;

        for (int r = 0; r < HALF_SCREEN_ROWS; r++) {
            for (int c = 0; c < MATRIX_COLS; c++) {
                map[r][c] = 1; // Gạch
            }
        }
        return map;
    }

    // 2. Level 2: Hình trái tim (1) được mở rộng và có lỗ hổng (0)
    public int[][] createMap2Matrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];

        // Tọa độ tương đối của hình trái tim (1: Gạch) - ĐÃ MỞ RỘNG (15 hàng)
        int[][] heartShape = {
                {0,0,0,0,1,1,0,0,0,1,1,0,0,0,0}, // Hàng 0
                {0,0,0,1,1,1,1,0,1,1,1,1,0,0,0}, // Hàng 1
                {0,0,1,1,1,1,1,1,1,1,1,1,0,0,0}, // Hàng 2
                {0,1,1,1,1,1,1,1,1,1,1,1,1,0,0}, // Hàng 3
                {0,1,1,1,1,1,1,1,1,1,1,1,1,0,0}, // Hàng 4
                {0,1,1,1,1,1,1,1,1,1,1,1,1,0,0}, // Hàng 5
                {0,0,1,1,1,1,1,1,1,1,1,0,0,0,0}, // Hàng 6
                {0,0,0,1,1,1,1,1,1,1,0,0,0,0,0}, // Hàng 7
                {0,0,0,0,1,1,1,1,1,0,0,0,0,0,0}, // Hàng 8
                {0,0,0,0,0,1,1,1,0,0,0,0,0,0,0}, // Hàng 9
                {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},  // row 9 (Tip nhọn: c=7)
        };

        // Bắt đầu vẽ từ hàng 1 (cao hơn)
        final int START_ROW = 1;
        final int HEART_ROWS = heartShape.length; // 15

        // 1. Vẽ Gạch Trái Tim (1)
        for (int r = 0; r < HEART_ROWS; r++) {
            for (int c = 0; c < MATRIX_COLS; c++) {
                if (heartShape[r][c] == 1) {
                    map[START_ROW + r][c] = 1; // Gạch
                }
            }
        }

        // 2. Vẽ Tường Bao (2) xung quanh gạch (1)
        for (int r = 0; r < HEART_ROWS; r++) {
            for (int c = 0; c < MATRIX_COLS; c++) {
                if (heartShape[r][c] == 1) {
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (dr == 0 && dc == 0) continue;

                            int nr = START_ROW + r + dr;
                            int nc = c + dc;

                            // Kiểm tra: nằm trong bounds VÀ là ô trống (0)
                            if (nr >= 0 && nr < MATRIX_ROWS && nc >= 0 && nc < MATRIX_COLS && map[nr][nc] == 0) {
                                map[nr][nc] = 2; // Đặt Tường
                            }
                        }
                    }
                }
            }
        }

        // 3. TẠO LỖ HỔNG CỐ ĐỊNH (BUỘC LÀ 0)
        // Vị trí lỗ hổng ngay dưới đỉnh nhọn của trái tim (Hàng 9 trong heartShape)
        // Lỗ hổng sẽ nằm ở Hàng 10 của heartShape, Cột 6, 7, 8.
        final int GAP_ROW = START_ROW + 10;
        final int GAP_COL_C = 7;

        if (GAP_ROW < MATRIX_ROWS) {
            // Xóa Tường để tạo lỗ hổng vào
            map[GAP_ROW][GAP_COL_C] = 0; // Trung tâm
            if (GAP_COL_C > 0) map[GAP_ROW][GAP_COL_C - 1] = 0; // Trái
            if (GAP_COL_C < MATRIX_COLS - 1) map[GAP_ROW][GAP_COL_C + 1] = 0; // Phải
        }

        return map;
    }

    // 3. Level 3: Hình cột (2) và Gạch chéo (1)
    public int[][] createMap3Matrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];

        final int TOWER_HEIGHT = 30;
        final int TOWER_COL_START_L = 4;
        final int TOWER_COL_END_R = 10;

        // Cột trái (Cols 4-5) và Cột phải (Cols 9-10)
        for (int r = 0; r < TOWER_HEIGHT; r++) {
            map[r][TOWER_COL_START_L] = 2;
            map[r][TOWER_COL_START_L + 1] = 2;
            map[r][TOWER_COL_END_R - 1] = 2;
            map[r][TOWER_COL_END_R] = 2;
        }

        // Gạch Xếp Chéo (1)
        final int DIAGONAL_LENGTH = 10;
        final int BRICK_START_ROW = 25;

        // Chéo Trái
        for (int i = 0; i < DIAGONAL_LENGTH; i++) {
            int r = BRICK_START_ROW - i;
            int c = TOWER_COL_START_L + 2 + i;
            if (r >= 0 && c < TOWER_COL_END_R - 1) map[r][c] = 1;
        }

        // Chéo Phải
        for (int i = 0; i < DIAGONAL_LENGTH; i++) {
            int r = BRICK_START_ROW - i;
            int c = TOWER_COL_END_R - 2 - i;
            if (r >= 0 && c > TOWER_COL_START_L + 1) map[r][c] = 1;
        }

        // Tường Đáy Dưới (2)
        final int GROUND_ROW = MATRIX_ROWS - 5;
        for (int c = 0; c <= 3; c++) map[GROUND_ROW][c] = 2;
        for (int c = 11; c < MATRIX_COLS; c++) map[GROUND_ROW][c] = 2;

        return map;
    }

    // 4. Level Ngẫu Nhiên: Dùng cho level 4 trở đi
    public int[][] createRandomMatrix() {
        int[][] map = new int[MATRIX_ROWS][MATRIX_COLS];

        for (int r = 0; r < MATRIX_ROWS / 2; r++) { // Chỉ tạo ở nửa trên
            for (int c = 0; c < MATRIX_COLS; c++) {
                int type = rand.nextInt(10);
                if (type < 4) map[r][c] = 1; // 40% Gạch
                else if (type < 5) map[r][c] = 2; // 10% Tường
                // Còn lại 50% là 0 (Trống)
            }
        }
        return map;
    }
}