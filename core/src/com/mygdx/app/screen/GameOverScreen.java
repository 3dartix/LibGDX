package com.mygdx.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.app.game.Background;
import com.mygdx.app.game.GameController;
import com.mygdx.app.game.GlobalStatistic;
import com.mygdx.app.screen.utils.Assets;
import com.mygdx.app.screen.utils.OptionsUtils;

public class GameOverScreen extends AbstractScreen {
    private Background background;
    private BitmapFont font72;
    private BitmapFont font24;
    private Stage stage; //сцена c кнопками
    private StringBuilder strBuilder;

    public GameOverScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        this.background = new Background(null);
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        this.strBuilder = new StringBuilder();
    }


    public void update(float dt) {
        background.update(dt);
        //для реалигорвания на события
        stage.act(dt);

        if(Gdx.input.isButtonPressed(0) || Gdx.input.isButtonPressed(1)){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        //задаем фон и пишем текст
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        background.render(batch);

        showResult();
        batch.end();
        //рисуем сцену
        stage.draw();
    }

    public void showResult(){
        //выравниваем надпись по центру
        font72.draw(batch, "GAME OVER", 0, ScreenManager.HALF_SCREEN_HEIGHT*1.2f, ScreenManager.SCREEN_WIDTH, 1, false);

        strBuilder.clear();
        strBuilder.append("TOTAL SCORE: ").append(GlobalStatistic.getInstance().getTotalScore()).append("\n");
        strBuilder.append("TOTAL SHOTS: ").append(GlobalStatistic.getInstance().getTotalShots()).append("\n");
        font24.draw(batch, strBuilder, 0, ScreenManager.HALF_SCREEN_HEIGHT*0.9f,ScreenManager.SCREEN_WIDTH,1,false);
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
