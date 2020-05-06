package com.gcstudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.gcstudios.entities.BulletShoot;
import com.gcstudios.entities.Enemy;
import com.gcstudios.entities.Entity;
import com.gcstudios.entities.Player;
import com.gcstudios.graficos.*;
import com.gcstudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	public static JFrame frame;
	private Thread thread;
	private Boolean isRunning = true;
	private int i = 0;
	protected int CUR_LEVEL = 1;
	protected int MAX_LEVEL = 3;
			
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	//public static Sound sound;
	
	public static Random rand;
	
	public static UI ui;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	public Menu menu;

	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		
		//Inicializando objetos.
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));		
		world = new World("/level1.png");
		menu = new Menu();
		entities.add(player);
	}

	private void initFrame() {
		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);		
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	                                         
	public synchronized void stop() {        
		isRunning = false;                   
		try {                                
			thread.join();                   
		} catch (InterruptedException e) {   
			e.printStackTrace();             
		}                                    
	}                                        
	                                         
	public static void main(String args[]) { 
	     Game game = new Game();
	     game.start();
	}
	
	public void tick() {
		if(gameState != "GAME_OVER" && !menu.pause) { Sound.gameOverMusic.stop(); Sound.background.loop(); }
		else Sound.background.stop(); 
		
		player.updateCamera();
		
		if(gameState == "NORMAL") {
			this.restartGame = false;
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < bullets.size(); i++) {
				BulletShoot b = bullets.get(i);
				b.tick();
			}
			
			if(enemies.size() == 0) {
				//Avançar para o próximo level!
				this.CUR_LEVEL++;
				System.out.println(this.CUR_LEVEL);
				if(this.CUR_LEVEL > this.MAX_LEVEL) this.CUR_LEVEL = 1;
				String newWorld = "level" + this.CUR_LEVEL + ".png";
				System.out.println(newWorld);
				World.restartGame(newWorld);
			}
		} else if(gameState == "GAME_OVER") {
			Sound.gameOverMusic.loop();
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver) this.showMessageGameOver = false;
				else this.showMessageGameOver = true;
			}
			
			if(restartGame) {
				this.restartGame = false;
				gameState = "NORMAL";
				Sound.begin.play();
				
				this.CUR_LEVEL = 1;
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		} else if(gameState == "MENU") {
			//Menu do meu jogo
			menu.tick();
		}
		
		
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/* Renderização do jogo */
		//Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for(int i = 0; i < bullets.size(); i++) {
			BulletShoot b = bullets.get(i);
			b.render(g);
		}
		
		ui.render(g);
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.setColor(Color.WHITE);
		g.drawString("Munição: " + player.ammo, 600, 25);
		
		if(gameState == "GAME_OVER") {
			g.setColor(new Color(0,0,0,100));
			g.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD, 36));
			g.setColor(Color.WHITE);
			g.drawString("Game Over", WIDTH*SCALE/2-100, HEIGHT*SCALE/2);
			if(showMessageGameOver)
				g.drawString(">Pressione Enter para reniciar<", WIDTH*SCALE/2-260, HEIGHT*SCALE/2+60);
		} else if(gameState == "MENU") {
			menu.render(g);
		}
		
		bs.show();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer+= 1000;
			}
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if
		(
			e.getKeyCode() == KeyEvent.VK_RIGHT ||
			e.getKeyCode() == KeyEvent.VK_D
		) {
			player.right = true;
		} else if
			(
				e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A
			) {
				player.left = true;
			}
		
		if
		(
			e.getKeyCode() == KeyEvent.VK_UP ||
			e.getKeyCode() == KeyEvent.VK_W
		) {
			player.up = true;
			
			if(gameState == "MENU") menu.up = true;
		} else if
			(
				e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S
			) {
				player.down = true;
				
				if(gameState == "MENU") menu.down = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if
		(
			e.getKeyCode() == KeyEvent.VK_RIGHT ||
			e.getKeyCode() == KeyEvent.VK_D
		) {
			player.right = false;
		} else if
			(
				e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A
			) {
				player.left = false;
			}
		
		if
		(
			e.getKeyCode() == KeyEvent.VK_UP ||
			e.getKeyCode() == KeyEvent.VK_W
		) {
			player.up = false;
		} else if
			(
				e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S
			) {
				player.down = false;
			}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) player.shoot = true;
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(gameState == "MENU") menu.enter = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = e.getX() / SCALE;
		player.my = e.getY() / SCALE;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
