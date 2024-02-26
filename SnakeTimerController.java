//Dacz Krisztian,521/2, Projekt : SNAKE

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnakeTimerController extends JPanel {

    private Timer timer;
    private JLabel timerLabel;
    public JPanel timerPanel;

    int seconds = 0;

    public SnakeTimerController(JPanel gamePanel) {
        timer = new Timer(1000, new TimerActionListener());
        timerLabel = new JLabel("Time: 0");
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setFont(new Font("Cfont", Font.PLAIN, 16));
        timerLabel.setHorizontalAlignment(JLabel.RIGHT);
        timerLabel.setBounds(175, 50, 150, 50);
        timerPanel = new JPanel(new BorderLayout());
        timerPanel.add(timerLabel, BorderLayout.EAST);

        gamePanel.add(timerPanel, BorderLayout.EAST);

        startTimer();
    }

    public void startTimer() {
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }

    public void resetTimer() {
        timer.restart();
    }

    public int getSeconds() {
        return seconds;
    }

    private class TimerActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            seconds++;
            timerLabel.setText("Time: " + seconds + "s");
        }
    }
}
