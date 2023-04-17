package model;

import com.badlogic.gdx.math.Vector2;

public abstract class MovingEnemy extends Enemy {

	protected boolean movingRight;
	protected Vector2 velocity = new Vector2();

	public MovingEnemy(float x, float y, float sizeX, float sizeY,
			 int enemyType, int points,
			int life) {
		super(x, y, sizeX, sizeY, enemyType, points, life);
		movingRight = true;
	}
	
	public boolean isMovingRight() {
		return movingRight;
	}

	public void changeMoveDirection(){
		movingRight = !movingRight;
	}
	protected abstract float speed();

	public Vector2 getVelocity() {
		return velocity;
	}

}
