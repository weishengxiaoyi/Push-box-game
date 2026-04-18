/**
 * Represents one floor of the Magic Tower.
 * Contains a 9x9 grid of Cell objects.
 * Each floor is built with its own layout.
 */
public class Floor {

    // 9×9 grid — each element is a Cell or subclass
    private Cell[][] grid;

    // Which floor number this is (1 to 5)
    private int floorNumber;

    public static final int SIZE = 9; // Grid dimensions

    /**
     * Constructor — build floor layout based on floor number.
     * @param floorNumber floor number 1–5
     */
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        grid = new Cell[SIZE][SIZE]; // Initialise 2D array
        buildFloor();
    }

    // Return the cell at position (x=col, y=row)
    public Cell getCell(int x, int y) {
        return grid[y][x]; // row = y, col = x
    }

    // Replace a cell (e.g., when door opens or item collected)
    public void setCell(int x, int y, Cell c) {
        grid[y][x] = c;
    }

    public int getFloorNumber() { return floorNumber; }

    /**
     * Build the grid layout for this floor.
     * Uses floor number to determine layout.
     */
    public void buildFloor() {
        // First fill everything as WALL
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = new Cell(col, row, "WALL");
            }
        }

        // Load layout based on floor number
        switch (floorNumber) {
            case 1: buildFloor1(); break;
            case 2: buildFloor2(); break;
            case 3: buildFloor3(); break;
            case 4: buildFloor4(); break;
            case 5: buildFloor5(); break;
        }
    }

    // Helper: set a cell as empty floor
    private void setEmpty(int col, int row) {
        grid[row][col] = new Cell(col, row, "EMPTY");
    }

    /**
     * Floor 1 layout:
     * W  W  W  W  W  W  W  W  W
     * W  P  .  .  YK .  .  E  W
     * W  .  W  W  YD W  W  .  W
     * W  .  W  .  .  .  W  .  W
     * W  .  W  .  W  .  W  .  W
     * W  .  W  .  .  .  W  .  W
     * W  .  W  W  W  W  W  .  W
     * W  .  .  .  .  .  .  U  W
     * W  W  W  W  W  W  W  W  W
     */
    private void buildFloor1() {
        // Row 1
        setEmpty(1, 1); setEmpty(2, 1); setEmpty(3, 1);
        grid[1][4] = new Item(4, 1, "KEY_YELLOW");
        setEmpty(5, 1); setEmpty(6, 1);
        grid[1][7] = new Enemy(7, 1, 10, 3);

        // Row 2
        setEmpty(1, 2);
        grid[2][4] = new Door(4, 2, "YELLOW");
        setEmpty(7, 2);

        // Rows 3–5
        setEmpty(1, 3); setEmpty(3, 3);
        setEmpty(4, 3); setEmpty(5, 3); setEmpty(7, 3);
        setEmpty(1, 4); setEmpty(3, 4);
        setEmpty(5, 4); setEmpty(7, 4);
        setEmpty(1, 5); setEmpty(3, 5);
        setEmpty(4, 5); setEmpty(5, 5); setEmpty(7, 5);

        // Row 6
        setEmpty(1, 6);

        // Row 7 — corridor to stairs
        for (int col = 1; col <= 6; col++) {
            setEmpty(col, 7);
        }
        grid[7][7] = new Item(7, 7, "STAIRS_UP");
    }

    // Floor 2 — corridor layout with Blue Key and Blue Door
    private void buildFloor2() {
        for (int col = 1; col <= 7; col++) setEmpty(col, 1);
        grid[1][2] = new Item(2, 1, "KEY_BLUE");

        for (int row = 1; row <= 6; row++) setEmpty(1, row);
        grid[3][4] = new Door(4, 3, "BLUE");
        for (int col = 4; col <= 7; col++) setEmpty(col, 4);
        grid[4][7] = new Enemy(7, 4, 15, 5);
        grid[7][7] = new Item(7, 7, "STAIRS_UP");
        grid[7][1] = new Item(1, 7, "STAIRS_DOWN");
    }

    // Floor 3 — open corridors with enemies and Yellow Door
    private void buildFloor3() {
        for (int col = 1; col <= 7; col++) setEmpty(col, 4);
        for (int row = 1; row <= 7; row++) setEmpty(1, row);
        for (int row = 1; row <= 7; row++) setEmpty(7, row);
        grid[1][4] = new Enemy(4, 1, 20, 6);
        grid[4][4] = new Enemy(4, 4, 25, 7);
        grid[1][2] = new Item(2, 1, "KEY_YELLOW");
        grid[3][6] = new Door(6, 3, "YELLOW");
        grid[7][7] = new Item(7, 7, "STAIRS_UP");
        grid[7][1] = new Item(1, 7, "STAIRS_DOWN");
    }

    // Floor 4 — perimeter walkable with Blue Door and enemies
    private void buildFloor4() {
        for (int col = 1; col <= 7; col++) setEmpty(col, 1);
        for (int col = 1; col <= 7; col++) setEmpty(col, 7);
        for (int row = 1; row <= 7; row++) setEmpty(1, row);
        for (int row = 1; row <= 7; row++) setEmpty(7, row);
        grid[1][3] = new Item(3, 1, "KEY_BLUE");
        grid[4][4] = new Door(4, 4, "BLUE");
        grid[1][5] = new Enemy(5, 1, 30, 8);
        grid[5][5] = new Enemy(5, 5, 35, 9);
        grid[7][7] = new Item(7, 7, "STAIRS_UP");
        grid[7][1] = new Item(1, 7, "STAIRS_DOWN");
    }

    // Floor 5 — contains the Boss at centre
    private void buildFloor5() {
        for (int col = 1; col <= 7; col++) setEmpty(col, 1);
        for (int col = 1; col <= 7; col++) setEmpty(col, 7);
        for (int row = 1; row <= 7; row++) setEmpty(1, row);
        for (int row = 1; row <= 7; row++) setEmpty(7, row);
        for (int col = 2; col <= 6; col++) setEmpty(col, 4);

        // Guards before boss
        grid[1][3] = new Enemy(3, 1, 40, 10);
        grid[1][5] = new Enemy(5, 1, 40, 10);

        // Boss at centre
        grid[4][4] = new Enemy(4, 4, 100, 20, true);

        // Stairs down to floor 4
        grid[7][1] = new Item(1, 7, "STAIRS_DOWN");
    }
}
