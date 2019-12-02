package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;

public class WorldRenderer {
    private GameController  gc;
    private SpriteBatch batch;
    private BitmapFont font32;
    private BitmapFont font16;
    private StringBuilder strBuilder;
    private StringBuilder strHeroHp;
    private StringBuilder strGameOver;

    public WorldRenderer(GameController gc, SpriteBatch batch) {
        this.gc = gc;
        this.batch = batch;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf", BitmapFont.class);
        this.font16 = Assets.getInstance().getAssetManager().get("fonts/font16.ttf", BitmapFont.class);
        this.strBuilder = new StringBuilder();
        this.strHeroHp = new StringBuilder();
        this.strGameOver = new StringBuilder();
    }

    public void render(){
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gc.getBackgrond().render(batch);
        gc.getAsteroidController().render(batch);
        gc.getHero().render(batch);
        gc.getBulletController().render(batch);
        gc.getParticleController().render(batch);
        gc.getBonusController().render(batch);
        gc.getHero().renderGUI(batch, font32);

        batch.end();
    }
}
