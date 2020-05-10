package com.gcstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.gcstudios.entities.*;
import com.gcstudios.world.World;

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
	
	
	public static boolean pause = false;
	
	public static boolean saveExists = false;
	//public static boolean saveGame = false;
	
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
		File file = new File("save.txt");
		if(file.exists()) {
			saveExists = true;
		} else {
			saveExists = false;
		}
		
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
				if(!pause) Sound.begin.play();
				else Sound.select.play();
				pause = false;
				file = new File("save.txt");
				file.delete();
			} else if(this.options[this.currentOption] == "carregar jogo") { // CARREGAR JOGO
				file = new File("save.txt");
				if(file.exists()) {
					String saver = loadGame(45);
					applyGame(saver);
				}
			}
			
			else System.exit(1);; //SAIR
		}
	}
	
	public static void applyGame(String str) {
		String[] spl = str.split("/");
		for(int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch(spl2[0]) {
				case "level":
					Game.CUR_LEVEL = Integer.parseInt(spl2[1]);
					World.restartGame("level"+spl2[1]+".png");
					Game.gameState = "NORMAL";
					pause = false;
					break;
					
				case "life":
					System.out.println("life");
					Game.player.life = Integer.parseInt(spl2[1]);
					break;
					
				case "ammo":
					Game.player.ammo = Integer.parseInt(spl2[1]);
					break;
					
				case "lifepack":
					int gotLifepack = Integer.parseInt(spl2[1]);
					int numRemovedL = 0;
					System.out.println("entrei: " +gotLifepack);
					for(int n = 0; n < Game.entities.size(); n++) {
						if(Game.entities.get(n) instanceof Lifepack) {
							if(numRemovedL >= gotLifepack) break;
							Game.entities.remove(n);
							numRemovedL++;
						}
						System.out.println("removed: "+numRemovedL);
					}
					break;
			}
		}
	}
	
	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if(file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				
				try {
					while((singleLine = reader.readLine()) != null) {
						String[] trans = singleLine.split(":");
						char[] val = trans[1].toCharArray();
						trans[1] = "";
						for(int i = 0; i < val.length; i++) {
							val[i]-=encode;
							trans[1]+=val[i];
						}
						// chave:valor/  -> ex: life:36/
						line+=trans[0]; line+=":"; line+=trans[1]; line+="/";
					}
				} catch (IOException e) {}
			} catch (FileNotFoundException e) {}
		}
		
		return line;
	}
	
	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("save.txt"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current+=":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			for(int n = 0; n < value.length; n++) {
				value[n]+=encode;
				current+=value[n];
			}
			try {
				writer.write(current);
				if(i < val1.length - 1) writer.newLine();
			} catch(IOException e) {}
		}
		
		try {
			writer.flush();
			writer.close();
		} catch(IOException e) {}
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
		
		if(!pause) g.drawString("Novo Jogo", Game.WIDTH*Game.SCALE / 2 - 60, Game.HEIGHT*Game.SCALE / 2 - 80);
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
