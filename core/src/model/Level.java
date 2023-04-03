package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class Level{
	
	// The only really needed properties to render a level
	private String mapFile;
	private float damp = 0.90f;
	private float gravity = -600f;
	
	// Render related properties
	private Player player;
	private TiledMap map;
	private ArrayList<Enemy> enemyList;
	private ArrayList<Item> itemList;
	private float tileWidth;
	private float tileHeight;
	private float totalWidth;
	private float totalHeight;
	private float timeElapsed;
	private int score;
	private boolean loaded;
	private World world;
	
	// Game progress related properties (persisted properties)
	private int levelNumber;
	private boolean completed;
	private int highScore;
	private float bestTime;
	
	public Level(){
	}
	
	
	public Level(String mapFile) {
		this.mapFile = mapFile;
		this.loaded = false;
	}
	
	public Level(String mapFile, float damp, float gravity, World world) {
		this.mapFile = mapFile;
		this.loaded = false;
		this.damp = damp;
		this.gravity = gravity;
		this.world = world;
	}	
	
	public void loadLevel(){
		this.map = new TmxMapLoader().load(mapFile);
		tileWidth = world.getWidth();
		tileHeight = world.getHeight();
		totalWidth = tileWidth;
		totalHeight = tileHeight;
		
		//this.player = new Player(2 * getTileWidth(), 3 * getTileHeight());
		this.enemyList = new ArrayList<Enemy>();
		this.itemList = new ArrayList<Item>();
		
		fillEnemyList((TiledMapTileLayer) map.getLayers().get("enemigos"));
		fillItemList((TiledMapTileLayer) map.getLayers().get("objetos"));
		
		timeElapsed = 0;
		score = 0;
		this.loaded = true;
	}
	
	public void unLoadLevel(){
		this.map = null;
		this.player = null;
		this.enemyList = null;
		this.itemList = null;
		this.loaded = false;
	}
	
	public void restartLevel(){
		Gdx.app.log(getClass().getName(), "Level Restarting...");
		//player = new Player(2 * getTileWidth(), 3 * getTileHeight());
		
		enemyList.clear();
		itemList.clear();
		
		fillEnemyList((TiledMapTileLayer) map.getLayers().get("enemigos"));
		fillItemList((TiledMapTileLayer) map.getLayers().get("objetos"));
		
		timeElapsed = 0;
		score = 0;
	}
	
	private void fillItemList(TiledMapTileLayer mapLayerItems) {
		for(int y=0;y<mapLayerItems.getHeight();y++){
			for(int x =0;x<mapLayerItems.getWidth();x++){
				Cell cell = mapLayerItems.getCell(x, y);
				if ((cell != null) && (cell.getTile() != null)){
					int points = Integer.parseInt((String) cell.getTile().getProperties().get("points"));
					int type = Integer.parseInt((String) cell.getTile().getProperties().get("type"));
					Item newItem = new Item(x*tileWidth, y*tileHeight, tileWidth, tileHeight, type, points);
					itemList.add(newItem);
				}
			}
		}
	}

	private void fillEnemyList(TiledMapTileLayer mapLayerEnemies) {
		for(int y=0;y<mapLayerEnemies.getHeight();y++){
			for(int x =0;x<mapLayerEnemies.getWidth();x++){
				Cell cell = mapLayerEnemies.getCell(x, y);
				if ((cell != null) && (cell.getTile() != null)){
					int type = Integer.parseInt((String) cell.getTile().getProperties().get("type"));
					int points = Integer.parseInt((String) cell.getTile().getProperties().get("points"));
					Enemy newEnemy;
					switch (type){
						case Enemy.CACTUS:
							newEnemy = new CactusEnemy(x*tileWidth, y*tileHeight, tileWidth, tileHeight, points);
							break;
						case Enemy.FIRST_BOSS:
							newEnemy = new FirstBoss(x*tileWidth, y*tileHeight , points);
							break;
						case Enemy.FLY:
							newEnemy = new FlyEnemy(x*tileWidth, y*tileHeight , points);
							break;
						case Enemy.SNAIL:
							newEnemy = new SnailEnemy(x*tileWidth, y*tileHeight, points);
							break;
						case Enemy.JELLY:
						default:
							newEnemy = new JellyEnemy(x*tileWidth, y*tileHeight, points);
							break;
					}
					enemyList.add(newEnemy);
				}
			}
		}
		
	}


	public float getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(float tileWidth) {
		this.tileWidth = tileWidth;
	}

	public float getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(float tileHeight) {
		this.tileHeight = tileHeight;
	}

	public int getScore() {
		return score;
	}
	
	public void increaseScore(int amount){
		score += amount;
	}

	public void update(float delta) {
		timeElapsed += delta;
	}

	public void setLevelNumber(int i) {
		this.levelNumber = i;
	}

	public int getLevelNumber() {
		return levelNumber;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public float getBestTime() {
		return bestTime;
	}

	public void setBestTime(float bestTime) {
		this.bestTime = bestTime;
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	public float getDamp() {
		return damp;
	}

	public void setDamp(float damp) {
		this.damp = damp;
	}
	
	public float getGravity() {
		return gravity;
	}

	public void setGravity(float gravity){
		this.gravity = gravity;
	}


}

