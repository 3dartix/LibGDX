package com.mygdx.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.app.game.helpers.ObjectPool;

public class BulletController extends ObjectPool<Bullet> {
    private GameController gc;
    private Texture bulletTexture;
    //private float angle;

    @Override
    protected Bullet newObject() {
        return new Bullet();
    }

    public BulletController(GameController gc){
        this.gc = gc;
        this.bulletTexture = new Texture("bullet.png");
    }

    public void render(SpriteBatch batch){
        float scale = 0.3f;
        for (int i = 0; i < activeList.size(); i++) {
            Bullet b = activeList.get(i);
            batch.draw(bulletTexture,b.getPosition().x - 16, b.getPosition().y - 16 * scale, 16,16 * scale, 32, 32, 1, scale, b.getAngle(), 0,0,32,32, false, false);
            //batch.draw(texture, position.x - texture.getWidth()/2 * scale, position.y - texture.getHeight()/2 * scale,texture.getWidth()/2,texture.getHeight()/2, 256,256,scale,scale,angle,0,0,256,256,false, false);
        }
    }

    //vx - скорость, vy - направление
    public void setup(float x, float y, float vx, float vy){
        getActiveElement().activate(x, y, vx, vy, gc.getHero().getAngle());
    }

    public void update(float dt){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
