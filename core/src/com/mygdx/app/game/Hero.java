package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;
import com.mygdx.app.screen.utils.OptionsUtils;

public class Hero {
    public class Skill {
        private int level;
        private int maxLevel;
        private String title;
        private Runnable[] effects;
        private int[] cost;

        public int getLevel() {
            return level;
        }

        public String getTitle() {
            return title;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public int getCurrentLevelCost() {
            return cost[level - 1];
        }

        public Skill(String title, Runnable[] effects, int[] cost) {
            this.level = 1;
            this.title = title;
            this.effects = effects;
            this.cost = cost;
            this.maxLevel = effects.length;
            if (effects.length != cost.length) {
                throw new RuntimeException("Unable to create skill tree");
            }
        }

        public boolean isUpgradable() {
            return level < effects.length + 1;
        }

        public void upgrade() {
            effects[level - 1].run();
            level++;
        }
    }

    private Skill[] skills;
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
    private int hpMax;
    private Circle hitArea;
    private Shop shop;

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
    public boolean isAlive() {
        return hp > 0;
    }
    public int getScore() {
        return score;
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
        this.hpMax = 100;
        this.hp = hpMax;
        this.money = 10000;
        this.strBuilder = new StringBuilder();
        this.keysControl = new KeysControl(OptionsUtils.loadProperties(), keysControlPrefix);
        this.createSkillsTable();
        this.shop = new Shop(this);

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
    }

    public Skill[] getSkills() {
        return skills;
    }
    public Shop getShop() {
        return shop;
    }
    public boolean isMoneyEnough(int amount) {
        return money >= amount;
    }

    public void decreaseMoney(int amount) {
        money -= amount;
    }

    public void consume(PowerUp p) {
        switch (p.getType()) {
            case MEDKIT: // todo add max hp check
                hp += p.getPower();
                if (hp > hpMax) {
                    hp = hpMax;
                }
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
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            shop.setVisible(true);
        }

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
    }

    public void tryToFire(){
        if(fireTimer > currentWeapon.getFirePeriod()){
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
    }

    public void upgrade(int index) {
        int level = this.skills[index].level;
        this.skills[index].effects[level - 1].run();
        this.skills[index].level++;
    }

    public void createSkillsTable() {
        this.skills = new Skill[2];
        skills[0] = new Skill("HP",
                new Runnable[]{
                        () -> hpMax += 10,
                        () -> hpMax += 20,
                        () -> hpMax += 30,
                        () -> hpMax += 40,
                        () -> hpMax += 50,
                        () -> hpMax += 50
                },
                new int[]{
                        10,
                        20,
                        30,
                        50,
                        100,
                        500
                }
        );

        skills[1] = new Skill("WX-I",
                new Runnable[]{
                        () -> {
                            this.currentWeapon = new Weapon(
                                    gc, this, "Laser", 0.3f, 1, 600.0f, 320,
                                    new Vector3[]{
                                            new Vector3(24, 90, 10),
                                            new Vector3(24, 0, 0),
                                            new Vector3(24, -90, -10)
                                    }
                            );
                        },
                        () -> {
                            this.currentWeapon = new Weapon(
                                    gc, this, "Laser", 0.3f, 1, 600.0f, 320,
                                    new Vector3[]{
                                            new Vector3(24, 90, 20),
                                            new Vector3(24, 20, 0),
                                            new Vector3(24, -20, 0),
                                            new Vector3(24, -90, -20)
                                    }
                            );
                        },
                        () -> {
                            this.currentWeapon = new Weapon(
                                    gc, this, "Laser", 0.05f, 2, 600.0f, 32000,
                                    new Vector3[]{
                                            new Vector3(24, 90, 20),
                                            new Vector3(24, 20, 0),
                                            new Vector3(24, 0, 0),
                                            new Vector3(24, -20, 0),
                                            new Vector3(24, -90, -20)
                                    }
                            );
                        }
                },
                new int[]{
                        100,
                        200,
                        300
                }
        );
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
