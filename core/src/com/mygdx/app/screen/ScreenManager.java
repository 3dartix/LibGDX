package com.mygdx.app.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.app.StarGame;
import com.mygdx.app.screen.utils.Assets;

public class ScreenManager {
    public enum ScreenType {
        MENU, GAME
    }

    public static final int SCREEN_WIDTH = 1000;
    public static final int HALF_SCREEN_WIDTH = SCREEN_WIDTH / 2;
    public static final int SCREEN_HEIGHT = 600;
    public static final int HALF_SCREEN_HEIGHT = SCREEN_HEIGHT / 2;

    private StarGame game;
    private SpriteBatch batch;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private Screen targetScreen;
    private Viewport viewport;
    private Camera camera;

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    private ScreenManager() {
    }

    public void init(StarGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        //отвечает за выравнивание экрана (растягивает с сохранение заданнных пропорций)
        this.viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
        this.gameScreen = new GameScreen(batch);
        this.menuScreen = new MenuScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
    }

    //прокидываем новые значения длины и высоты
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    // сброс камеры в центр нашего окна
    public void resetCamera() {
        camera.position.set(HALF_SCREEN_WIDTH, HALF_SCREEN_HEIGHT, 0);
        camera.update();
        //рисовать картинку с учетом всех смещений камеры
        batch.setProjectionMatrix(camera.combined);
    }

    //заставляет игру перейти на какой-то экран
    public void changeScreen(ScreenType type) {
        //послучаем ссылку на текущий экран
        Screen screen = game.getScreen();
        //чистим все ресурсы
        Assets.getInstance().clear();
        if (screen != null) {
            //если вдруг остались какие-то элементы на которые у нас нет ссылок уничтожаем (напр звуки)
            screen.dispose();
        }
        //сбрасываем камеру (если мы ее увели)
        resetCamera();
        //при переходе на любой экран попадаем в экран загрузки
        game.setScreen(loadingScreen);
        //одновременно грузим нужный нам экран
        switch (type) {
            case GAME:
                //ссылка целевой экран
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case MENU:
                targetScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
        }
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }

}
