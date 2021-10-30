package com.ztd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Zombie {
    float x, y, w, h, speed;
    Texture zombieTexture;
    int hp, zscore;
    boolean active = true, drop = false;
    float health_chunk;
    String type;
    int damage;

    //Animation Variables
    int cols, rows;
    Animation animation;
    TextureRegion[] frames;
    TextureRegion frame;
    float frame_time;

     Zombie(String type, float x, float y){
         this.type = type;
         this.x = x;
         this.y = y;
         this.cols = UI.columns.get(type) == null ? 4 : UI.columns.get(type);
         this.rows = 1;
         zombieTexture = UI.zombie_selector.get(type) == null ? Resources.zombieTexture : UI.zombie_selector.get(type);
         this.damage = UI.zombie_damage.get(type) == null ? 1 : UI.zombie_damage.get(type);
         w = zombieTexture.getWidth() / cols;
         h = zombieTexture.getHeight() / rows;
         this.hp = UI.zombie_health.get(type) == null ? 50 : UI.zombie_health.get(type);
         health_chunk = ((w - 6) / hp);
         this.speed = UI.zombie_speed.get(type) == null ? 2 : UI.zombie_speed.get(type);
         this.zscore = UI.zombie_score.get(type) == null ? 1 : UI.zombie_score.get(type);
         animate();
    }

    void draw(SpriteBatch batch){
         frame_time += Gdx.graphics.getDeltaTime();
         frame = (TextureRegion)animation.getKeyFrame(frame_time, true);

         batch.draw(Resources.red_bar, x - ( w/ 2 ) + 3, y + ( h/ 2 ) + 2, w - 6, 5);
         batch.draw(Resources.green_bar, x - ( w/ 2 ) + 3, y + ( h/ 2 ) + 2, hp * health_chunk, 5);
         batch.draw(frame, x - w / 2, y - h / 2);
    }

    void update(){
         if(this.hp <= 0) {
             active = false;
             drop = true;
             UI.score += zscore;
         } else if (this.x < -15) {
             active = false;
             UI.life--;
             UI.score -= zscore;
         }
         x -= speed;
    }

    Rectangle getHitbox(){
         return new Rectangle(x, y, w, h);
    }

    void animate() {
        TextureRegion[][] sheet = TextureRegion.split(zombieTexture, (int)w, (int)h);

        frames = new TextureRegion[rows * cols];

        int index = 0;
        for(int r = 0; r < rows; r++)
            for(int c = 0; c < cols; c++)
                frames[index++] = sheet[r][c];

            animation = new Animation(0.2f, frames);
    }

}
