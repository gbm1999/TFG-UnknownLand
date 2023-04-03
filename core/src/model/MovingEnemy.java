package model;

import com.badlogic.gdx.math.Vector2;

public abstract class MovingEnemy extends Enemy {

	protected boolean movingLeft;
	protected Vector2 velocity = new Vector2();		

	public MovingEnemy(float x, float y, float sizeX, float sizeY,
			 int enemyType, int points,
			int life) {
		super(x, y, sizeX, sizeY, enemyType, points, life);
		this.velocity.x = -this.speed();
		movingLeft = true;
	}
	
	public boolean isMovingLeft() {
		return movingLeft;
	}

	public void changeMoveDirection(){
		this.velocity.x = -this.velocity.x;
		this.movingLeft = !this.movingLeft;
	}

	public Vector2 getVelocity() {
		return velocity;
	}
	
	protected abstract float speed();
}
