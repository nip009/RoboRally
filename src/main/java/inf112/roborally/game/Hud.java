package inf112.roborally.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.roborally.game.objects.Player;


public class Hud {

    public Stage stage;
    private com.badlogic.gdx.utils.viewport.Viewport viewport;

    private Integer lives;
    private Integer cards;
    private Integer damage;
    private Integer testIncrement = 0;
    Label livesLabel;
    Label damageLabel;
    Label testIncrementLabel;

    public Hud(SpriteBatch sb, Player player){
        lives = player.getLives();
        damage = player.getDamage();
        cards = 0;


        viewport = new FitViewport(1920, 1080, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table hud = new Table();
        hud.top();
        hud.setFillParent(true);

        livesLabel = new Label("Lives: " + lives, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        damageLabel = new Label("Damage taken: " + damage, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        testIncrementLabel = new Label(String.format("IncrementingValue:%d", testIncrement), new Label.LabelStyle(new BitmapFont(), Color.WHITE));


        Texture buttonPic = new Texture(Gdx.files.internal("assets/robot/tvBot.png"));
        TextureRegion buttonTexture= new TextureRegion(buttonPic);
        TextureRegionDrawable buttonTextureDrawable = new TextureRegionDrawable(buttonTexture);
        ImageButton button = new ImageButton(buttonTextureDrawable);


        hud.add(livesLabel).width(100).padTop(10);
        hud.add(damageLabel).width(15).padTop(10);

        stage.addActor(hud);
        Table InteractiveHud = new Table();
        InteractiveHud.align(Align.topRight);
        InteractiveHud.setFillParent(true);
        InteractiveHud.add(button).top();

        stage.addActor(InteractiveHud);

        Gdx.input.setInputProcessor(stage);
        button.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("TEST");

            }

        });


    }

    public void update(Player player) {
        lives = player.getLives();
        damage = player.getDamage();

        livesLabel.setText("Lives: " + lives);
        damageLabel.setText("Damage taken: " + damage);
    }


}
