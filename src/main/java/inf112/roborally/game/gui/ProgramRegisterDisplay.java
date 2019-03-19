package inf112.roborally.game.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import inf112.roborally.game.board.ProgramRegisters;
import inf112.roborally.game.objects.Player;

import java.util.ArrayList;

public class ProgramRegisterDisplay {
    private final CardVisuals cardVisual;
    private Player player;
    private ProgramRegisters registers;
    private Sprite board;
    private Sprite lifeToken;
    private Sprite damageToken;
    private Sprite lockToken;
    private Sprite card;
    private Sprite wires;
    private ArrayList<TextureRegion> wireTextures;

    float scale = 0.51f;

    /**
     * Draws the program register of a given player.
     * Shows cards in the players register slots. If a register is locked a small lock icon will appear.
     * It also shows lives and damage.
     *
     * @param player
     */
    public ProgramRegisterDisplay(Player player) {
        this.player = player;
        registers = player.getRegisters();


        board = new Sprite(new Texture("assets/cards/programregisters.png"));
        board.setSize(board.getWidth() * scale, board.getHeight() * scale);
        board.setOriginCenter();
        board.setOriginBasedPosition(1920/2,board.getHeight()/2);

        wires = new Sprite(new Texture("assets/cards/wires.png"));
        wires.setSize(board.getWidth(), board.getHeight());
        wires.setPosition(board.getX(), board.getY());

        wireTextures = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            wireTextures.add(new TextureRegion(wires.getTexture(), 0,481*i,1024,481));
        }

        lifeToken = new Sprite(new Texture("assets/cards/tokens/lifeToken.png"));
        lifeToken.setSize(lifeToken.getWidth() * scale, lifeToken.getHeight() * scale);

        damageToken = new Sprite(new Texture("assets/cards/tokens/damageToken.png"));
        damageToken.setSize(damageToken.getWidth() * scale, damageToken.getHeight() * scale);

        lockToken = new Sprite(new Texture("assets/cards/tokens/lockToken.png"));
        lockToken.setSize(lockToken.getWidth() * scale, lockToken.getHeight() * scale);

        card = new Sprite();
        float cardScale = 0.77f;
        card.setSize(238 * cardScale * scale, 300 * cardScale * scale);

        cardVisual = new CardVisuals();
    }

    public void draw(SpriteBatch batch) {
        updateWires();
        board.draw(batch);
        wires.draw(batch);
        drawLifeTokens(batch);
        drawDamageTokens(batch);
        drawCardsInRegisters(batch);
        drawLocks(batch);
    }

    private void updateWires() {
        int wireIndex = 5 - registers.getUnlockedRegisters();
        wires.setRegion(wireTextures.get(wireIndex));

    }

    private void drawLifeTokens(SpriteBatch batch) {
        final float start = board.getX(); // start x
        final float space = 100; // space from one token to the next
        for (int i = player.getLives(); i > 0; i--) {
            lifeToken.setPosition(start * scale - space * scale * i, board.getHeight() - 215 * scale);
            lifeToken.draw(batch);
        }
    }

    private void drawDamageTokens(SpriteBatch batch) {
        final int start = 850; // start x
        final int space = 70; // space from one token to the next
        for (int i = 0; i < player.getDamage(); i++) {
            damageToken.setPosition(start * scale - space * scale * i, board.getHeight() - 125 * scale);
            damageToken.draw(batch);

            if (i > 8) return;
        }
    }

    private void drawLocks(SpriteBatch batch) {
        float startX = 830;
        for (int i = 4; i >= 0; i--) {
            if (registers.isLocked(4 - i)) {
                lockToken.setPosition(startX * scale - 204 * scale * i, board.getHeight() - 365 * scale);
                lockToken.draw(batch);
            }
        }
    }

    private void drawCardsInRegisters(SpriteBatch batch) {
        for (int i = 0; i < 5; i++) {
            if (player.getRegisters().getCardInRegister(i) != null) {
                card.setPosition(board.getX() + 19 * scale + 200 * scale * i, 10 * scale);
                card.setRegion(cardVisual.getRegion(registers.getCardInRegister(i)));
                card.draw(batch);
            }
        }
    }

    public void dispose() {
        System.out.println("disposing ProgramRegisterDisplay");
        board.getTexture().dispose();
        wires.getTexture().dispose();
        lifeToken.getTexture().dispose();
        damageToken.getTexture().dispose();
        lockToken.getTexture().dispose();
        cardVisual.dispose();
    }
}
