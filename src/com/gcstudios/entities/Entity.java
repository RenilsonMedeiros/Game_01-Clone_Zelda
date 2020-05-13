package com.gcstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.gcstudios.main.Game;
import com.gcstudios.world.Camera;
import com.gcstudios.world.Node;
import com.gcstudios.world.Vector2i;

public class Entity {
	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6*16, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7*16, 0, 16, 16);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(6*16, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7*16, 16, 16, 16);
	public static BufferedImage ENEMY_DAMAGE = Game.spritesheet.getSprite(16, 16, 16, 16);
	public static BufferedImage PLAYER_DAMAGE = Game.spritesheet.getSprite(0, 16, 16, 16);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(9*16, 0, 16, 16);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(9*16, 16, 16, 16);

	protected double x;
	protected double y;
	protected int z;
	protected int width;
	protected int height;
	
	protected List<Node> path;
	
	private BufferedImage sprite;
	
	protected int maskx, masky, mwidth, mheight;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}
	
	public void setMask(int maskx, int masky, int mwidth, int mheight) {
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}

	public int getX() {
		return (int)this.x;
	}         
	
	public void setX(double x) {
		this.x = x;
	}
              
	public int getY() {
		return (int)this.y;
	}         
	
	public int getZ() {
		return (int)this.z;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public int getWidth() {
		return this.width;
	}         
              
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {
		
	}
	
	public void followPath(List<Node> path) {
		if(path != null) {
			if(path.size() > 0) {
				Vector2i target = path.get(path.size() - 1).tile;
				
				if(x < target.x * 16) x++;
				else if(x > target.x * 16) x--;
				
				if(y < target.y * 16) y++;
				else if(y> target.y * 16) y--;
				
				if(x == target.x * 16 && y == target.y * 16) path.remove(path.size() - 1);
			}
		}
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.mwidth, e2.mheight);
		
		if(e1Mask.intersects(e2Mask) && e1.z == e2.z) return true;
		else return false;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		//g.setColor(Color.RED);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, this.mwidth , this.mheight);
	}

}
