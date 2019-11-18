package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Texture texture;
    private Vector2 position;
    private Vector2 lastDisplacement;
    private float angle;
    private float velocity = 200;

    public Vector2 getLastDisplacement() {
        return lastDisplacement;
    }

    public Hero(){
        this.texture = new Texture("ship.png");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH/2, ScreenManager.SCREEN_HEIGHT/2);
        lastDisplacement = new Vector2(0,0);
        this.angle = 0;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32,32,32, 64,64,1,1,angle,0,0,64,64,false, false);
    }

    public void update(float dt){
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            angle += 180 * dt;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            angle -= 180 * dt;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            position.x += (float)Math.cos(Math.toRadians(angle)) * velocity * dt;
            position.y += (float)Math.sin(Math.toRadians(angle)) * velocity * dt;
            lastDisplacement.set((float)Math.cos(Math.toRadians(angle)) * velocity * dt,(float)Math.sin(Math.toRadians(angle)) * velocity * dt);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.x -= (float)Math.cos(Math.toRadians(angle)) * velocity * dt;
            position.y -= (float)Math.sin(Math.toRadians(angle)) * velocity * dt;
            lastDisplacement.set(-(float)Math.cos(Math.toRadians(angle)) * velocity * dt,-(float)Math.sin(Math.toRadians(angle)) * velocity * dt);
        } else {
            lastDisplacement.set(0,0);
        }

        if(position.x < 0){
            position.x = 0;
            lastDisplacement.set(0,0);
        }

        if(position.x > ScreenManager.SCREEN_WIDTH){
            position.x = ScreenManager.SCREEN_WIDTH;
            lastDisplacement.set(0,0);
        }

        if(position.y < 0){
            position.y = 0;
            lastDisplacement.set(0,0);
        }

        if(position.y > ScreenManager.SCREEN_HEIGHT){
            position.y = ScreenManager.SCREEN_HEIGHT;
            lastDisplacement.set(0,0);
        }
    }
}
