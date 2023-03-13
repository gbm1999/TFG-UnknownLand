package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;

import net.gbm.unknowland.UnknownLand;

import model.Material;
import model.Player;



public class GameScreen extends AbstractScreen {
    private long jumpPressedTime;
    private boolean jumpingPressed;
    private long canJump;

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
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            System.out.println("Se mueve Izquierda");
            unknownLand.getWorld().getPlayer().setX(unknownLand.getWorld().getPlayer().getX() + 0.1f );
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            System.out.println("Se mueve Derecha");
            unknownLand.getWorld().getPlayer().setX(unknownLand.getWorld().getPlayer().getX() - 0.1f );
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            System.out.println("Se mueve Izquierda");
           // unknownLand.getWorld().getPlayer().setY(unknownLand.getWorld().getPlayer().getY() + 1 );
            unknownLand.getWorld().getPlayer().setY(unknownLand.getWorld().getPlayer().getY() + unknownLand.getWorld().getPlayer().getVelocity().y * delta);

            if (unknownLand.getWorld().getPlayer().canJump()) { // player can jump if is not jumping or
                // falling or dying
                jumpingPressed = true;
                jumpPressedTime = System.currentTimeMillis();
                canJump = jumpPressedTime + 300l;
                unknownLand.getWorld().getPlayer().setState(Player.State.JUMPING);
                unknownLand.getWorld().getPlayer().getVelocity().y = 200f;
            } else {
                if (jumpingPressed
                        && ((System.currentTimeMillis() - jumpPressedTime) >= 300l)) {
                    jumpingPressed = false;
                } else {
                    if (jumpingPressed) {
                        unknownLand.getWorld().getPlayer().getVelocity().y = 200f;
                    }
                }
            }
        } else {
            jumpingPressed = false;
        }


        this.unknownLand.renderWorld();


    }
}
