package inf112.skeleton.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class RoboRallyGame extends Game {

    //MenuScreen
    //EndScreen
    //etc...

    public GameScreen gameScreen;

    private boolean finished;

    @Override
    public void create() {
        finished = false;

        Assets.load();
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
        //gameScreen.getPlayer().move(2);
    }

}
