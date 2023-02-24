package model;

public class JellyEnemy extends MovingEnemy {

	private static final float ENEMY_SIZE = 12f;
	private static final float ENEMY_SPEED = 75f;
	private static final int LIFE = 1;
	
	public JellyEnemy(float x, float y, float tileWidth, float tileHeight, int points) {
		super(x, y,ENEMY_SIZE * 1.5f, ENEMY_SIZE, tileWidth, tileHeight, Enemy.JELLY, points, JellyEnemy.LIFE);
	}

	@Override
	protected float speed() {
		return ENEMY_SPEED;
	}
	
}
