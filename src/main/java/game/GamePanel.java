package game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel {
    private static final int TILE_SIZE = 64;
    private final GameController controller;

    public GamePanel() {
        controller = new GameController();
        setBackground(Color.BLACK);
        setFocusable(true);
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        javax.swing.InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        javax.swing.ActionMap actionMap = getActionMap();

        // Arrow keys
        addBinding(inputMap, actionMap, KeyEvent.VK_UP,     0, "up",      () -> controller.move(-1,  0));
        addBinding(inputMap, actionMap, KeyEvent.VK_DOWN,   0, "down",    () -> controller.move( 1,  0));
        addBinding(inputMap, actionMap, KeyEvent.VK_LEFT,   0, "left",    () -> controller.move( 0, -1));
        addBinding(inputMap, actionMap, KeyEvent.VK_RIGHT,  0, "right",   () -> controller.move( 0,  1));

        // WASD keys
        addBinding(inputMap, actionMap, KeyEvent.VK_W,      0, "w",       () -> controller.move(-1,  0));
        addBinding(inputMap, actionMap, KeyEvent.VK_S,      0, "s",       () -> controller.move( 1,  0));
        addBinding(inputMap, actionMap, KeyEvent.VK_A,      0, "a",       () -> controller.move( 0, -1));
        addBinding(inputMap, actionMap, KeyEvent.VK_D,      0, "d",       () -> controller.move( 0,  1));

        // Other controls
        addBinding(inputMap, actionMap, KeyEvent.VK_R,      0, "restart", () -> controller.restartLevel());
        addBinding(inputMap, actionMap, KeyEvent.VK_N,      0, "next",    () -> { controller.nextLevel();     updateWindowTitle(); });
        addBinding(inputMap, actionMap, KeyEvent.VK_P,      0, "prev",    () -> { controller.previousLevel(); updateWindowTitle(); });
        addBinding(inputMap, actionMap, KeyEvent.VK_ESCAPE, 0, "quit",    () -> System.exit(0));
    }

    private void addBinding(javax.swing.InputMap inputMap, javax.swing.ActionMap actionMap,
                            int key, int modifiers, String name, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(key, modifiers), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                repaint();
            }
        });
    }

    private void updateWindowTitle() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            ((Frame) window).setTitle("Sokoban - Level " + (controller.getCurrentLevelIndex() + 1));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Board board = controller.getBoard();
        Theme theme = controller.getCurrentLevel().theme;
        int rows = board.getRows();
        int cols = board.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;
                int cell = board.getCell(r, c);
                drawTile(g, cell, theme, x, y);
            }
        }

        // HUD
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, 220, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Level " + (controller.getCurrentLevelIndex() + 1) + "/" +
                     controller.getTotalLevels() + " - " + controller.getCurrentLevel().name, 6, 20);

        if (controller.isAllLevelsComplete()) {
            drawCenteredMessage(g, "You Won! All levels completed!", new Color(255, 215, 0));
        } else if (controller.isLevelComplete()) {
            drawCenteredMessage(g, "Level Complete! Press N for next level", Color.GREEN);
        }

        // Controls hint
        int panelH = rows * TILE_SIZE;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, panelH - 24, getWidth(), 24);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Arrows: Move  R: Restart  N: Next  P: Prev  ESC: Quit", 6, panelH - 8);
    }

    private void drawTile(Graphics g, int cell, Theme theme, int x, int y) {
        if (cell == Board.EMPTY) {
            g.setColor(Color.BLACK);
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            return;
        }

        // Draw floor base for all non-empty cells
        BufferedImage floor = AssetManager.getImage(theme.groundImage);
        if (floor != null) {
            g.drawImage(floor, x, y, TILE_SIZE, TILE_SIZE, null);
        } else {
            g.setColor(new Color(180, 160, 130));
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        }

        switch (cell) {
            case Board.WALL:
                drawImage(g, theme.wallImage, x, y, new Color(80, 60, 40));
                break;
            case Board.GOAL:
                drawImage(g, theme.endPointImage, x, y, new Color(255, 220, 50));
                break;
            case Board.CRATE:
                drawImage(g, theme.crateImage, x, y, new Color(160, 100, 50));
                break;
            case Board.CRATE_ON_GOAL:
                drawImage(g, theme.crateDarkImage, x, y, new Color(120, 80, 30));
                break;
            case Board.PLAYER:
                drawImage(g, "keeper.png", x, y, Color.BLUE);
                break;
            case Board.PLAYER_ON_GOAL:
                drawImage(g, theme.endPointImage, x, y, new Color(255, 220, 50));
                drawImage(g, "keeper.png", x, y, Color.BLUE);
                break;
            case Board.FLOOR:
                // floor already drawn
                break;
        }
    }

    private void drawImage(Graphics g, String filename, int x, int y, Color fallback) {
        BufferedImage img = AssetManager.getImage(filename);
        if (img != null) {
            g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
        } else {
            g.setColor(fallback);
            g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        }
    }

    private void drawCenteredMessage(Graphics g, String msg, Color color) {
        int panelW = controller.getBoard().getCols() * TILE_SIZE;
        int panelH = controller.getBoard().getRows() * TILE_SIZE;
        g.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        int msgW = fm.stringWidth(msg);
        int msgH = fm.getHeight();
        int mx = (panelW - msgW) / 2;
        int my = panelH / 2;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(mx - 10, my - msgH, msgW + 20, msgH + 12, 12, 12);
        g.setColor(color);
        g.drawString(msg, mx, my);
    }

    @Override
    public Dimension getPreferredSize() {
        Board board = controller.getBoard();
        return new Dimension(board.getCols() * TILE_SIZE, board.getRows() * TILE_SIZE);
    }
}
