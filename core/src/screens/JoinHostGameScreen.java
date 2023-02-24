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

public class JoinHostGameScreen extends AbstractScreen{

	private Table table;
	
	public JoinHostGameScreen(UnknownLand unknownLand) {
		super(unknownLand);
	}
	
	@Override
	public void show() {
		super.show();
		
		// create the table actor
        table = createJoinHostTable();
        		
        // add the table to the stage
        stage.addActor( table );			
	}
	
	private Table createJoinHostTable() {
		// retrieve the skin (created on the AbstractScreen class)
		Skin skin = super.getSkin();
		Table resultTable = new Table( skin );
		
        resultTable.defaults().pad(10);
        resultTable.defaults().width(600);
        resultTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("images/menu_bg.png"))));
 
        Label screenTitle = new Label("Join Host Screen", skin);
        screenTitle.setAlignment(Align.center);
        resultTable.add(screenTitle).height(40);
        resultTable.row();
        
        // button "Join Game"
        TextButton joinGameButton = new TextButton( "Join Game", skin );
        joinGameButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		//superMegaBuzz.moveToJoinGameScreen();
        	};
        } );
        resultTable.defaults().height(60);
        resultTable.add(joinGameButton);
        resultTable.row();
        
        // button "Host Game"
        TextButton hostGameButton = new TextButton( "Host Game", skin );
        hostGameButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		//superMegaBuzz.moveToHostGameScreen();
        	};
        } );
        resultTable.add(hostGameButton);
        resultTable.row();
        
        // button "Back"
        TextButton backButton = new TextButton( "Back", skin );
        backButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToMainMenuScreen();
        	};
        } );
        resultTable.add(backButton);
		
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
