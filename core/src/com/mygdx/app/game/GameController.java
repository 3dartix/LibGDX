package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class GameController {
    private Background backgrond;
    private BulletController bulletController;
    private Hero hero;
    private AsteroidController asteroidController;
    //private Asteroids asteroid;

    public GameController() {
        this.backgrond = new Background(this);
        this.hero = new Hero(this);
        this.asteroidController = new AsteroidController(this,10);
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

//    public Asteroids getAsteroid() {
//        return asteroid;
//    }

    public Hero getHero() {
        return hero;
    }

    public void update(float dt){
        backgrond.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        checkCollisions();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    public void checkCollisions(){
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);
                if(b.getPosition().dst(a.getPosition()) * 1.1f  < a.getRad()){
                    b.deactivate();
                    a.setLifeSub();
                    //a.deactivate();
                }
            }
        }
    }
}
