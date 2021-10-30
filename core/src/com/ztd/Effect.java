package com.ztd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Effect {
    int x, y, w, h;
    Texture etex;
    String type;
    boolean active = true;
    int counter = 0, delay = 30;

    //Animation Variables
    int cols, rows;
    Animation animation;
    TextureRegion[] frames;
    TextureRegion frame;
    float frame_time;

    Effect(String type, int x, int y){
        this.type = type;
        this.x = x;
        this.y = y;
        etex = UI.effects.get(type) == null ? Resources.click : UI.effects.get(type);
        cols = UI.columns.get(type) == null ? 4 : UI.columns.get(type);
        rows = 1;
        w = etex.getWidth() / cols;
        h = etex.getHeight() / rows;
        animate();
    }

    void update(){
        active = counter++ > delay;
    }

    void draw(SpriteBatch batch){
        frame_time += Gdx.graphics.getDeltaTime();
        frame = (TextureRegion)animation.getKeyFrame(frame_time, false);
        batch.draw(frame, x - w  / 2, y - h / 2);
    }

    void animate() {
        TextureRegion[][] sheet = TextureRegion.split(etex, (int)w, (int)h);

        frames = new TextureRegion[rows * cols];

        int index = 0;
        for(int r = 0; r < rows; r++)
            for(int c = 0; c < cols; c++)
                frames[index++] = sheet[r][c];

        animation = new Animation(0.2f, frames);
    }
}
