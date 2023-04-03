package model;

public class FlyEnemy extends MovingEnemy {

	private static final float ENEMY_SIZE = 12f;
	private static final float ENEMY_SPEED = 75f;
	private static final int LIFE = 1;
	
	private float nextFlyDirectionChange;
	
	public FlyEnemy(float x, float y, int points) {
		super(x, y,ENEMY_SIZE * 1.5f, ENEMY_SIZE, Enemy.FLY, points, FlyEnemy.LIFE);
	}
	
	public void changeFlyDirectionIfNeeded(){
		if (shouldChangeFlyDirection()){
			this.nextFlyDirectionChange = this.getStateTime() + (float) (Math.random()*7);
			this.changeMoveDirection();
		}
	}

	private boolean shouldChangeFlyDirection(){
		return this.getStateTime() >= this.nextFlyDirectionChange;
	}

	@Override
	protected float speed() {
		return ENEMY_SPEED;
	}

}
