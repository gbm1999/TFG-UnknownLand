package model;

import com.badlogic.gdx.math.Vector2;

public class Bullet extends RectangleCollider{
	
	public final static float bullet_speed = 200f;
	private final static float maxLifetime = 2.5f;
	private float lifeTime = 0;
	private Vector2 velocity = new Vector2();
	
	public Bullet(float x, float y, float tileWidth, float tileHeight, float velocityX, float velocityY) {
		super(x, y, tileWidth, tileHeight);
		this.velocity.x = velocityX;
		this.velocity.y = velocityY;
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void update (float delta){
		lifeTime += delta;

		// Mueve la bala en función de la velocidad.
		this.x += this.velocity.x * delta;
		this.y += this.velocity.y * delta;
	}
	
	public boolean checkLifeTime(){
		if (lifeTime > maxLifetime)
			return true;
		return false;
	}

	public boolean isMovingLeft() {
		return this.velocity.x < 0;
	}
	
	public boolean isMovingRight() {
		return this.velocity.x > 0;
	}
	
	public boolean isMovingDown() {
		return this.velocity.y < 0;
	}
	
	public boolean isMovingUp() {
		return this.velocity.y > 0;
	}	
}
