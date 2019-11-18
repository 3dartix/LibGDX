package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Asteroids {
    private class Asteroid {
        private Texture texture;
        private Vector2 position;
        private float velocity = 100;
        private float scale;
        private float angle;

        private StarGame game;


        public Asteroid(StarGame game){
            this.game = game;
            this.texture = new Texture("asteroid.png");
            this.position = new Vector2(MathUtils.random(0,ScreenManager.SCREEN_WIDTH),MathUtils.random(0,ScreenManager.SCREEN_HEIGHT));
            this.angle = MathUtils.random(0,360);
            if(MathUtils.random(0,100) < 50){
                this.velocity = MathUtils.random(-40, -20);
            } else {
                this.velocity = MathUtils.random(20, 40);
            }
            this.scale = MathUtils.random(0.3f,0.6f);
        }

        public void render(SpriteBatch batch) {
            batch.draw(texture, position.x - texture.getWidth()/2 * scale, position.y - texture.getHeight()/2 * scale,texture.getWidth()/2,texture.getHeight()/2, 256,256,scale,scale,angle,0,0,256,256,false, false);
        }

        public void update(float dt){
            angle += velocity * dt;
            position.x += (velocity - game.getHero().getLastDisplacement().x * 15) * dt;
            position.y += (velocity - game.getHero().getLastDisplacement().y * 15)  * dt;

            if(position.x < - texture.getWidth()/2){
                position.x = ScreenManager.SCREEN_WIDTH + texture.getWidth()/2;
            }

            if(position.x - texture.getWidth()/2 * scale > ScreenManager.SCREEN_WIDTH){
                position.x = - texture.getWidth()/2;
            }

            if(position.y < - texture.getHeight()/2){
                position.y = ScreenManager.SCREEN_HEIGHT + texture.getHeight()/2;
            }

            if(position.y - texture.getHeight()/2 > ScreenManager.SCREEN_HEIGHT){
                position.y = - texture.getHeight()/2;
            }
        }
    }

    private Asteroid asteroids[];

    public Asteroids (int count, StarGame game) {
        asteroids = new Asteroid[count];
        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i] = new Asteroid(game);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i].render(batch);
        }
    }

    public void update(float dt){
        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i].update(dt);
        }
    }

}
