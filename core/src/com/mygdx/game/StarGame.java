package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StarGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Backgrond backgrond;
	private Hero hero;
	private Asteroids asteroid;

	public Hero getHero() {
		return hero;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		backgrond = new Backgrond(this);
		hero = new Hero();
		asteroid = new Asteroids(10, this);
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		backgrond.render(batch);
		asteroid.render(batch);
		hero.render(batch);
		batch.end();
	}

	public void update(float dt){
		backgrond.update(dt);
		asteroid.update(dt);
		hero.update(dt);

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			Gdx.app.exit();
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
