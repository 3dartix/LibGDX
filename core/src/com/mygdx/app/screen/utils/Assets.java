package com.mygdx.app.screen.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.mygdx.app.screen.ScreenManager;

public class Assets {
    private static final Assets ourInstance = new Assets();

    public static Assets getInstance() {
        return ourInstance;
    }

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public TextureAtlas getAtlas() {
        return textureAtlas;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    private Assets() {
        assetManager = new AssetManager();
    }

    public void loadAssets(ScreenManager.ScreenType type) {
        switch (type) {
            case MENU:
                //загруз атласа текстур
                assetManager.load("images/game.pack", TextureAtlas.class);
                createStandardFont(72);
                createStandardFont(24);
                break;
            case GAME:
                //загружаем атлас текстур
                assetManager.load("images/game.pack", TextureAtlas.class);
                assetManager.load("audio/Shoot.mp3", Sound.class);
                assetManager.load("audio/Music.mp3", Music.class);
                assetManager.load("audio/BigExplosion.mp3", Sound.class);
                assetManager.load("audio/SmallExplosion.mp3", Sound.class);
                createStandardFont(32);
                createStandardFont(16);
                createStandardFont(24);
                createStandardFont(72);
                //ожидаем пока текстура зарузится
                assetManager.finishLoading();
                //получаем загруженный атлас
                textureAtlas = assetManager.get("images/game.pack", TextureAtlas.class);
                break;
            case GAMEOVER:
                //загруз атласа текстур
                assetManager.load("images/game.pack", TextureAtlas.class);
                createStandardFont(72);
                createStandardFont(24);
                createStandardFont(48);
                break;
        }
    }

    //создание шрифтов
    private void createStandardFont(int size) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "fonts/Roboto-Medium.ttf";
        fontParameter.fontParameters.size = size;
        fontParameter.fontParameters.color = Color.WHITE;
        fontParameter.fontParameters.shadowOffsetX = 1;
        fontParameter.fontParameters.shadowOffsetY = 1;
        fontParameter.fontParameters.shadowColor = Color.DARK_GRAY;
        assetManager.load("fonts/font" + size + ".ttf", BitmapFont.class, fontParameter);
    }

    //Достает ссылку на атлас
    public void makeLinks() {
        textureAtlas = assetManager.get("images/game.pack", TextureAtlas.class);
    }

    public void clear() {
        assetManager.clear();
    }
}
