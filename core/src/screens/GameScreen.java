package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;

import net.gbm.unknowland.UnknownLand;

import model.Material;

public class GameScreen extends AbstractScreen {

    public GameScreen(UnknownLand unknownLand) {
        super(unknownLand);
    }

    @Override
    public void render(float delta) {
        //the following code clears the screen with the given RGB color (black)
        Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
        super.render(delta);


		if (Gdx.input.isTouched()){
            this.unknownLand.getCamera().translate(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
            this.unknownLand.getCamera().update();
		}
		if (Gdx.input.justTouched()){
			Vector3 position = this.unknownLand.getCamera().unproject(new Vector3(Gdx.input.getX() , Gdx.input.getY(), 0));
			Material material = this.unknownLand.getSelectedWorld().getMaterialByLocation(1, position.x, position.y);
			if (material != null){
				System.out.println("Material: " + material.getId() + " " + material.getSymbol() +" " + position.x + " " + position.y);
			}
		}


        this.unknownLand.renderWorld();


    }
}
