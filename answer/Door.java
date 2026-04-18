import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents a locked door in the Magic Tower game.
 * Extends Cell — inherits position, type, and draw() base.
 * Player must have the correct key colour to open this door.
 */
public class Door extends Cell {

    // Colour of this door: "YELLOW" or "BLUE"
    private String colour;

    // Whether this door has been opened
    private boolean isOpen;

    /**
     * Constructor — create a closed door of the given colour.
     * @param x      grid column
     * @param y      grid row
     * @param colour "YELLOW" or "BLUE"
     */
    public Door(int x, int y, String colour) {
        super(x, y, "DOOR"); // Call superclass constructor
        this.colour = colour;
        this.isOpen = false;
    }

    // Open this door (called when player uses correct key)
    public void open() {
        this.isOpen = true;
        this.type   = "EMPTY"; // Once open, treat as walkable
    }

    public boolean isOpen()    { return isOpen; }
    public String  getColour() { return colour; }

    /**
     * Override draw() to show coloured door appearance.
     * @param g        Graphics context
     * @param cellSize pixel size of one grid cell
     */
    @Override
    public void draw(Graphics g, int cellSize) {
        if (isOpen) {
            // Draw as empty floor when opened
            g.setColor(new Color(200, 180, 140));
            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        } else {
            // Draw as coloured door when closed
            if (colour.equals("YELLOW")) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.BLUE);
            }
            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

            // Draw door outline
            g.setColor(Color.BLACK);
            g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);

            // Draw lock symbol
            g.setColor(Color.BLACK);
            g.drawOval(x * cellSize + cellSize / 2 - 5, y * cellSize + 5, 10, 10);
            g.drawRect(x * cellSize + cellSize / 2 - 5, y * cellSize + 12, 10, 10);
        }
    }
}
