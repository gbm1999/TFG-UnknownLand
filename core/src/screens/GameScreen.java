package screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import controller.LevelController;
import model.Material;
import model.Player;
import model.World;


import net.gbm.unknowland.UnknownLand;

import java.util.Random;


public class GameScreen extends AbstractScreen {
    private InputMultiplexer multiplexer;
    private LevelController levelController;
    private Table tableUITop;
    private Table tableUIBottom;
    private Table tablePause;
    private Table tableInventory;
    private Table tableSummary;
    private Label timeAvailableLabel;
    private Label scoreLabel;
    private boolean pause = false;
    private boolean inventoryBoolean = false;
    private Label hiScoreLabel;
    private Label summaryScoreLabel;
    Music mp3Music;

    List<String> inventory;
    List<String> sell;

    private ClickListener exitClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            mp3Music.stop();
            mp3Music.dispose();
            mp3Music = null;
            unknownLand.moveToMainMenuScreen();
        };
    };

    public GameScreen(UnknownLand unknownLand) {
        super(unknownLand);
        Skin skin = super.getSkin();
        skin.getFont("default-font");
        FileHandle skinFile = Gdx.files.internal("skin/ui/uiskin.json");
        final Skin skin2 = new Skin(skinFile);
        inventory = new List<>(skin2);
        sell = new List<>(skin2);

        show();
    }
    @Override
    public void show(){
        super.show();
        levelController = new LevelController(this.unknownLand.getWorld());
        // create the table actors
        tableUITop = createUITopTable();
        tableUIBottom = createUIBottomTable();
        tablePause = createPauseTable();
        tableInventory = createInventoryTable();
        tableSummary = createSummaryTable();

        this.setTableVisibility(tableSummary,false);
        this.setTableVisibility(tablePause,pause);
        this.setTableVisibility(tableInventory,inventoryBoolean);

        // add the table to the stage
        stage.addActor( tableUITop );
        stage.addActor( tablePause );
        stage.addActor( tableSummary );
        stage.addActor(tableInventory);

        // Only add the touch buttons when running on Android
        if (Gdx.app.getType().equals(Application.ApplicationType.Android))
            stage.addActor( tableUIBottom );

        // the level Controller receives key strokes
        // the stage receives touch events
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(levelController);
        Gdx.input.setInputProcessor(multiplexer);

        if (mp3Music != null){
            mp3Music.stop();
            mp3Music.dispose();
            mp3Music = null;
        }
        mp3Music = Gdx.audio.newMusic(Gdx.files.internal(getRandomMusic()));
        mp3Music.setLooping(true);
        mp3Music.play();
        pause = false;

    }
    @Override
    public void render(float delta) {
        //the following code clears the screen with the given RGB color (black)
        Gdx.gl.glClearColor( 0f, 0f, 0f, 0f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        levelController.update(delta);

        this.unknownLand.renderWorld();

        updateLabels();

        super.render(delta);

        //setTableVisibility(tableSummary,false);

        this.unknownLand.getCamera().position.set(this.unknownLand.getSelectedWorld().getPlayer().getX() * Material.SIZE, this.unknownLand.getSelectedWorld().getPlayer().getY() * Material.SIZE, 0);
        this.unknownLand.getCamera().update();

        if (Gdx.input.justTouched()){
            Vector3 position = this.unknownLand.getCamera().unproject(new Vector3(Gdx.input.getX() , Gdx.input.getY(), 0));
            Material material = this.unknownLand.getSelectedWorld().getMaterialByLocation(1, position.x, position.y);
            if (material != null){
                System.out.println("Material: " + material.getId() + " " + material.getSymbol() +" " + position.x + " " + position.y);
            }
            System.out.println(this.unknownLand.getSelectedWorld().getPlayer().getX() + " " + this.unknownLand.getSelectedWorld().getPlayer().getY());
        }

    }

    private void setTableVisibility(Table targetTable, boolean visibility){
        targetTable.setVisible(visibility);
    }


    // Here we will create the UI elements like: time, score, pause button
    private Table createUITopTable() {
        // retrieve the skin (created on the AbstractScreen class)
        Skin skin = super.getSkin();
        Table resultTable = new Table( skin );

        resultTable.defaults().pad(10);
        resultTable.defaults().height(40);
        resultTable.setFillParent(true);
        resultTable.left().top();

        timeAvailableLabel = new Label("Time: "+10, skin);
        timeAvailableLabel.setWidth(100);
        scoreLabel = new Label("Score: "+10, skin);
        // button Pause
        Drawable imagePause = new TextureRegionDrawable(new TextureRegion(new Texture("images/pause_ui.png")));
        ImageButton pauseButton = new ImageButton( imagePause );
        pauseButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            };
        } );

        Drawable imageInventory = new TextureRegionDrawable(new TextureRegion(new Texture("images/inventory.png")));
        ImageButton inventoryButton = new ImageButton( imageInventory );
        inventoryButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleInventory();
            };
        } );

        resultTable.add(timeAvailableLabel).width(400).left();
        resultTable.add(inventoryButton).expandX().left();
        resultTable.add(scoreLabel).expandX().center();
        resultTable.add(pauseButton).expandX().right();

        return resultTable;
    }

    // Here we will create the buttons
    private Table createUIBottomTable() {
        // retrieve the skin (created on the AbstractScreen class)
        Skin skin = super.getSkin();
        Table resultTable = new Table( skin );

        // button Left
        Drawable imageLeft = new TextureRegionDrawable(new TextureRegion(new Texture("images/left_ui.png")));
        ImageButton leftButton = new ImageButton( imageLeft );
        leftButton.addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                levelController.keyDown(Input.Keys.LEFT);
                return true;
            };
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                levelController.keyUp(Input.Keys.LEFT);
            };
        } );

        // button Right
        Drawable imageRight = new TextureRegionDrawable(new TextureRegion(new Texture("images/right_ui.png")));
        ImageButton rightButton = new ImageButton( imageRight );
        rightButton.align(Align.left);
        rightButton.padLeft(60);
        rightButton.addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                levelController.keyDown(Input.Keys.RIGHT);
                return true;
            };
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                levelController.keyUp(Input.Keys.RIGHT);
            };
        } );

        // button Jump
        Drawable imageJump = new TextureRegionDrawable(new TextureRegion(new Texture("images/jump_ui.png")));
        ImageButton jumpButton = new ImageButton( imageJump );
        jumpButton.addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                levelController.keyDown(Input.Keys.SPACE);
                return true;
            };
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                levelController.keyUp(Input.Keys.SPACE);
            };
        } );

        // button Fire
        Drawable imageFire = new TextureRegionDrawable(new TextureRegion(new Texture("images/fire_ui.png")));
        ImageButton fireButton = new ImageButton( imageFire );
        fireButton.align(Align.right);
        fireButton.padRight(60);
        fireButton.addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                levelController.keyDown(Input.Keys.CONTROL_LEFT);
                return true;
            };
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                levelController.keyUp(Input.Keys.CONTROL_LEFT);
            };
        } );

        resultTable.defaults().padLeft(5);
        resultTable.defaults().padRight(5);
        resultTable.defaults().height(120);
        resultTable.setFillParent(true);
        resultTable.left().bottom();
        resultTable.setColor(1, 1, 1, 0.5f);

        resultTable.add(leftButton).width(240).padLeft(0);
        resultTable.add(rightButton).expandX().fillX();
        resultTable.add(fireButton).expandX().fillX();
        resultTable.add(jumpButton).width(240).padRight(0);;
        resultTable.row();

        return resultTable;
    }

    private Table createPauseTable() {
        // retrieve the skin (created on the AbstractScreen class)
        Skin skin = super.getSkin();
        Table resultTableRoot = new Table( skin );
        resultTableRoot.setFillParent(true);
        resultTableRoot.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("images/pause_bg.png"))));
        resultTableRoot.setColor(1f, 1f, 1f, 0.75f);

        Table resultTable = new Table( skin );
        resultTable.defaults().pad(10);
        resultTable.defaults().height(60);
        resultTable.defaults().width(350);
        resultTable.defaults().center();
        resultTable.setBackground("grey_panel");


        Label pauseMenuLabel = new Label("Pause", skin);
        pauseMenuLabel.setAlignment(Align.center);

        // button Resume
        TextButton resumeButton = new TextButton( "Resume Game", skin );
        resumeButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            };
        } );

        // button Resume
        TextButton restartButton = new TextButton( "Restart Level", skin );
        restartButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        } );

        // button Exit
        TextButton exitButton = new TextButton( "Exit Game", skin );
        exitButton.addListener( exitClickListener );


        resultTable.add(pauseMenuLabel);
        resultTable.row();
        resultTable.add(resumeButton);
        resultTable.row();
        resultTable.add(restartButton);
        resultTable.row();
        resultTable.add(exitButton);

        resultTableRoot.add(resultTable).center();

        return resultTableRoot;
    }


    private Table createInventoryTable() {
        // retrieve the skin (created on the AbstractScreen class)
        Skin skin = super.getSkin();
        Table table= new Table();
        table.defaults().pad(10F);
        table.setFillParent(true);

        Label label=new Label("Inventory",skin);
        label.setAlignment(Align.center);

        table.add(label).colspan(2).fillX();

        Array<String> Items= new Array<>();

        for(int i = 0; i < unknownLand.getWorld().getPlayer().getInventory().getSize(); i++){
            Items.add(unknownLand.getWorld().getPlayer().getInventory().getItem(i).getType().getSymbol() +" ("+ unknownLand.getWorld().getPlayer().getInventory().getItem(i).getAmount() +")");
        }
        inventory.setItems(Items);

        table.setFillParent(true);
        stage.addActor(table);

        Table first_table=new Table(skin);
        Label label2 = (new Label("ITEMS",skin));
        label2.setColor(Color.RED);
        first_table.add(label2);
        first_table.row();
        first_table.setBackground("grey_panel");
        first_table.setColor(Color.TEAL);
        first_table.add(inventory).expand().fill();

        Table second_table= new Table(skin);
        label2 = (new Label("EQUIPMENT",skin));
        label2.setColor(Color.RED);
        second_table.add(label2);
        second_table.row();
        second_table.setBackground("grey_panel");
        second_table.setColor(Color.TEAL);
        second_table.add(sell).expand().fill();

        table.pad(100);
        table.defaults().height(60);
        table.defaults().width(350);
        table.defaults().center();
        table.row();


        inventory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String item = inventory.getSelected();
                //inventory.getItems().removeIndex(inventory.getSelectedIndex());

                sell.getItems().clear();
                sell.getItems().add(item);
                //update
                tableInventory.remove();
                tableInventory = createInventoryTable();
            }
        });

        if(sell.getItems().size != 0){
            TextButton UseButton = new TextButton("use", skin);
            UseButton.addListener( new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println(sell.getItems().get(0));
                }
            } );
            second_table.add(UseButton);
        }

        table.add(first_table).expand().right().width(350).height(350);
        table.add(second_table).expand().left().width(350).height(350);;

        return table;

    }

    private Table createSummaryTable() {
        // retrieve the skin (created on the AbstractScreen class)
        Skin skin = super.getSkin();
        Table resultTableRoot = new Table( skin );
        resultTableRoot.setFillParent(true);
        resultTableRoot.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("images/pause_bg.png"))));
        resultTableRoot.setColor(1f, 1f, 1f, 0.75f);

        Table resultTable = new Table( skin );
        resultTable.defaults().pad(10);
        resultTable.defaults().height(60);
        resultTable.defaults().width(400);
        resultTable.defaults().center();
        resultTable.setBackground("grey_panel");



        Label summaryMenuLabel = new Label("Level Finished!", skin);
        summaryMenuLabel.setColor(Color.GOLD);
        summaryMenuLabel.setAlignment(Align.center);

        summaryScoreLabel = new Label("Score: "+0, skin);
        summaryScoreLabel.setColor(Color.GOLD);
        summaryScoreLabel.setAlignment(Align.center);

        hiScoreLabel = new Label("Hi-Score: "+0, skin);
        hiScoreLabel.setColor(Color.GOLD);
        hiScoreLabel.setAlignment(Align.center);

        // button Next Level
        Drawable imageNextLevel = new TextureRegionDrawable(new TextureRegion(new Texture("images/next_ui.png")));
        ImageButton nextLevelButton = new ImageButton( imageNextLevel );
        nextLevelButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                World aux = unknownLand.getSelectedWorld();
                if (aux != null){
                    setTableVisibility(tableSummary,false);
                }else{
                    unknownLand.moveToSelectWorldScreen();
                }
            };
        } );

        // button restart
        Drawable imageRestartLevel = new TextureRegionDrawable(new TextureRegion(new Texture("images/restart_ui.png")));
        ImageButton restartButton = new ImageButton( imageRestartLevel );
        restartButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                levelController.setLevelFinished(false);
                setTableVisibility(tableSummary,false);
            };
        } );

        // button Exit
        Drawable imageExit = new TextureRegionDrawable(new TextureRegion(new Texture("images/exit_ui.png")));
        ImageButton exitButton = new ImageButton( imageExit );
        exitButton.addListener( exitClickListener );


        resultTable.add(summaryMenuLabel).colspan(3);
        resultTable.row();
        resultTable.add(summaryScoreLabel).colspan(3);
        resultTable.row();
        resultTable.add(hiScoreLabel).colspan(3);
        resultTable.row();
        resultTable.add(exitButton).width(60);
        resultTable.add(restartButton).width(60);
        resultTable.add(nextLevelButton).width(60);

        resultTableRoot.add(resultTable).center();

        return resultTableRoot;
    }

    protected void togglePause() {
        this.pause = !this.pause ;
        setTableVisibility(tablePause, pause);

    }

    protected void toggleInventory() {
        tableInventory.remove();
        this.inventoryBoolean = !this.inventoryBoolean ;
        tableInventory = createInventoryTable();
        setTableVisibility(tableInventory, inventoryBoolean);

    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose(){
        super.dispose();
        mp3Music.stop();
        mp3Music.dispose();
        mp3Music = null;
    }
    private void updateLabels() {
        timeAvailableLabel.setText("Time: "+10);
        scoreLabel.setText("Live: " + this.unknownLand.getWorld().getPlayer().getHEALTH());
        summaryScoreLabel.setText("Score: "+10);
        hiScoreLabel.setText("Hi-Score: "+10);
    }

    private String getRandomMusic(){
        String[] musics = {"music/8bit_loop.mp3","music/chipChippy_loop.mp3","music/chippyCloudKid_loop.mp3",
                "music/endlessSand_loop.mp3","music/gasolineRainbows_loop.mp3","music/vanguardBouncy_loop.mp3"};
        int numberOfElements = musics.length;
        return musics[(new Random()).nextInt(numberOfElements)];
    }
}
