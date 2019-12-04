package com.mygdx.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.app.game.Background;
import com.mygdx.app.game.Hero;
import com.mygdx.app.screen.utils.Assets;

public class GameOverScreen extends AbstractScreen {
    private Background background;
    private BitmapFont font72;
    private BitmapFont font48;
    private BitmapFont font24;
    private StringBuilder strBuilder;
    private Hero defeatedHero;

    public void setDefeatedHero(Hero defeatedHero) {
        this.defeatedHero = defeatedHero;
    }

    public GameOverScreen(SpriteBatch batch) {
        super(batch);
        this.strBuilder = new StringBuilder();
    }

    @Override
    public void show() {
        this.background = new Background(null);
        //this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        this.font48 = Assets.getInstance().getAssetManager().get("fonts/font48.ttf");
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
    }


    public void update(float dt) {
        background.update(dt);
        //для реалигорвания на события
        //stage.act(dt);

        if(Gdx.input.justTouched()){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.render(batch);
        font72.draw(batch, "Game Over", 0, 960, ScreenManager.SCREEN_WIDTH, Align.center, false);
        strBuilder.clear();
        strBuilder.append("Hero Score: ").append(defeatedHero.getScore()).append("\n");
        strBuilder.append("Hero Score (copy): ").append(defeatedHero.getScore()).append("\n");
        font48.draw(batch, strBuilder, 0, 600, ScreenManager.SCREEN_WIDTH, Align.center, false);
        font24.draw(batch, "Tap screen to return to main menu...", 0, 40, ScreenManager.SCREEN_WIDTH, Align.center, false);
        batch.end();
    }

    public void showResult(){
        //выравниваем надпись по центру
        font72.draw(batch, "GAME OVER", 0, ScreenManager.HALF_SCREEN_HEIGHT*1.2f, ScreenManager.SCREEN_WIDTH, 1, false);
        font24.draw(batch, "Tap screen to return to main menu...", 0, 40, ScreenManager.SCREEN_WIDTH, Align.center, false);
        strBuilder.clear();
        //strBuilder.append("TOTAL SCORE: ").append(GlobalStatistic.getInstance().getTotalScore()).append("\n");
        //strBuilder.append("TOTAL SHOTS: ").append(GlobalStatistic.getInstance().getTotalShots()).append("\n");
        font24.draw(batch, strBuilder, 0, ScreenManager.HALF_SCREEN_HEIGHT*0.9f,ScreenManager.SCREEN_WIDTH,1,false);
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
