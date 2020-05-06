package com.gcstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Menu {
	
	//Carregando as Images
	private BufferedImage logoMenu = getImage("/logoMenu.jpg");
	private int posX = Game.WIDTH*Game.SCALE / 2 - logoMenu.getWidth() / 2;
	private int posY = Game.HEIGHT*Game.SCALE - logoMenu.getHeight();
	
	private BufferedImage logoController = getImage("/logoController.png");
	
	private boolean blink = false;
	private int frame = 0;
	
	public String[] options = {"novo jogo", "carregar jogo", "sair"};
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	public boolean up, down , enter;
	
	public boolean pause = false;
	
	public BufferedImage getImage(String path) {
		BufferedImage imageMenu = null;
		try {
			imageMenu = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return imageMenu;
	}

	public void tick() {
		
		
		if(this.up) {
			this.up = false;
			this.currentOption--;
			Sound.select.play();
			if(this.currentOption < 0) this.currentOption = this.maxOption;
		}
		
		if(this.down) {
			this.down = false;
			this.currentOption++;
			Sound.select.play();
			if(this.currentOption > this.maxOption) this.currentOption = 0;
		}
		
		if(this.enter) {
			this.enter = false;
			if(this.options[this.currentOption] == "novo jogo" /*|| this.options[this.currentOption]  == "continuar"*/) {
				Game.gameState = "NORMAL";
				if(!this.pause) Sound.begin.play();
				else Sound.select.play();
				this.pause = false;
				
			} else if(this.options[this.currentOption] == "carregar jogo"); // CARREGAR JOGO
			
			else System.exit(1);; //SAIR
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 130));
		g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		
		g.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.RED);
		g.setFont(new Font("arial", Font.BOLD, 36));
		g.drawString(">Clone Zelda<", Game.WIDTH*Game.SCALE / 2 - 110, Game.HEIGHT*Game.SCALE / 2 - 170);
		g.drawImage(this.logoMenu, this.posX, this.posY - 20, this.logoMenu.getWidth(), this.logoMenu.getHeight(), null);
		
		//Opções do Menu
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 24));
		
		if(!this.pause) g.drawString("Novo Jogo", Game.WIDTH*Game.SCALE / 2 - 60, Game.HEIGHT*Game.SCALE / 2 - 80);
		else g.drawString("Continuar", Game.WIDTH*Game.SCALE / 2 - 55, Game.HEIGHT*Game.SCALE / 2 - 80);
		
		g.drawString("Carregar Jogo", Game.WIDTH*Game.SCALE / 2 - 80, Game.HEIGHT*Game.SCALE / 2 - 40);
		g.drawString("Sair", Game.WIDTH*Game.SCALE / 2 - 20, Game.HEIGHT*Game.SCALE / 2);
		
		this.frame++;
		if(!this.blink) {
			if(this.frame == 10) {
				this.frame = 0;
				this.blink = true;
			}
			
			if(this.options[this.currentOption] == "novo jogo") {
				//g.drawString(">", Game.WIDTH*Game.SCALE / 2 - 90, Game.HEIGHT*Game.SCALE / 2 - 80);
				g.drawImage(this.logoController, Game.WIDTH*Game.SCALE / 2 - 90, Game.HEIGHT*Game.SCALE / 2 - 100, 20, 20, null);
				
				
			} else if(this.options[this.currentOption] == "carregar jogo") {
				//g.drawString(">", Game.WIDTH*Game.SCALE / 2 - 110, Game.HEIGHT*Game.SCALE / 2 - 40);
				g.drawImage(this.logoController, Game.WIDTH*Game.SCALE / 2 - 110, Game.HEIGHT*Game.SCALE / 2 - 60, 20, 20, null);
				
			} else if(this.options[this.currentOption] == "sair") {
				//g.drawString(">", Game.WIDTH*Game.SCALE / 2 - 50, Game.HEIGHT*Game.SCALE / 2);
				g.drawImage(this.logoController, Game.WIDTH*Game.SCALE / 2 - 50, Game.HEIGHT*Game.SCALE / 2 - 20, 20, 20, null);
			} 
			
		} else if(this.frame == 10) {
			this.frame = 0;
			this.blink = false;
		}
	}
}
