package model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class FirstBoss extends MovingEnemy {

	private static final float ENEMY_SPEED = 25f;
	private static final float JUMP_SPEED = 200f;
	private Vector2 velocity = new Vector2();
	private Vector2 acceleration = new Vector2();
	private static final float ENEMY_SIZE_X = 30f;
	private static final float ENEMY_SIZE_Y = 36f;
	private static final int LIFE = 6;
	
	
	private static final float ACTION_RADIUS = 225f;
	private static final float FIRE_RATE = 1.5f; //seconds
	private Float lastShotTime = null;
	private boolean facingLeft = true;
	
	private float nextJumpTime = 0;
	private float stopJumpTime = 0;
	private boolean jumping = false;
	
	private List<Bullet> bulletList;

	public FirstBoss(float x, float y, int points) {
		super(x, y, ENEMY_SIZE_X, ENEMY_SIZE_Y, Enemy.FIRST_BOSS, points, FirstBoss.LIFE);
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
			if (this.x > x){ // we are at the right of the player
				facingLeft = true;
				origin = new Vector2(this.x,this.y + (this.height*1/2));
			}else{ // we are at the left of the player
				facingLeft = false;
				origin = new Vector2(this.x + this.width,this.y + (this.height*1/2));
			}
			Vector2 target1 = new Vector2(x,y);
			Vector2 direction1 = target1.sub(origin);
			Vector2 target2 = new Vector2(x,y+tileHeight*2);
			Vector2 direction2 = target2.sub(origin);
			Vector2 target3 = new Vector2(x,y-tileHeight*2);
			Vector2 direction3 = target3.sub(origin);
			direction1.nor();
			direction2.nor();
			direction3.nor();
			direction1.scl(Bullet.bullet_speed/2);
			direction2.scl(Bullet.bullet_speed/2);
			direction3.scl(Bullet.bullet_speed/2);
			bulletList.add(new Bullet(origin.x, origin.y, tileWidth, tileHeight, direction1.x, direction1.y));
			bulletList.add(new Bullet(origin.x, origin.y, tileWidth, tileHeight, direction2.x, direction2.y));
			bulletList.add(new Bullet(origin.x, origin.y, tileWidth, tileHeight, direction3.x, direction3.y));
		}
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	@Override
	protected float speed()  {
		return ENEMY_SPEED;
	}

	public Vector2 getVelocity() {
		if (jumping && stillJumping())
			velocity.y = JUMP_SPEED;
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public void jumpRandom() {
		jumping = false;
		if (shouldJump()){
			this.nextJumpTime = this.getStateTime() + 1 + (float) (Math.random()*4);
			this.jump();
		}
	}

	private boolean shouldJump(){
		return this.getStateTime() >= this.nextJumpTime;
	}
	
	private boolean stillJumping(){
		return this.getStateTime() <= this.stopJumpTime;
	}
	
	private void jump(){
		jumping = true;
		this.stopJumpTime = this.getStateTime() + 0.2f;
	}
	
	
}
