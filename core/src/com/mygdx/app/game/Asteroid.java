package com.mygdx.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.game.helpers.Poolable;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;

public class Asteroid implements Poolable {
    GameController gc;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private float rotationSpeed;
    private float scale;
    private boolean active;
    private int hpMax;
    private int hp;
    private Circle hitArea;
    private final float BASE_SIZE = 256f;
    private final float BASE_RADIUS = BASE_SIZE / 2f;

    public int getHpMax() {
        return hpMax;
    }
    public Vector2 getPosition() {
        return position;
    }
    public Circle getHitArea() {
        return hitArea;
    }
    public Vector2 getVelocity() {
        return velocity;
    }

    public Asteroid(GameController gc) {
        this.gc = gc;
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0,0);
        this.active = false;
        this.rotationSpeed = 0;
        this.scale = 0;
        this.hp = 0;
        this.hitArea = new Circle(0,0,0);
        this.texture = Assets.getInstance().getAtlas().findRegion("asteroid");
    }

    public boolean takeDamage(int amount){
        hp -= amount;
        if(hp <= 0){
            deactivate();
            if(scale > 0.25f){
                gc.getAsteroidController().setup(position.x,position.y,MathUtils.random(-80,80),MathUtils.random(-80,80),scale - 0.2f);
                gc.getAsteroidController().setup(position.x,position.y,MathUtils.random(-80,80),MathUtils.random(-80,80),scale - 0.2f);
                gc.getAsteroidController().setup(position.x,position.y,MathUtils.random(-80,80),MathUtils.random(-80,80),scale - 0.2f);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    //vx - скорость, vy - направление
    public void activate(float x, float y, float vx, float vy, float scale){
        this.position.set(x, y);
        this.velocity.set(vx,vy);
        this.hpMax = (int)(10 * scale);
        this.hp = this.hpMax;
        this.angle = MathUtils.random(0, 360f);
        this.hitArea.setPosition(position);
        this.rotationSpeed = MathUtils.random(-60f,60f);
        this.scale = scale;
        this.hitArea.setRadius(BASE_RADIUS * scale * 0.9f);
        this.active = true;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x - 128, position.y - 128, 128, 128, 256, 256, scale, scale, angle);
    }

    public void update(float dt){
        position.mulAdd(velocity,dt);
        angle += rotationSpeed * dt;

        if(position.x < - BASE_RADIUS * scale){
            position.x = ScreenManager.SCREEN_WIDTH + BASE_RADIUS * scale;
        }
        if(position.x > ScreenManager.SCREEN_WIDTH + BASE_RADIUS * scale){
            position.x = - BASE_RADIUS * scale;
        }
        if(position.y < - BASE_RADIUS * scale){
            position.y = ScreenManager.SCREEN_HEIGHT + BASE_RADIUS * scale;
        }
        if(position.y > ScreenManager.SCREEN_HEIGHT + BASE_RADIUS * scale ){
            position.y = - BASE_RADIUS * scale;
        }
        hitArea.setPosition(position);
    }
}
