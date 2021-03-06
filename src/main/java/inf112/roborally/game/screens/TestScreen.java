package inf112.roborally.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import inf112.roborally.game.RoboRallyGame;
import inf112.roborally.game.enums.Direction;
import inf112.roborally.game.player.ProgramCard;
import inf112.roborally.game.gui.Hud;
import inf112.roborally.game.player.Player;
import inf112.roborally.game.tools.AssMan;

import java.util.Stack;

public class TestScreen implements Screen {
    private final RoboRallyGame game;
    private Player player;
    private Sprite background;
    private Hud hud;
    private Stack<ProgramCard> stack;


    public TestScreen(RoboRallyGame game) {
        this.game = game;
        game.fixedViewPort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        background = new Sprite(AssMan.manager.get(AssMan.TEST_BACKGROUND));

        player = new Player("player", AssMan.getPlayerSkins()[0], Direction.SOUTH, null, null);
        stack = ProgramCard.makeProgramCardDeck();
        for (int i = 0; i < player.getCardLimit(); i++) {
            player.getHand().receiveCard(stack.pop());
        }

        hud = new Hud(player, game);
        hud.updateCardButtons();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(hud.stage);
    }

    @Override
    public void render(float v) {
        handleInput();
        player.updateSprite();

        float r = 0 / 255f;
        float g = 20 / 255f;
        float b = 15 / 255f;

        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.fixedCamera.combined);
        game.batch.begin();
        background.draw(game.batch);
        game.batch.end();

        hud.createButtons();
        hud.draw();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            game.setScreen(game.settingsScreen);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            player.takeDamage();
            player.updateSprite();
            System.out.println(player.getPlayerState());
            player.respawn();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            player.getRegisters().placeCard(0);
            hud.updateCardButtons();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            player.returnCards();
            hud.updateCardButtons();
        }
    }

    @Override
    public void resize(int w, int h) {
        game.fixedViewPort.update(w, h);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        hud.dispose();
        background.getTexture().dispose();
    }
}
