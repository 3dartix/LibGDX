package com.mygdx.app.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.game.helpers.Poolable;
import com.mygdx.app.screen.ScreenManager;

public class Bullet implements Poolable {
    private GameController gc;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private float angle; // угол повората на момент создания

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
    public float getAngle() {
        return angle;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Bullet(GameController gc) {
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0,0);
        this.active = false;
        this.gc = gc;
    }

    public void deactivate() {
        active = false;
    }

    //vx - скорость, vy - направление
    public void activate(float x, float y, float vx, float vy, float angle){
        position.set(x, y);
        velocity.set(vx,vy);
        active = true;
        this.angle = angle;
    }

    public void update(float dt){
        position.mulAdd(velocity,dt);
        //если мы залетели за экран
        if (position.x < 0.0f || position.x > GameController.SPACE_WIDTH || position.y < 0.0f || position.y > GameController.SPACE_WIDTH) {
            deactivate();
        }
        //particleUpdate(dt);
    }

    public void particleUpdate(float dt){
        gc.getParticleController().setup(
                position.x + MathUtils.random(-4, 4), position.y + MathUtils.random(-4, 4),
                velocity.x * -0.3f + MathUtils.random(-20, 20), velocity.y * -0.3f + MathUtils.random(-20, 20),
                0.5f,
                1.2f, 0.2f,
                1.0f, 0.5f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 0.0f
        );
    }
}
