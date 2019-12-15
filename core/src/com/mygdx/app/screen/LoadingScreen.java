package com.mygdx.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.app.screen.utils.Assets;

// экран с прогресс баром
public class LoadingScreen extends AbstractScreen {
    private Texture texture;

    public LoadingScreen(SpriteBatch batch) {
        super(batch);
        //создаем массив пикселей (полоску) Pixmap.Format.RGB888 у каждого пикселя RGB и каждый пиксель занимает 8 бит
        Pixmap pixmap = new Pixmap(1280, 20, Pixmap.Format.RGB888);
        pixmap.setColor(Color.GREEN);
        //заполняем
        pixmap.fill();
        //по pixmap мы формируем текстуру и удаляем его
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        //очищаем экран черным цветом
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //getAssetManager().update() проверяем зарузились ли все ресурсы или нет
        if (Assets.getInstance().getAssetManager().update()) {
            //запоминаем для чего-то атлас с текстурами
            Assets.getInstance().makeLinks();
            // после загрузки всех ресурсов мы переходим в экран, который запомнили
            ScreenManager.getInstance().goToTarget();
        }
        batch.begin();
        batch.draw(texture, 0, 0, 1280 * Assets.getInstance().getAssetManager().getProgress(), 20);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
