package inf112.roborally.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.roborally.game.RoboRallyGame;
import inf112.roborally.game.enums.GameState;
import inf112.roborally.game.enums.PlayerState;
import inf112.roborally.game.player.Player;
import inf112.roborally.game.player.ProgramCard;
import inf112.roborally.game.tools.AssMan;

import java.util.List;

public class Hud {

    private final RoboRallyGame game;
    public Stage stage;
    Group registerGui; // All register elements except locks
    Group handGui; // Cards in player hand. Needs a separate so the cards can be hidden during phases.
    private HandDisplay handDisplay;
    private RegisterDisplay registerDisplay;
    private PlayerStatusDisplay playerStatusDisplay;
    private Player player;
    private ImageButton submitButton;
    private ImageButton greySubmitButton;
    private ImageButton clearButton;
    private float scale = 0.4f;

    public Hud(final Player player, final RoboRallyGame game) {
        this.player = player;
        this.game = game;
        stage = new Stage(game.fixedViewPort, game.batch);
        Gdx.input.setInputProcessor(stage);
        stage.addListener(game.cameraListener);

        registerGui = new Group();
        // Lock are register elements, but need a separate group so the can be drawn on top of cards
        Group lockGui = new Group();
        handGui = new Group();
        ImageButton settings = new ImageButton(new TextureRegionDrawable(AssMan.manager.get(AssMan.BUTTON_SETTINGS)));
        settings.setPosition(1920 - settings.getWidth() - 50, 1080 - settings.getHeight() - 20);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.settingsScreen);
            }
        });

        stage.addActor(settings);
        stage.addActor(registerGui);
        stage.addActor(lockGui);
        stage.addActor(handGui);

        handDisplay = new HandDisplay(player, this);
        registerDisplay = new RegisterDisplay(player, registerGui, lockGui);
    }

    public void addPlayerStatusDisplay(List<Player> players) {
        playerStatusDisplay = new PlayerStatusDisplay(player, players, this);
    }


    private boolean createSubmitButton() {
        if (AssMan.manager.isLoaded(AssMan.BUTTON_SUBMIT.fileName)) {
            submitButton = new ImageButton(new TextureRegionDrawable(AssMan.manager.get(AssMan.BUTTON_SUBMIT)));
            submitButton.setSize(submitButton.getWidth() * scale, submitButton.getHeight() * scale);
            submitButton.setPosition((1920 / 2) + 7, 260);
            submitButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.getRegisters().isFull() && !player.outOfLives()) {
                        player.setPlayerState(PlayerState.READY);
                        setButtonTouchable(false);
                        if(RoboRallyGame.multiPlayer) {
                            StringBuilder allCards = new StringBuilder();
                            allCards.append("CARD ");
                            for (ProgramCard card : player.getRegisters().getAllCards()) {
                                allCards.append(game.playerName + " " + card.toString());
                                allCards.append("!");
                            }
                            allCards.deleteCharAt(allCards.length()-1); // Remove the last !
                            game.client.sendMessage(allCards.toString());
                        }
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private boolean createSubmitButtonGrey() {
        if (AssMan.manager.isLoaded(AssMan.BUTTON_SUBMIT_GREY.fileName)) {
            greySubmitButton = new ImageButton(new TextureRegionDrawable(AssMan.manager.get(AssMan.BUTTON_SUBMIT_GREY)));
            greySubmitButton.setSize(submitButton.getWidth(), submitButton.getHeight());
            greySubmitButton.setPosition(submitButton.getX(), submitButton.getY());
            greySubmitButton.addListener(new ClickListener());
            return true;
        } else {
            return false;
        }
    }

    private boolean createButtonClear() {
        if (AssMan.manager.isLoaded(AssMan.BUTTON_CLEAR.fileName)) {
            clearButton = new ImageButton(new TextureRegionDrawable(AssMan.manager.get(AssMan.BUTTON_CLEAR)));
            clearButton.setSize(clearButton.getWidth() * scale, clearButton.getHeight() * scale);
            clearButton.setPosition((1920 / 2) - (clearButton.getWidth()) - 7, 260);
            clearButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!player.outOfLives()) {
                        player.getRegisters().returnCards();
                        updateCardButtons();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public void createButtons() {
        if (createSubmitButton() && createSubmitButtonGrey() && createButtonClear()) {
            addButtonsToStage();
        }
    }

    private void addButtonsToStage() {
        if (greySubmitButton != null && submitButton != null && clearButton != null) {
            stage.addActor(greySubmitButton);
            stage.addActor(submitButton);
            stage.addActor(clearButton);
        }
    }

    public void draw() {
        if (playerStatusDisplay != null) playerStatusDisplay.update();
        // dont draw buttons for AI:
        if (((RoboRallyGame) Gdx.app.getApplicationListener()).AIvsAI){
            greySubmitButton.setVisible(false);
            submitButton.setVisible(false);
            clearButton.setVisible(false);
            stage.draw();
            return;
        }

        // update before draw
        submitButton.setVisible(player.getRegisters().isFull() && !player.isReady());
        greySubmitButton.setVisible(!submitButton.isVisible());
        clearButton.setVisible(!player.getRegisters().isEmpty() && !player.isReady());
        registerDisplay.update();

        // hide buttons if in a round:
        if (game.gameScreen != null && game.gameScreen.getBoardLogic().getState() != GameState.PICKING_CARDS){
            submitButton.setVisible(false);
            greySubmitButton.setVisible(false);
            clearButton.setVisible(false);
        }

        //To make GuiTest and BoardTest work
        if(game.gameScreen == null){
            stage.draw();
            return;
        }

        if(game.gameScreen.getBoardLogic().getState() != GameState.GAME_OVER) {
            stage.draw();
        }
    }

    private void clearAllCardButtons() {
        for (int i = 0; i < 4; i++) {
            for (Actor button : registerGui.getChildren()) {
                if (button instanceof ProgramCardButton) {
                    ((ProgramCardButton) button).dispose();
                    button.remove();
                }
            }
            for (Actor button : handGui.getChildren()) {
                if (button instanceof ProgramCardButton) {
                    ((ProgramCardButton) button).dispose();
                    button.remove();
                }
            }
        }
    }

    /**
     * Updates program cards in {@link Hud#handDisplay} and program cards in {@link Hud#registerDisplay} visually.
     */
    @SuppressWarnings("Duplicates")
    public void updateCardButtons() {
        clearAllCardButtons();
        handDisplay.updateCardButtons();
        registerDisplay.drawCardsInProgramRegister(this);
    }

    public void setButtonTouchable(boolean canTouch) {
        if (canTouch && player.isPoweredDown()) {
            registerDisplay.getPowerDown().setTouchable(Touchable.enabled);
            submitButton.setTouchable(Touchable.enabled);
            clearButton.setTouchable(Touchable.enabled);
        } else {
            registerDisplay.getPowerDown().setTouchable(Touchable.disabled);
            submitButton.setTouchable(Touchable.disabled);
            clearButton.setTouchable(Touchable.disabled);
        }
    }

    public void resetPowerDown() {
        if (registerDisplay.getPowerDown().isChecked()) registerDisplay.getPowerDown().toggle();
    }

    public PlayerStatusDisplay getPlayerStatusDisplay() {
        return playerStatusDisplay;
    }

    public HandDisplay getHandDisplay(){
        return handDisplay;
    }


    public void dispose(){
        stage.dispose();
        if (playerStatusDisplay != null) {
            playerStatusDisplay.dispose();
        }
    }
}