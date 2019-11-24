package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;

public class Hero {
    private GameController gc;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    //private Vector2 lastDisplacement;
    private float angle;
    private float acceleration = 700; //ускорение
    private float fireTimer;
    private int score;
    private int scoreView;

    private int hp;
    private String hpView;
    private Circle hitArea;

    public Vector2 getVelocity() {
        return velocity;
    }
    public float getAngle() {
        return angle;
    }
    public void addScore(int amount){
        score += amount;
    }
    public int getScoreView() {
        return scoreView;
    }
    public Circle getHitArea() {
        return hitArea;
    }
    public Vector2 getPosition() {
        return position;
    }
    public int getHp() {
        return hp;
    }
    public String getHpView() {
        hpView = "";
        for (int i = 0; i < hp; i++) {
            hpView = hpView + "|";
        }
        return hpView;
    }

    public Hero(GameController gc){
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH/2, ScreenManager.SCREEN_HEIGHT/2);
        this.velocity = new Vector2(0,0);
        this.angle = 0;
        this.hitArea = new Circle(position.x,position.y,texture.getRegionHeight()/2);
        this.hp = 10;
    }

    public void takeDamag(int amount){
        hp -= amount;
        if(hp <= 0){
            //игра окончена
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32,32,32, 64,64,1,1,angle);
    }

    public void update(float dt){
        fireTimer += dt;

        if (scoreView < score) {
            float scoreSpeed = (score - scoreView) / 2.0f;
            if (scoreSpeed < 2000.0f) {
                scoreSpeed = 2000.0f;
            }
            scoreView += scoreSpeed * dt;
            if (scoreView > score) {
                scoreView = score;
            }
        }


        if(Gdx.input.isKeyPressed(Input.Keys.P)){
            if(fireTimer > 0.05f){
                fireTimer = 0.0f;

                //координаты ор.1
                float weaponX1 = (position.x) +(20 * MathUtils.cos((float)Math.toRadians(angle + 90)));
                float weaponY1 =(position.y)+(20* MathUtils.sin((float)Math.toRadians(angle +90)));
                //координаты ор.2
                float weaponX2 = (position.x) +(20 * MathUtils.cos((float)Math.toRadians(angle - 90)));
                float weaponY2 =(position.y)+(20* MathUtils.sin((float)Math.toRadians(angle - 90)));

                gc.getBulletController().setup(weaponX1, weaponY1,(float)Math.cos(Math.toRadians(angle)) * 800, (float)Math.sin(Math.toRadians(angle))*800);
                gc.getBulletController().setup(weaponX2, weaponY2,(float)Math.cos(Math.toRadians(angle)) * 800, (float)Math.sin(Math.toRadians(angle))*800);
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            angle += 180 * dt;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            angle -= 180 * dt;
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

        hitArea.setPosition(position.x, position.y);
    }
}
