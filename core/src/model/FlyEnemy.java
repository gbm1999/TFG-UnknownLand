package model;

public class FlyEnemy extends MovingEnemy {

	private static final float ENEMY_SIZE = 12f;
	private static final float ENEMY_SPEED = 75f;
	private static final int LIFE = 1;
	
	public FlyEnemy(float x, float y, int points) {
		super(x, y,ENEMY_SIZE * 1.5f, ENEMY_SIZE, Enemy.FLY, points, FlyEnemy.LIFE);
	}

	@Override
	protected float speed() {
		return ENEMY_SPEED;
	}

}
