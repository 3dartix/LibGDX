package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.screen.ScreenManager;

public class Hero {
    private GameController gc;
    private Texture texture;
    private Vector2 position;
    private Vector2 velocity;
    //private Vector2 lastDisplacement;
    private float angle;
    private float acceleration = 700; //ускорение
    private float fireTimer;

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getAngle() {
        return angle;
    }

    public Hero(GameController gc){
        this.gc = gc;
        this.texture = new Texture("ship.png");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH/2, ScreenManager.SCREEN_HEIGHT/2);
        this.velocity = new Vector2(0,0);
        this.angle = 0;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32,32,32, 64,64,1,1,angle,0,0,64,64,false, false);
    }

    public void update(float dt){
        fireTimer += dt;
        if(Gdx.input.isKeyPressed(Input.Keys.P)){
            if(fireTimer > 0.1f){
                fireTimer = 0.0f;

                //координаты ор.1
                float weaponX1 = (position.x) +(25 * MathUtils.cos((float)Math.toRadians(angle + 90)));
                float weaponY1 =(position.y)+(25* MathUtils.sin((float)Math.toRadians(angle +90)));
                //координаты ор.2
                float weaponX2 = (position.x) +(25 * MathUtils.cos((float)Math.toRadians(angle - 90)));
                float weaponY2 =(position.y)+(25* MathUtils.sin((float)Math.toRadians(angle - 90)));

                gc.getBulletController().setup(weaponX1, weaponY1,(float)Math.cos(Math.toRadians(angle)) * 600 + velocity.x, (float)Math.sin(Math.toRadians(angle))*600 + velocity.y);
                gc.getBulletController().setup(weaponX2, weaponY2,(float)Math.cos(Math.toRadians(angle)) * 600 + velocity.x, (float)Math.sin(Math.toRadians(angle))*600 + velocity.y);
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            angle += 360 * dt;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            angle -= 360 * dt;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            velocity.x += (float)Math.cos(Math.toRadians(angle)) * acceleration * dt;
            velocity.y += (float)Math.sin(Math.toRadians(angle)) * acceleration * dt;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.x -= (float)Math.cos(Math.toRadians(angle)) * acceleration * dt;
            velocity.y -= (float)Math.sin(Math.toRadians(angle)) * acceleration * dt;
        }

        //position.x += velocity.x * dt;
        //position.y += velocity.y * dt;
        position.mulAdd(velocity, dt);

        float stopKoef = 1 - 2 * dt;
        if(stopKoef < 0) {
            stopKoef = 0;
        }
        velocity.scl(stopKoef);

        if(position.x < 0){
            position.x = 0;
            velocity.x *= -1; //меняем вектор в другую сторону (отскок)
        }

        if(position.x > ScreenManager.SCREEN_WIDTH){
            position.x = ScreenManager.SCREEN_WIDTH;
            velocity.x *= -1; //меняем вектор в другую сторону (отскок)
        }

        if(position.y < 0){
            position.y = 0;
            velocity.y *= -1; //меняем вектор в другую сторону (отскок)
        }

        if(position.y > ScreenManager.SCREEN_HEIGHT){
            position.y = ScreenManager.SCREEN_HEIGHT;
            velocity.y *= -1; //меняем вектор в другую сторону (отскок)
        }
    }
}
