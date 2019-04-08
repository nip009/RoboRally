package inf112.roborally.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import inf112.roborally.game.Main;

public abstract class GameObject {
    public Position position;
    protected Sprite sprite;

    /**
     * Objects on the board that needs to be drawn, that are not on the TiledMap
     * It only has a position and needs a sprite
     * <p>
     * Constructor doesn't create a sprite for easier testing. Tests get Texture null pointer.
     *
     * @param x position x
     * @param y position y
     */
    public GameObject(int x, int y) {
        position = new Position(x, y);
    }

    public GameObject(Position position) {
        this.position = position;
    }


    public void updateSprite() {
        System.out.println("Position: " + position + "\n Sprite: " + sprite);
        sprite.setPosition(position.getX() * Main.PIXELS_PER_TILE, position.getY() * Main.PIXELS_PER_TILE);
    }


    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void move(int x, int y) {
        position.move(x, y);
    }

    public void moveToPosition(Position position) {
        this.position.move(position.getX(), position.getY());
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Vector3 getSpritePosition() {
        return new Vector3(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2, 0);
    }

    public void dispose(){
        sprite.getTexture().dispose();
    }
}
