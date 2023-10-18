package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import net.gbm.unknowland.UnknownLand;

public class CreditsScreen extends AbstractScreen{

	private Table table;
	
	public CreditsScreen(UnknownLand unknownLand) {
		super(unknownLand);
	}
	
	@Override
	public void show() {
		super.show();
		
		// create the table actor
        table = createCreditsTable();
        		
        // add the table to the stage
        stage.addActor( table );	
	}
	
	private Table createCreditsTable() {
		// retrieve the skin (created on the AbstractScreen class)
		Skin skin = super.getSkin();
		Table resultTable = new Table( skin );
		
        resultTable.defaults().pad(10);
        resultTable.defaults().width(1000);
        resultTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("images/menu_bg.png"))));
 
        Label spacer = new Label("", skin);
        spacer.setAlignment(Align.center);
        resultTable.add(spacer).height(30);
        resultTable.row();        
        
        Label graphics = new Label("Graphics", skin);
        graphics.setAlignment(Align.center);
        resultTable.add(graphics).height(30);
        resultTable.row();

        Label omendez = new Label("***German Berna Martinez - We are working on multiplayer", skin);
        omendez.setAlignment(Align.left);
        LabelStyle linkColor = new LabelStyle(omendez.getStyle());
        linkColor.fontColor = new Color(0f,0.5f,1f,1f);
        omendez.setStyle(linkColor); 
        omendez.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Gdx.net.openURI("http://es.linkedin.com/pub/oliver-gonzalez-mendez/67/b95/419");
        	};
        } );
        resultTable.add(omendez).height(20);
        resultTable.row();
        
        Label kenney = new Label("***Kenney - Everything else", skin);
        kenney.setAlignment(Align.left);
        kenney.setStyle(linkColor);
        kenney.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Gdx.net.openURI("http://www.kenney.nl/");
        	};
        } );        
        resultTable.add(kenney).height(20);
        resultTable.row();
        
        Label sounds = new Label("Sounds", skin);
        sounds.setAlignment(Align.center);
        resultTable.add(sounds).height(30);
        resultTable.row();
        
        Label sauer2 = new Label("***sauer2", skin);
        sauer2.setAlignment(Align.left);
        sauer2.setStyle(linkColor);
        sauer2.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Gdx.net.openURI("http://opengameart.org/content/oldschool-win-and-die-jump-and-run-sounds");
        	};
        } );        
        resultTable.add(sauer2).height(20);
        resultTable.row();
        
        Label markMcCorkle = new Label("***8-bit Platformer SFX by Mark McCorkle\n--for http://opengameart.org", skin);
        markMcCorkle.setAlignment(Align.left);
        markMcCorkle.setStyle(linkColor);
        markMcCorkle.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Gdx.net.openURI("http://opengameart.org/content/8-bit-platformer-sfx");
        	};
        } );        
        resultTable.add(markMcCorkle).height(40);
        resultTable.row(); 
        
        Label music = new Label("Music", skin);
        music.setAlignment(Align.center);
        resultTable.add(music).height(40);
        resultTable.row();
        
        Label stratkat = new Label("***Daydream Anatomy - stratkat", skin);
        stratkat.setAlignment(Align.left);
        stratkat.setStyle(linkColor);
        stratkat.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Gdx.net.openURI("http://daydreamanatomy.bandcamp.com/");
        	};
        } );        
        resultTable.add(stratkat).height(20);
        resultTable.row();        
        
        resultTable.defaults().width(600);
        // button "Back"
        TextButton Back = new TextButton( "Back", skin );
        Back.addListener( new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		unknownLand.moveToMainMenuScreen();
        	};
        } );
        resultTable.defaults().height(60);
        resultTable.add(Back);        
        
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
