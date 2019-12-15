package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;
import com.mygdx.app.screen.utils.OptionsUtils;

public class Hero extends Ship{
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
    private TextureRegion starTexture;
    private Skill[] skills;
    private KeysControl keysControl;

    private int score;
    private int scoreView;
    private int money;

    private Shop shop;

    private StringBuilder tmpStr;
    private float objectCaptureRadius;

    public float getObjectCaptureRadius() {
        return objectCaptureRadius;
    }

    public void addScore(int amount){
        score += amount;
    }
//    public boolean isAlive() {
//        return hp.isAboveZero();
//    }
    public int getScore() {
        return score;
    }

    public int getScoreView() {
        return scoreView;
    }

    public Hero(GameController gc, String keysControlPrefix){
        super(gc,100);
        this.gc = gc;
        this.starTexture = Assets.getInstance().getAtlas().findRegion("star16");
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.changePosition(GameController.SPACE_WIDTH/2, GameController.SPACE_HEIGHT/2);
        this.velocity = new Vector2(0,0);
        this.angle = 0;
        //this.hitArea = new Circle(position.x,position.y,26); 26!!!!!!!!!!!!1
        this.money = 10000;
        this.keysControl = new KeysControl(OptionsUtils.loadProperties(), keysControlPrefix);
        this.createSkillsTable();
        this.shop = new Shop(this);
        this.tmpVector = new Vector2(0,0);
        this.objectCaptureRadius = 200f;
        this.tmpStr = new StringBuilder();
        this.ownerType = OwnerType.PLAYER;

        this.currentWeapon = new Weapon(
                gc, this, "Laser", 0.2f, 1, 1, 320, 600, 100,
                new Vector3[]{
                        new Vector3(28,0, 0), // нос
                        new Vector3(28,90, 20), // бок1
                        new Vector3(28,-90, -20), // бок2
                }
        );
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
                tmpStr.setLength(0);
                tmpStr.append("HP +").append(hp.increase(p.getPower()));
                gc.getInfoController().setup(p.getPosition().x, p.getPosition().y, tmpStr, Color.GREEN);
                break;
            case AMMOS:
                tmpStr.setLength(0);
                tmpStr.append("AMMOS +").append(p.getPower());
                gc.getInfoController().setup(p.getPosition().x, p.getPosition().y, tmpStr, Color.RED);
                currentWeapon.addAmmos(p.getPower());
                break;
            case MONEY:
                tmpStr.setLength(0);
                tmpStr.append("MONEY +").append(p.getPower());
                gc.getInfoController().setup(p.getPosition().x, p.getPosition().y, tmpStr, Color.YELLOW);
                money += p.getPower();
                break;
        }
    }


    public void renderGUI(SpriteBatch batch, BitmapFont font){
        tmpStr.setLength(0);
        tmpStr.append("SCORE: ").append(scoreView).append("\n");
        tmpStr.append("HP: ").append(hp.getCurrent()).append(" / ").append(hp.getMax()).append("\n");
        tmpStr.append("Bullets: ").append(currentWeapon.getCurBullets()).append("\n");
        tmpStr.append("Coins: ").append(money);
        font.draw(batch, tmpStr, ScreenManager.SCREEN_WIDTH * 3 / 100, ScreenManager.SCREEN_HEIGHT * 97 / 100);

        //миникарта
        int mapX = 1700;
        int mapY = 900;

        batch.setColor(Color.GREEN);
        batch.draw(starTexture, mapX - 24, mapY - 24, 48, 48);

        {
            for (int i = 0; i < gc.getBotController().getActiveList().size(); i++) {
                Bot b = gc.getBotController().getActiveList().get(i);
                
                float dst = position.dst(b.getPosition());
                if (dst < 3000.0f) {
                    tmpVector.set(b.getPosition()).sub(this.position);
                    tmpVector.scl(160.0f / 3000.0f);
                    batch.setColor(Color.PURPLE);
                    batch.draw(starTexture, mapX + tmpVector.x - 16, mapY + tmpVector.y - 16, 32, 32);
                }
            }

        }

        batch.setColor(Color.RED);
        for (int i = 0; i < gc.getAsteroidController().getActiveList().size(); i++) {
            Asteroid a = gc.getAsteroidController().getActiveList().get(i);
            float dst = position.dst(a.getPosition());
            if (dst < 3000.0f) {
                //в tmp кладем позицию астероида и вычитаем позицию игрока, т.е. определяем вектор от центра к астероиду
                tmpVector.set(a.getPosition()).sub(this.position);
                // масштабируем этот вектор из реального расстояния на расстояние карты
                tmpVector.scl(160.0f / 3000.0f);
                float pointScale = a.getScale() * 2.0f;
                batch.draw(starTexture, mapX + tmpVector.x - 8, mapY + tmpVector.y - 8, 8, 8,16,16,pointScale,pointScale,0.0f);
            }
        }
        batch.setColor(Color.WHITE);
        for (int i = 0; i < 120; i++) {
            batch.draw(starTexture, mapX + 160.0f * MathUtils.cosDeg(360.0f / 120.0f * i) - 8, mapY + 160.0f * MathUtils.sinDeg(360.0f / 120.0f * i) - 8);
        }
    }

    public void update(float dt){
        super.update(dt);
        updateScore(dt);

        if(Gdx.input.isKeyPressed(keysControl.fire)){
            currentWeapon.tryToFire();
        }
        if(Gdx.input.isKeyPressed(keysControl.left)){
            rotate(180,dt);
            //angle += 180 * dt;
        }
        if(Gdx.input.isKeyPressed(keysControl.right)){
            rotate(-180,dt);
            //angle -= 180 * dt;
        }

        if(Gdx.input.isKeyPressed(keysControl.forward)){
            accelerate(dt);
        } else if (Gdx.input.isKeyPressed(keysControl.backward)) {
            brake(dt);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            shop.setVisible(true);
        }


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

        //hitArea.setPosition(position.x, position.y); !!!!!!!!!!!
        //checkSpaceBorder();
    }

//    public void upgrade(int index) {
//        int level = this.skills[index].level;
//        this.skills[index].effects[level - 1].run();
//        this.skills[index].level++;
//    }

    public void createSkillsTable() {
        this.skills = new Skill[2];
        skills[0] = new Skill("HP",
                new Runnable[]{
                        () -> hp.increaseMax(10),
                        () -> hp.increaseMax(20),
                        () -> hp.increaseMax(30),
                        () -> hp.increaseMax(40),
                        () -> hp.increaseMax(50),
                        () -> hp.increaseMax(60)
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
                                    gc, this, "Laser", 0.3f, 1,1, 400, 600.0f, 320,
                                    new Vector3[]{
                                            new Vector3(24, 90, 10),
                                            new Vector3(24, 0, 0),
                                            new Vector3(24, -90, -10)
                                    }
                            );
                        },
                        () -> {
                            this.currentWeapon = new Weapon(
                                    gc, this, "Laser", 0.3f, 1, 2, 360,600.0f, 320,
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
                                    gc, this, "Laser", 0.05f, 2,4, 240, 600.0f, 32000,
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
