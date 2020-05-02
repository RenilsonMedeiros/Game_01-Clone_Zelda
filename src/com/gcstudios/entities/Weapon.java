package com.gcstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.world.Camera;

public class Weapon extends Entity {
	
	private int frames = 0, maxFrames = 30, index = 0, maxIndex = 1;
	
	private BufferedImage[] sprites;

	public Weapon(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(7*16, 0, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(8*16, 0, 16, 16);
	}
	
	public void tick() {
		frames++; 
		if(frames == maxFrames) { 
			frames = 0; 
			index++; 
			if(index > maxIndex) index = 0;
		}
	}
	
	public void render(Graphics g) {
		g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
	}

}
