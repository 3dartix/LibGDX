package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class GameController {
    private Background backgrond;
    private BulletController bulletController;
    private Hero hero;
    private AsteroidController asteroidController;
    //private Asteroids asteroid;

    public GameController() {
        this.backgrond = new Background(this);
        this.hero = new Hero(this);
        this.asteroidController = new AsteroidController(this,1);
        this.bulletController = new BulletController(this);
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


    public Hero getHero() {
        return hero;
    }

    public void update(float dt){
        backgrond.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        checkCollisions();
        checkHeroCollisions();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    public void checkCollisions(){
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);
                if(a.getHitArea().contains(b.getPosition())){
                    b.deactivate();
                    if(a.takeDamage(1)){
                        hero.addScore(a.getHpMax()* 100);
                    }
                    break;
                }
            }
        }
    }

    public void checkHeroCollisions(){
        float coffCollisions = 2;
        Circle ha = hero.getHitArea();
        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Circle astHitArea = asteroidController.getActiveList().get(i).getHitArea();
            if(ha.overlaps(astHitArea)) {
                Vector2 shV = getHero().getVelocity();
                Vector2 asV = asteroidController.getActiveList().get(i).getVelocity();
                //меняем верктор на против.
                shV.scl(-1);
                //в случае с астероидом это не всегда работает как нужно
                //если долбиться в одну сторону астр. его вектор движ. будет постоянно меняться.
                asV.scl(-1);
                //сравниваем позиции корабля и астер
                if (getHero().getPosition().y >= asteroidController.getActiveList().get(i).getPosition().y) {
                    getHero().getPosition().set(getHero().getPosition().x, getHero().getPosition().y + coffCollisions);
                } else {
                    getHero().getPosition().set(getHero().getPosition().x, getHero().getPosition().y - coffCollisions);
                }
                if (getHero().getPosition().x >= asteroidController.getActiveList().get(i).getPosition().x) {
                    getHero().getPosition().set(getHero().getPosition().x + coffCollisions, getHero().getPosition().y);
                } else {
                    getHero().getPosition().set(getHero().getPosition().x - coffCollisions, getHero().getPosition().y);
                }
                getHero().takeDamag(1);
            }
        }
    }
}
