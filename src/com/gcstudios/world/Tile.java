package com.gcstudios.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;

public class Tile {
	
	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage TILE_FLOOR_DAMAGE = Game.spritesheet.getSprite(3*16, 2*16, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
	
	public static BufferedImage[] WALL_DAMAGED = new BufferedImage[3];
	public int life = 20;
	
	private BufferedImage sprite;
	private int x,y;
	
	public Tile(int x, int y, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}
	
	public static Tile getTileColliding(int xnext, int ynext) {
		int TILE_SIZE = World.TILE_SIZE;
		
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		try {
			if(World.tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) return  World.tiles[x1 + y1*World.WIDTH];
			else if(World.tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) return  World.tiles[x2 + y2*World.WIDTH];
			else if(World.tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) return  World.tiles[x3 + y3*World.WIDTH];
			else if(World.tiles[x4 + (y4*World.WIDTH)] instanceof WallTile) return  World.tiles[x4 + y4*World.WIDTH];
		} catch(ArrayIndexOutOfBoundsException e) {}
		return null;
	}
	
	public BufferedImage[] getWallDamaged() {
		for(int i = 0; i < 3; i++) {
			WALL_DAMAGED[i] = Game.spritesheet.getSprite(16*i, 32, 16, 16);
		}
		return WALL_DAMAGED;
	}
	
	public void setSprite(BufferedImage sprite) {
		this.sprite = sprite;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, x - Camera.x, y - Camera.y, null); //Renderiza muro normal
	}
}
