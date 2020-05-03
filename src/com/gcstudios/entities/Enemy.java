package com.gcstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.world.Camera;
import com.gcstudios.world.World;

public class Enemy extends Entity {
	
	private double speed = 0.2;
	
	private int maskx = 5, masky = 9, maskw = 10, maskh = 10;
	
	
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	
	public Enemy(int x, int y, int width, int height, BufferedImage[] sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		this.sprites[0] = sprite[0];
		this.sprites[1] = sprite[1];
	}
	
	public void tick() {
		if(!isColiddingWithPlayer()) {
			if(frames >= maxFrames/2) Game.player.isTakeDamage = false;
			
			if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
					&& !isColidding((int)(x+speed), this.getY())) {
				x+=speed;
			} else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isColidding((int)(x-speed), this.getY())) {
				x-=speed;
			}
			if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
					&& !isColidding(this.getX(), (int)(y+speed))) {
				y+=speed;
			} else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
					&& !isColidding(this.getX(), (int)(y-speed))) {
				y-=speed;
			}
		} else Game.player.takeDamage();
		
		
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) index = 0;
		}
	}
	
	public boolean isColiddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX()+3, Game.player.getY()+3, 10, 10);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColidding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if(targetEnemy.intersects(enemyCurrent)) return true;
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		//g.setColor(Color.RED);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, maskw, maskh);
		//g.fillRect(Game.player.getX()+3 - Camera.x, Game.player.getY()+3 - Camera.y, 10, 10);
	}
	
}
