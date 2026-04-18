import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents an enemy in the Magic Tower game.
 * Extends Cell — placed on a grid cell and blocks movement.
 * Player must fight to pass through.
 */
public class Enemy extends Cell {

    private int     hp;
    private int     attack;
    private boolean isBoss;

    /**
     * Constructor — normal enemy with default isBoss = false.
     * @param x      grid column
     * @param y      grid row
     * @param hp     enemy hit points
     * @param attack enemy attack power
     */
    public Enemy(int x, int y, int hp, int attack) {
        super(x, y, "ENEMY");
        this.hp     = hp;
        this.attack = attack;
        this.isBoss = false;
    }

    /**
     * Overloaded constructor — supports Boss enemies.
     * @param x      grid column
     * @param y      grid row
     * @param hp     enemy hit points
     * @param attack enemy attack power
     * @param isBoss true if this enemy is a boss
     */
    public Enemy(int x, int y, int hp, int attack, boolean isBoss) {
        super(x, y, "ENEMY");
        this.hp     = hp;
        this.attack = attack;
        this.isBoss = isBoss;
    }

    // Reduce HP when player attacks
    public void takeDamage(int amount) {
        hp -= amount;
    }

    // Returns true when this enemy has been defeated
    public boolean isDead() {
        return hp <= 0;
    }

    public int     getHp()     { return hp;     }
    public int     getAttack() { return attack; }
    public boolean isBoss()    { return isBoss; }

    /**
     * Override draw() — boss appears larger and red,
     * normal enemy appears green.
     */
    @Override
    public void draw(Graphics g, int cellSize) {
        int px = x * cellSize;
        int py = y * cellSize;

        // Boss: red background, normal: green background
        if (isBoss) {
            g.setColor(Color.RED);
        } else {
            g.setColor(new Color(0, 180, 0));
        }
        g.fillRect(px, py, cellSize, cellSize);

        // Draw HP indicator text
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        g.drawString(isBoss ? "BOSS" : "E", px + cellSize / 2 - 8, py + cellSize / 2);
        g.drawString("HP:" + hp, px + 2, py + cellSize - 4);

        // Border
        g.setColor(Color.BLACK);
        g.drawRect(px, py, cellSize, cellSize);
    }
}
