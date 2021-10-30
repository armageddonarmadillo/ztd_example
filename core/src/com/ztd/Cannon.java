package com.ztd;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.Arrays;
import java.util.Hashtable;

public class Cannon {
    public float x, y, w, h, angle;
    public int fireDelay, count;
    public Sprite cannonSprite;
    public int health, health_chunk, counter, timer;
    public boolean active;
    public String type;

    public Sprite[][] sprites;
    public Texture cannon_texture;
    int cols, rows;
    int ticker = 0, duration = 5, aindex = 0;

    public Cannon(Texture ctex, float x, float y, int health){
        type = UI.current_type;
        cannon_texture = ctex;
        rows = 1;
        cols = UI.columns.get(UI.current_type) == null ? 1 : UI.columns.get(UI.current_type);
        w = ctex.getWidth() / cols;
        h = ctex.getHeight() / rows;
        cannonSprite = animate();
        this.x = lock(x  - w / 2);
        this.y = lock(y - h / 2);
        cannonSprite.setPosition(this.x, this.y);
        fireDelay = 30;
        count = 0;
        this.health = health;
        health_chunk = (int)((w - 6) / health);
        counter = 0;
        timer = 100;
        active = true;
    }

    void draw(SpriteBatch batch){
        batch.draw(Resources.red_bar, x + 3, y + 49, w - 6, 5);
        batch.draw(Resources.green_bar, x + 3, y + 49, health * health_chunk, 5);
        cannonSprite.draw(batch);
    }

    void update(){
        count++;
        if(count > fireDelay) {
            fire();
            count = 0;
        }
        ticker++;
        if(ticker > duration){
            Sprite temp = cannonSprite;
            cannonSprite = sprites[0][aindex++];
            cannonSprite.setPosition(temp.getX(), temp.getY());
            if(aindex >= cols) aindex = 0;
            ticker = 0;
        }
        getAngle();
        cannonSprite.setRotation(angle);
        active = health > 0;
    }

    public float lock(float pos){
        return ((int)(pos + 25)/50)*50;
    }

    public Rectangle getHitbox(){
        return new Rectangle(x, y, w, h);
    }

    public void fire(){
        switch(type){
            case "laser":
                ZTDS3A.blist.add(new Bullet(type, cannonSprite.getX() - w / 2, cannonSprite.getY() - h / 2));
                break;
            case "double":
                ZTDS3A.blist.add(new Bullet(type,x + 3 * (w / 4), y + h / 2));
                ZTDS3A.blist.add(new Bullet(type,x + w / 4, y + h / 2));
                break;
            default:
                ZTDS3A.blist.add(new Bullet(type, x + w / 2, y + h / 2));
                break;
        }
    }

    public void getAngle(){
        if(ZTDS3A.zlist.isEmpty()) return;

        float[] da = new float[ZTDS3A.zlist.size()]; //differences array
        Hashtable<Float, Zombie> closest_zombie_catcher = new Hashtable<Float, Zombie>();

        for(int i = 0; i < ZTDS3A.zlist.size(); i++){
            da[i] = Math.abs(x - (ZTDS3A.zlist.get(i).x + ZTDS3A.zlist.get(i).w / 2)) +
                    Math.abs(y - (ZTDS3A.zlist.get(i).y + ZTDS3A.zlist.get(i).h / 2));
            closest_zombie_catcher.put(da[i], ZTDS3A.zlist.get(i));
        }

        Arrays.sort(da);
        Zombie z = closest_zombie_catcher.get(da[0]);

        angle = (float)Math.atan((y - (z.y + z.h / 2)) / (x - (z.x + z.w / 2)));
        if(x >= z.x) angle += Math.PI;
        this.angle = (float) Math.toDegrees(angle);
    }

    public Sprite animate(){
        sprites = new Sprite[rows][cols];

        for(int r = 0; r < rows; r++)
            for(int c = 0; c < cols; c++)
                sprites[r][c] = new Sprite(cannon_texture, (int)(c * w), (int)(r * h), (int)w, (int)h);

        return sprites[0][0];
    }
}
