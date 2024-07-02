package snakeGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.Random;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE =25 ;
	static final int GAME_UNITS = (SCREEN_WIDTH* SCREEN_HEIGHT)/UNIT_SIZE;
	int delay = 75;
	
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten;
	int appleX;
	int appleY;
	char direction ='R';
	boolean running = false;
	boolean started = false;
	Timer timer;
	Random random;
	JButton[] difficulty = new JButton[3];
	JButton easy, medium, hard, option;
	JButton start, restart;
	
	Font myFont = new Font("Ink Free", Font.BOLD, 30);
	
	
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		option = new JButton("Select Difficulty: ");
		option.setBounds(0, 0 , 200, 200);
		this.add(option);
		easy = new JButton("Easy");
		medium = new JButton("Medium");
		hard = new JButton("Hard");
		
		
		
		this.add(easy);
		this.add(medium);
		this.add(hard);
		difficulty[0] = easy;
		difficulty[1] = medium;
		difficulty[2] = hard;
		start = new JButton("Start");
		start.setBounds((int)SCREEN_HEIGHT, SCREEN_WIDTH/2, 300, 300);
		this.add(start);
		start.addActionListener(this);
		start.setFont(myFont);
		start.setFocusable(false);
		for (int i = 0; i < 3 ; i++) {
			difficulty[i].addActionListener(this);
			difficulty[i].setFont(myFont);
			difficulty[i].setFocusable(false);
		}
		restart = new JButton("Restart");
		restart.setBounds(SCREEN_HEIGHT/2, SCREEN_WIDTH/2, 100, 100);
		restart.setVisible(false);
		this.add(restart);
		
		restart.addActionListener(this);
		restart.setFont(myFont);
		restart.setFocusable(false);
		//startGame();
	}
	public void startGame() {
		newApple();
		this.running = true;
		timer = new Timer(delay, this);
		timer.start();
		
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (running) {
			this.remove(start);
			this.remove(easy);
			this.remove(option);
			this.remove(medium);
			this.remove(hard);
			draw(g);
		}
		else if (started){
			gameOver(g);
			
		}
		
		
	}
	
	public void draw(Graphics g) {
		
		if(running) {
			for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
					
				}
				if (i != 0) {
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: ".concat(String.valueOf(applesEaten)), (SCREEN_WIDTH - metrics.stringWidth("Score: ".concat(String.valueOf(applesEaten))))/2, 40);
			
		}
		else {
			gameOver(g);

		}
	}
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
	}
	public void move() {
		for (int i = bodyParts; i> 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
			
		}
		switch(direction) {
		case 'U' :
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D' :
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L' :
			x[0] = x[0] - UNIT_SIZE;
			break;
			
		case 'R' :
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
		
	}
	
	public void checkApple() {
		if (x[0] == appleX && y[0] == appleY) {
			bodyParts++;
			applesEaten++;
		}
		
	}
	public void checkCrash() {
		//checks if head collides with body
		for (int i = bodyParts; i > 0; i--) {
			if((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		//check if head crashes left boarder
		if (x[0] < 0) {
				running = false;
		}
		//check if head touches right boarder
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		//check if head crashes top boarder
		if (y[0] < 0) {
						running = false;
				}
				//check if head crashes bottom boarder
				if (y[0] > SCREEN_HEIGHT) {
						running = false;
				}
				if (!running) {
					timer.stop();
				}
		
	}
	public void gameOver(Graphics g) {
		
		//setup game over text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: ".concat(String.valueOf(applesEaten)), (SCREEN_WIDTH - metrics1.stringWidth("Score: ".concat(String.valueOf(applesEaten))))/2, 40);
		
		restart.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == easy) {
			this.delay = 100;
			
			
		}
		if (e.getSource() == medium) {
			this.delay = 75;
		}
		if (e.getSource() == hard) {
			this.delay = 50;
		}
		if (e.getSource() == start) {
			startGame();
			started = true;
			
		}
		if (running) {
			move();
			checkApple();
			checkCrash();
			
		}
		if(e.getSource() == restart) {
			
			restartGame();
		}
		repaint();
		
		
	}
	public void restartGame() {
	    // Reset game variables
	    bodyParts = 6;
	    applesEaten = 0;
	    direction = 'R';
	    running = false;
	    started = false;
	    timer.stop();
	    restart.setVisible(false);

	    // Reset snake position
	    for (int i = 0; i < bodyParts; i++) {
	        x[i] = 0;
	        y[i] = 0;
	    }

	    
	    

	}
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
					break;
				}
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
					break;
				}
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
					break;
				}
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
					break;
				}
			}
		}
	}

}
