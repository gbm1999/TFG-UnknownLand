package net.gbm.unknowland;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import model.Level;
import model.World;
import screens.AbstractScreen;
import screens.CreditsScreen;
import screens.GameScreen;
import screens.JoinHostGameScreen;
import screens.MainMenuScreen;
import screens.OptionsScreen;
import screens.SelectWorldScreen;
import screens.SplashScreen;

public class UnknownLand extends Game {

	public static final String LOG = UnknownLand.class.getSimpleName();
	AbstractScreen nextScreen;
	MainMenuScreen mainMenuScreen;
	SplashScreen splashScreen;
	SelectWorldScreen selectWorldScreen;
	JoinHostGameScreen joinHostGameScreen;
	OptionsScreen optionsScreen;
	CreditsScreen creditsScreen;
	GameScreen gameScreen;


	OrthographicCamera camera;
	SpriteBatch batch;

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	private World world;

	private Level selectedLevel;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 400, 300);
		camera.update();

		splashScreen = new SplashScreen(this);

		world = new World("1",150);
		Gdx.app.log( UnknownLand.LOG, "Creating game" );

		mainMenuScreen = new MainMenuScreen(this);
		selectWorldScreen = new SelectWorldScreen(this);

		joinHostGameScreen = new JoinHostGameScreen(this);
		optionsScreen = new OptionsScreen(this);
		creditsScreen = new CreditsScreen(this);
		gameScreen = new GameScreen(this);

		Gdx.app.log( UnknownLand.LOG, "Creating game" );

		moveToSplashScreen();
	}

	@Override
	public void render () {
		//ScreenUtils.clear(1, 0, 0, 1);
		super.render();
		if (nextScreen != null){
			setScreen(nextScreen);
			nextScreen = null;
		}
		/*
		if (Gdx.input.isTouched()){
			camera.translate(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
			camera.update();
		}
		if (Gdx.input.justTouched()){
			Vector3 position = camera.unproject(new Vector3(Gdx.input.getX() , Gdx.input.getY(), 0));
			Material material = world.getMaterialByLocation(1, position.x, position.y);
			if (material != null){
				System.out.println("Material: " + material.getId() + " " + material.getSymbol() +" " + position.x + " " + position.y);
			}
		}


		world.render(camera,batch);
		*/

	}
	
	@Override
	public void dispose () {
		splashScreen.dispose();
		batch.dispose();
		Gdx.app.log( UnknownLand.LOG, "Disposing game" );
	}

	@Override
	public void resize(int width, int height) {
		super.resize( width, height );
		Gdx.app.log( UnknownLand.LOG, "Resizing game to: " + width + " x " + height );
	}

	@Override
	public void pause() {
		super.pause();
		Gdx.app.log( UnknownLand.LOG, "Pausing game" );
	}

	@Override
	public void resume() {
		super.resume();
		Gdx.app.log( UnknownLand.LOG, "Resuming game" );
	}

	public void setScreen(AbstractScreen screen ){
		super.setScreen( screen );
		Gdx.app.log( UnknownLand.LOG, "Setting screen: " + screen.getClass().getSimpleName() );
	}

	public void renderWorld(){
		world.render(camera,batch);
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void moveToSplashScreen() {
		nextScreen = splashScreen;
	}

	public void moveToMainMenuScreen() {
		nextScreen = mainMenuScreen;
	}

	public void moveToSelectWorldScreen() {
		nextScreen = selectWorldScreen;
	}

	public void moveToJoinHostGameScreen() {
		nextScreen = joinHostGameScreen;
	}

	public void moveToOptionsScreen() {
		nextScreen = optionsScreen;
	}

	public void moveToCreditsScreen() {
		nextScreen = creditsScreen;
	}

	public void moveToGameScreen() {
		nextScreen = gameScreen;
	}

	public void setSelectedWorld(World auxWorld) {
		this.world = auxWorld;
	}

	public World getSelectedWorld() {
		return this.world;
	}


}
