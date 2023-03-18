package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;

import model.Bullet;
import model.CactusEnemy;
import model.Enemy;
import model.FirstBoss;
import model.FlyEnemy;
import model.Item;
import model.JellyEnemy;
import model.Level;
import model.Material;
import model.Player;
import model.Player.State;
import model.RectangleCollider;
import model.SnailEnemy;

public class LevelRenderer {

	static Sprite jump;
	static Sprite playerIdleRight;
	static Sprite playerIdleLeft;
	static Sprite playerJumpLeft;
	static Sprite playerJumpRight;

	private TextureRegion jellyFrame;

	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;

	private Animation jellyLeftAnimation;
	private Animation jellyRightAnimation;

	static TextureAtlas atlas;
	static TextureAtlas atlas2;
	private OrthographicCamera camera;
	private static final float LERP = 0.15f;
	private float minCameraPositionX;
	private float minCameraPositionY;
	private float maxCameraPositionX;
	private float maxCameraPositionY;

	public LevelRenderer( Player player){
		load(player);
	}
	public void load(Player player){
		atlas = new TextureAtlas(Gdx.files.internal("gamesprites/playersprites/Player.pack"));
		atlas2 = new TextureAtlas(Gdx.files.internal("gamesprites/jellysprites/jelly.pack"));
		jump = atlas.createSprite("player1");
		playerIdleRight = atlas.createSprite("player2");
		playerIdleLeft = atlas.createSprite("player5");
		playerJumpRight = atlas.createSprite("player1");
		playerJumpLeft = atlas.createSprite("player4");

		TextureRegion[] walkRightFrames = new TextureRegion[2];
		TextureRegion[] walkLeftFrames = new TextureRegion[2];
		int j = 1;
		for (int i = 0; i < 2; i++) {
			walkRightFrames[i] = atlas.findRegion("player" + j );
			j = j + 2;
			walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
			walkLeftFrames[i].flip(true, false);
		}

		walkLeftAnimation = new Animation(Player.WALK_FRAME_DURATION, walkLeftFrames);
		walkRightAnimation = new Animation(Player.WALK_FRAME_DURATION, walkRightFrames);

		TextureRegion[] jellyRightFrames = new TextureRegion[2];
		TextureRegion[] jellyLeftFrames = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			jellyLeftFrames[i] = atlas2.findRegion("jelly" + (i+1) );
			jellyRightFrames[i] = new TextureRegion(jellyLeftFrames[i]);
			jellyRightFrames[i].flip(true, false);
		}
		jellyLeftAnimation = new Animation(0.30f, jellyLeftFrames);
		jellyRightAnimation = new Animation(0.30f, jellyRightFrames);

		camera = new OrthographicCamera();

		camera.viewportWidth = 50;
		camera.viewportHeight = 50;

		minCameraPositionX = camera.viewportWidth/2;
		maxCameraPositionX = 50 - minCameraPositionX;
		minCameraPositionY = camera.viewportHeight/2;
		maxCameraPositionY = 50 - minCameraPositionY;
		camera.position.x = player.getX();
		camera.position.y = player.getY();
		camera.update();

	}

	public void render(Player player,ArrayList<Enemy> enemies, SpriteBatch sb) {

		float targetXPosition, targetYPosition;

		targetXPosition = player.getX() + (player.getWidth()/2);
		// we want the camera to be a little bit over the player
		targetYPosition = player.getY() + (player.getHeight()/2);

		camera.position.x += (targetXPosition - camera.position.x) * LERP;
		camera.position.y += (targetYPosition - camera.position.y) * LERP;

		// we don't want the camera to go beyond the map boundaries
		if (camera.position.x < minCameraPositionX)
			camera.position.x = minCameraPositionX;
		else if (camera.position.x > maxCameraPositionX)
			camera.position.x = maxCameraPositionX;
		if (camera.position.y < minCameraPositionY)
			camera.position.y = minCameraPositionY;
		else if (camera.position.y > maxCameraPositionY)
			camera.position.y = maxCameraPositionY;

		camera.update();

		drawPlayer(player, sb);
		drawEnemies(sb, enemies);

	}
	public static void dispose() {
		atlas.dispose();
	}

	public void drawPlayer(Player player, SpriteBatch sb) {
		TextureRegion keyFrame = LevelRenderer.playerIdleRight;

			keyFrame = player.isFacingLeft() ? playerIdleLeft : playerIdleRight;
		if(player.getState().equals(Player.State.WALKING)) {
			keyFrame = (TextureRegion) (player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(player.getStateTime(), true) : walkRightAnimation.getKeyFrame(player.getStateTime(), true));
		} else if (player.getState().equals(State.JUMPING) || player.getState().equals(State.FALLING)) {
				keyFrame = player.isFacingLeft() ? playerJumpLeft : playerJumpRight;
		}

			sb.draw(keyFrame, player.getX() * Material.SIZE, player.getY() * Material.SIZE);

	}

	private void drawEnemies(SpriteBatch sb, ArrayList<Enemy> enemyList) {
		for (Enemy enemy: enemyList){
			boolean isDeadButVisible = !enemy.isAlive() && enemy.timeSinceDeath() < 1f;

			if (isDeadButVisible){
				animationDeath(enemy);
			}else if (!enemy.isAlive()){
				continue;
			}

			if(enemy instanceof JellyEnemy){
				JellyEnemy jellyEnemy = (JellyEnemy) enemy;
				jellyFrame = (TextureRegion) (jellyEnemy.isMovingLeft()?jellyLeftAnimation.getKeyFrame(jellyEnemy.getStateTime(),true):jellyRightAnimation.getKeyFrame(jellyEnemy.getStateTime(),true));
				sb.draw(jellyFrame, jellyEnemy.getX() * Material.SIZE, jellyEnemy.getY() * Material.SIZE, jellyEnemy.getWidth()/2, jellyEnemy.getHeight()/2, jellyEnemy.getWidth(), jellyEnemy.getHeight(),1f,1f,jellyEnemy.getRotation());
			}
			else{

			}
		}

	}

	private void animationDeath(RectangleCollider dyingEntity) {
		float oldWidth = dyingEntity.getWidth();
		float oldHeight= dyingEntity.getHeight();
		float newWidth = dyingEntity.getWidth()*.94f;
		float newHeight = dyingEntity.getHeight()*.94f;

		dyingEntity.setX(dyingEntity.getX() + ((oldWidth  - newWidth)  / 2));
		dyingEntity.setY(dyingEntity.getY() + ((oldHeight - newHeight) / 2));
		dyingEntity.setWidth(newWidth);
		dyingEntity.setHeight(newHeight);
		dyingEntity.setRotation(dyingEntity.getRotation() + 20f);

	}
}
