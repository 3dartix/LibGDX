package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.app.screen.ScreenManager;

import static java.lang.Math.*;

public class GameController {
    private Background backgrond;
    private BulletController bulletController;
    private ParticleController particleController;
    private Hero hero;
    private AsteroidController asteroidController;
    private PowerUpsController powerUpsController;
    private Vector2 tmpVec; // Вектор для столкновений
    private Stage stage;

    public GameController(SpriteBatch batch) {
        this.backgrond = new Background(this);
        this.hero = new Hero(this, "PLAYER1");
        this.asteroidController = new AsteroidController(this,2);
        this.bulletController = new BulletController(this);
        this.tmpVec = new Vector2(0,0);
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.particleController = new ParticleController();
        this.powerUpsController = new PowerUpsController(this);
        this.stage.addActor(hero.getShop());
        Gdx.input.setInputProcessor(stage);
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

    public void update(float dt){
        backgrond.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        particleController.update(dt);
        powerUpsController.update(dt);
        checkCollisions();
        if(!hero.isAlive()) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER, hero);
        }
        stage.act(dt);
    }

    public void checkCollisions(){
        // столкн астр и кор
        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid a = asteroidController.getActiveList().get(i);
            if(a.getHitArea().overlaps(hero.getHitArea())) {
                float dst = a.getPosition().dst(hero.getPosition()); //расстояние между ними
                float halfOverLen = (a.getHitArea().radius + hero.getHitArea().radius - dst) / 2;//на сколько они пересеклись, делим на 2
                tmpVec.set(hero.getPosition()).sub(a.getPosition()).nor(); //любой из векторов (нормир)
                hero.getPosition().mulAdd(tmpVec, halfOverLen); //tmpVec * halfOverLen
                a.getPosition().mulAdd(tmpVec, -halfOverLen); //tmpVec * halfOverLen

                hit(hero, a);

                if(a.takeDamage(2)){
                    hero.addScore(a.getHpMax()* 10);
                };
                hero.takeDamag(2);
            }
        }

        // пули
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);
                if(a.getHitArea().contains(b.getPosition())){

                    particleController.setup(
                            b.getPosition().x + MathUtils.random(-4, 4), b.getPosition().y + MathUtils.random(-4, 4),
                            b.getVelocity().x * -0.3f + MathUtils.random(-30, 30), b.getVelocity().y * -0.3f + MathUtils.random(-30, 30),
                            0.2f,
                            2.2f, 1.7f,
                            1.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 0.0f
                    );

                    b.deactivate();
                    if(a.takeDamage(1)){
                        hero.addScore(a.getHpMax()* 100);
                        //GlobalStatistic.getInstance().addTotalScore(a.getHpMax()* 100);
                        // бросаем бонус
                        for (int k = 0; k < 3; k++) {
                            powerUpsController.setup(a.getPosition().x, a.getPosition().y, a.getScale() / 4.0f);
                        }
                    }
                    break;
                }
            }
        }

        //коллиз с бонус.
        for (int i = 0; i < powerUpsController.getActiveList().size(); i++) {
            PowerUp p = powerUpsController.getActiveList().get(i);
            if (hero.getHitArea().contains(p.getPosition())) {
                hero.consume(p);
                //вызываем эффект при подъеме бонуса
                particleController.getEffectBuilder().takePowerUpEffect(p.getPosition().x, p.getPosition().y);
                p.deactivate();
            }
        }
    }

    // столкновение астероида и героя
    public void hit(Hero h, Asteroid a) {
        // h - 1
        // a - 2
        //длины скоростей героя и астероида
        float v1 = h.getVelocity().len();
        float v2 = a.getVelocity().len();
        // находим их массы
        float m1 = 0.1f;
        float m2 = a.getScale();
        //направление вектора скорости
        float th1 = h.getVelocity().angleRad();
        float th2 = a.getVelocity().angleRad();
        //угол от игрока в сторону астероида
        float phi1 = tmpVec.set(a.getPosition()).sub(h.getPosition()).angleRad();
        //угол от астероида в сторону игрока
        float phi2 = tmpVec.set(h.getPosition()).sub(a.getPosition()).angleRad();
        //новые скорости персонажа
        float v1xN = (float) (((v1 * cos(th1 - phi1) * (m1 - m2) + 2 * m2 * v2 * cos(th2 - phi1)) / (m1 + m2)) * cos(phi1) + v1 * sin(th1 - phi1) * cos(phi1 + PI / 2.0f));
        float v1yN = (float) (((v1 * cos(th1 - phi1) * (m1 - m2) + 2 * m2 * v2 * cos(th2 - phi1)) / (m1 + m2)) * sin(phi1) + v1 * sin(th1 - phi1) * sin(phi1 + PI / 2.0f));
        //новые скорости астероида
        float v2xN = (float) (((v2 * cos(th2 - phi2) * (m2 - m1) + 2 * m1 * v1 * cos(th1 - phi2)) / (m2 + m1)) * cos(phi2) + v2 * sin(th2 - phi2) * cos(phi2 + PI / 2.0f));
        float v2yN = (float) (((v2 * cos(th2 - phi2) * (m2 - m1) + 2 * m1 * v1 * cos(th1 - phi2)) / (m2 + m1)) * sin(phi2) + v2 * sin(th2 - phi2) * sin(phi2 + PI / 2.0f));

        h.getVelocity().set(v1xN, v1yN);
        a.getVelocity().set(v2xN, v2yN);
    }

    public void dispose(){
        backgrond.dispose();
    }
}
