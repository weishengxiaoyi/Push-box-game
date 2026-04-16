package game;

public class Board {
    public static final int EMPTY = 0;
    public static final int FLOOR = 1;
    public static final int WALL = 2;
    public static final int GOAL = 3;
    public static final int CRATE = 4;
    public static final int CRATE_ON_GOAL = 5;
    public static final int PLAYER = 6;
    public static final int PLAYER_ON_GOAL = 7;

    private int[][] grid;
    private final int rows;
    private final int cols;
    private int playerRow;
    private int playerCol;

    public Board(int[][] grid, int playerRow, int playerCol) {
        this.grid = deepCopy(grid);
        this.rows = grid.length;
        this.cols = maxCols(grid);
        this.playerRow = playerRow;
        this.playerCol = playerCol;
    }

    private static int[][] deepCopy(int[][] src) {
        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i].clone();
        }
        return copy;
    }

    private static int maxCols(int[][] grid) {
        int max = 0;
        for (int[] row : grid) {
            if (row.length > max) max = row.length;
        }
        return max;
    }

    public int getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= grid[row].length) return EMPTY;
        return grid[row][col];
    }

    public void setCell(int row, int col, int value) {
        if (row >= 0 && row < rows && col >= 0 && col < grid[row].length) {
            grid[row][col] = value;
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getPlayerRow() { return playerRow; }
    public int getPlayerCol() { return playerCol; }
    public void setPlayerRow(int r) { this.playerRow = r; }
    public void setPlayerCol(int c) { this.playerCol = c; }

    public boolean isWon() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == CRATE) return false;
            }
        }
        return true;
    }

    public Board copy() {
        return new Board(grid, playerRow, playerCol);
    }

    public static Board fromLevel(Level level) {
        String[] mapData = level.mapData;
        int rows = mapData.length;
        int cols = 0;
        for (String row : mapData) {
            if (row.length() > cols) cols = row.length();
        }
        int[][] grid = new int[rows][cols];
        int playerRow = 0, playerCol = 0;

        for (int r = 0; r < rows; r++) {
            String line = mapData[r];
            for (int c = 0; c < cols; c++) {
                char ch = c < line.length() ? line.charAt(c) : ' ';
                switch (ch) {
                    case '#': grid[r][c] = WALL; break;
                    case ' ': grid[r][c] = EMPTY; break;
                    case '.': grid[r][c] = GOAL; break;
                    case '$': grid[r][c] = CRATE; break;
                    case '@': grid[r][c] = PLAYER; playerRow = r; playerCol = c; break;
                    case '*': grid[r][c] = CRATE_ON_GOAL; break;
                    case '+': grid[r][c] = PLAYER_ON_GOAL; playerRow = r; playerCol = c; break;
                    default:  grid[r][c] = FLOOR; break;
                }
            }
        }
        return new Board(grid, playerRow, playerCol);
    }
}
