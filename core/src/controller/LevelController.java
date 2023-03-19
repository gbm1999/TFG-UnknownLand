package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import model.Bullet;
import model.CactusEnemy;
import model.Enemy;
import model.FirstBoss;
import model.FlyEnemy;
import model.Item;
import model.JellyEnemy;
import model.Level;
import model.MovingEnemy;
import model.Player;
import model.Player.State;
import model.RectangleCollider;
import model.SnailEnemy;
import model.World;
import view.LevelRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Natalio
 * 
 *         This class is responsible of handling the user
 *         input(left,right,jump,fire) also is the class that brings to life
 *         every single element of the game. For instance, it moves the enemies,
 *         it also moves the player and reacts to collisions.
 */

public class LevelController implements InputProcessor {

	// Maximum movement speed the player can reach
	private static final float MAX_SPEED = 150f;
	// Jump speed applied when Jump Button is pressed
	private static final float JUMP_SPEED = 200f;
	// Maximum time we spend applying JUMP_SPEED to the player
	private static final long LONG_JUMP_PRESS = 300l;
	// Minimum elapsed time between shots
	private static final long FIRE_TIMER = 750l;

	// Acceleration applied to the player when moving left or right
	private static final float ACCELERATION = 750f;
	
	// We use this variable in order to stop the player smoothly
	private float damp;
	private float gravity;

	private static final long TIME_TO_RESTART_LEVEL = 1000l;
	//private static final long TIME_TO_FINISH_LEVEL = (long) (LevelRenderer.ANIMATION_WIN_DURATION * 1500l);

	private World level;
	private TiledMapTileLayer collisionLayer;

	private long jumpPressedTime;
	private boolean jumpingPressed;
	private float mapHeight;
	private float mapWidth;
	private long firePressedTime = System.currentTimeMillis();
	private long timeOfDeath;
	private long timeOfFinish;
	private boolean levelNeedRestart;
	private boolean levelFinished;
	
	//sound effects
	Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Jump.wav"));
	Sound fireSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Laser1.wav"));
	Sound coinSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Pickup_Coin.wav"));
	Sound warpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Randomize2.wav"));
	Sound deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/death.wav"));
	

	static Map<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.SPACE, false); // JUMP
		keys.put(Keys.CONTROL_LEFT, false); // FIRE
	}

	public LevelController(World level) {
		this.level = level;
		this.damp = level.getDamp();
		this.gravity = level.getGravity();
		this.collisionLayer = (TiledMapTileLayer) level.getMap().getLayers().get("colisiones");
		mapHeight = collisionLayer.getHeight() * collisionLayer.getTileHeight();
		mapWidth = collisionLayer.getWidth() * collisionLayer.getTileWidth();
		levelNeedRestart = false;
	}

	public void update(float delta) {
		Player player = level.getPlayer();
		processInput();

		// Setting initial vertical acceleration
		player.getAcceleration().y = gravity;

		// Convert acceleration to frame time
		player.getAcceleration().scl(delta);

		// apply acceleration to change velocity
		player.getVelocity().add(player.getAcceleration().x,
				player.getAcceleration().y);

		if (player.isAlive()) {
			moveAndCollidePlayer(delta);
			checkItemsPickedUp(delta);
		} else {
			if (player.getState().equals(State.DYING) 
					&& (System.currentTimeMillis() - timeOfDeath > TIME_TO_RESTART_LEVEL)) {
				askForLevelToRestart();
			}
			
			if (player.getState().equals(State.WINNING)){
					//&& (System.currentTimeMillis() - timeOfFinish > TIME_TO_FINISH_LEVEL)) {
				notifyLevelFinished();
			}			
		}
		moveAndCollideBullets(delta);
		moveAndUpdateEnemies(delta);

		if (player.getAcceleration().x == 0) {
			// When we are not applying any acceleration to the player
			// we apply damping to halt the player nicely
			player.getVelocity().x *= damp;
		}

		// ensure terminal velocity is not exceeded
		if (player.getVelocity().x > MAX_SPEED) {
			player.getVelocity().x = MAX_SPEED;
		}
		if (player.getVelocity().x < -MAX_SPEED) {
			player.getVelocity().x = -MAX_SPEED;
		}

		// simply updates the state time (animation)
		//player.update(delta);
		// simply updates timer
		level.update(delta);
	}

	private void moveAndUpdateEnemies(float delta) {
		Player player = level.getPlayer();
		ArrayList<Enemy> enemies = level.getEnemyList();
		for (Enemy enemy : enemies) {
			if (enemy.isAlive()){
				if (enemy instanceof JellyEnemy) {
					moveAndUpdateMovingEnemy((JellyEnemy) enemy, delta); 
				} else if(enemy instanceof CactusEnemy){
					moveAndUpdateCactusEnemy((CactusEnemy) enemy, player, delta); 
				} else if(enemy instanceof FlyEnemy) {
					moveAndUpdateFlyEnemy((FlyEnemy) enemy, delta);
				} else if (enemy instanceof SnailEnemy){
					moveAndUpdateMovingEnemy((SnailEnemy) enemy, delta);
				}else if(enemy instanceof FirstBoss){
					moveAndUpdateFirstBoss((FirstBoss) enemy, player, delta);
				}
				if (enemy.collidesWith(player)) {
					killPlayer();
				}
			}
			
			enemy.update(delta);
			
			if(enemy instanceof CactusEnemy){
				moveCactusEnemyBullets((CactusEnemy) enemy, player, delta);
			} else if (enemy instanceof FirstBoss){
				moveFirstBossBullets((FirstBoss) enemy, player, delta);
			}
		}
	}

	private void moveAndUpdateFirstBoss(FirstBoss firstBoss, Player player, float delta) {
		//IA
		if (firstBoss.distanceTo(player) < firstBoss.getActionRadius()){
			firstBoss.shoot(player.getX()+player.getWidth()/2, player.getY()+player.getHeight()/2,this.level.getTileHeight(),this.level.getTileHeight());
		}
		
		// Setting initial vertical acceleration
		firstBoss.getAcceleration().y = gravity;
		// Convert acceleration to frame time
		firstBoss.getAcceleration().scl(delta);
		// apply acceleration to change velocity
		firstBoss.getVelocity().add(firstBoss.getAcceleration().x,
		firstBoss.getAcceleration().y);
		
		Vector2 velocity = firstBoss.getVelocity();

		float oldY = firstBoss.getY();
		boolean collisionY = false;

		firstBoss.setY(firstBoss.getY() + velocity.y * delta);

		collisionY = collidesBottom(firstBoss.getX(), firstBoss.getY(),
				firstBoss.getWidth(), firstBoss.getHeight(),
				firstBoss.getIncrementX());
		
		if (Math.abs(oldY-firstBoss.getY()) > this.level.getTileHeight()){
			collisionY = true;
		}
		
		if (collisionY) {
			velocity.y = 0;
			firstBoss.setY(oldY);
			firstBoss.jumpRandom();
		}
	}
	
	private void moveFirstBossBullets(FirstBoss firstBoss, Player player, float delta){
		//Move bullets
		Iterator<Bullet> iter = firstBoss.getBulletList().iterator();
		while (iter.hasNext()) {
			Bullet bullet = iter.next();
			bullet.update(delta);
			// move Bullet
			bullet.setX(bullet.getX() + (bullet.getVelocity().x * delta));
			bullet.setY(bullet.getY() + (bullet.getVelocity().y * delta));
			if (bullet.collidesWith(player)) {
				killPlayer();
				iter.remove();
				continue;
			}
			// collide bullet with walls
			if (checkWalls(bullet)) {
				iter.remove();
				continue;
			}
			// check if bullet life time is over
			if (bullet.checkLifeTime())
				iter.remove();
		}
	}	
	
	private void moveAndUpdateCactusEnemy(CactusEnemy cactusEnemy, Player player, float delta) {
		//IA
		if (cactusEnemy.distanceTo(player) < cactusEnemy.getActionRadius()){
			cactusEnemy.shoot(player.getX()+player.getHeight()/2, player.getY()+player.getWidth()/2,this.level.getTileHeight(),this.level.getTileHeight());
		}
	}
	
	private void moveCactusEnemyBullets(CactusEnemy cactusEnemy, Player player, float delta){
		//Move bullets
		Iterator<Bullet> iter = cactusEnemy.getBulletList().iterator();
		while (iter.hasNext()) {
			Bullet bullet = iter.next();
			bullet.update(delta);
			// move Bullet
			bullet.setX(bullet.getX() + (bullet.getVelocity().x * delta));
			bullet.setY(bullet.getY() + (bullet.getVelocity().y * delta));
			if (bullet.collidesWith(player)) {
				killPlayer();
				iter.remove();
			}
			// collide bullet with walls
			if (checkWalls(bullet)) {
				iter.remove();
				continue;
			}
			// check if bullet life time is over
			if (bullet.checkLifeTime())
				iter.remove();
		}
	}

	private void moveAndUpdateMovingEnemy(MovingEnemy moveEnemy, float delta) {
		moveEnemy.setX(moveEnemy.getX() + (moveEnemy.getVelocity().x * delta));
		if (checkWalls(moveEnemy) 
				|| checkFall(moveEnemy) 
				|| checkCellWithPropertyCollisionX(moveEnemy, moveEnemy.getVelocity(), "kills")) {
			moveEnemy.changeMoveDirection();
		}
	}
	
	private void moveAndUpdateFlyEnemy(FlyEnemy flyEnemy, float delta) {
		flyEnemy.setX(flyEnemy.getX() + (flyEnemy.getVelocity().x * delta));
		if (checkWalls(flyEnemy) 
				|| checkCellWithPropertyCollisionX(flyEnemy, flyEnemy.getVelocity(), "kills")) {
			flyEnemy.changeMoveDirection();
		}
		flyEnemy.changeFlyDirectionIfNeeded();
	}
	
	private boolean checkFall(MovingEnemy enemy) {
		return ((enemy.isMovingLeft() && !isCellBlocked(enemy.getX(),
				enemy.getY() - 2)) || (!enemy.isMovingLeft() && !isCellBlocked(
				enemy.getX() + enemy.getWidth(), enemy.getY() - 2)));
	}

	private boolean checkWalls(MovingEnemy enemy) {
		return ((enemy.isMovingLeft() && collidesLeft(enemy.getX(),
				enemy.getY(), enemy.getWidth(), enemy.getHeight(),
				enemy.getIncrementY())) || (!enemy.isMovingLeft() && collidesRight(
				enemy.getX(), enemy.getY(), enemy.getWidth(),
				enemy.getHeight(), enemy.getIncrementY())));
	}

	private void killPlayer() {
		Player player = level.getPlayer();
		if (player.isAlive()){
			timeOfDeath = System.currentTimeMillis();
			player.setState(State.DYING);
			deathSound.play();
			Gdx.app.log( LevelController.class.getName(), "Killing Player!" );
		}
	}
	
	private void moveToNextLevel() {
		Player player = level.getPlayer();
		if (level.getScore() > level.getHighScore()){
			level.setHighScore(level.getScore());
		}
		if (level.getTimeElapsed() > level.getBestTime()){
			level.setBestTime(level.getTimeElapsed());
		}
		timeOfFinish = System.currentTimeMillis();
		player.setState(State.WINNING);
		warpSound.play();
		Gdx.app.log( LevelController.class.getName(), "Player won!" );
	}	

	private void checkItemsPickedUp(float delta) {
		Player player = level.getPlayer();
		ArrayList<Item> items = level.getItemList();
		for (Item item : items) {
			if (item.isActive() && player.collidesWith(item)) {
				level.increaseScore(item.getPoints());
				item.setActive(false);
				coinSound.play();
			}
		}

	}

	private void moveAndCollideBullets(float delta) {
		Player player = level.getPlayer();
		Iterator<Bullet> iter = player.getBulletList().iterator();
		ArrayList<Enemy> enemies = level.getEnemyList();

		while (iter.hasNext()) {
			Bullet bullet = iter.next();
			bullet.update(delta);
			// move Bullet
			bullet.setX(bullet.getX() + (bullet.getVelocity().x * delta));
			boolean removeBullet = false;
			// collide bullet with enemies
			for (Enemy enemy : enemies) {
				if (enemy.isAlive() && bullet.collidesWith(enemy)) {
					if (enemy.reduceLife(player.getDamage())){
						level.increaseScore(enemy.getPoints());
					}
					removeBullet = true;
					break;
				}
			}
			if (removeBullet) {
				iter.remove();
				continue;
			}
			// collide bullet with walls
			if (checkWalls(bullet)) {
				iter.remove();
				continue;
			}

			// check if bullet life time is over
			if (bullet.checkLifeTime())
				iter.remove();
		}
	}

	private boolean checkWalls(Bullet bullet) {
		return ((bullet.isMovingLeft() && collidesLeft(bullet.getX(),
				bullet.getY(), bullet.getWidth(), bullet.getHeight(),
				bullet.getIncrementY())) || (!bullet.isMovingLeft() && collidesRight(
				bullet.getX(), bullet.getY(), bullet.getWidth(),
				bullet.getHeight(), bullet.getIncrementY())));
	}

	private void moveAndCollidePlayer(float delta) {
		Player player = level.getPlayer();
		Vector2 velocity = player.getVelocity();

		float oldX = player.getX(), oldY = player.getY();
		boolean collisionX = false, collisionY = false;

		// move the player
		player.setX(player.getX() + velocity.x * delta);

		// check if we collided
		collisionX = checkPlayerCollisionX(velocity);
		if (checkCellWithPropertyCollisionX(player, velocity, "kills")) {
			killPlayer();
			return;
		}
		if (checkCellWithPropertyCollisionX(player, velocity, "levelEnd")){
			moveToNextLevel();
			return;
		}

		// if collided we go back
		if (collisionX) {
			//Gdx.app.log( SuperMegaBuzz.LOG, "Player can't move in the X direction" );
			player.setX(oldX);
			velocity.x = 0;
		}

		// the same for Y axis
		player.setY(player.getY() + velocity.y * delta);

		collisionY = checkPlayerCollisionY(velocity);
		if (checkCellWithPropertyCollisionY(player, velocity, "kills")) {
			killPlayer();
			return;
		}
		if (checkCellWithPropertyCollisionY(player, velocity, "levelEnd")){
			moveToNextLevel();
			return;			
		}		

		if (collisionY) {
			//Gdx.app.log( SuperMegaBuzz.LOG, "Player can't move in the Y direction" );
			if (player.getState().equals(State.FALLING)) {
				player.setState(State.IDLE);
			} else if (player.getState().equals(State.JUMPING)) {
				player.setState(State.FALLING);
				jumpingPressed = false;
			}
			velocity.y = 0;
			player.setY(oldY);
		} else {
			if (velocity.y < 0) {
				player.setState(State.FALLING);
			}
		}

	}

	private void processInput() {
		Player player = level.getPlayer();
		float tileHeight = level.getTileHeight();
		float tileWidth = level.getTileWidth();
		// jumping logic
		if (keys.get(Keys.SPACE)) {
			if (player.canJump()) { // player can jump if is not jumping or
									// falling or dying
				jumpingPressed = true;
				jumpPressedTime = System.currentTimeMillis();
				player.setState(State.JUMPING);
				player.getVelocity().y = JUMP_SPEED;
				jumpSound.play();
			} else {
				if (jumpingPressed
						&& ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
					jumpingPressed = false;
				} else {
					if (jumpingPressed) {
						player.getVelocity().y = JUMP_SPEED;
					}
				}
			}
		} else {
			jumpingPressed = false;
		}

		if (player.isAlive()) {
			// movement logic
			if (keys.get(Keys.LEFT)) {
				// left is pressed
				player.setFacingLeft(true);
				if (!player.getState().equals(State.JUMPING)
						&& !player.getState().equals(State.FALLING)
						&& !player.getState().equals(State.WALKING)) {
					player.setState(State.WALKING);
				}
				player.getAcceleration().x = -ACCELERATION;
			} else if (keys.get(Keys.RIGHT)) {
				// left is pressed
				player.setFacingLeft(false);
				if (!player.getState().equals(State.JUMPING)
						&& !player.getState().equals(State.FALLING)
						&& !player.getState().equals(State.WALKING)) {
					player.setState(State.WALKING);
				}
				player.getAcceleration().x = ACCELERATION;
			} else {
				if (!player.getState().equals(State.JUMPING)
						&& !player.getState().equals(State.FALLING)
						&& !player.getState().equals(State.IDLE)) {
					player.setState(State.IDLE);
				}
				player.getAcceleration().x = 0;
			}

			// fire logic
			if (keys.get(Keys.CONTROL_LEFT)
					&& ((System.currentTimeMillis() - firePressedTime) > FIRE_TIMER)) {
				firePressedTime = System.currentTimeMillis();
				if (player.isFacingLeft()) {
					player.getBulletList().add(
							new Bullet(player.getX(), player.getY()
									+ (player.getHeight() / 3),
									tileWidth, tileHeight,-Bullet.bullet_speed,0f));
				} else {
					player.getBulletList().add(
							new Bullet(player.getX() + player.getWidth(),
									player.getY() + (player.getHeight() / 3),
									tileWidth, tileHeight,Bullet.bullet_speed,0f));
				}
				fireSound.play();
			}
		}
	}

	private boolean checkPlayerCollisionY(Vector2 velocity) {
		Player player = level.getPlayer();		
		boolean collisionY = false;
		if (velocity.y < 0) {
			collisionY = collidesBottom(player.getX(), player.getY(),
					player.getWidth(), player.getHeight(),
					player.getIncrementX());
		} else if (velocity.y > 0) {
			collisionY = collidesTop(player.getX(), player.getY(),
					player.getWidth(), player.getHeight(),
					player.getIncrementX());
		}
		return collisionY;
	}

	private boolean checkPlayerCollisionX(Vector2 velocity) {
		Player player = level.getPlayer();		
		boolean collisionX = false;
		if (velocity.x < 0) {
			collisionX = collidesLeft(player.getX(), player.getY(),
					player.getWidth(), player.getHeight(),
					player.getIncrementY());
		} else if (velocity.x > 0) {
			collisionX = collidesRight(player.getX(), player.getY(),
					player.getWidth(), player.getHeight(),
					player.getIncrementY());
		}
		return collisionX;
	}

	public boolean collidesRight(float x, float y, float objectWidth,
			float objectHeight, float increment) {
		for (float step = 0; step <= objectHeight; step += increment)
			if (isCellBlocked(x + objectWidth, y + step))
				return true;
		return false;
	}

	public boolean collidesLeft(float x, float y, float objectWidth,
			float objectHeight, float increment) {
		for (float step = 0; step <= objectHeight; step += increment)
			if (isCellBlocked(x, y + step))
				return true;
		return false;
	}

	public boolean collidesTop(float x, float y, float objectWidth,
			float objectHeight, float increment) {
		for (float step = 0; step <= objectWidth; step += increment)
			if (isCellBlocked(x + step, y + objectHeight))
				return true;
		return false;
	}

	public boolean collidesBottom(float x, float y, float objectWidth,
			float objectHeight, float increment) {
		for (float step = 0; step <= objectWidth; step += increment)
			if (isCellBlocked(x + step, y))
				return true;
		return false;
	}

	private boolean isCellBlocked(float x, float y) {
		float tileHeight = level.getTileHeight();
		float tileWidth = level.getTileWidth();		
		if (outSideMap(x, y)) {
			return true;
		}
		Cell cellToCheck = collisionLayer.getCell((int) (x / tileWidth),
				(int) (y / tileHeight));
		if (cellToCheck != null && cellToCheck.getTile() != null)
			return cellToCheck.getTile().getProperties().containsKey("blocked");
		else
			return false;
	}

	private boolean outSideMap(float x, float y) {
		if ((x > mapWidth) || (x < 0))
			return true;
		else if ((y > mapHeight) || (y < 0))
			return true;
		else
			return false;
	}
	
	private boolean checkCellWithPropertyCollisionY(RectangleCollider entity, Vector2 velocity, String property) {
		boolean collisionY = false;
		if (velocity.y < 0) {
			collisionY = collidesBottomCellWithProperty(entity.getX(), entity.getY(),
					entity.getWidth(), entity.getHeight(),
					entity.getIncrementX(), property);
		} else if (velocity.y > 0) {
			collisionY = collidesTopCellWithProperty(entity.getX(), entity.getY(),
					entity.getWidth(), entity.getHeight(),
					entity.getIncrementX(), property);
		}
		return collisionY;
	}	
	
	private boolean checkCellWithPropertyCollisionX(RectangleCollider entity, Vector2 velocity, String property) {			
		boolean collisionX = false;
		if (velocity.x < 0) {
			collisionX = collidesLeftCellWithProperty(entity.getX(), entity.getY(),
					entity.getWidth(), entity.getHeight(),
					entity.getIncrementY(), property);
		} else if (velocity.x > 0) {
			collisionX = collidesRightCellWithProperty(entity.getX(), entity.getY(),
					entity.getWidth(), entity.getHeight(),
					entity.getIncrementY(), property);
		}
		return collisionX;
	}

	private boolean collidesRightCellWithProperty(float x, float y, float width,
			float height, float incrementY , String property) {
		float[] offsets;
		for (float step = 0; step <= height; step += incrementY) {
			offsets = getCellOffsets(x + width, y + step);
			if (doesCellHaveProperty(x + width, y + step, offsets[0], offsets[1], offsets[2], offsets[3], property))
				return true;
		}
		return false;
	}

	private boolean collidesLeftCellWithProperty(float x, float y, float width,
			float height, float incrementY, String property) {
		float[] offsets;
		for (float step = 0; step <= height; step += incrementY) {
			offsets = getCellOffsets(x, y + step);
			if (doesCellHaveProperty(x, y + step, offsets[0], offsets[1], offsets[2], offsets[3], property))
				return true;
		}
		return false;
	}

	private boolean collidesTopCellWithProperty(float x, float y, float width,
			float height, float incrementX, String property) {
		float[] offsets;
		for (float step = 0; step <= width; step += incrementX) {
			offsets = getCellOffsets(x + step, y + height);
			if (doesCellHaveProperty(x + step, y + height, offsets[0], offsets[1], offsets[2], offsets[3], property))
				return true;
		}
		return false;
	}

	private boolean collidesBottomCellWithProperty(float x, float y, float width,
			float height, float incrementX, String property) {
		float[] offsets;
		for (float step = 0; step <= width; step += incrementX) {
			offsets = getCellOffsets(x + step, y);
			if (doesCellHaveProperty(x + step, y, offsets[0], offsets[1], offsets[2], offsets[3], property))
				return true;
		}
		return false;
	}

	private float[] getCellOffsets(float x, float y) {
		float tileHeight = level.getTileHeight();
		float tileWidth = level.getTileWidth();			
		float[] offsets = {0,0,0,0};
		Cell cellToCheck = collisionLayer.getCell((int) (x / tileWidth),
				(int) (y / tileHeight));
		if (cellToCheck != null && cellToCheck.getTile() != null) {
			MapProperties mapProperties = cellToCheck.getTile().getProperties();
			try{
				offsets[0] = Float.parseFloat((String) mapProperties
						.get("topOffset"));
				offsets[1] = Float.parseFloat((String) mapProperties
						.get("rightOffset"));
				offsets[2] = Float.parseFloat((String) mapProperties
						.get("bottomOffset"));
				offsets[3] = Float.parseFloat((String) mapProperties
						.get("leftOffset"));
			}catch(Exception ex){
				//Gdx.app.log(SuperMegaBuzz.LOG, "Cell does not have offsets defined or there was a problem loading the offsets, returning zero offsets");
				offsets[0] = 0;
				offsets[1] = 0;
				offsets[2] = 0;
				offsets[3] = 0;
			}
		}
		return offsets;
	}

	private boolean doesCellHaveProperty(float x, float y, float offsetTop, float offsetRight, float offsetBottom, float offsetLeft, String property) {
		float tileHeight = level.getTileHeight();
		float tileWidth = level.getTileWidth();			
		int indexCellX = (int) (x / tileWidth);
		int indexCellY = (int) (y / tileHeight);
		Cell cellToCheck = collisionLayer.getCell(indexCellX,indexCellY);
		if (cellToCheck != null && cellToCheck.getTile() != null) {
			MapProperties mapProperties = cellToCheck.getTile().getProperties();
			if (mapProperties.containsKey(property)) {
				float floatCellX = indexCellX*tileWidth;
				float floatCellY = indexCellY*tileHeight;
				RectangleCollider cellCollider = new RectangleCollider(floatCellX+offsetLeft, floatCellY+offsetBottom, 
													 tileWidth-offsetRight, tileHeight-offsetTop, tileWidth, tileHeight);
				if (cellCollider.isPointInsideRectangle(x,y))
					return true;
				else
					return false;
			} else {
				return false;
			}
		} else{
			return false;
		}
	}

	private void askForLevelToRestart() {
		Gdx.app.log(getClass().getName(), "Asking for Level to Restart");
		this.levelNeedRestart = true;
	}

	public void levelRestarted(){
		this.levelNeedRestart = false;
	}
	
	public boolean getLevelNeedRestart() {
		return levelNeedRestart;
	}
	
	private void notifyLevelFinished() {
		if (!this.levelFinished){
			Gdx.app.log(getClass().getName(), "Notify Level Finished");
			this.levelFinished = true;
		}
	}

	public boolean getLevelFinished() {
		return levelFinished;
	}
	
	public void setLevelFinished(boolean levelFinished) {
		this.levelFinished = levelFinished;
	}

	@Override
	public boolean keyDown(int keycode) {
		Gdx.app.log(getClass().getName(), "keyDown");
		if (keys.containsKey(keycode)) {
			keys.put(keycode, true);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		Gdx.app.log(getClass().getName(), "keyUp");
		if (keys.containsKey(keycode)) {
			keys.put(keycode, false);
		}
		return false;
	}

	// We simply ignore these ones
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

}
