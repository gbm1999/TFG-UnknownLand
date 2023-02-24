package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import net.gbm.unknowland.UnknownLand;

public class MainMenuScreen extends AbstractScreen{

	// setup the dimensions of the menu buttons
	
    private Table table;
	
	public MainMenuScreen(UnknownLand unknownLand) {
		super(unknownLand);
	}
	
	@Override
	public void show() {
		super.show(); // clears the stage and sets input processor to "stage"

		//the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
		
        // create the table actor
        table = createMainMenuTable();
        		
        // add the table to the stage
        stage.addActor( table );
	}
	
	
	private Table createMainMenuTable() {
		
		// retrieve the skin (created on the AbstractScreen class)
		Skin skin = super.getSkin();
		Table resultTable = new Table( skin );
		
        resultTable.defaults().pad(10);
        resultTable.defaults().width(600);
        resultTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("images/menu_bg.png"))));
 
        Label screenTitle = new Label("", skin);
        screenTitle.setAlignment(Align.center);
        resultTable.add(screenTitle).height(40);
        resultTable.row();
        
        // button "SinglePlayer"
        TextButton singlePlayerButton = new TextButton( "Single Player Game", skin );
        singlePlayerButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToSelectWorldScreen();
        	};
        } );
        resultTable.defaults().height(60);
        resultTable.add(singlePlayerButton);
        resultTable.row();
        
        // button "MultiPlayer"
        TextButton multiPlayerButton = new TextButton( "Multiplayer Game", skin );
        multiPlayerButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToJoinHostGameScreen();
        	};
        } );
        resultTable.add(multiPlayerButton);
        resultTable.row();
        
        // button "Options"
        TextButton optionsButton = new TextButton( "Options", skin );
        optionsButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToOptionsScreen();
        	};
        } );
        resultTable.add(optionsButton);
        resultTable.row();
        
        // button "Credits"
        TextButton creditsButton = new TextButton( "Credits", skin );
        creditsButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToCreditsScreen();
        	};        	
        } );
        resultTable.add(creditsButton);  
		return resultTable;
	}

	@Override
	public void resize(int width, int height) {
		
		super.resize(width, height);
        table.setWidth(width);
        table.setHeight(height);	
        
        table.invalidateHierarchy();
	}

	@Override
	public void render(float delta) {
		//the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
		super.render(delta);
	}	
	
}
