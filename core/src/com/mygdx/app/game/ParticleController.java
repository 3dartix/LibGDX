package com.mygdx.app.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.game.helpers.ObjectPool;
import com.mygdx.app.screen.utils.Assets;


public class ParticleController extends ObjectPool<Particle> {
    public class EffectBuilder {
        public void buildMonsterSplash(float x, float y) {
            for (int i = 0; i < 15; i++) {
                float randomAngle = MathUtils.random(0, 6.28f);
                float randomSpeed = MathUtils.random(0, 50.0f);
                setup(x, y, (float) Math.cos(randomAngle) * randomSpeed, (float) Math.sin(randomAngle) * randomSpeed, 1.2f, 2.0f, 1.8f, 1, 0, 0, 1, 1, 0, 0, 0.2f);
            }
        }

        //эффект при поднятии поверапа (бонуса)
        public void takePowerUpEffect(float x, float y) {
            for (int i = 0; i < 16; i++) {
                float angle = 6.28f / 16.0f * i;
                setup(x, y, (float) Math.cos(angle) * 100.0f, (float) Math.sin(angle) * 100.0f, 0.8f, 3.0f, 2.8f, 0, 1, 0, 1, 1, 1, 0, 0.4f);
            }
        }

        public void bulletCollideWithAsteroid(Vector2 bulletPosition, Vector2 bulletVelocity) {
            setup(
                    bulletPosition.x + MathUtils.random(-4, 4), bulletPosition.y + MathUtils.random(-4, 4),
                    bulletVelocity.x * -0.3f + MathUtils.random(-30, 30), bulletVelocity.y * -0.3f + MathUtils.random(-30, 30),
                    0.2f,
                    2.2f, 1.7f,
                    1.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 0.0f
            );
        }

        public void asteroidCollideWithPlanet(Vector2 bulletPosition, Vector2 bulletVelocity) {
            setup(
                    bulletPosition.x + MathUtils.random(-4, 4), bulletPosition.y + MathUtils.random(-4, 4),
                    bulletVelocity.x * -0.3f + MathUtils.random(-60, 60), bulletVelocity.y * -0.3f + MathUtils.random(-60, 60),
                    0.3f,
                    3.2f, 1.7f,
                    1.0f, 0.8f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 0.0f
            );
        }

        public void createAsteroidTrace(Vector2 asteroidPosition, Vector2 asteroidVelocity, float scale) {
            setup(
                    asteroidPosition.x + MathUtils.random(-4, 4), asteroidPosition.y + MathUtils.random(-4, 4),
                    asteroidVelocity.x + 100, asteroidVelocity.y + 100,
                    0.3f,
                    scale * 30, 1f,
                    1.0f, 0.8f, 0.0f, 1.0f - scale,
                    1.0f, 1.0f, 1.0f, 0.5f
            );
        }



        public void createBulletTrace(String weaponTitle, Vector2 bulletPosition, Vector2 bulletVelocity) {
            if(weaponTitle.equals("Laser")) {
                setup(
                        bulletPosition.x + MathUtils.random(-4, 4), bulletPosition.y + MathUtils.random(-4, 4),
                        bulletVelocity.x * -0.3f + MathUtils.random(-20, 20), bulletVelocity.y * -0.3f + MathUtils.random(-20, 20),
                        0.2f,
                        1.4f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 0.0f
                );
            }
            if(weaponTitle.equals("GreenLaser")) {
                setup(
                        bulletPosition.x + MathUtils.random(-4, 4), bulletPosition.y + MathUtils.random(-4, 4),
                        bulletVelocity.x * -0.3f + MathUtils.random(-20, 20), bulletVelocity.y * -0.3f + MathUtils.random(-20, 20),
                        0.3f,
                        1.2f, 2.4f,
                        0.2f, 1.0f, 0.2f, 1.0f,
                        0.3f, 1.0f, 0.3f, 0.4f
                );
            }
        }
    }

    private TextureRegion oneParticle;
    private EffectBuilder effectBuilder;

    public EffectBuilder getEffectBuilder() {
        return effectBuilder;
    }

    public ParticleController() {
        this.oneParticle = Assets.getInstance().getAtlas().findRegion("star16");
        this.effectBuilder = new EffectBuilder();
    }

    @Override
    protected Particle newObject() {
        return new Particle();
    }

    public void render(SpriteBatch batch) {
        // setBlendFunction - функция смешивания
        // GL20.GL_SRC_ALPHA - цвет рисуемой картинки (партикла)
        // GL20.GL_ONE_MINUS_SRC_ALPHA - цвет фона
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < activeList.size(); i++) {
            Particle o = activeList.get(i);
            float t = o.getTime() / o.getTimeMax(); //вычисляем текущее время жизни 0 - 100%
            float scale = lerp(o.getSize1(), o.getSize2(), t); //интерполяция масштаба в зависимости от t
            //вычисляем интерполяцию для цветов setColor - цвет пикселя (в нашем случае белой звезды)
            batch.setColor(lerp(o.getR1(), o.getR2(), t), lerp(o.getG1(), o.getG2(), t), lerp(o.getB1(), o.getB2(), t), lerp(o.getA1(), o.getA2(), t));
            batch.draw(oneParticle, o.getPosition().x - 8, o.getPosition().y - 8, 8, 8, 16, 16, scale, scale, 0);
        }
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        //далее мы делаем все тоже самое за искл. смешив. берем пиксель и умножаем его на 2, тем самым делая цвета ярче.
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < activeList.size(); i++) {
            Particle o = activeList.get(i);
            float t = o.getTime() / o.getTimeMax();
            float scale = lerp(o.getSize1(), o.getSize2(), t);
            if(MathUtils.random(0, 200) < 3) {
                scale *= 5.0f;
            }
            batch.setColor(lerp(o.getR1(), o.getR2(), t), lerp(o.getG1(), o.getG2(), t), lerp(o.getB1(), o.getB2(), t), lerp(o.getA1(), o.getA2(), t));
            batch.draw(oneParticle, o.getPosition().x - 8, o.getPosition().y - 8, 8, 8, 16, 16, scale, scale, 0);
        }
        //возвращаем какую-то штуку из-за которой все прозрачное.
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setup(float x, float y, float vx, float vy, float timeMax, float size1, float size2, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
        Particle item = getActiveElement();
        item.init(x, y, vx, vy, timeMax, size1, size2, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    public float lerp(float value1, float value2, float point) {
        return value1 + (value2 - value1) * point;
    }
}
