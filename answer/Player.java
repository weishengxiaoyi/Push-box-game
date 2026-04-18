/**
 * Represents the player character in the Magic Tower game.
 * Tracks position, HP, attack power, and key inventory.
 */
public class Player {

    private int x;          // Current grid column
    private int y;          // Current grid row
    private int hp;         // Current hit points
    private int attack;     // Attack power
    private int yellowKeys; // Yellow keys held
    private int blueKeys;   // Blue keys held

    /**
     * Constructor — initialise player at given position
     * with default stats.
     * @param x starting grid column
     * @param y starting grid row
     */
    public Player(int x, int y) {
        this.x          = x;
        this.y          = y;
        this.hp         = 100;
        this.attack     = 10;
        this.yellowKeys = 0;
        this.blueKeys   = 0;
    }

    // Move player by offset — called after validation
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    // Add a key to inventory based on type
    public void collectKey(String type) {
        if (type.equals("KEY_YELLOW")) {
            yellowKeys++;
        } else if (type.equals("KEY_BLUE")) {
            blueKeys++;
        }
    }

    /**
     * Use one key of the specified colour.
     * @param type "KEY_YELLOW" or "KEY_BLUE"
     * @return true if key was used, false if none available
     */
    public boolean useKey(String type) {
        if (type.equals("KEY_YELLOW") && yellowKeys > 0) {
            yellowKeys--;
            return true;
        } else if (type.equals("KEY_BLUE") && blueKeys > 0) {
            blueKeys--;
            return true;
        }
        return false; // Pre-validation: not enough keys
    }

    // Reduce player HP when taking damage
    public void takeDamage(int amount) {
        hp -= amount;
    }

    // Returns true when player HP reaches zero
    public boolean isDead() {
        return hp <= 0;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────
    public int getX()          { return x;          }
    public int getY()          { return y;          }
    public int getHp()         { return hp;         }
    public int getAttack()     { return attack;     }
    public int getYellowKeys() { return yellowKeys; }
    public int getBlueKeys()   { return blueKeys;   }
}
