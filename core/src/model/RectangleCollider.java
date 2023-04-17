package model;

public class RectangleCollider {
	
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	protected float rotation;
	public boolean grounded;
	public float velocityY = 0;

	public RectangleCollider(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rotation = 0;
		grounded = true;
	}
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	
	public boolean collidesWith(RectangleCollider rectangleCollider2){
		if(Math.abs(this.getX() - rectangleCollider2.getX()) < 0.2 && Math.abs(this.getY() - rectangleCollider2.getY()) < 0.2){
			return true;
		}
		return false;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public boolean isPointInsideRectangle(float x2, float y2) {
		if (x2 < this.getX())
			return false;
		if (x2 > (this.getX()+this.getWidth()))
			return false;
		if (y2 < this.getY())
			return false;
		if (y2 > this.getY()+this.getHeight())
			return false;
		
		return true;
	}
	
	public float distanceTo(RectangleCollider other){
		return (float) Math.hypot(this.x-other.x, this.y-other.y);
	}

	
}
