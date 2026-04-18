import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents a collectable item in the Magic Tower game.
 * Extends Cell — placed on a grid cell, collected by walking over.
 * Item types: "KEY_YELLOW", "KEY_BLUE", "STAIRS_UP", "STAIRS_DOWN"
 */
public class Item extends Cell {

    // Type of item this cell contains
    private String itemType;

    /**
     * Constructor — create an item of the specified type.
     * @param x        grid column
     * @param y        grid row
     * @param itemType "KEY_YELLOW", "KEY_BLUE", "STAIRS_UP", or "STAIRS_DOWN"
     */
    public Item(int x, int y, String itemType) {
        super(x, y, "ITEM");
        this.itemType = itemType;
    }

    public String getItemType() { return itemType; }

    /**
     * Override draw() — each item type has distinct colour.
     */
    @Override
    public void draw(Graphics g, int cellSize) {
        int px = x * cellSize;
        int py = y * cellSize;

        // Floor background
        g.setColor(new Color(200, 180, 140));
        g.fillRect(px, py, cellSize, cellSize);

        // Item symbol
        switch (itemType) {
            case "KEY_YELLOW":
                g.setColor(Color.YELLOW);
                g.fillOval(px + 8, py + 8, cellSize - 16, cellSize - 16);
                g.setColor(Color.BLACK);
                g.drawString("YK", px + 5, py + cellSize / 2);
                break;
            case "KEY_BLUE":
                g.setColor(Color.CYAN);
                g.fillOval(px + 8, py + 8, cellSize - 16, cellSize - 16);
                g.setColor(Color.BLACK);
                g.drawString("BK", px + 5, py + cellSize / 2);
                break;
            case "STAIRS_UP":
                g.setColor(Color.ORANGE);
                g.fillRect(px + 5, py + 5, cellSize - 10, cellSize - 10);
                g.setColor(Color.BLACK);
                g.drawString("UP", px + 5, py + cellSize / 2);
                break;
            case "STAIRS_DOWN":
                g.setColor(new Color(150, 75, 0));
                g.fillRect(px + 5, py + 5, cellSize - 10, cellSize - 10);
                g.setColor(Color.WHITE);
                g.drawString("DN", px + 5, py + cellSize / 2);
                break;
            default:
                break;
        }

        // Border
        g.setColor(Color.BLACK);
        g.drawRect(px, py, cellSize, cellSize);
    }
}
