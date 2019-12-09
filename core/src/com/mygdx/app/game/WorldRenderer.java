package com.mygdx.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.app.screen.ScreenManager;
import com.mygdx.app.screen.utils.Assets;

public class WorldRenderer {
    private GameController  gc;
    private SpriteBatch batch;
    private BitmapFont font32;
    private BitmapFont font72;
    private BitmapFont font16;
    private StringBuilder strBuilder;
    private StringBuilder strHeroHp;
    private StringBuilder strGameOver;
    private Camera camera;

    // для отрисовки промежжуточный объект
    private FrameBuffer frameBuffer;
    // для того чтобы отрисовать разом весь фреймбуфер
    private TextureRegion frameBufferRegion;
    private ShaderProgram shaderProgram;



    public WorldRenderer(GameController gc, SpriteBatch batch) {
        this.gc = gc;
        this.batch = batch;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf", BitmapFont.class);
        this.font16 = Assets.getInstance().getAssetManager().get("fonts/font16.ttf", BitmapFont.class);
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf", BitmapFont.class);
        this.strBuilder = new StringBuilder();
        this.strHeroHp = new StringBuilder();
        this.strGameOver = new StringBuilder();

        //инициализация
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, ScreenManager.SCREEN_WIDTH, ScreenManager.SCREEN_HEIGHT, false);
        this.frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        //переворачиваем картинку
        this.frameBufferRegion.flip(false, true);
        //загружаем шейдеры
        this.shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(), Gdx.files.internal("shaders/fragment.glsl").readString());
        //компилируем шейдер
        if (!shaderProgram.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shaderProgram.getLog());
        }
        this.camera = ScreenManager.getInstance().getCamera();

    }

    public void render(){
        //переключаем на фреймбуфер
        frameBuffer.begin();
        // чистим его
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //рисуем космос
        batch.begin();
        gc.getBackgrond().render(batch);
        batch.end();

        //задаем камере положение игрока (по центру)
        camera.position.set(gc.getHero().getPosition().x, gc.getHero().getPosition().y, 0.0f);
        //пересчитываем координаты камеры и вьюпорта
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //начинаем рисовать
        batch.begin();
        gc.getAsteroidController().render(batch);
        gc.getHero().render(batch);
        gc.getBulletController().render(batch);
        gc.getParticleController().render(batch);
        gc.getBonusController().render(batch);
        gc.getBot().render(batch);
        batch.end();
        frameBuffer.end();
        //нарисовали картнку, далее показываем эту картинку
        //для этого выставляем камеру по центру экрана иначе картинка будет смещена, т.к она рисуется относительно центра камеры
        camera.position.set(ScreenManager.HALF_SCREEN_WIDTH, ScreenManager.HALF_SCREEN_HEIGHT, 0.0f);
        camera.update();
        ScreenManager.getInstance().getViewport().apply();
        batch.setProjectionMatrix(camera.combined);


        batch.begin();

        //при отрисовки указываем программе использовать наш шейдер
        //batch.setShader(shaderProgram);
        shaderProgram.setUniformf("px", gc.getHero().getPosition().x / ScreenManager.SCREEN_WIDTH);
        shaderProgram.setUniformf("py", gc.getHero().getPosition().y / ScreenManager.SCREEN_HEIGHT);



        //Очищаем экран
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //рисуем мир добавленны выше
        batch.draw(frameBufferRegion, 0, 0);
        batch.end();
        //как только отрисовка закончена сбрось этот шейдер
        //batch.setShader(null);

        //левел 1 рисуем, когда камера сброшена (интерфейс)
        batch.begin();
        gc.getHero().renderGUI(batch, font32);
        if(gc.getMsgTimer() > 0){
            font72.draw(batch, gc.getMsg(), 0, ScreenManager.HALF_SCREEN_HEIGHT, ScreenManager.SCREEN_WIDTH, Align.center,false);
        }
        batch.end();


        gc.getStage().draw();
    }
}
