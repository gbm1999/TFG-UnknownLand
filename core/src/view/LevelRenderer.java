package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import model.Player;
import model.RectangleCollider;
import model.SnailEnemy;

public class LevelRenderer {
	
	public static final float ANIMATION_WIN_DURATION = 0.25f;
	public static final float ANIMATION_DEATH_DURATION = 1f;
	private static final float RUNNING_FRAME_DURATION = 0.15f;
	private static final float JELLY_WALK_FRAME_DURATION = 0.30f;
	private static final float FLY_WALK_FRAME_DURATION = 0.30f;
	private static final float SNAIL_WALK_FRAME_DURATION = 0.30f;	
	private static final float NUMBER_OF_TILES_X = 20;
	private static final float NUMBER_OF_TILES_Y = 11;
	private static final float LERP = 0.15f;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private float minCameraPositionX, minCameraPositionY, maxCameraPositionX, maxCameraPositionY;
	private SpriteBatch sb;

	TextureAtlas atlas;
	/** Textures **/
    private TextureRegion playerIdleLeft;
    private TextureRegion playerIdleRight;
    private TextureRegion playerFrame;
    private TextureRegion playerJumpLeft;
    private TextureRegion playerFallLeft;
    private TextureRegion playerJumpRight;
    private TextureRegion playerFallRight;
    private TextureRegion goldCoin;
    private TextureRegion silverCoin;
    private TextureRegion copperCoin;
    private TextureRegion bulletTexture;
    private TextureRegion laserTexture;
    private TextureRegion jellyFrame;
    private TextureRegion flyFrame;
    private TextureRegion snailFrame;
    private TextureRegion snailHidedFrame;
    private TextureRegion cactusTexture;
    private TextureRegion firstBossIdleLeft;
    private TextureRegion firstBossIdleRight;
    private TextureRegion firstBossJumpLeft;
    private TextureRegion firstBossJumpRight;    
	
    /** Animations **/
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private Animation jellyLeftAnimation;
    private Animation jellyRightAnimation;
    private Animation flyLeftAnimation;
    private Animation flyRightAnimation;
    private Animation snailLeftAnimation;
    private Animation snailRightAnimation;      
    
    private Float winAnimationStartTime = null;

	private ArrayList<Enemy> enemyList;
	
	
	
	public LevelRenderer(SpriteBatch sb, ArrayList<Enemy> creatures) {
		//renderer = new OrthogonalTiledMapRenderer(level.getMap());
		camera = new OrthographicCamera();
		//Player player = level.getPlayer();
		
		//camera.viewportWidth = NUMBER_OF_TILES_X*level.getTileWidth();
		//camera.viewportHeight = NUMBER_OF_TILES_Y*level.getTileHeight();
		
		minCameraPositionX = camera.viewportWidth/2;
		//maxCameraPositionX = level.getTotalWidth()*level.getTileWidth() - minCameraPositionX;
		minCameraPositionY = camera.viewportHeight/2;
		//maxCameraPositionY = level.getTotalHeight()*level.getTileHeight() - minCameraPositionY;
		//camera.position.x = player.getX();
		//camera.position.y = player.getY();
		camera.update();
		this.sb = sb;
		this.enemyList = creatures;
		loadTextures();
	}

	private void loadTextures() {
        atlas = new TextureAtlas(Gdx.files.internal("gamesprites/textures.pack"));
        playerIdleRight = atlas.findRegion("player_stand");
        playerIdleLeft = new TextureRegion(playerIdleRight);
        playerIdleLeft.flip(true, false);
        
        firstBossIdleRight = atlas.findRegion("boss_stand");
        firstBossIdleLeft = new TextureRegion(firstBossIdleRight);
        firstBossIdleLeft.flip(true, false);
        firstBossJumpRight = atlas.findRegion("boss_jumping");
        firstBossJumpLeft = new TextureRegion(firstBossJumpRight);
        firstBossJumpLeft.flip(true, false);
        
        TextureRegion[] walkRightFrames = new TextureRegion[2];
        TextureRegion[] walkLeftFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
        	walkRightFrames[i] = atlas.findRegion("player_walk" + (i+1) );
        	walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
        	walkLeftFrames[i].flip(true, false);
        }
        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);
        walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);
        
        playerJumpRight = atlas.findRegion("player_jumping");
        playerJumpLeft = new TextureRegion(playerJumpRight);
        playerJumpLeft.flip(true, false);
        
        playerFallLeft = new TextureRegion(playerJumpLeft);
        playerFallRight = new TextureRegion(playerJumpRight);
        
        goldCoin = atlas.findRegion("gold_coin");
        silverCoin = atlas.findRegion("silver_coin");
        copperCoin = atlas.findRegion("copper_coin");
        
        bulletTexture = atlas.findRegion("bullet");
        laserTexture = atlas.findRegion("laser");
        
        TextureRegion[] jellyRightFrames = new TextureRegion[2];
        TextureRegion[] jellyLeftFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
        	jellyLeftFrames[i] = atlas.findRegion("pink_jelly" + (i+1) );
        	jellyRightFrames[i] = new TextureRegion(jellyLeftFrames[i]);
        	jellyRightFrames[i].flip(true, false);
        }
        jellyLeftAnimation = new Animation(JELLY_WALK_FRAME_DURATION, jellyLeftFrames);
        jellyRightAnimation = new Animation(JELLY_WALK_FRAME_DURATION, jellyRightFrames);
        
        TextureRegion[] flyRightFrames = new TextureRegion[2];
        TextureRegion[] flyLeftFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
        	flyLeftFrames[i] = atlas.findRegion("fly" + (i+1) );
        	flyRightFrames[i] = new TextureRegion(flyLeftFrames[i]);
        	flyRightFrames[i].flip(true, false);
        }
        flyLeftAnimation = new Animation(FLY_WALK_FRAME_DURATION, flyLeftFrames);
        flyRightAnimation = new Animation(FLY_WALK_FRAME_DURATION, flyRightFrames);
        
        TextureRegion[] snailRightFrames = new TextureRegion[2];
        TextureRegion[] snailLeftFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
        	snailLeftFrames[i] = atlas.findRegion("snail" + (i+1) );
        	snailRightFrames[i] = new TextureRegion(snailLeftFrames[i]);
        	snailRightFrames[i].flip(true, false);
        }
        snailLeftAnimation = new Animation(SNAIL_WALK_FRAME_DURATION, snailLeftFrames);
        snailRightAnimation = new Animation(SNAIL_WALK_FRAME_DURATION, snailRightFrames);
        snailHidedFrame = atlas.findRegion("snail_hided");
        
        cactusTexture = atlas.findRegion("cactus1");
}	
	
	public void dispose() {
		renderer.dispose();
		atlas.dispose();
	}

	public void render() {
		/*if (!level.isLoaded()){
			return;
		}
		Player player = level.getPlayer();
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
		
		renderer.setView(camera);
		renderer.render();
*/
		//drawItems(sb, level.getItemList());
		drawEnemies();
		//drawPlayerAndBullets(sb, player);
		
	}

	private void drawItems(SpriteBatch sb, ArrayList<Item> itemList) {
		sb.begin();
		for(Item item : itemList){
			if (item.isActive()){
				switch (item.getItemType()){
					case Item.GOLD_COIN:
						sb.draw(goldCoin, item.getX(), item.getY(), item.getWidth(), item.getHeight());						
						break;
					case Item.SILVER_COIN:
						sb.draw(silverCoin, item.getX(), item.getY(), item.getWidth(), item.getHeight());
						break;
					case Item.COPPER_COIN:
					default:
						sb.draw(copperCoin, item.getX(), item.getY(), item.getWidth(), item.getHeight());
						break;
				}
			}
		}
		sb.end();
	}

	public void drawEnemies() {

		for (Enemy enemy: enemyList){
			boolean isDeadButVisible = !enemy.isAlive() && enemy.timeSinceDeath() < ANIMATION_DEATH_DURATION; 
			
			if (isDeadButVisible){
				animationDeath(enemy);
			}else if (!enemy.isAlive()){
				continue;
			}

			if(enemy instanceof JellyEnemy){
				JellyEnemy jellyEnemy = (JellyEnemy) enemy;
				jellyFrame = (TextureRegion) (jellyEnemy.isMovingLeft()?jellyLeftAnimation.getKeyFrame(jellyEnemy.getStateTime(),true):jellyRightAnimation.getKeyFrame(jellyEnemy.getStateTime(),true));
				sb.draw(jellyFrame, jellyEnemy.getX(), jellyEnemy.getY(), jellyEnemy.getWidth()/2, jellyEnemy.getHeight()/2, jellyEnemy.getWidth(), jellyEnemy.getHeight(),1f,1f,jellyEnemy.getRotation());
			}else if (enemy instanceof CactusEnemy){
				CactusEnemy cactusEnemy = (CactusEnemy) enemy;
				sb.draw(cactusTexture, cactusEnemy.getX(), cactusEnemy.getY(), cactusEnemy.getWidth()/2, cactusEnemy.getHeight()/2, cactusEnemy.getWidth(), cactusEnemy.getHeight(),1f,1f,cactusEnemy.getRotation());
				for (Bullet bullet: cactusEnemy.getBulletList()){
					bullet.setRotation(bullet.getRotation() + 20f);
					sb.draw(bulletTexture, bullet.getX(), bullet.getY(), bullet.getWidth()/2, bullet.getHeight()/2, bullet.getWidth(), bullet.getHeight(),1f,1f,bullet.getRotation());	
				}
			}else if (enemy instanceof FlyEnemy){
				FlyEnemy flyEnemy = (FlyEnemy) enemy;
				flyFrame = (TextureRegion) (flyEnemy.isMovingLeft()?flyLeftAnimation.getKeyFrame(flyEnemy.getStateTime(),true):flyRightAnimation.getKeyFrame(flyEnemy.getStateTime(),true));
				sb.draw(flyFrame, flyEnemy.getX(), flyEnemy.getY(), flyEnemy.getWidth()/2, flyEnemy.getHeight()/2, flyEnemy.getWidth(), flyEnemy.getHeight(),1f,1f,flyEnemy.getRotation());
			}else if (enemy instanceof SnailEnemy){
				SnailEnemy snailEnemy = (SnailEnemy) enemy;
				if (!snailEnemy.almostDead()){
					snailFrame = (TextureRegion) (snailEnemy.isMovingLeft()?snailLeftAnimation.getKeyFrame(snailEnemy.getStateTime(),true):snailRightAnimation.getKeyFrame(snailEnemy.getStateTime(),true));
					sb.draw(snailFrame, snailEnemy.getX(), snailEnemy.getY(), snailEnemy.getWidth()/2, snailEnemy.getHeight()/2, snailEnemy.getWidth(), snailEnemy.getHeight(),1f,1f,snailEnemy.getRotation());
				}else{
					sb.draw(snailHidedFrame, snailEnemy.getX(), snailEnemy.getY(), snailEnemy.getWidth()/2, snailEnemy.getHeight()/2, snailEnemy.getWidth(), snailEnemy.getHeight(),1f,1f,snailEnemy.getRotation());
				}
			}else if (enemy instanceof FirstBoss){
				FirstBoss firstBoss = (FirstBoss) enemy;
				sb.draw(firstBoss.isFacingLeft()?firstBossIdleLeft:firstBossIdleRight, firstBoss.getX(), firstBoss.getY(), firstBoss.getWidth()/2, firstBoss.getHeight()/2, firstBoss.getWidth(), firstBoss.getHeight(),1f,1f,firstBoss.getRotation());
				for (Bullet bullet: firstBoss.getBulletList()){
					bullet.setRotation(bullet.getRotation() + 20f);
					sb.draw(bulletTexture, bullet.getX(), bullet.getY(), bullet.getWidth()/2, bullet.getHeight()/2, bullet.getWidth(), bullet.getHeight(),1f,1f,bullet.getRotation());	
				}
			}
			
		}

		
	}

	private void drawPlayerAndBullets(SpriteBatch sb, Player player) {
        playerFrame = player.isFacingLeft() ? playerIdleLeft : playerIdleRight;
        if(player.getState().equals(Player.State.WALKING)) {
            playerFrame = (TextureRegion) (player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(player.getStateTime(), true) : walkRightAnimation.getKeyFrame(player.getStateTime(), true));
        } else if (player.getState().equals(Player.State.JUMPING) || player.getState().equals(Player.State.FALLING)) {
            if (player.getVelocity().y > 0) {
                playerFrame = player.isFacingLeft() ? playerJumpLeft : playerJumpRight;
            } else {
                playerFrame = player.isFacingLeft() ? playerFallLeft : playerFallRight;
            }
        }
        sb.begin();
        
        if (!player.isAlive()){
        	if (player.getState().equals(Player.State.DYING)){
        		animationDeath(player);
        	} else if (player.getState().equals(Player.State.WINNING)){
        		animationWinPlayer(player);
        	}
        	sb.draw(playerFrame, player.getX(), player.getY(), player.getWidth()/2, player.getHeight()/2, player.getWidth(), player.getHeight(),1f,1f,player.getRotation());
        }else{
        	clearAnimations();
        	sb.draw(playerFrame, player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }
        
        for (Bullet bullet : player.getBulletList()){
        	sb.draw(laserTexture, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
        }
        sb.end();
	}

	private void clearAnimations() {
		winAnimationStartTime = null;
	}

	private void animationWinPlayer(Player player) {
		if (winAnimationStartTime == null){
			winAnimationStartTime = player.getStateTime();
		}
    	if ((player.getStateTime() - winAnimationStartTime.floatValue()) < ANIMATION_WIN_DURATION){
    		float oldWidth = player.getWidth();
        	float newWidth = player.getWidth()*.8f;
        	float newHeight = player.getHeight()*.8f;
	    	player.setX(player.getX() + ((oldWidth  - newWidth)  / 2));
	    	player.setY(player.getY() + 3f);
	    	player.setWidth(newWidth);
	    	player.setHeight(newHeight);
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

	public OrthographicCamera getCamera() {
		return camera;
	}

}
