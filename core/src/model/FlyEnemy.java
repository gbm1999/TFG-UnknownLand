package model;

public class FlyEnemy extends MovingEnemy {

	private static final float ENEMY_SIZE = 12f;
	private static final float ENEMY_SPEED = 75f;
	private static final int LIFE = 1;
	
	public FlyEnemy(float x, float y, int points) {
		super(x, y,0.88f, 0.88f, Enemy.FLY, points, FlyEnemy.LIFE);
	}

	@Override
	protected float speed() {
		return ENEMY_SPEED;
	}

}
