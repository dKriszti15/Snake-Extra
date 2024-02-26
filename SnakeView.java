//Dacz Krisztian,521/2, Projekt : SNAKE

import javax.swing.*;

public class SnakeView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField username;
    private JButton start;

    public SnakeView() {

        GamePanel panel = new GamePanel();

        this.add(panel);
        this.setTitle("mySnake");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}