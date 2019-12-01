package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GameController {
    private Background backgrond;
    private BulletController bulletController;
    private ParticleController particleController;
    private Hero hero;
    private AsteroidController asteroidController;
    private PowerUpsController powerUpsController;
    private Vector2 tmpVec; // Вектор для столкновений

    public GameController() {
        this.backgrond = new Background(this);
        this.hero = new Hero(this, "PLAYER1");
        this.asteroidController = new AsteroidController(this,2);
        this.bulletController = new BulletController(this);
        this.tmpVec = new Vector2(0,0);
        this.particleController = new ParticleController();
        this.powerUpsController = new PowerUpsController(this);
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
        //checkHeroCollisions();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
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

                float sumScl = hero.getHitArea().radius * 2 + a.getHitArea().radius;

                //если радиус астер усл 10 у гер 1, то гер ускорится в 10 раз быстрее, чем астер
                hero.getVelocity().mulAdd(tmpVec, 100 * halfOverLen * a.getHitArea().radius/sumScl); // герой получит ускорение равное своему радиусу делен на общий
                a.getVelocity().mulAdd(tmpVec, 100 * -halfOverLen * hero.getHitArea().radius/sumScl);

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

    public void dispose(){
        backgrond.dispose();
    }
}
