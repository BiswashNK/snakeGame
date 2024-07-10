package snakeGame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    private static final int INITIAL_BODY_PARTS = 6;
    private static final int EASY_DELAY = 100;
    private static final int MEDIUM_DELAY = 75;
    private static final int HARD_DELAY = 50;
    private static final Font GAME_FONT = new Font("Ink Free", Font.BOLD, 30);
    private static final Font SCORE_FONT = new Font("Ink Free", Font.BOLD, 40);
    private static final Font GAME_OVER_FONT = new Font("Ink Free", Font.BOLD, 75);

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = INITIAL_BODY_PARTS;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private boolean started = false;
    private Timer timer;
    private final Random random = new Random();

    private final JButton[] difficultyButtons = new JButton[3];
    private final JButton startButton = new JButton("Start");
    private final JButton restartButton = new JButton("Restart");
    private final JPanel buttonPanel = new JPanel();

    public GamePanel() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        buttonPanel.setLayout(new FlowLayout());
        this.add(buttonPanel, BorderLayout.SOUTH);

        setupDifficultyButtons();
        setupStartButton();
        setupRestartButton();
    }

    private void setupDifficultyButtons() {
        String[] difficultyLabels = {"Easy", "Medium", "Hard"};
        int[] delays = {EASY_DELAY, MEDIUM_DELAY, HARD_DELAY};
        Color[] buttonColors = {Color.GREEN, Color.YELLOW, Color.RED};

        for (int i = 0; i < difficultyButtons.length; i++) {
            String label = difficultyLabels[i];
            int delay = delays[i];
            Color color = buttonColors[i];

            JButton button = new JButton(label);
            button.setFont(GAME_FONT);
            button.setFocusable(false);
            button.setBackground(color);
            button.setForeground(Color.BLACK); // Set text color to black for better contrast
            button.addActionListener(e -> {
                setDelay(delay);
                
                button.setBackground(Color.BLUE); // Change the button color on click
            });

            difficultyButtons[i] = button;
            buttonPanel.add(button);
        }
    }

    private void setupStartButton() {
        startButton.setFont(GAME_FONT);
        startButton.setFocusable(false);
        startButton.addActionListener(e -> startGame());
        buttonPanel.add(startButton);
    }

    private void setupRestartButton() {
        restartButton.setFont(GAME_FONT);
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setVisible(false);
        buttonPanel.add(restartButton);
    }

    private void setDelay(int delay) {
        if (timer != null) {
            timer.setDelay(delay);
        } else {
            timer = new Timer(delay, this);
        }
    }

    private void startGame() {
        newApple();
        running = true;
        started = true;
        timer.start();
        startButton.setVisible(false);
        for (JButton button : difficultyButtons) {
            button.setVisible(false);
        }
        buttonPanel.setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            drawGame(g);
        } else if (started) {
            drawGameOver(g);
        }
    }

    private void drawGame(Graphics g) {
        for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
        }
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
        g.setColor(Color.red);
        g.setFont(SCORE_FONT);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, 40);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(GAME_OVER_FONT);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        g.setFont(SCORE_FONT);
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, 40);

        restartButton.setVisible(true);
    }

    private void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkCrash() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // Check if head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // Check if head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            buttonPanel.setVisible(true);
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCrash();
        }
        repaint();
    }

    private void restartGame() {
        // Reset game variables
        bodyParts = INITIAL_BODY_PARTS;
        applesEaten = 0;
        direction = 'R';
        running = false;
        started = false;
        timer.stop();
        restartButton.setVisible(false);

        // Reset snake position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        // Show start button and difficulty buttons
        startButton.setVisible(true);
        for (JButton button : difficultyButtons) {
            button.setVisible(true);
        }
    }

    private class MyKeyAdapter extends KeyAdapter {
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