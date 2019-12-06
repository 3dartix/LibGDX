package com.mygdx.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.app.game.GameController;
import com.mygdx.app.game.WorldRenderer;
import com.mygdx.app.screen.utils.Assets;

public class GameScreen extends AbstractScreen {
    private GameController gameController;
    private WorldRenderer worldRenderer;
    //дз 5 лекция
    //private BitmapFont font24;
    //private Stage stage; //сцена c кнопками

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }



    @Override
    public void show() {
        //загружаем текстуры
        Assets.getInstance().loadAssets(ScreenManager.ScreenType.GAME);

        this.gameController = new GameController(batch);
        this.worldRenderer = new WorldRenderer(gameController, batch);
        //удаляем атлас из памяти
        //Assets.getInstance().getAtlas().dispose();

//        //дз 5 лекция
//        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
//        //Gdx.input.setInputProcessor(stage);
//
//        Skin skin = new Skin();
//        skin.addRegions(Assets.getInstance().getAtlas());
//
//        //кнопка назад в меню
//        addBtn(skin, "smButton", new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
//            }
//        }, "Back to menu", 2,2);
//        //кнопка пауза
//        addBtn(skin, "smButton", new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                //ScreenManager.getInstance().setGamePause();
//            }
//        }, "Pause", 125,2);
//
//        skin.dispose();
    }

//    void addBtn(Skin skin, String nameImg, ChangeListener event, String name, float posX, float posY){
//        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
//        textButtonStyle.up = skin.getDrawable(nameImg);
//        textButtonStyle.font = Assets.getInstance().getAssetManager().get("fonts/font16.ttf");
//        skin.add(nameImg, textButtonStyle);
//        //создаем и настр. кнопки
//        Button newBtn = new TextButton(name, textButtonStyle);
//        newBtn.setPosition(posX, posY);
//        //добавляем слушателя
//        newBtn.addListener(event);
//        stage.addActor(newBtn);
//    }

    public void update(float dt){
        //stage.act(dt);
    }

    public void shoWDamage(){

    }

    @Override
    public void render(float delta) {
        //update(delta);
        gameController.update(delta);
        worldRenderer.render();

        //рисуем сцену
        //stage.draw();
    }

    @Override
    public void dispose(){
        gameController.dispose();
    }
}
