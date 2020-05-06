package com.gcstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.main.Sound;
import com.gcstudios.world.Camera;
import com.gcstudios.world.FloorTile;
import com.gcstudios.world.Tile;
import com.gcstudios.world.WallTile;
import com.gcstudios.world.World;

public class BulletShoot extends Entity {
	
	private double dx;
	private double dy;
	private double spd = 4;
	
	private int life = 30, curLife = 0;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		
		curLife++;
		if(curLife == life || !World.isFree((int)x-6, (int)y-6)) {
			if(!World.isFree((int)x-6, (int)y-6)) {
				Tile tile = Tile.getTileColliding((int)x-6, (int)y-6);
				if(tile != null) {
					tile.life--;
					for(int i = 0; i < World.tiles.length; i++) {
						if(World.tiles[i] == tile && tile instanceof WallTile) {
							if(tile.life == 15) { World.tiles[i].setSprite(tile.getWallDamaged()[0]); Sound.wallWrecked.play(); }
							if(tile.life == 10) { World.tiles[i].setSprite(tile.getWallDamaged()[1]); Sound.wallWrecked.play(); }
							if(tile.life == 5) { World.tiles[i].setSprite(tile.getWallDamaged()[2]); Sound.wallWrecked.play(); }
							if(tile.life == 0) { World.tiles[i] = new FloorTile(tile.getX(), tile.getY(), Tile.TILE_FLOOR_DAMAGE); Sound.wallWrecked.play(); }
						}
					}
				}
				/*WallTile.curLife++;
				if(WallTile.curLife >= WallTile.partLife) {
					WallTile.Life-=WallTile.partLife;
					WallTile.curLife = 0;
					//WallTile.isDamaged == true;
				}*/
			}
			
			Game.bullets.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, 3, 3);
	}
	
}
