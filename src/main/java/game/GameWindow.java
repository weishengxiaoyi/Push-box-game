package game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class GameWindow extends JFrame {
    public GameWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setTitle(panel.getWindowTitle());
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                panel.requestFocusInWindow();
            }
        });
    }
}
