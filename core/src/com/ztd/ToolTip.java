package com.ztd;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ToolTip {
    Texture ttex;
    Button btn;
    float x, y, w, h;
    float px, py, pw, ph; //parent
    int variant;
    boolean hidden = true;
    String type;
    BitmapFont font = new BitmapFont();
    GlyphLayout layout = new GlyphLayout();

    /*
     * 1) TOOLTIP INFO (tips, cost details for unlocking / using) + CLOSE BUTTON
     * 2) Double Tap Button to purchase / perform action (tap 1x to show tooltip & cost, tap again to unlock)
     * 3) Disabling ToolTips
     */

    ToolTip(String type, float x, float y, float w, float h){
        ttex = Resources.tooltip_bg;
        this.type = type;
        px = x;
        py = y;
        pw = w;
        ph = h;

        this.w = 200;
        this.h = 100;
        this.x = (x + w / 2) - this.w / 2;
        this.y = y - this.h - 15;
        btn = new Button(Resources.x, "no", this.x + this.w - 26, this.y + this.h - 26);
        variant = 1;
    }

    void draw(SpriteBatch batch){
        if(!hidden) {
            batch.draw(ttex, x, y, w, h);

            if(UI.tooltip_info.get(type) != null){
                String[] words = UI.tooltip_info.get(type).split(" ");
                int rtx = 0, rty = 0; //relative position to actual positions
                for (String s : words){
                    if (rtx + layout.width >= w - 50){
                        rtx = 0;
                        rty += layout.height + 5;
                    }
                    font.setColor(Color.BLACK);
                    font.draw(batch, s, x + rtx + 2 + 1, y + h - rty - 2 - 1);
                    font.setColor(Color.MAROON);
                    font.draw(batch, s, x + rtx + 2, y + h - rty - 2);
                    layout.setText(font, " " + s);
                    rtx += layout.width;
                }
            }

            font.setColor(Color.BLACK);
            font.draw(batch, "Unlock: " + UI.unlock_costs.get(type), x + 50 + 1, y + 35 - 1);
            font.setColor(Color.OLIVE);
            font.draw(batch, "Unlock: " + UI.unlock_costs.get(type), x + 50, y + 35);

            font.setColor(Color.YELLOW);
            font.draw(batch, "(tap again to unlock)", x + 35 + 1, y + 15 - 1);
            font.setColor(Color.BLACK);
            font.draw(batch, "(tap again to unlock)", x + 35, y + 15);

            btn.draw(batch);
        }
    }

    Rectangle hitbox(){
        return new Rectangle(x, y, w, h);
    }
}
