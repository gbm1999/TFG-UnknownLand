package screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.gbm.unknowland.UnknownLand;

public class SplashScreen extends AbstractScreen {
	
	private Texture splashTexture;
	private Image splashImage;
	
	public SplashScreen(UnknownLand unknownLand) {
		super(unknownLand);
	}

    @Override
    public void show()
    {
        super.show(); // clears the stage and sets input processor to "stage"
        
		//the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );        

        // load the splash image and create the texture region
        splashTexture = new Texture( Gdx.files.internal("unknownland.png" ));

        // we set the linear texture filter to improve the stretching
        splashTexture.setFilter( TextureFilter.Linear, TextureFilter.Linear );
        
        // here we create the splash image actor and set its size
        splashImage = new Image( splashTexture );

        // this is needed for the fade-in effect to work correctly; we're just
        // making the image completely transparent
        splashImage.getColor().a = 0f;

        // configure the fade-in/out effect on the splash image
        splashImage.addAction( sequence( fadeIn( 0.75f ), delay( 1.75f ), fadeOut( 0.75f ),
        		run(new Runnable() {
        			@Override
        			public void run() {
        				unknownLand.moveToMainMenuScreen();
        			}
        			}) ) );

        // and finally we add the actors to the stage
        stage.addActor( splashImage );        

    }
    
    @Override
    public void resize(int width, int height)
    {
        super.resize(width,height);

        splashImage.setWidth(width);
        splashImage.setHeight(height);

        splashImage.invalidateHierarchy();
    }    

    @Override
    public void dispose()
    {
        super.dispose();
        splashTexture.dispose();
    }
    
	@Override
	public void render(float delta) {
		//the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
		super.render(delta);
	}    
}
