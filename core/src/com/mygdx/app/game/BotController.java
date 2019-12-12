package com.mygdx.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.app.game.helpers.ObjectPool;

public class BotController extends ObjectPool<Bot> {
    private GameController gc;

    @Override
    protected Bot newObject() {
        return new Bot(gc);
    }

    public BotController(GameController gc){
        this.gc = gc;
//        for (int i = 0; i < amount; i++) {
//            setup(MathUtils.random(0, GameController.SPACE_WIDTH), MathUtils.random(0, GameController.SPACE_HEIGHT));
//        }
    }


    public void render(SpriteBatch batch){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    //vx - скорость, vy - направление
    public void setup(float x, float y, float probability){
        if (MathUtils.random() <= probability) {
            getActiveElement().activate(x, y);
        }
    }

    public void update(float dt){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
