package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Map;

import model.Bullet;
import model.CactusEnemy;
import model.Enemy;
import model.FirstBoss;
import model.FlyEnemy;
import model.ItemStack;
import model.JellyEnemy;
import model.Material;
import model.Player;
import model.Player.State;
import model.RectangleCollider;
import model.World;

public class LevelRenderer {

	static Sprite jump;
	static Sprite playerIdleRight;
	static Sprite playerIdleLeft;
	static Sprite playerJumpLeft;
	static Sprite playerJumpRight;
	private Texture texture;
	private Texture texture2;

	private TextureRegion jellyFrame;
	private TextureRegion cactusFrame;
	private TextureRegion bossFrame;
	private TextureRegion FlyEnemyFrame;

	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;

	private Animation jellyLeftAnimation;
	private Animation jellyRightAnimation;

	private Animation flyLeftAnimation;
	private Animation flyRightAnimation;

	private TextureRegion bossFrameLeft;
	private TextureRegion bossFrameRight;

	static TextureAtlas atlas;
	static TextureAtlas atlas2;
	static TextureAtlas atlas3;
	static TextureAtlas atlas4;
	static TextureAtlas atlas5;
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
		atlas3 = new TextureAtlas(Gdx.files.internal("gamesprites/cactussprites/Cactus.pack"));
		atlas4 = new TextureAtlas(Gdx.files.internal("gamesprites/boss1sprites/boss1.pack"));
		atlas5 = new TextureAtlas(Gdx.files.internal("gamesprites/flysprites/fly.pack"));
		jump = atlas.createSprite("player1");
		playerIdleRight = atlas.createSprite("player2");
		playerIdleLeft = atlas.createSprite("player5");
		playerJumpRight = atlas.createSprite("player1");
		playerJumpLeft = atlas.createSprite("player4");

		TextureRegion[] walkRightFrames = new TextureRegion[2];
		TextureRegion[] walkLeftFrames = new TextureRegion[2];

		walkRightFrames[0] = atlas.findRegion("player1");
		walkRightFrames[1] = atlas.findRegion("player3");
		walkLeftFrames[0] = new TextureRegion(walkRightFrames[0]);
		walkLeftFrames[1] = new TextureRegion(walkRightFrames[1]);
		walkLeftFrames[0].flip(true, false);
		walkLeftFrames[1].flip(true, false);

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

		cactusFrame = new TextureRegion();
		cactusFrame = atlas3.findRegion("New Piskel-1.png");

		bossFrameRight = atlas4.findRegion("New Piskel-1.png");
		bossFrameLeft = new TextureRegion(bossFrameRight);
		bossFrameLeft.flip(true, false);

		TextureRegion[] flyRightFrames = new TextureRegion[2];
		TextureRegion[] flyLeftFrames = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			flyLeftFrames[i] = atlas5.findRegion("flyenemy" + (i+1) );
			flyRightFrames[i] = new TextureRegion(flyLeftFrames[i]);
			flyRightFrames[i].flip(true, false);
		}
		flyLeftAnimation = new Animation(0.08f, flyLeftFrames);
		flyRightAnimation = new Animation(0.08f, flyRightFrames);


		Pixmap pixmap = new Pixmap(20, 2, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.YELLOW);
		pixmap.fillCircle(0, -2, 2);
		texture = new Texture(pixmap);
		pixmap.setColor(Color.RED);
		texture2 = new Texture(pixmap);

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

	public void render(Player player, ArrayList<Enemy> enemies, Map<World.Coord, ItemStack> items, SpriteBatch sb) {

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
		drawItems(sb, items);
		drawBullets(sb, player);

	}
	public static void dispose() {
		atlas.dispose();
	}

	public void drawPlayer(Player player, SpriteBatch sb) {
		TextureRegion keyFrame;

		if(player.getState().equals(Player.State.WALKING)) {
			keyFrame = (TextureRegion) (player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(player.getStateTime(), true) : walkRightAnimation.getKeyFrame(player.getStateTime(), true));
		} else if (player.getState().equals(State.JUMPING) || player.getState().equals(State.FALLING)) {
				keyFrame = player.isFacingLeft() ? playerJumpLeft : playerJumpRight;
		}
		else{
			keyFrame =  player.isFacingLeft() ? playerIdleLeft : playerIdleRight;
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
				jellyFrame = (TextureRegion) (jellyEnemy.isMovingRight()?jellyLeftAnimation.getKeyFrame(jellyEnemy.getStateTime(),true):jellyRightAnimation.getKeyFrame(jellyEnemy.getStateTime(),true));
				sb.draw(jellyFrame, jellyEnemy.getX() * Material.SIZE, jellyEnemy.getY() * Material.SIZE);
			}
			if(enemy instanceof FlyEnemy){
				FlyEnemy flyEnemy = (FlyEnemy) enemy;
				FlyEnemyFrame = (TextureRegion) (flyEnemy.isMovingRight()?flyLeftAnimation.getKeyFrame(flyEnemy.getStateTime(),true):flyRightAnimation.getKeyFrame(flyEnemy.getStateTime(),true));
				sb.draw(FlyEnemyFrame, flyEnemy.getX() * Material.SIZE, flyEnemy.getY() * Material.SIZE);
			}
			if(enemy instanceof CactusEnemy){
				CactusEnemy cactusEnemy = (CactusEnemy) enemy;
				sb.draw(cactusFrame, cactusEnemy.getX() * Material.SIZE, cactusEnemy.getY() * Material.SIZE);
				for (Bullet bullet: cactusEnemy.getBulletList()){
					sb.draw(texture2, bullet.getX(), bullet.getY());
				}
			}
			if(enemy instanceof FirstBoss){
				FirstBoss firstBoss = (FirstBoss) enemy;
				bossFrame = (TextureRegion) (firstBoss.isMovingRight()?bossFrameRight:bossFrameLeft);
				sb.draw(bossFrame, firstBoss.getX() * Material.SIZE, firstBoss.getY() * Material.SIZE);
			}
		}

	}

	private void drawItems(SpriteBatch sb, Map<World.Coord, ItemStack> itemList) {
		for (Map.Entry<World.Coord, ItemStack> entry : itemList.entrySet()) {
			sb.draw(texture, entry.getKey().getX() * Material.SIZE, entry.getKey().getY() * Material.SIZE);
		}
	}

	private void drawBullets(SpriteBatch sb, Player player) {
		for (Bullet bullet : player.getBulletList()){
			sb.draw(texture2, bullet.getX()  * Material.SIZE, bullet.getY()  * Material.SIZE);
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
