package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

public class Hero {
    private GameController gc;
    private TextureRegion texture;
    private KeysControl keysControl;
    private Vector2 position;
    private Vector2 velocity;
    //private Vector2 lastDisplacement;
    private float angle;
    private float acceleration = 700; //ускорение
    private float fireTimer;
    private int score;
    private int scoreView;
    private StringBuilder strBuilder;
    private Weapon currentWeapon;
    private int money;
    private int hp;
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
    public Circle getHitArea() {
        return hitArea;
    }
    public Vector2 getPosition() {
        return position;
    }

    public int getScoreView() {
        return scoreView;
    }

    public Hero(GameController gc, String keysControlPrefix){
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH/2, ScreenManager.SCREEN_HEIGHT/2);
        this.velocity = new Vector2(0,0);
        this.angle = 0;
        this.hitArea = new Circle(position.x,position.y,26);
        this.hp = 100;
        this.money = 0;
        this.strBuilder = new StringBuilder();
        this.keysControl = new KeysControl(OptionsUtils.loadProperties(), keysControlPrefix);

        this.currentWeapon = new Weapon(
                gc, this, "Laser", 0.2f, 1, 600, 100,
                new Vector3[]{
                        new Vector3(28,0, 0), // нос
                        new Vector3(28,90, 20), // бок1
                        new Vector3(28,-90, -20), // бок2
                }
        );
    }

    public void takeDamag(int amount){
        hp -= amount;
        if(hp <= 0){
            //игра окончена
        }
    }

    public void consume(PowerUp p) {
        switch (p.getType()) {
            case MEDKIT: // todo add max hp check
                hp += p.getPower();
                break;
            case AMMOS:
                currentWeapon.addAmmos(p.getPower());
                break;
            case MONEY:
                money += p.getPower();
                break;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32,32,32, 64,64,1,1,angle);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font){
        strBuilder.clear();
        strBuilder.append("SCORE: ").append(scoreView).append("\n");
        strBuilder.append("HP: ").append(hp).append("\n");
        strBuilder.append("Bullets: ").append(currentWeapon.getCurBullets()).append("\n");
        strBuilder.append("Coins: ").append(money);
        font.draw(batch, strBuilder, ScreenManager.SCREEN_WIDTH * 3 / 100, ScreenManager.SCREEN_HEIGHT * 97 / 100);
    }

    public void update(float dt){
        fireTimer += dt;
        updateScore(dt);

        if(Gdx.input.isKeyPressed(keysControl.fire)){
            tryToFire();
        }
        if(Gdx.input.isKeyPressed(keysControl.left)){
            angle += 180 * dt;
        }
        if(Gdx.input.isKeyPressed(keysControl.right)){
            angle -= 180 * dt;
        }
        if(Gdx.input.isKeyPressed(keysControl.forward)){
            velocity.x += (float)Math.cos(Math.toRadians(angle)) * acceleration * dt;
            velocity.y += (float)Math.sin(Math.toRadians(angle)) * acceleration * dt;
        } else if (Gdx.input.isKeyPressed(keysControl.backward)) {
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
        if(velocity.len() > 50) {
            float bx, by; //хвост кор.
            bx = position.x - 28 * (float)Math.cos(Math.toRadians(angle));
            by = position.y - 28 * (float)Math.sin(Math.toRadians(angle));
            for (int i = 0; i < 5; i++) {
                gc.getParticleController().setup(
                        bx + MathUtils.random(-4, 4), by + MathUtils.random(-4, 4),
                        velocity.x * -0.3f + MathUtils.random(-20, 20), velocity.y * -0.3f + MathUtils.random(-20, 20),
                        0.5f,
                        1.2f, 0.2f,
                        1.0f, 0.5f, 0.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 0.0f
                );
            }
        }

        hitArea.setPosition(position.x, position.y);
        checkSpaceBorder();

        if(hp < 0){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER);
        }
    }

    public void tryToFire(){
        if(fireTimer > currentWeapon.getFirePeriod()){
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
    }

    public void checkSpaceBorder(){
        if(position.x < hitArea.radius){
            position.x = hitArea.radius;
            velocity.x *= -1; //меняем вектор в другую сторону (отскок)
        }
        if(position.x > ScreenManager.SCREEN_WIDTH - hitArea.radius){
            position.x = ScreenManager.SCREEN_WIDTH - hitArea.radius;
            velocity.x *= -1; //меняем вектор в другую сторону (отскок)
        }
        if(position.y < hitArea.radius){
            position.y = hitArea.radius;
            velocity.y *= -1; //меняем вектор в другую сторону (отскок)
        }
        if(position.y > ScreenManager.SCREEN_HEIGHT - hitArea.radius){
            position.y = ScreenManager.SCREEN_HEIGHT - hitArea.radius;
            velocity.y *= -1; //меняем вектор в другую сторону (отскок)
        }
    }

    public void updateScore(float dt){
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
    }

}
