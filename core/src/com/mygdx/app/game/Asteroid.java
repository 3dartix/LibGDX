package com.mygdx.app.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.game.helpers.Poolable;
import com.mygdx.app.screen.ScreenManager;

public class Asteroid implements Poolable {
    private Vector2 position;
    private Vector2 velocity;

    private float angel;
    private float angelVeloscity;

    private float scale;

    private boolean active;
    private int life;


    public float getScale() {
        return scale;
    }

    //радиус для проверки столкновений с пулями
    public float getRad() {
        return 128 * scale;
    }

    public void setLifeSub(){
        life -= 1;
        if(life <= 0){
            deactivate();
        }
    }

    public float getAngel() {
        return angel;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Asteroid() {
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0,0);
        this.active = false;
        this.angel = 0;

        if(MathUtils.random(0,100) < 50){
            this.angelVeloscity = MathUtils.random(5f,50f);
        } else {
            this.angelVeloscity = MathUtils.random(-5f,-50f);
        }

        this.scale = 1;// MathUtils.random(0.4f,0.9f);
        life = 10;
    }

    public void deactivate() {
        active = false;
    }

    //vx - скорость, vy - направление
    public void activate(float x, float y, float vx, float vy){
        position.set(x, y);
        velocity.set(vx,vy);
        active = true;
    }


    public void update(float dt){
        position.mulAdd(velocity,dt);
        angel += angelVeloscity * dt;
        //если мы залетели за экран
        //if (position.x < 0.0f || position.x > ScreenManager.SCREEN_WIDTH || position.y < 0.0f || position.y > ScreenManager.SCREEN_HEIGHT) {
            //deactivate();
        //}
        if(position.x < - 128){
            position.x = ScreenManager.SCREEN_WIDTH + 128;
        }
        if(position.x - 128 > ScreenManager.SCREEN_WIDTH){
            position.x = - 128;
        }
        if(position.y < - 128){
            position.y = ScreenManager.SCREEN_HEIGHT + 128;
        }
        if(position.y - 128 > ScreenManager.SCREEN_HEIGHT){
            position.y = - 128;
        }
    }
}
