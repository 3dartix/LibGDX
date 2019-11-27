package com.mygdx.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.app.game.helpers.ObjectPool;

public class BonusController extends ObjectPool<Bonus> {

    @Override
    protected Bonus newObject() {
        return new Bonus();
    }

    public void setup(float x, float y, float vx, float vy){
        getActiveElement().activate(MathUtils.random(0,2), x, y, vx,vy);
    }

    public void render(SpriteBatch batch){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    public void update(float dt){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
