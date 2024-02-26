//Dacz Krisztian,521/2, Projekt : SNAKE

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.util.Objects;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 20;
    static final int NUMBER_OF_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private int x[] = new int[NUMBER_OF_UNITS];
    private int y[] = new int[NUMBER_OF_UNITS];
    private int length = 1;
    private int foodEaten;
    private int foodX, foodY, specialfoodX, specialfoodY;
    private boolean specialFood1Eaten = false;
    private boolean gameoverornot = false;
    private char direction = 'D';
    private boolean running = false;
    private Random random;
    private Timer timer;
    private JLabel boomLabel;
    private JButton exit, restart, save, showHigh;
    private JTextField username, high;
    private JPanel gameOverPanel;
    private final String[] usernames = new String[500];
    private final String[] times = new String[500];
    private final int[] scores = new int[500];
    private SnakeTimerController timerController;
    private Clip backgroundMusicClip;

    public GamePanel() {

        random = new Random();

        gameOverPanel = new JPanel();
        timerController = new SnakeTimerController(gameOverPanel);

        //GAMEOVER

        username = new JTextField(" username");

        gameOverPanel.setVisible(false);

        restart = new JButton("Restart");
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        this.add(restart);
        restart.setVisible(false);

        username.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                username.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        this.add(username);
        username.setFont(new Font("Cfont", Font.ITALIC, 13));
        username.setVisible(false);

        save = new JButton("Save score");
        save.setBounds(150, 150, 150, 150);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("D:\\Egyetem\\III\\Java\\mySnake"));
                int result = fileChooser.showSaveDialog(save);

                String user = username.getText();
                username.setText("");

                if (Objects.equals(user, " username")) {
                    user = "anonymus";
                }

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(file, true))) {

                        bWriter.write(user + " " + scoreToString(foodEaten) + " " + timerController.getSeconds() + "s");
                        bWriter.newLine();

                    } catch (IOException i) {
                        System.out.println("Save error");
                    }
                }
            }
        });

        this.add(save);
        save.setVisible(false);

        exit = new JButton("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        this.add(exit);
        exit.setVisible(false);

        showHigh = new JButton("Show highscore");
        high = new JTextField();

        high.setLocation(175, 225);
        high.setFont(new Font("Cfont", Font.BOLD, 13));
        high.setVisible(false);

        showHigh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser(new File("D:\\Egyetem\\III\\Java\\mySnake"));
                int result = fileChooser.showSaveDialog(showHigh);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        String[] parts;
                        int k = 0;
                        while ((line = reader.readLine()) != null) {
                            parts = line.split(" ");
                            usernames[k] = parts[0];
                            scores[k] = Integer.parseInt(parts[1]);
                            times[k] = parts[2];
                            k++;
                        }

                        String maxUsername = usernames[0];
                        String maxsTime = times[0];
                        int maxScore = scores[0];


                        for (int i = 1; i < k; i++) {
                            if (scores[i] > maxScore) {
                                maxScore = scores[i];
                                maxUsername = usernames[i];
                                maxsTime = times[i];
                            }
                        }

                        high.setText(maxUsername + " : " + maxScore + " in " + maxsTime);

                    } catch (IOException i) {
                        System.out.println("Load error -- Highscore");
                    }
                }

                high.setVisible(true);
                showHigh.setVisible(false);

            }
        });

        showHigh.setVisible(false);

        this.add(high);
        this.add(showHigh);
        this.add(gameOverPanel);

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        play();
    }

    public void play() {
        addFood();
        addSpecialFood();
        running = true;
        timer = new Timer(80, this);
        timer.start();

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("D:\\Egyetem\\III\\Java\\mySnake\\backgroundSound.wav"));
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);
            backgroundMusicClip.start();
            backgroundMusicClip.loop(10);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.out.println("IO error");
        }


    }

    private void restartGame() {
        length = 1;
        foodEaten = 0;
        direction = 'D';
        running = false;

        new SnakeView();
        play();

        timerController.resetTimer();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void move() {
        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (direction == 'L') {
            x[0] = x[0] - UNIT_SIZE;
        } else if (direction == 'R') {
            x[0] = x[0] + UNIT_SIZE;
        } else if (direction == 'U') {
            y[0] = y[0] - UNIT_SIZE;
        } else {
            y[0] = y[0] + UNIT_SIZE;
        }
    }

    public void checkFood() {
        int F = 0;
        if (x[0] == foodX && y[0] == foodY) {   // normal food
            length++;
            foodEaten++;
            F = 1;
            addFood();
        } else if (x[0] == specialfoodX && y[0] == specialfoodY && !specialFood1Eaten) { // "bonus" food
            int which = random.nextInt(3);
            int bonus = random.nextInt(5) + 1;
            if (which <= 1) {               // 2/3 chance to +
                length += bonus;
                foodEaten += bonus;
            } else {                        // 1/3 to -
                length -= bonus;
                foodEaten -= bonus;
            }
            F = 1;
            addSpecialFood();
        }

        if (F == 1) {
            try {           // eating soundeffect
                //File URL relative to project folder
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("D:\\Egyetem\\III\\Java\\mySnake\\yumyum.wav"));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                System.out.println("IO error");
            }
        }
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(new Color(255, 0, 0));
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            if (!specialFood1Eaten) {
                g.setColor(new Color(255, 174, 0)); // Red for special food 1
                g.fillOval(specialfoodX, specialfoodY, UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.white);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

            Color color;

            if (foodEaten < 10) {                     //snake color based on progress(actual score)
                color = Color.BLUE;
            } else if (foodEaten <= 25) {
                color = Color.PINK;
            } else {
                color = new Color(110, 22, 100);
            }

            for (int i = 1; i < length; i++) {  // snake
                g.setColor(color);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("CustomFont", Font.PLAIN, 25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2,
                    g.getFont().getSize());

        } else {
            gameOver(g);
        }
    }

    public void addFood() {
        foodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void addSpecialFood() {
        specialfoodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        specialfoodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void checkHit() {
        for (int i = length; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) { // hitting the body of the snake with the head
                running = false;
            }
        }

        if (x[0] < 0 || x[0] > WIDTH || y[0] < 0 || y[0] > HEIGHT) { // hitting border
            running = false;
        }

        if (!running) {
            timer.stop();

            if (!gameoverornot) {
                gameoverornot = true;
                ImageIcon boomIcon = new ImageIcon("D:\\Egyetem\\III\\Java\\mySnake\\boom.gif"); // boom gif
                boomLabel = new JLabel(boomIcon);
                boomLabel.setBounds(0, 0, WIDTH, HEIGHT);
                boomLabel.setVisible(true);
                this.add(boomLabel);

                Timer gifTimer = new Timer(1000, new ActionListener() { // timer for the gif's duration
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        remove(boomLabel);
                        revalidate();
                        repaint();
                    }
                });

                try {   // boom sound effect
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("D:\\Egyetem\\III\\Java\\mySnake\\boom1.wav"));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    System.out.println("IO error");
                }

                gifTimer.setRepeats(false); // only once
                gifTimer.start();
            }
        }
    }

    public void gameOver(Graphics g) {

        //sizes
        username.setBounds(175, 165, 150, 30);
        restart.setBounds(175, 225, 150, 50);
        showHigh.setBounds(175, 275, 150, 50);
        high.setBounds(175, 275, 150, 50);
        save.setBounds(175, 325, 150, 50);
        exit.setBounds(175, 375, 150, 50);

        username.setVisible(true);
        save.setVisible(true);
        showHigh.setVisible(true);
        exit.setVisible(true);
        restart.setVisible(true);

        if (high.isVisible()) {
            showHigh.setVisible(false);
        }

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        g.setColor(Color.red);
        g.setFont(new Font("Cfont", Font.PLAIN, 65));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 4);

        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }

        timerController.stopTimer();

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (running) {
            move();
            checkFood();
            checkHit();
        }
        repaint();
    }

    public String scoreToString(int foodEaten) {
        return ("" + foodEaten);
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
