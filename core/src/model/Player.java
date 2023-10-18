package model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player extends RectangleCollider{

	private final static int BASE_DAMAGE = 1;
	public static final float WALK_FRAME_DURATION = 0.05f;
	public static final int SPEED = 6;
	public static final int JUMP_VELOCITY = 6;
	public static int HEALTH = 20;
	private Inventory inventory;

	public enum State {
		IDLE, WALKING, JUMPING, DYING, FALLING, WINNING
	}		

	private Vector2 position;
	private Vector2 velocity = new Vector2();
	private Vector2 acceleration = new Vector2();
	private State state = State.IDLE;
    private float stateTime = 0;
	private boolean facingLeft = false;
	private ArrayList<Bullet> bulletList;
	private static final float FIRE_RATE = 4f; //seconds
	private Float lastShotTime = null;
	
	public Player(float x, float y){
		super(x, y, 0.99f, 1);
		position = new Vector2();
		bulletList = new ArrayList<>();
		inventory = new Inventory();

		ItemStack pistol = null;
		try {
			pistol = new ItemStack(Material.PISTOL,1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inventory.setItemInHand(pistol);
		try {
			inventory.addItem(pistol);
			inventory.addItem(new ItemStack(Material.PICKAXE, 1));
			inventory.addItem(new ItemStack(Material.EGG, 5));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		stateTime = 0;
		this.state = state;
	}

	public void update(float delta) {
		stateTime += delta;
	}

	public boolean isFacingLeft(){
		return facingLeft;	
	}
	
	public void setFacingLeft(boolean b) {
		this.facingLeft = b;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public ArrayList<Bullet> getBulletList() {
		return bulletList;
	}

	public void shoot(float x, float y, float tileWidth, float tileHeight){
			Vector2 origin;

			origin = new Vector2(this.x,this.y);
			Vector2 target = new Vector2(x,y);
			Vector2 direction = target.sub(origin);
			direction.nor();
			direction.scl(Player.SPEED);
			bulletList.add(
					new Bullet(origin.x,
							origin.y + 0.2f,
							tileWidth, tileHeight,direction.x,direction.y));
			bulletList.add(
					new Bullet(origin.x + 0.3f,
							origin.y + 0.3f,
							tileWidth, tileHeight,direction.x,direction.y));

	}

	public boolean canJump() {
		return (!getState().equals(State.JUMPING) && !getState().equals(State.FALLING) && isAlive());
	}
	
	public boolean isAlive() {
		return !getState().equals(State.DYING) && !getState().equals(State.WINNING);
	}
	
	public int getDamage(){
		return Player.BASE_DAMAGE;
	}

	public static int getHEALTH() {
		return HEALTH;
	}

	public static void setHEALTH(int HEALTH) {
		Player.HEALTH = HEALTH;
		if(Player.HEALTH > 20) {
			Player.HEALTH = 20;
		}
		if(Player.HEALTH < 0) {
			Player.HEALTH = 0;
		}
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

}

