import javax.swing.JFrame;

/**
 * Main window for the Magic Tower game.
 * Creates the GamePanel and sets up the JFrame.
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Magic Tower — Simplified");
        GamePanel panel = new GamePanel();
        add(panel);

        // Window size:
        // Grid:   9 cells × 60px = 540px wide
        // HUD:    additional 180px on right side
        // Total:  720px wide, 580px tall (includes border)
        setSize(750, 590);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GameFrame();
    }
}
