package com.gcstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.gcstudios.main.Game;
import com.gcstudios.main.Sound;
import com.gcstudios.world.Camera;
import com.gcstudios.world.World;

public class Player extends Entity {
	
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;             //Default.
	public double speed = 1;
	
	private int frames = 0, maxFrames = 4, index = 0, maxIndex = 4;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private boolean hasGun;
	
	public int ammo = 0;
	
	public boolean shoot = false;
	public boolean mouseShoot = false;
	public int my, mx;
	
	public double life = 100, maxLife = 100;
	public boolean isTakeDamage = false;
	private BufferedImage spriteDamage;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}
	}
	
	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x+speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed; 
		} else if(left && World.isFree((int)(x-speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		if(up && World.isFree(this.getX(), (int)(y-speed))) {
			moved = true;
			y-=speed;
		}
		else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index == maxIndex) index = 0;
			}
		}
		
		this.checkCollisionLifepack();
		this.checkCollisionAmmo();
		this.checkCollisionGun();
		
		if(shoot) {
			shoot = false;
			if(hasGun && ammo > 0) {
				ammo--;
				Sound.shoot.play();
				// Criar Bala e atirar!
				int dx = 0; int px = 0; int py = 6;
				if(dir == right_dir) {
					dx = 1; px = 18;
				}
				else {
					dx = -1; px = -8;
				}
			
				BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py, 3, 3, null, dx, 0);
				Game.bullets.add(bullet);
			}
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			if(hasGun && ammo > 0) {
				ammo--;
				Sound.shoot.play();
				// Criar Bala e atirar!
				int px = 0, py = 8; double angle = 0;
				if(dir == right_dir) {
					px = 18;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				else {
					px = -8;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		if(life <= 0) {
			//GAME OVER
			Sound.gameOver.play();
			life = 0;
			Game.gameState = "GAME_OVER";
		}

		this.updateCamera();

	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkCollisionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColliding(this, atual)) {
					hasGun = true;
					Sound.pickup.play();
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColliding(this, atual)) {
					ammo+=30;
					Sound.pickup.play();
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkCollisionLifepack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if(Entity.isColliding(this, atual)) {
					life+=10;
					Sound.pickup.play();
					if(life >= maxLife) life = maxLife;
					Game.entities.remove(i);
				}
			}
		}
	}
	
	 public void takeDamage() {
	 	if(Game.rand.nextInt(100) < 10) {
	 		life-=Game.rand.nextInt(3);
	 		Sound.hurtPlayer.play();
	 		isTakeDamage = true;
	 		spriteDamage = Entity.PLAYER_DAMAGE;
	 		if(life <= 0) {
	 			//Game Over
	 		}
	 	} 
	 }
	
	public void render(Graphics g) {
		if(!isTakeDamage) {	
			if(moved && dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) g.drawImage(Entity.GUN_RIGHT, this.getX()+8 - Camera.x, this.getY() - Camera.y, null); 
				
			} else if (moved && dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) g.drawImage(Entity.GUN_LEFT, this.getX()-8 - Camera.x, this.getY() - Camera.y, null); 
				
			} else if(dir == right_dir) {
				g.drawImage(rightPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) g.drawImage(Entity.GUN_RIGHT, this.getX()+8 - Camera.x, this.getY() - Camera.y, null); 
				
			} else if(dir == left_dir) {
				g.drawImage(leftPlayer[3], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) g.drawImage(Entity.GUN_LEFT, this.getX()-8 - Camera.x, this.getY() - Camera.y, null); 
			}
			
		} else g.drawImage(spriteDamage,this.getX() - Camera.x, this.getY() - Camera.y, null);
	}

}
