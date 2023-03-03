package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import net.gbm.unknowland.UnknownLand;

public abstract class AbstractScreen implements Screen {

	protected UnknownLand unknownLand;
	protected final Stage stage;
	
    private BitmapFont font;
    private Skin skin;
	
	public AbstractScreen(UnknownLand unknownLand) {
		this.unknownLand = unknownLand;
		this.stage = new Stage( new ScreenViewport());
	}

    protected String getName()
    {
        return getClass().getSimpleName();
    }
    
    public BitmapFont getFont()
    {
        if( font == null ) {
            font = new BitmapFont();
        }
        return font;
    }
    
    protected Skin getSkin()
    {
        if( skin == null ) {
        	FileHandle skinFile = Gdx.files.internal( "skin/firstSkin.json" );
            skin = new Skin( skinFile );
            skin.getFont("default-font");
        }
        return skin;
    }    
    
	@Override
	public void render(float delta) {
        // update  stage actors
        stage.act( delta );
        
        // draw the actors
        stage.draw();
        //Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log( UnknownLand.LOG, "Resizing screen: " + getName() + " to: " + width + " x " + height );

        // resize the stage
        stage.getViewport().update( width, height, true );
	}

    @Override
    public void show()
    {
        stage.clear();
        
    	Gdx.app.log( UnknownLand.LOG, "Showing screen: " + getName() );

        // set the input processor
        Gdx.input.setInputProcessor( stage );
    }

	@Override
	public void hide() {
        Gdx.app.log( UnknownLand.LOG, "Hiding screen: " + getName() );
	}

	@Override
	public void pause() {
		Gdx.app.log( UnknownLand.LOG, "Pausing screen: " + getName() );
	}

	@Override
	public void resume() {
		Gdx.app.log( UnknownLand.LOG, "Resuming screen: " + getName() );
	}

	@Override
	public void dispose() {
		Gdx.app.log( UnknownLand.LOG, "Disposing screen: " + getName() );
        stage.dispose();
        if( font != null ) font.dispose();
        if( skin != null ) skin.dispose();
	}
	
}
