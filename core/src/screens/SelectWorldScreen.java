package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import model.World;

public class SelectWorldScreen extends AbstractScreen{

	private Table table;
	
	public SelectWorldScreen(UnknownLand unknownLand) {
		super(unknownLand);
	}
	
	@Override
	public void show() {
		super.show(); // clears the stage and sets input processor to "stage"
		
		//the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );		
        
		// create the table actor
        table = createSelectWorldTable();
        		
        // add the table to the stage
        stage.addActor( table );		
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
        table.setWidth(width);
        table.setHeight(height);	
        
        table.invalidateHierarchy();		
	}
	
	private Table createSelectWorldTable() {
		
		// retrieve the skin (created on the AbstractScreen class)
		Skin skin = super.getSkin();
		Table resultTable = new Table( skin );
		
        resultTable.defaults().pad(10);
        resultTable.defaults().width(600);
        resultTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("images/menu_bg.png"))));
 
        Label screenTitle = new Label("Select World", skin);
        screenTitle.setAlignment(Align.center);
        resultTable.add(screenTitle).height(40).colspan(5);
        resultTable.row();
        
        resultTable.defaults().width(200);
        List<World> worldList = Arrays.asList(unknownLand.getSelectedWorld());
        // Select World buttons
        int i = 1;
        boolean nextWorldEnabled = true;
        for (World world : worldList){
        	final World auxWorld = world;
	        TextButton selectLevelButton = new TextButton( auxWorld.getId(), skin );
	        if (nextWorldEnabled){
		        selectLevelButton.addListener( new ClickListener() {
		        	@Override
		        	public void clicked(InputEvent event, float x, float y) {
		        		unknownLand.setSelectedWorld(auxWorld);
		        		unknownLand.moveToGameScreen();
		        	};
		        });
	        }else{
	        	selectLevelButton.setColor(new Color(0f, 0f, 0f, 0.5f));
	        }
	        resultTable.defaults().height(60);
	        resultTable.add(selectLevelButton);
	        if ((i++%5) == 0){
	        	resultTable.row();
	        }
        }
        if (i%5 != 0){
        	resultTable.row();
        }
        
        resultTable.defaults().width(600);
        // button "Back"
        TextButton backButton = new TextButton( "Back", skin );
        backButton.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToMainMenuScreen();
        	};
        } );
        resultTable.add(backButton).colspan(5);
        
		return resultTable;
	}	

	@Override
	public void render(float delta) {
		//the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
		super.render(delta);
	}	
	
}
