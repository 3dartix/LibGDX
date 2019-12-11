package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;

import static java.lang.Math.*;

public class GameController {
    public static final int SPACE_WIDTH = 9600;
    public static final int SPACE_HEIGHT = 5400;

    private Music music;
    private Background backgrond;
    private BulletController bulletController;
    private ParticleController particleController;
    private Hero hero;
    private AsteroidController asteroidController;
    private PowerUpsController powerUpsController;
    private InfoController infoController;
    private Vector2 tmpVec; // Вектор для столкновений
    private Stage stage;
    private int level;
    private StringBuilder tmpStr;

    private Bot bot;

    private float msgTimer;
    private String msg;

    public GameController(SpriteBatch batch) {
        this.music = Assets.getInstance().getAssetManager().get("audio/Music.mp3");
        this.music.setLooping(true);
        music.play();

        this.backgrond = new Background(this);
        this.hero = new Hero(this, "PLAYER1");
        this.asteroidController = new AsteroidController(this);
        this.bulletController = new BulletController(this);
        this.tmpVec = new Vector2(0,0);
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.particleController = new ParticleController();
        this.powerUpsController = new PowerUpsController(this);
        this.infoController = new InfoController();
        this.stage.addActor(hero.getShop());
        this.tmpStr = new StringBuilder();
        Gdx.input.setInputProcessor(stage);
        generateTwoBigAsteroids();

        this.bot = new Bot(this);

        this.msg = "Level 1";
        this.msgTimer = 3.0f;
    }

    public int getLevel() {
        return level;
    }
    public AsteroidController getAsteroidController() {
        return asteroidController;
    }
    public BulletController getBulletController() {
        return bulletController;
    }
    public Background getBackgrond() {
        return backgrond;
    }
    public Stage getStage() {
        return stage;
    }
    public PowerUpsController getBonusController() {
        return powerUpsController;
    }
    public ParticleController getParticleController() {
        return particleController;
    }
    public Hero getHero() {
        return hero;
    }
    public Bot getBot() {
        return bot;
    }
    public float getMsgTimer() {
        return msgTimer;
    }
    public String getMsg() {
        return msg;
    }
    public InfoController getInfoController() {
        return infoController;
    }

    public void update(float dt){
        msgTimer -= dt;
        backgrond.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        particleController.update(dt);
        powerUpsController.update(dt);
        infoController.update(dt);
        bot.update(dt);
        checkCollisions(dt);

        if(!hero.isAlive()) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER, hero);
        }

        if (asteroidController.getActiveList().size() == 0) {
            level++;
            generateTwoBigAsteroids();
            this.msg = "Level " + (level +1);
            this.msgTimer = 3.0f;
        }

        stage.act(dt);
    }

    public void generateTwoBigAsteroids() {
        for (int i = 0; i < 200; i++) {
            this.asteroidController.setup(MathUtils.random(0, SPACE_WIDTH), MathUtils.random(0, SPACE_HEIGHT),
                    MathUtils.random(-150.0f, 150.0f), MathUtils.random(-150.0f, 150.0f), 0.6f);
        }
    }

    public void checkCollisions(float dt){
        // столкн астр и кор
        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid a = asteroidController.getActiveList().get(i);
            if(CheckPhysicHit(hero, a)) {
                hero.takeDamage(2);
                if(a.takeDamage(2)){
                    hero.addScore(a.getHpMax()* 10);
                    continue; // если астероид столкнулся и уничтожен, то он не может столкнуться с кем-то еще
                };
            }
            //столкновение с ботом
            if(CheckPhysicHit(bot, a)) {
                a.takeDamage(2);
            }
        }

        // пули
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);
                if(a.getHitArea().contains(b.getPosition())){

                    particleController.getEffectBuilder().bulletCollideWithAsteroid(b.getPosition(), b.getVelocity());

                    b.deactivate();
                    if(a.takeDamage(b.getDamage())){
                        if(b.getOwner().ownerType == OwnerType.PLAYER){
                            ((Hero)b.getOwner()).addScore(a.getHpMax()* 100);
                            //GlobalStatistic.getInstance().addTotalScore(a.getHpMax()* 100);
                            // бросаем бонус
                            for (int k = 0; k < 3; k++) {
                                powerUpsController.setup(a.getPosition().x, a.getPosition().y, a.getScale() / 4.0f);
                            }
                        }
                    }
                    break;
                }
            }
        }

        //столкновение с игроком
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            // если выстрелили мы
            if(b.getOwner().ownerType == OwnerType.PLAYER){
                //бот получает урон
                if(bot.getHitArea().contains(b.getPosition())){
                    bot.takeDamage(b.getDamage());
                    b.deactivate();
                }
            }
            //если владелец бот
            if(b.getOwner().ownerType == OwnerType.BOT){
                //мы получает урон
                if(hero.getHitArea().contains(b.getPosition())){
                    hero.takeDamage(b.getDamage());
                    b.deactivate();
                }
            }
        }

        //коллиз с бонус.
        for (int i = 0; i < powerUpsController.getActiveList().size(); i++) {
            PowerUp p = powerUpsController.getActiveList().get(i);
            if (p.getPosition().dst(hero.getPosition()) < hero.getObjectCaptureRadius()) {
                tmpVec.set(hero.getPosition()).sub(p.getPosition()).nor().scl(100.0f);
                p.getPosition().mulAdd(tmpVec, dt);
            }
            if (hero.getHitArea().contains(p.getPosition())) {
                hero.consume(p);
                //particleController.getEffectBuilder().takePowerUpEffect(p.getPosition().x, p.getPosition().y, p.getType().index);
                p.deactivate();
            }
        }
    }

    // столкновение астероида и героя (эластичное)
    public boolean CheckPhysicHit(Ship s, Asteroid a) {
        if(a.getHitArea().overlaps(s.getHitArea())) {
            float dst = a.getPosition().dst(s.getPosition()); //расстояние между ними
            float halfOverLen = (a.getHitArea().radius + s.getHitArea().radius - dst) / 2f;//на сколько они пересеклись, делим на 2
            tmpVec.set(s.getPosition()).sub(a.getPosition()).nor(); //любой из векторов (нормир)
            s.getPosition().mulAdd(tmpVec, halfOverLen); //tmpVec * halfOverLen
            a.getPosition().mulAdd(tmpVec, -halfOverLen); //tmpVec * halfOverLen

            // h - 1
            // a - 2
            //длины скоростей героя и астероида
            float v1 = s.getVelocity().len();
            float v2 = a.getVelocity().len();
            // находим их массы
            float m1 = 0.1f;
            float m2 = a.getScale();
            //направление вектора скорости
            float th1 = s.getVelocity().angleRad();
            float th2 = a.getVelocity().angleRad();
            //угол от игрока в сторону астероида
            float phi1 = tmpVec.set(a.getPosition()).sub(s.getPosition()).angleRad();
            //угол от астероида в сторону игрока
            float phi2 = tmpVec.set(s.getPosition()).sub(a.getPosition()).angleRad();
            //новые скорости персонажа
            float v1xN = (float) (((v1 * cos(th1 - phi1) * (m1 - m2) + 2 * m2 * v2 * cos(th2 - phi1)) / (m1 + m2)) * cos(phi1) + v1 * sin(th1 - phi1) * cos(phi1 + PI / 2.0f));
            float v1yN = (float) (((v1 * cos(th1 - phi1) * (m1 - m2) + 2 * m2 * v2 * cos(th2 - phi1)) / (m1 + m2)) * sin(phi1) + v1 * sin(th1 - phi1) * sin(phi1 + PI / 2.0f));
            //новые скорости астероида
            float v2xN = (float) (((v2 * cos(th2 - phi2) * (m2 - m1) + 2 * m1 * v1 * cos(th1 - phi2)) / (m2 + m1)) * cos(phi2) + v2 * sin(th2 - phi2) * cos(phi2 + PI / 2.0f));
            float v2yN = (float) (((v2 * cos(th2 - phi2) * (m2 - m1) + 2 * m1 * v1 * cos(th1 - phi2)) / (m2 + m1)) * sin(phi2) + v2 * sin(th2 - phi2) * sin(phi2 + PI / 2.0f));

            s.getVelocity().set(v1xN, v1yN);
            a.getVelocity().set(v2xN, v2yN);
            return true;
        }
        return false;
    }

    public void dispose(){
        backgrond.dispose();
    }
}
