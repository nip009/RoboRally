package inf112.roborally.game.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import inf112.roborally.game.RoboRallyGame;
import inf112.roborally.game.objects.Player;

public class Hud {

    private CardsInHandDisplay cardsInHandDisplay;
    private ProgramRegisterDisplay programRegisterDisplay;

    public Hud(final Player player, RoboRallyGame game) {
        cardsInHandDisplay = new CardsInHandDisplay(player, new Stage(game.fixedViewPort, game.batch));
        programRegisterDisplay = new ProgramRegisterDisplay(player);
    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        programRegisterDisplay.draw(batch);
        batch.end();
        cardsInHandDisplay.stage.draw();
    }

    public CardsInHandDisplay getCardsInHandDisplay(){
        return cardsInHandDisplay;
    }

    public void dispose(){
        System.out.println("disposing hud");
        cardsInHandDisplay.dispose();
        programRegisterDisplay.dispose();
    }
}
