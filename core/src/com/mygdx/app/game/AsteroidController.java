package com.mygdx.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.app.game.helpers.ObjectPool;
import com.mygdx.app.screen.ScreenManager;

public class AsteroidController extends ObjectPool<Asteroid> {
    private GameController gc;
    private Texture bulletTexture;

    @Override
    protected Asteroid newObject() {
        return new Asteroid();
    }

    public AsteroidController(GameController gc, int amount){
        this.gc = gc;
        this.bulletTexture = new Texture("asteroid.png");
        for (int i = 0; i < amount; i++) {
            float posX = MathUtils.random(0, ScreenManager.SCREEN_WIDTH);
            float posY = MathUtils.random(0, ScreenManager.SCREEN_HEIGHT);
            setup(posX, posY, (float)Math.cos(Math.toRadians(MathUtils.random(0,360)))*MathUtils.random(30,60), (float)Math.sin(Math.toRadians(MathUtils.random(0,360)))*MathUtils.random(30,60)); //создание астероида
        }
    }

    public void render(SpriteBatch batch){
        for (int i = 0; i < activeList.size(); i++) {
            Asteroid b = activeList.get(i);
            batch.draw(bulletTexture, b.getPosition().x - 128 * b.getScale(), b.getPosition().y - 128 * b.getScale(),128,128, 256,256,b.getScale(),b.getScale(),b.getAngel(),0,0,256,256,false, false);
        }
    }

    //vx - скорость, vy - направление
    public void setup(float x, float y, float vx, float vy){
        getActiveElement().activate(x, y, vx, vy);
    }

    public void update(float dt){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
