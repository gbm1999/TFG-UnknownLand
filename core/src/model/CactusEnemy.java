package model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class CactusEnemy extends Enemy {
	
	private static final float ENEMY_SIZE_X = 12f;
	private static final float ENEMY_SIZE_Y = 18f;
	private static final int LIFE = 2;
	private static final float ACTION_RADIUS = 180f;
	private static final float FIRE_RATE = 1f; //seconds
	private Float lastShotTime = null;
	
	private List<Bullet> bulletList;

	public CactusEnemy(float x, float y,float tileWidth, float tileHeight, int points) {
		super(x, y, ENEMY_SIZE_X * 1.5f, ENEMY_SIZE_Y, Enemy.CACTUS, points, CactusEnemy.LIFE);
		bulletList = new ArrayList<Bullet>();
	}
	
	public float getActionRadius() {
		return ACTION_RADIUS;
	}

	public List<Bullet> getBulletList() {
		return bulletList;
	}
	
	public void shoot(float x, float y, float tileWidth, float tileHeight){
		if ((lastShotTime == null)||((this.getStateTime() - lastShotTime) > FIRE_RATE )){
			lastShotTime = this.getStateTime();
			Vector2 origin;
			if (this.x > x){ // we are at the left of the player
				origin = new Vector2(this.x,this.y + (this.height*3/4));
			}else{ // we are at the right of the player
				origin = new Vector2(this.x + this.width,this.y + (this.height*3/4));
			}
			Vector2 target = new Vector2(x,y);
			Vector2 direction = target.sub(origin);
			direction.nor();
			direction.scl(Bullet.bullet_speed/2);
			bulletList.add(new Bullet(origin.x, origin.y, tileWidth, tileHeight, direction.x, direction.y));
		}
	}

}
