package model;


public abstract class Enemy extends RectangleCollider {
	
	public static final int JELLY = 21;
	public static final int CACTUS = 22;
	public static final int FLY = 23;
	public static final int SNAIL = 24;
	public static final int FIRST_BOSS = 25;
	
	private int points;
	private int enemyType;
	private int life;
	private float timeOfDeath = 0;
	private float stateTime = 0;

	public Enemy(float x, float y, float sizeX, float sizeY, int enemyType, int points, int life) {
		
		super(x,y,sizeX,sizeY);

		this.enemyType = enemyType;
		this.points = points;
		this.life = life;
	}

	public boolean isAlive() {
		return this.life > 0;
	}

	public int getEnemyType() {
		return enemyType;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void update(float delta){
		stateTime += delta;
	}

	public int getPoints() {
		return points;
	}
	
	public float timeSinceDeath(){
		if (!isAlive()){
			return stateTime - timeOfDeath;
		}else{
			return 0f;
		}
	}
	
	public boolean reduceLife(int damage){
		this.life -= damage;
		if (!isAlive()){
			timeOfDeath = stateTime;
		}
		return !isAlive();
	}
	
	protected int getLife(){
		return life;
	}
	
	protected void setLife(int life){
		this.life = life;
	}	
}