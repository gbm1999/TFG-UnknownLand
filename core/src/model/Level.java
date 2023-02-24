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

public class Level implements Serializable{
	
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
	
	public Level(String mapFile, float damp, float gravity) {
		this.mapFile = mapFile;
		this.loaded = false;
		this.damp = damp;
		this.gravity = gravity;
	}	
	
	public void loadLevel(){
		this.map = new TmxMapLoader().load(mapFile);
		tileWidth = ((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth();
		tileHeight = ((TiledMapTileLayer) map.getLayers().get(0)).getTileHeight();
		totalWidth = ((TiledMapTileLayer) map.getLayers().get(0)).getWidth();
		totalHeight = ((TiledMapTileLayer) map.getLayers().get(0)).getHeight();
		
		this.player = new Player(2 * getTileWidth(), 3 * getTileHeight(), getTileWidth(), getTileHeight());
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
		player = new Player(2 * getTileWidth(), 3 * getTileHeight(), getTileWidth(), getTileHeight());
		
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
							newEnemy = new FirstBoss(x*tileWidth, y*tileHeight, tileWidth, tileHeight, points);
							break;
						case Enemy.FLY:
							newEnemy = new FlyEnemy(x*tileWidth, y*tileHeight, tileWidth, tileHeight, points);
							break;
						case Enemy.SNAIL:
							newEnemy = new SnailEnemy(x*tileWidth, y*tileHeight, tileWidth, tileHeight, points);
							break;
						case Enemy.JELLY:
						default:
							newEnemy = new JellyEnemy(x*tileWidth, y*tileHeight, tileWidth, tileHeight, points);
							break;
					}
					enemyList.add(newEnemy);
				}
			}
		}
		
	}

	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public void setMap(TiledMap map) {
		this.map = map;
	}

	public void dispose() {
		map.dispose();
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

	public float getTotalWidth() {
		return totalWidth;
	}

	public void setTotalWidth(float totalWidth) {
		this.totalWidth = totalWidth;
	}

	public float getTotalHeight() {
		return totalHeight;
	}

	public void setTotalHeight(float totalHeight) {
		this.totalHeight = totalHeight;
	}

	public ArrayList<Enemy> getEnemyList() {
		return enemyList;
	}

	public ArrayList<Item> getItemList() {
		return itemList;
	}

	public float getTimeElapsed() {
		return timeElapsed;
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

	@Override
	public void write(Json json) {
		json.writeValue("levelNumber", levelNumber);
		json.writeValue("completed", completed);
		json.writeValue("highScore", highScore);
		json.writeValue("bestTime", bestTime);
		json.writeValue("mapFile", mapFile);
		json.writeValue("damp", damp);
		json.writeValue("gravity", gravity);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		levelNumber = json.readValue("levelNumber", Integer.class, jsonData);
		completed = json.readValue("completed", Boolean.class, jsonData);
		highScore = json.readValue("highScore", Integer.class, jsonData);
		bestTime = json.readValue("bestTime", Float.class, jsonData);
		mapFile = json.readValue("mapFile", String.class, jsonData);
		damp = json.readValue("damp", Float.class, jsonData);
		gravity = json.readValue("gravity", Float.class, jsonData);
	}
	
}

