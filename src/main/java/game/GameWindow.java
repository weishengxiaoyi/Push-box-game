package game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Sokoban - Level 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                panel.requestFocusInWindow();
            }
        });
    }
}
