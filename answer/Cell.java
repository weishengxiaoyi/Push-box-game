import java.awt.Color;
import java.awt.Graphics;

/**
 * Superclass for all cell types in the Magic Tower game.
 * Represents a single grid cell with a position and type.
 */
public class Cell {

    // Position in grid coordinates
    protected int x;
    protected int y;

    // Cell type identifier: "EMPTY", "WALL", "PLAYER" etc.
    protected String type;

    /**
     * Constructor — initialise position and type.
     * @param x    grid column
     * @param y    grid row
     * @param type string identifier for this cell type
     */
    public Cell(int x, int y, String type) {
        this.x    = x;
        this.y    = y;
        this.type = type;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────
    public int    getX()    { return x;    }
    public int    getY()    { return y;    }
    public String getType() { return type; }

    /**
     * Draw this cell on screen.
     * Subclasses override this to draw their own appearance.
     * @param g        Graphics context
     * @param cellSize pixel size of one grid cell
     */
    public void draw(Graphics g, int cellSize) {
        int px = x * cellSize;
        int py = y * cellSize;

        // Default rendering based on type
        switch (type) {
            case "WALL":
                g.setColor(new Color(80, 80, 80));
                g.fillRect(px, py, cellSize, cellSize);
                break;
            case "EMPTY":
                g.setColor(new Color(200, 180, 140));
                g.fillRect(px, py, cellSize, cellSize);
                break;
            case "PLAYER":
                g.setColor(Color.BLUE);
                g.fillOval(px + 5, py + 5, cellSize - 10, cellSize - 10);
                break;
            default:
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(px, py, cellSize, cellSize);
                break;
        }

        // Draw grid border
        g.setColor(Color.BLACK);
        g.drawRect(px, py, cellSize, cellSize);
    }
}
