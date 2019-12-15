package com.mygdx.app.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.app.screen.utils.Assets;

public class Planet {
    GameController gc;
    private TextureRegion texture;
    private Vector2 position;
    private Circle hitArea;
    private float radius;

    public Vector2 getPosition() {
        return position;
    }
    public Circle getHitArea() {
        return hitArea;
    }

    public Planet (GameController gc) {
        this.gc = gc;
        this.position = new Vector2(GameController.SPACE_WIDTH/2, GameController.SPACE_HEIGHT/2);
        this.radius = 300;
        this.hitArea = new Circle(position.x, position.y, radius);
        this.texture = Assets.getInstance().getAtlas().findRegion("planet");
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x - 400, position.y - 400, 800, 800);
    }

}
