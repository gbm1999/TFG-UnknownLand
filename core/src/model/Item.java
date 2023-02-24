package model;

public class Item extends RectangleCollider{
	
	// types
	public final static int GOLD_COIN = 1;
	public final static int SILVER_COIN = 2;
	public final static int COPPER_COIN = 3;
	
	private static final float ITEM_SIZE = 10f;
	private int points;
	private boolean active;
	private int itemType;
	
	public Item(float x, float y, float tileWidth, float tileHeight, int itemType, int points) {
		
		super(x + ((tileWidth - ITEM_SIZE) / 2), y + ((tileHeight - ITEM_SIZE) / 2), ITEM_SIZE, ITEM_SIZE, tileWidth, tileHeight);
		
		this.points = points;
		this.active = true;
		this.itemType = itemType;
	}
	
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getItemType() {
		return itemType;
	}

}
