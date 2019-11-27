package com.mygdx.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.game.helpers.Poolable;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;

public class Bonus implements Poolable {
    private TextureRegion mainTexture;
    private TextureRegion[] textures;
    private Vector2 position;
    private Vector2 velocity;
    private int hp;
    private int bullets;
    private int coins;
    private boolean active;
    private Circle hitArea;
    private float scale;
    private final float BASE_RADIUS = 32;

    public Vector2 getPosition() {
        return position;
    }

    public int getHp() {
        return hp;
    }

    public TextureRegion getMainTexture() {
        return mainTexture;
    }

    public int getBullets() {
        return bullets;
    }

    public int getMoney() {
        return coins;
    }

    public Circle getHitArea() {
        return hitArea;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Bonus(){
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0,0);
        this.active = false;
        this.hitArea = new Circle();
        this.scale = 0.8f;
        this.hitArea.radius = 32 * scale;

        this.textures = new TextureRegion[] {
                Assets.getInstance().getAtlas().findRegion("help"),
                Assets.getInstance().getAtlas().findRegion("bullet"),
                Assets.getInstance().getAtlas().findRegion("money")
        };
    }

    //vx - скорость, vy - направление
    public void activate(int type, float x, float y, float vx, float vy){
        System.out.println("Создан");
        position.set(x, y);
        hitArea.setPosition(x,y);
        velocity.set(vx,vy);
        active = true;
        System.out.println(type);
        switch (type) {
            case 0:
                mainTexture = textures[0];
                hp = 100;
                bullets = 0;
                coins = 0;
                break;
            case 1:
                mainTexture = textures[1];
                hp = 0;
                bullets = 100;
                coins = 0;
                break;
            case 2:
                mainTexture = textures[2];
                hp = 0;
                bullets = 0;
                coins = 100;
                break;
        }
    }

    public void render(SpriteBatch batch){
        batch.draw(mainTexture,position.x - 32 * scale, position.y - 32 * scale, 32 * scale, 32 * scale, 64, 64, scale, scale, 0);
    }

    public void update(float dt){
        position.mulAdd(velocity,dt);
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

    public void deactivate() {
        this.active = false;
    }

}
