package net.gbm.unknowland;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import model.Material;
import model.World;
import persistence.WorldMapProvider;

public class UnknownLand extends ApplicationAdapter {

	OrthographicCamera camera;
	SpriteBatch batch;
	World world;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();

		world = new World("1",150);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);

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
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
