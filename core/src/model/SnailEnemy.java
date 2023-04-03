package model;

import com.badlogic.gdx.math.Vector2;

public class SnailEnemy extends MovingEnemy {
	
	private static final float ENEMY_SIZE = 12f;
	private static final float ENEMY_SPEED = 75f;
	private static final int LIFE = 2;
	
	private float resurrectionTime;
	
	public SnailEnemy(float x, float y, int points) {
		super(x, y,ENEMY_SIZE * 1.5f, ENEMY_SIZE, Enemy.SNAIL, points, SnailEnemy.LIFE);
	}

	@Override
	public Vector2 getVelocity(){
		if (!this.almostDead()){
			return super.getVelocity();
		}else{
			checkResurrection();
			return new Vector2(0f,0f);
		}
	}
	
	private void checkResurrection(){
		if (this.getStateTime() > this.resurrectionTime){
			this.setLife(SnailEnemy.LIFE);
			this.setWidth(ENEMY_SIZE * 1.5f);
			this.setHeight(ENEMY_SIZE);
		}
	}
	
	public boolean almostDead(){
		return (this.getLife() <= 1);
	}
	
	@Override
	public boolean reduceLife(int damage){
		boolean result = super.reduceLife(damage);
		if (this.almostDead()){
			this.resurrectionTime = this.getStateTime() + 3;
			this.setWidth(ENEMY_SIZE);
			this.setHeight(ENEMY_SIZE-1);
		}
		return result;
	}
	
	@Override
	protected float speed() {
		return ENEMY_SPEED;
	}

}
