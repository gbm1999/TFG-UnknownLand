package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.gbm.unknowland.UnknownLand;

public class DragAndDropInventory extends AbstractScreen {
    private Stage stage = new Stage();
    private ClickListener exitClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            unknownLand.moveToGameScreen();
        };
    };
    public DragAndDropInventory(UnknownLand unknownLand) {
        super(unknownLand);
        FileHandle skinFile = Gdx.files.internal("skin/ui/uiskin.json");
        final Skin skin = new Skin(skinFile);
        skin.getFont("default-font");

        stage.setDebugAll(true);

        final List<String> inventory = new List<>(skin), sell = new List<>(skin);
        inventory.setItems("Axe", "Fuel", "Helmet", "Flux Capacitor", "Shoes", "Hammer", "Trash Can", "The Hitchhiker's Guide To The Galaxy", "Cucumber");

        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        table.defaults();
        table.add("Inventory");
        table.add("Equipment").row();
        table.add(inventory).expand().fill();
        table.add(sell).expand().fill();

        DragAndDrop dnd = new DragAndDrop();
        dnd.addSource(new Source(inventory) {
            final Payload payload = new Payload();

            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                String item = inventory.getSelected();
                payload.setObject(item);
                inventory.getItems().removeIndex(inventory.getSelectedIndex());
                payload.setDragActor(new Label(item, skin));
                payload.setInvalidDragActor(new Label(item + " (\"No equip!\")", skin));
                payload.setValidDragActor(new Label(item + " (\"Equip!\")", skin));
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                if (target == null)
                    inventory.getItems().add((String) payload.getObject());
            }
        });
        dnd.addTarget(new Target(sell) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                return !"Cucumber".equals(payload.getObject());
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                sell.getItems().add((String) payload.getObject());
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}