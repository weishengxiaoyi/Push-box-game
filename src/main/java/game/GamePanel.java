package game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel {
    private static final int TILE_SIZE  = 64;
    /** Extra pixels at the top reserved for the level-name / difficulty HUD. */
    private static final int HUD_TOP    = 50;
    /** Extra pixels at the bottom reserved for the controls-hint bar. */
    private static final int HUD_BOTTOM = 28;
    /** Minimum panel width so the HUD text is never clipped. */
    private static final int MIN_WIDTH  = 520;

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
        addBinding(inputMap, actionMap, KeyEvent.VK_N,      0, "next",    () -> { controller.nextLevel();     updateWindow(); });
        addBinding(inputMap, actionMap, KeyEvent.VK_P,      0, "prev",    () -> { controller.previousLevel(); updateWindow(); });
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

    /** Builds the window title string for the current level. */
    public String getWindowTitle() {
        Level level = controller.getCurrentLevel();
        String difficulty = ComplexityCalculator.getDifficultyLabel(level.complexityScore);
        return "Sokoban - Level " + (controller.getCurrentLevelIndex() + 1) + " [" + difficulty + "]";
    }

    /** Updates the parent window title and resizes it to fit the new level. */
    private void updateWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            ((Frame) window).setTitle(getWindowTitle());
        }
        if (window instanceof JFrame) {
            ((JFrame) window).pack();
            window.setLocationRelativeTo(null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Board board = controller.getBoard();
        Theme theme = controller.getCurrentLevel().theme;
        int rows = board.getRows();
        int cols = board.getCols();

        // Tile area starts below the top HUD
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * TILE_SIZE;
                int y = HUD_TOP + r * TILE_SIZE;
                int cell = board.getCell(r, c);
                drawTile(g, cell, theme, x, y);
            }
        }

        // ── Top HUD bar (full width) ───────────────────────────────────────
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), HUD_TOP);

        Level level = controller.getCurrentLevel();
        String difficulty = ComplexityCalculator.getDifficultyLabel(level.complexityScore);

        // Line 1: level number / name
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(
            "Level " + (controller.getCurrentLevelIndex() + 1) + "/" +
            controller.getTotalLevels() + "  " + level.name,
            8, 20);

        // Line 2: difficulty label and numeric score
        g.setColor(difficultyColor(difficulty));
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString(
            "Difficulty: " + difficulty + "  (complexity score: " + level.complexityScore + ")",
            8, 40);

        // ── Win messages ──────────────────────────────────────────────────
        if (controller.isAllLevelsComplete()) {
            drawCenteredMessage(g, "You Won! All levels completed!", new Color(255, 215, 0));
        } else if (controller.isLevelComplete()) {
            drawCenteredMessage(g, "Level Complete! Press N for next level", Color.GREEN);
        }

        // ── Bottom controls-hint bar ──────────────────────────────────────
        int boardBottom = HUD_TOP + rows * TILE_SIZE;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, boardBottom, getWidth(), HUD_BOTTOM);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Arrows/WASD: Move  R: Restart  N: Next  P: Prev  ESC: Quit", 6, boardBottom + 18);
    }

    /** Returns a color appropriate for each difficulty label. */
    private Color difficultyColor(String difficulty) {
        switch (difficulty) {
            case "Easy":   return new Color(100, 220, 100);
            case "Medium": return new Color(255, 200,  50);
            case "Hard":   return new Color(255, 130,  40);
            default:       return new Color(220,  60,  60); // Expert
        }
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
        int panelW = Math.max(controller.getBoard().getCols() * TILE_SIZE, MIN_WIDTH);
        int tileAreaCentreY = HUD_TOP + (controller.getBoard().getRows() * TILE_SIZE) / 2;
        g.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        int msgW = fm.stringWidth(msg);
        int msgH = fm.getHeight();
        int mx = (panelW - msgW) / 2;
        int my = tileAreaCentreY;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(mx - 10, my - msgH, msgW + 20, msgH + 12, 12, 12);
        g.setColor(color);
        g.drawString(msg, mx, my);
    }

    @Override
    public Dimension getPreferredSize() {
        Board board = controller.getBoard();
        int w = Math.max(board.getCols() * TILE_SIZE, MIN_WIDTH);
        int h = board.getRows() * TILE_SIZE + HUD_TOP + HUD_BOTTOM;
        return new Dimension(w, h);
    }
}
