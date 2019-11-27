package com.mygdx.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.app.game.helpers.ObjectPool;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;

public class AsteroidController extends ObjectPool<Asteroid> {
    private GameController gc;

    @Override
    protected Asteroid newObject() {
        return new Asteroid(gc);
    }

    public AsteroidController(GameController gc, int amount){
        this.gc = gc;
//        TextureRegion[] a = new TextureRegion(Assets.getInstance().getAtlas().findRegion("asteroids64")).split(64, 64)[0];
//        this.bulletTexture = a[0];
        for (int i = 0; i < amount; i++) {
            float posX = MathUtils.random(0, ScreenManager.SCREEN_WIDTH);
            float posY = MathUtils.random(0, ScreenManager.SCREEN_HEIGHT);
            setup(posX, posY, (float)Math.cos(Math.toRadians(MathUtils.random(0,360)))*MathUtils.random(30,60), (float)Math.sin(Math.toRadians(MathUtils.random(0,360)))*MathUtils.random(30,60),1f); //создание астероида
        }
    }

    public void render(SpriteBatch batch){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    //vx - скорость, vy - направление
    public void setup(float x, float y, float vx, float vy, float scale){
        getActiveElement().activate(x, y, vx, vy, scale);
    }

    public void update(float dt){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
