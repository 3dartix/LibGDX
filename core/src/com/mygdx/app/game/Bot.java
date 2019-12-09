package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;
import com.mygdx.app.screen.utils.OptionsUtils;

public class Bot{
    private GameController gc;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private float acceleration = 700; //ускорение
    private float fireTimer;
    private Circle hitArea;
    private int money;
    private int hp;
    private int hpMax;
    //оружие
    private float firePeriod;
    private int damage;
    private float bulletSpeed;
    private int maxBullets;
    private int curBullets;
    private Vector3[] slots;

    private String type;

    private boolean isActive;

    private Sound shootSound;

    public Bot(GameController gc, float firePeriod, int damage, float bulletSpeed, int maxBullets, Vector3[] slots){
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH/2+200, ScreenManager.SCREEN_HEIGHT/2+200);
        this.velocity = new Vector2(0,0);
        this.angle = 0;
        this.hitArea = new Circle(position.x,position.y,26);
        this.hpMax = 100;
        this.hp = hpMax;
        this.money = 10000;
        //оружие
        this.firePeriod = firePeriod;
        this.damage = damage;
        this.bulletSpeed = bulletSpeed;
        this.maxBullets = maxBullets;
        this.curBullets = this.maxBullets;
        this.slots = slots;
        this.shootSound = Assets.getInstance().getAssetManager().get("audio/Shoot.mp3");

        this.isActive = true;
        this.type = "enemy";

    }

    public Circle getHitArea() {
        return hitArea;
    }

    public void fire() {
        if (curBullets > 0) {
            shootSound.play(0.2f);
            curBullets--;

            for (int i = 0; i < slots.length; i++) {
                float x, y, vx, vy;
                x = position.x + slots[i].x * MathUtils.cosDeg(angle + slots[i].y);
                y = position.y + slots[i].x * MathUtils.sinDeg(angle + slots[i].y);
                vx = velocity.x + bulletSpeed * MathUtils.cosDeg(angle + slots[i].z);
                vy = velocity.y + bulletSpeed * MathUtils.sinDeg(angle + slots[i].z);
                gc.getBulletController().setup(type, x, y, vx, vy, angle + slots[i].z);
            }
        }
    }

    public void render(SpriteBatch batch) {
        if(isActive)
        batch.draw(texture, position.x - 32, position.y - 32,32,32, 64,64,1,1,angle);
    }

    public void update(float dt){
        if(isActive){
            float dst = gc.getHero().getPosition().dst(position);

            //угол в зависимоти от вектора
            angle = velocity.angle();
            if(dst > 200 && dst < 700) {
                //получаем вектор между 2 координатами
                velocity.set(gc.getHero().getPosition()).sub(position);
                //нормализуем и задаем постоянную скорость
                velocity.nor().scl(150);
                position.mulAdd(velocity, dt);
            }
            if (dst < 500) {
                fireTimer += dt;
                tryToFire();
            }
            hitArea.setPosition(position.x, position.y);
        }
    }

    public void tryToFire(){
        if(fireTimer > firePeriod){
            fireTimer = 0.0f;
            fire();
        }
    }


    public void takeDamag(int amount){
        hp -= amount;
        if(hp < 0) {
            isActive = false;
        }
    }


}
