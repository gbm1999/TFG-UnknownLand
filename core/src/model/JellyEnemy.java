package model;

public class JellyEnemy extends MovingEnemy {

	private static final float ENEMY_SIZE = 12f;
	private static final float ENEMY_SPEED = 7;
	private static final int LIFE = 1;
	
	public JellyEnemy(float x, float y, int points) {
		super(x, y,0.99f, 1, Enemy.JELLY, points, JellyEnemy.LIFE);
	}

	@Override
	protected float speed() {
		return ENEMY_SPEED;
	}
	
}
