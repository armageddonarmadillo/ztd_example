package com.ztd;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Button {
    public float x, y, w, h;
    public String type;     //The type of the button, represented as a string (example: A Cannon button will have a type of cannon)
    public Texture btex;
    public boolean selected, locked;
    public int cost;
    ToolTip t;

    public Button(Texture btex, String type, float x, float y){
        this.btex = btex;
        this.type = type;
        this.x = x;
        this.y = y;
        this.w = btex.getWidth();
        this.h = btex.getHeight();
        cost = UI.unlock_costs.get(type) == null ? 100 : UI.unlock_costs.get(type);

        switch(type){
            case "cannon":
                selected = true;
                locked = false;
                break;
            case "fire":
            case "super":
            case "double":
            case "laser":
            case "saw":
            case "mounted":
                selected = false;
                locked = true;
                break;
            case "wall":
            case "pause":
            case "play":
                selected = false;
                locked = false;
                break;
        }

        if(!type.equals("no")) t = new ToolTip(type, x, y, w, h);
    }

    public void draw(SpriteBatch batch){
        batch.draw(btex, x, y);
        if(locked) batch.draw(Resources.locked, x, y);
        if(selected) batch.draw(Resources.selection, x - (int)(15 / 2), y - (int)(15 / 2));
        if(t != null) t.draw(batch);
    }

    public Rectangle getHitbox(){
        return new Rectangle(x, y, w, h);
    }
}
