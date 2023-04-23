package model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player extends RectangleCollider{

	private final static int BASE_DAMAGE = 1;
	public static final float WALK_FRAME_DURATION = 0.05F;
	public static final int SPEED = 5;
	public static final int JUMP_VELOCITY = 2;
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
	
	public Player(float x, float y){
		super(x, y, 32, 14);
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
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

}

