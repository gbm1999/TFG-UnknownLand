package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import model.Bullet;
import model.CactusEnemy;
import model.Enemy;
import model.FirstBoss;
import model.FlyEnemy;
import model.ItemStack;
import model.JellyEnemy;
import model.MovingEnemy;
import model.Player;
import model.Player.State;
import model.RectangleCollider;
import model.World;

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

	// Minimum elapsed time between shots
	private static final long FIRE_TIMER = 750l;

	private float delay = 2.0f;
	private float timer;

	private World level;
	private Player player;
	private ArrayList<Enemy> creatures;

	private long firePressedTime = System.currentTimeMillis();
	private boolean levelFinished;

	Sound fireSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Laser1.wav"));
	Sound coinSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Pickup_Coin.wav"));
	

	static Map<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.SPACE, false); // JUMP
		keys.put(Keys.CONTROL_LEFT, false); // FIRE
	}

	public LevelController(World level) {
		this.level = level;
		player = level.getPlayer();
		creatures = level.getEnemyList();
	}

	public void update(float delta) {

		movePlayer (player,delta);
		for (Enemy entity : creatures) {
			updateEntity(entity, delta, -9.8f);
			if (entity instanceof JellyEnemy) {
				moveAndUpdateMovingEnemy((JellyEnemy)entity,delta);
			} else if(entity instanceof CactusEnemy) {
				moveCactusEnemyBullets((CactusEnemy) entity, player, delta);
			}
			else if(entity instanceof FlyEnemy) {
				moveAndUpdateMovingEnemy((FlyEnemy) entity, delta);
			}
			else if(entity instanceof FirstBoss){
				moveAndUpdateBoss1((FirstBoss) entity, delta);
				//moveFirstBossBullets((FirstBoss) moveEnemy, player, delta);
			}
		}
		checkCollisionDamage();
		checkCollisionItem();
		moveAndCollideBullets(delta);

		// simply updates timer
		player.update(delta);
		level.update(delta);
	}
	private void updateEntity(RectangleCollider rectangleCollider,float deltaTime, float gravity){
		float newY = rectangleCollider.getY();

		rectangleCollider.velocityY += gravity * deltaTime * rectangleCollider.getWidth();
		newY += rectangleCollider.velocityY * deltaTime;

		if (level.doesRectCollideWithMap(rectangleCollider.getX() + 0.4f, newY, (int)rectangleCollider.getWidth(), (int)rectangleCollider.getHeight())) {
			if (rectangleCollider.velocityY < 0) {
				rectangleCollider.setY((float) Math.floor(rectangleCollider.getY()));
				rectangleCollider.grounded = true;
			}
			rectangleCollider.velocityY = 0;
		} else {
			rectangleCollider.setY(newY);
			rectangleCollider.grounded = false;
		}
	}
	protected void movePlayer (Player player,float deltaTime) {
		if (keys.get(Keys.SPACE) && player.grounded)
			player.velocityY += player.JUMP_VELOCITY * player.getWidth();
		else if (keys.get(Keys.SPACE) && !player.grounded && player.velocityY > 0)
			player.velocityY += player.JUMP_VELOCITY * player.getWidth() * deltaTime;

		updateEntity(player,deltaTime,-9.8f);//Apply gravity

		if (keys.get(Keys.LEFT)){
			movePlayer(player,-player.SPEED * deltaTime , false);
			// left is pressed
			player.setFacingLeft(true);
			if (!player.getState().equals(State.JUMPING)
					&& !player.getState().equals(State.FALLING)
					&& !player.getState().equals(State.WALKING)) {
				player.setState(State.WALKING);
			}
		}
		if (keys.get(Keys.RIGHT)){
			movePlayer(player,0.6f + player.SPEED * deltaTime , true);
			player.setFacingLeft(false);
			if (!player.getState().equals(State.JUMPING)
					&& !player.getState().equals(State.FALLING)
					&& !player.getState().equals(State.WALKING)) {
				player.setState(State.WALKING);
			}
		} else {
			if (!player.getState().equals(State.IDLE)) {
				player.setState(State.IDLE);
			}
		}


		// fire logic
		if (keys.get(Keys.CONTROL_LEFT)
				&& ((System.currentTimeMillis() - firePressedTime) > FIRE_TIMER)) {
			firePressedTime = System.currentTimeMillis();
			if (player.isFacingLeft()) {
				player.getBulletList().add(
						new Bullet(player.getX(), player.getY() + 0.2f,
								1, 1,-Player.SPEED + 2,0f));
			} else {
				player.getBulletList().add(
						new Bullet(player.getX(),
								player.getY() + 0.2f,
								1, 1,Player.SPEED + 2,0f));
			}
			fireSound.play();
		}
	}

	protected void movePlayer (RectangleCollider rectangleCollider,float amount, boolean type) {
		float newX = rectangleCollider.getX() + amount;
		float val = 0;
		if (type){
			val = -0.6f;
		}
		if (!level.doesRectCollideWithMap(newX, rectangleCollider.getY(), (int)rectangleCollider.getWidth(), (int)rectangleCollider.getHeight()))

			rectangleCollider.setX(newX + val);
	}

	private void moveAndUpdateMovingEnemy(MovingEnemy moveEnemy, float delta) {
		float newX = moveEnemy.getX();
		float newY = moveEnemy.getY();

		if(moveEnemy instanceof FirstBoss){
			if(moveEnemy.getX() < player.getX()){
				newX = moveEnemy.getX() + 2 * delta;
				moveEnemy.setMovingRight(true);
			}
			else if (moveEnemy.getX() > player.getX()) {
				newX = moveEnemy.getX() - 2 * delta;
				moveEnemy.setMovingRight(false);
			}
			else{

			}
			if (moveEnemy.isMovingRight() && level.doesRectCollideWithMap(newX + 0.6f, newY, (int)moveEnemy.getWidth(), (int)moveEnemy.getHeight())) {
				if ( moveEnemy.grounded)
					moveEnemy.velocityY += moveEnemy.getWidth() ;
				else
					moveEnemy.velocityY += moveEnemy.getWidth() * delta;
				newX = moveEnemy.getX();
			}
			if (!moveEnemy.isMovingRight() && level.doesRectCollideWithMap(newX, newY, (int)moveEnemy.getWidth(), (int)moveEnemy.getHeight())) {
				if ( moveEnemy.grounded)
					moveEnemy.velocityY += moveEnemy.getWidth() ;
				else
					moveEnemy.velocityY += moveEnemy.getWidth() * delta;
				newX = moveEnemy.getX();
			}
		}
		else{
			if(	moveEnemy.isMovingRight()){
				newX = moveEnemy.getX() + player.SPEED * delta;
			}
			else {
				newX = moveEnemy.getX() - player.SPEED * delta;
			}
			if (moveEnemy.isMovingRight() && level.doesRectCollideWithMap(newX + 0.6f, newY, (int)moveEnemy.getWidth(), (int)moveEnemy.getHeight())) {
				moveEnemy.changeMoveDirection();
			}
			if (!moveEnemy.isMovingRight() && level.doesRectCollideWithMap(newX, newY, (int)moveEnemy.getWidth(), (int)moveEnemy.getHeight())) {
				moveEnemy.changeMoveDirection();
			}
			if (moveEnemy.isMovingRight() && !level.doesRectCollideWithMap(newX + 0.7f, newY - 1, (int)moveEnemy.getWidth(), (int)moveEnemy.getHeight())) {
				moveEnemy.changeMoveDirection();
			}
			if (!moveEnemy.isMovingRight() && !level.doesRectCollideWithMap(newX - 0.2f, newY - 1, (int)moveEnemy.getWidth(), (int)moveEnemy.getHeight())) {
				moveEnemy.changeMoveDirection();
			}
		}

		moveEnemy.setX(newX);
	}

	private void moveAndUpdateBoss1(FirstBoss moveEnemy, float deltaTime) {

		if (moveEnemy.distanceTo(this.player) < moveEnemy.getActionRadius()){
			moveEnemy.shoot(this.player.getX()+ this.player.getWidth()/2, this.player.getY()+ this.player.getHeight()/2,this.level.getHeight(),this.level.getHeight());
		}


		//moveEnemy.moveTowardsPlayer(player);
		//moveEnemy.setY(moveEnemy.getY() + velocity.y * delta);
		updateEntity(moveEnemy,deltaTime,-0.03f);
		moveAndUpdateMovingEnemy(moveEnemy,deltaTime);


	}

	private void checkCollisionDamage() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		timer += deltaTime;
		ArrayList<Enemy> enemies = level.getEnemyList();
		for (Enemy enemy : enemies) {
			if (player.collidesWith(enemy) && timer >= delay) {
				player.setHEALTH(player.getHEALTH() - 1);
				coinSound.play();
				timer = 0.0f;
			}
		}
	}

	private void checkCollisionItem() {
		Map<World.Coord, ItemStack> items = level.getItems();

		for (Map.Entry<World.Coord, ItemStack>  entry : items.entrySet()) {
			if(Math.abs(player.getX() - entry.getKey().getX()) < 0.2 && Math.abs(player.getY() - entry.getKey().getY()) < 0.2){
				player.getInventory().addItem(entry.getValue());
				level.getItems().remove(entry.getKey());
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
						level.getEnemyList().remove(enemy);
					}
					removeBullet = true;
					break;
				}
			}
			if (removeBullet) {
				iter.remove();
				continue;
			}
			if (level.doesRectCollideWithMap(bullet.getX() + 0.6f, bullet.getY(), (int)bullet.getWidth(), (int)bullet.getHeight())) {
				iter.remove();
			}
			// check if bullet life time is over
			if (bullet.checkLifeTime())
				iter.remove();
		}
	}

	private void moveCactusEnemyBullets(CactusEnemy cactusEnemy, Player player, float delta){
		//Move bullets

		if (cactusEnemy.distanceTo(player) < cactusEnemy.getActionRadius()){
			cactusEnemy.shoot(player.getX(), player.getY(),1,1);
		}
		Iterator<Bullet> iter = cactusEnemy.getBulletList().iterator();
		while (iter.hasNext()) {
			Bullet bullet = iter.next();
			bullet.update(delta);
			// move Bullet
			bullet.setX(bullet.getX() + (bullet.getVelocity().x * delta));
			bullet.setY(bullet.getY() + (bullet.getVelocity().y * delta));
			if (bullet.collidesWith(player)) {
				player.setHEALTH(player.getHEALTH() - 1);
				iter.remove();
				continue;
			}
			// check if bullet life time is over
			else if (bullet.checkLifeTime())
				iter.remove();
		}
	}

/*
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
			firstBoss.shoot(player.getX()+player.getWidth()/2, player.getY()+player.getHeight()/2,this.level.getHeight(),this.level.getHeight());
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
		
		if (Math.abs(oldY-firstBoss.getY()) > this.level.getHeight()){
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
			cactusEnemy.shoot(player.getX()+player.getHeight()/2, player.getY()+player.getWidth()/2,this.level.getHeight(),this.level.getHeight());
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

	private void killPlayer() {
		Player player = level.getPlayer();
		if (player.isAlive()){
			timeOfDeath = System.currentTimeMillis();
			player.setState(State.DYING);
			deathSound.play();
			Gdx.app.log( LevelController.class.getName(), "Killing Player!" );
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
*/

	
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
