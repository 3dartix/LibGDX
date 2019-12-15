package com.mygdx.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.game.helpers.Counter;

public class Ship {
    protected GameController gc;
    protected Vector2 position;
    Vector2 velocity;
    Counter hp;
    protected TextureRegion texture;
    Circle hitArea;
    protected Vector2 tmpVector;
    protected float angle;
    protected float enginePower;
    float maxSpeed;
    protected Weapon currentWeapon;
    protected OwnerType ownerType;

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public boolean isAlive() {
        return hp.isAboveZero();
    }

    public float getAngle() {
        return angle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Circle getHitArea() {
        return hitArea;
    }

    public Ship(GameController gc, int hpMax) {
        this.gc = gc;
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.tmpVector = new Vector2();
        this.hp = new Counter(hpMax, true);
        this.hitArea = new Circle(0, 0, 28);
        this.maxSpeed = 1000.0f;
        this.enginePower = 700;
    }

    public void update(float dt) {
        if (velocity.len() > maxSpeed) {
            velocity.nor().scl(maxSpeed);
        }
        currentWeapon.update(dt);

        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
        float stopKoef = 1.0f - 2.0f * dt;
        if (stopKoef < 0.0f) {
            stopKoef = 0.0f;
        }
        velocity.scl(stopKoef);
        checkSpaceBorders();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 1, 1, angle);
    }

    public void changePosition(float x, float y) {
        position.set(x, y);
        hitArea.setPosition(x, y);
    }

    public void takeDamage(int amount) {
        hp.decrease(amount);
        System.out.println(hp.getCurrent());
    }

    public void checkSpaceBorders() {
        if (position.x < hitArea.radius) {
            position.x += GameController.SPACE_WIDTH;
        }
        if (position.x > GameController.SPACE_WIDTH - hitArea.radius) {
            position.x -= GameController.SPACE_WIDTH;
        }
        if (position.y < hitArea.radius) {
            position.y = GameController.SPACE_HEIGHT - hitArea.radius - 1;
        }
        if (position.y > GameController.SPACE_HEIGHT - hitArea.radius) {
            position.y = hitArea.radius + 1;
        }
    }

    public void accelerate(float dt) {
        velocity.x += (float) Math.cos(Math.toRadians(angle)) * enginePower * dt;
        velocity.y += (float) Math.sin(Math.toRadians(angle)) * enginePower * dt;
    }

    public void brake(float dt) {
        velocity.x -= (float) Math.cos(Math.toRadians(angle)) * enginePower * dt / 2.0f;
        velocity.y -= (float) Math.sin(Math.toRadians(angle)) * enginePower * dt / 2.0f;
    }

    public void rotate(float rotationSpeed, float dt) {
        angle += rotationSpeed * dt;
    }
}
