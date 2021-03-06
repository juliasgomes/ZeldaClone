package com.juliasgomes.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.juliasgomes.main.Game;
import com.juliasgomes.world.Camera;
import com.juliasgomes.world.World;

public class Enemy extends Entity{

	private double speed = Game.rand.nextDouble (0.4,1);
	
	private int maskX = 1, maskY = 2, maskW = 14, maskH = 14;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	
	public int right_dir = 0,left_dir = 1,up_dir = 2,down_dir = 3;
	public int dir = right_dir;
	
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	private BufferedImage[] downEnemy;
	private BufferedImage[] upEnemy;
	
	private BufferedImage rightEnemy_FEEDBACK;
	private BufferedImage leftEnemy_FEEDBACK;
	private BufferedImage up_downEnemy_FEEDBACK;
	
	private int life = 10;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		
		rightEnemy = new BufferedImage[4];
		leftEnemy = new BufferedImage[4];
		upEnemy = new BufferedImage[4];
		downEnemy = new BufferedImage[4];
		
		rightEnemy_FEEDBACK = Game.spritesheet.getSprite(96, 64, 16, 16);
		leftEnemy_FEEDBACK = Game.spritesheet.getSprite(112, 64, 16, 16);
		up_downEnemy_FEEDBACK = Game.spritesheet.getSprite(128, 64, 16, 16);
		
		
		for(int i = 0; i < 4; i++) {
			rightEnemy[i] = Game.spritesheet.getSprite(32 + (i * 16), 64, 16, 16);
		}
		
		for(int i = 0; i < 4; i++) {
			leftEnemy[i] = Game.spritesheet.getSprite(32 + (i * 16), 80, 16, 16);
		}
		
		for(int i = 0; i < 4; i ++) {
			upEnemy[i] = Game.spritesheet.getSprite(32 + (i * 16), 96, 16, 16);
		}
		
		for(int i = 0; i < 4; i ++) {
			downEnemy[i] = Game.spritesheet.getSprite(32 + (i * 16),112,16,16);
		}
	}
	
	public void tick() {
		if(isCollidingWithPlayer() == false) {
			if ((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
					&& !isColliding((int)(x+speed),this.getY())) {
				dir = left_dir;
				x+=speed;
				
			}else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isColliding((int)(x-speed),this.getY())) {
				dir = right_dir;
				x-=speed;
			}
			
			if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y + speed))
					&& !isColliding(this.getX(), (int)(y+speed))) {
				dir = down_dir;
				y+=speed;
				
			}else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y - speed))
					&& !isColliding(this.getX(),(int)(y-speed))) {
				dir = up_dir;
				y-=speed;
			}
		}else {
			//Colliding
			if(Game.rand.nextInt(100) < 10) {
				Game.player.life-=Game.rand.nextInt(5, 8);
				Game.player.isDamaged = true;
			}
		}
		
	
	
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex)
				index = 0;
		}
		
		isCollidingWithBullet();
		
		if(life <= 0) {
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			this.damageCurrent ++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
		
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void isCollidingWithBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColliding(this, e)) {
					isDamaged = true;
					life --;
					Game.bullets.remove(i);
					return;
				}
			}
		}

	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskW, maskH);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16,16);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskX,ynext + maskY,maskW, maskH);
		
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this)
				continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskX, e.getY() + maskY,maskW,maskH);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		
		
		return false;
	}

	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == right_dir) {
				g.drawImage(rightEnemy[index], this.getX()- Camera.x, this.getY()- Camera.y, null);
			}else if(dir == left_dir) {
				g.drawImage(leftEnemy[index], this.getX()- Camera.x, this.getY() - Camera.y, null);
			}
			if(dir == up_dir) {
				g.drawImage(upEnemy[index],this.getX() - Camera.x, this.getY() - Camera.y, null);
			}else if(dir == down_dir) {
				g.drawImage(downEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}else {
			if(dir == right_dir) {
				g.drawImage(rightEnemy_FEEDBACK, this.getX()- Camera.x, this.getY()- Camera.y, null);
			}else if(dir == left_dir) {
				g.drawImage(leftEnemy_FEEDBACK, this.getX()- Camera.x, this.getY() - Camera.y, null);
			}
			if(dir == up_dir) {
				g.drawImage(up_downEnemy_FEEDBACK,this.getX() - Camera.x, this.getY() - Camera.y, null);
			}else if(dir == down_dir) {
				g.drawImage(up_downEnemy_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskW, maskH);
	}
	
}
