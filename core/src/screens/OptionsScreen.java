package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import net.gbm.unknowland.UnknownLand;

public class OptionsScreen extends AbstractScreen{
	
	private Table table;
	
	public OptionsScreen(UnknownLand unknownLand) {
		super(unknownLand);
	}
	
	@Override
	public void show() {
		super.show();
		
		//the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );		
		
		// create the table actor
        table = createOptionsTable();
        		
        // add the table to the stage
        stage.addActor( table );			
	}
	
	private Table createOptionsTable() {
		// retrieve the skin (created on the AbstractScreen class)
		Skin skin = super.getSkin();
		Table resultTable = new Table( skin );
		
        resultTable.defaults().width(400);
        resultTable.defaults().height(40);
        resultTable.defaults().pad(10);
        resultTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("images/menu_bg.png"))));
 
        Label screenTitle = new Label("Options", skin);
        screenTitle.setAlignment(Align.center);
        resultTable.add(screenTitle).width(600).colspan(2);
        resultTable.row();
        
        CheckBox musicCheckBox = new CheckBox("", skin);
        Label musicLabel = new Label("Music", skin);
        musicLabel.setAlignment(Align.left);
        resultTable.add(musicLabel);
        resultTable.add(musicCheckBox).width(200);
        resultTable.row();
        
        CheckBox soundCheckBox = new CheckBox("", skin);
        Label soundLabel = new Label("Sounds", skin);
        soundLabel.setAlignment(Align.left);
        resultTable.add(soundLabel);
        resultTable.add(soundCheckBox).width(200);
        resultTable.row();
        
        Slider musicVolumeSlider = new Slider(0f,100f,10f,false,skin);
        Label musicVolumeLabel = new Label("Music Volume", skin);
        musicVolumeLabel.setAlignment(Align.left);
        resultTable.add(musicVolumeLabel);
        resultTable.add(musicVolumeSlider).width(200);
        resultTable.row();
        
        Slider soundVolumeSlider = new Slider(0f,100f,10f,false,skin);
        Label soundVolumeLabel = new Label("Sounds Volume", skin);
        soundVolumeLabel.setAlignment(Align.left);
        resultTable.add(soundVolumeLabel);
        resultTable.add(soundVolumeSlider).width(200);
        resultTable.row(); 
        
        // button "Back"
        TextButton Back = new TextButton( "Back", skin );
        Back.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToMainMenuScreen();
        	};
        } );
        resultTable.add(Back).height(60).width(600).colspan(2);
        
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
