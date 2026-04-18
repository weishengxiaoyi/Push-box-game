/**
 * Controls all game logic for the Magic Tower game.
 * Manages the player, all 5 floors, and game state.
 * Separation of concerns: logic only, no drawing here.
 */
public class GameController {

    private Player  player;       // The player character
    private Floor[] floors;       // All 5 floors
    private int     currentFloor; // Index 0–4 (floor 1–5)
    private boolean gameOver;
    private boolean gameComplete;
    private boolean paused;

    // Grid and cell size constants
    public static final int CELL_SIZE  = 60;
    public static final int GRID_SIZE  = Floor.SIZE; // 9
    public static final int NUM_FLOORS = 5;

    // Player starting position on floor 1
    private static final int START_X = 1;
    private static final int START_Y = 1;

    public GameController() {
        resetGame();
    }

    // Reset all game state to initial values
    public void resetGame() {
        // Build all 5 floors
        floors = new Floor[NUM_FLOORS];
        for (int i = 0; i < NUM_FLOORS; i++) {
            floors[i] = new Floor(i + 1); // Floor 1 to 5
        }

        // Create player at starting position
        player       = new Player(START_X, START_Y);
        currentFloor = 0;     // Start on floor 1 (index 0)
        gameOver     = false;
        gameComplete = false;
        paused       = true;
    }

    /**
     * Attempt to move the player by (dx, dy).
     * Validates target cell before moving.
     * Handles: walls, doors, enemies, items, stairs.
     * @param dx column offset (-1, 0, or +1)
     * @param dy row offset (-1, 0, or +1)
     */
    public void movePlayer(int dx, int dy) {
        if (paused || gameOver || gameComplete) return;

        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        // Pre-validation: check bounds
        if (newX < 0 || newX >= GRID_SIZE ||
            newY < 0 || newY >= GRID_SIZE) {
            return; // Cannot move outside grid
        }

        Cell   target = getCurrentFloor().getCell(newX, newY);
        String type   = target.getType();

        switch (type) {
            case "WALL":
                // Cannot walk into walls — do nothing
                break;
            case "EMPTY":
                // Free to move
                player.move(dx, dy);
                break;
            case "DOOR":
                handleDoor((Door) target, newX, newY);
                break;
            case "ENEMY":
                handleCombat((Enemy) target, newX, newY);
                break;
            case "ITEM":
                handleItem((Item) target, newX, newY);
                break;
            default:
                break;
        }
    }

    /**
     * Handle player attempting to open a door.
     * Uses instanceof to identify door colour and required key.
     */
    private void handleDoor(Door door, int newX, int newY) {
        String keyType = door.getColour().equals("YELLOW")
                         ? "KEY_YELLOW" : "KEY_BLUE";

        // Pre-validation: check if player has the correct key
        if (player.useKey(keyType)) {
            door.open(); // Consumes key and opens door
            player.move(newX - player.getX(), newY - player.getY());
        }
        // If no key: door stays closed, player does not move
    }

    /**
     * Handle combat between player and enemy.
     * Player attacks first, then enemy retaliates.
     */
    private void handleCombat(Enemy enemy, int newX, int newY) {
        // Combat loop: player and enemy exchange attacks
        while (!enemy.isDead() && !player.isDead()) {
            enemy.takeDamage(player.getAttack());
            if (!enemy.isDead()) {
                player.takeDamage(enemy.getAttack());
            }
        }

        if (player.isDead()) {
            // Player loses
            gameOver = true;
        } else {
            // Enemy defeated — player moves to that cell
            getCurrentFloor().setCell(newX, newY,
                    new Cell(newX, newY, "EMPTY"));
            player.move(newX - player.getX(), newY - player.getY());

            // Check if boss was defeated on floor 5
            if (enemy.isBoss() && currentFloor == 4) {
                gameComplete = true;
            }
        }
    }

    /**
     * Handle player walking over an item (key or stairs).
     */
    private void handleItem(Item item, int newX, int newY) {
        String itemType = item.getItemType();

        switch (itemType) {
            case "KEY_YELLOW":
            case "KEY_BLUE":
                // Collect key and remove from floor
                player.collectKey(itemType);
                getCurrentFloor().setCell(newX, newY,
                        new Cell(newX, newY, "EMPTY"));
                player.move(newX - player.getX(), newY - player.getY());
                break;
            case "STAIRS_UP":
                // Pre-validation: cannot go above floor 5
                if (currentFloor < NUM_FLOORS - 1) {
                    currentFloor++;
                    // Place player at bottom-left of new floor
                    respawnPlayerOnFloor(1, 7);
                }
                break;
            case "STAIRS_DOWN":
                // Pre-validation: cannot go below floor 1
                if (currentFloor > 0) {
                    currentFloor--;
                    // Place player at stairs-up position
                    respawnPlayerOnFloor(7, 7);
                }
                break;
        }
    }

    // Move player to a specific position on current floor
    private void respawnPlayerOnFloor(int x, int y) {
        int dx = x - player.getX();
        int dy = y - player.getY();
        player.move(dx, dy);
    }

    // ─── Getters ──────────────────────────────────────────────────────────────
    public Floor   getCurrentFloor()       { return floors[currentFloor]; }
    public Player  getPlayer()             { return player;               }
    public boolean isGameOver()            { return gameOver;             }
    public boolean isGameComplete()        { return gameComplete;         }
    public boolean isPaused()              { return paused;               }
    public int     getCurrentFloorNumber() { return currentFloor + 1;     }
    public void    togglePause()           { paused = !paused;            }
}
