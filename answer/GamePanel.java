import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Handles all drawing and user input for the Magic Tower game.
 * Draws the floor grid and HUD.
 * Listens for keyboard events to move the player.
 */
public class GamePanel extends JPanel {

    private GameController controller;
    private static final int CELL_SIZE = GameController.CELL_SIZE;

    public GamePanel() {
        setBackground(Color.BLACK);
        controller = new GameController();
        setFocusable(true);

        // Keyboard listener — arrow keys move player
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ESC: toggle pause/resume
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    controller.togglePause();
                    repaint();
                    return;
                }

                // R: restart game
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    controller.resetGame();
                    repaint();
                    return;
                }

                // Arrow keys: move player
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        controller.movePlayer(0, -1);
                        break;
                    case KeyEvent.VK_DOWN:
                        controller.movePlayer(0, 1);
                        break;
                    case KeyEvent.VK_LEFT:
                        controller.movePlayer(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        controller.movePlayer(1, 0);
                        break;
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw all cells in the current floor
        drawFloor(g);

        // Draw the player on top of the floor
        drawPlayer(g);

        // Draw HUD information
        drawHUD(g);

        // Draw overlay messages
        if (controller.isPaused()) {
            drawMessage(g, "Press ESC to Start", Color.WHITE);
        }

        if (controller.isGameOver()) {
            drawMessage(g, "GAME OVER — Press R to Restart", Color.RED);
        }

        if (controller.isGameComplete()) {
            drawMessage(g, "YOU WIN! Press R to Play Again", Color.YELLOW);
        }
    }

    // Draw all cells in the current floor grid
    private void drawFloor(Graphics g) {
        Floor floor = controller.getCurrentFloor();
        for (int row = 0; row < GameController.GRID_SIZE; row++) {
            for (int col = 0; col < GameController.GRID_SIZE; col++) {
                // Polymorphism: each cell draws itself correctly
                floor.getCell(col, row).draw(g, CELL_SIZE);
            }
        }
    }

    // Draw the player character at current position
    private void drawPlayer(Graphics g) {
        Player player = controller.getPlayer();
        int px = player.getX() * CELL_SIZE;
        int py = player.getY() * CELL_SIZE;

        // Draw player as blue circle
        g.setColor(Color.BLUE);
        g.fillOval(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("P", px + CELL_SIZE / 2 - 4, py + CELL_SIZE / 2 + 4);
    }

    // Draw player stats in the HUD area on the right
    private void drawHUD(Graphics g) {
        int hudX = GameController.GRID_SIZE * CELL_SIZE + 10;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        Player player = controller.getPlayer();

        g.drawString("MAGIC TOWER",    hudX, 30);
        g.drawString("─────────────",  hudX, 45);
        g.drawString("Floor: " + controller.getCurrentFloorNumber(), hudX, 70);
        g.drawString("HP:    " + player.getHp(),      hudX, 100);
        g.drawString("ATK: " + player.getAttack(),    hudX, 125);
        g.drawString("─────────────",  hudX, 145);

        g.setColor(Color.YELLOW);
        g.drawString("Yellow Keys: " + player.getYellowKeys(), hudX, 170);

        g.setColor(Color.CYAN);
        g.drawString("Blue Keys: " + player.getBlueKeys(), hudX, 195);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString("─────────────",    hudX, 215);
        g.drawString("Arrow Keys: Move", hudX, 235);
        g.drawString("ESC: Pause",       hudX, 255);
        g.drawString("R: Restart",       hudX, 275);
    }

    // Draw a centred message overlay
    private void drawMessage(Graphics g, String msg, Color color) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, getHeight() / 2 - 40, getWidth(), 70);
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int x = (GameController.GRID_SIZE * CELL_SIZE - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, getHeight() / 2);
    }
}
