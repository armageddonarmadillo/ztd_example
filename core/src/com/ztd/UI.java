package com.ztd;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Hashtable;

public class UI {
    public static int money = 10000;
    public static int score = 0;
    public static int wave = 0;
    public static int life = 20;
    public static int cost = 10;
    public static BitmapFont font = new BitmapFont();
    public static String current_type;
    public static String last_zombie = "zzz";

    //TABLES
    //cannons
    public static Hashtable<String, Texture> cannon_selector = new Hashtable<String, Texture>();                  // cannon texture table
    public static Hashtable<String, Integer> columns = new Hashtable<String, Integer>();                          // animation columns table
    //bullets
    public static Hashtable<String, Texture> bullet_selector = new Hashtable<String, Texture>();                  // bullet texture table
    public static Hashtable<String, Integer> bullet_damage = new Hashtable<String, Integer>();                    // bullet damage table
    public static Hashtable<String, Integer> bullet_speed = new Hashtable<String, Integer>();                     // bullet speed table
    public static Hashtable<String, Texture> loot_table = new Hashtable<String, Texture>();                       // loot texture table
    //zombies
    public static Hashtable<String, Texture> zombie_selector = new Hashtable<String, Texture>();                  // zombie texture table
    public static Hashtable<String, Integer> zombie_health = new Hashtable<String, Integer>();                    // zombie health table
    public static Hashtable<String, Integer> zombie_speed = new Hashtable<String, Integer>();                     // zombie speed table
    public static Hashtable<String, Integer> zombie_score = new Hashtable<String, Integer>();                     // zombie score table
    public static Hashtable<String, Integer> zombie_value = new Hashtable<String, Integer>();
    public static Hashtable<String, Integer> zombie_damage = new Hashtable<String, Integer>();

    //random tables
    public static Hashtable<String, Integer> unlock_costs = new Hashtable<String, Integer>();
    public static Hashtable<String, Integer> placement_costs = new Hashtable<String, Integer>();
    public static Hashtable<String, String> tooltip_info = new Hashtable<String, String>();
    public static Hashtable<String, Texture> effects = new Hashtable<String, Texture>();

    public static void draw(SpriteBatch batch){

            font.setColor(Color.YELLOW);
            font.draw(batch, "Money: " + money, 85, 515);
            font.setColor(Color.GREEN);
            font.draw(batch, "Life: " + life, 25, 525);
            font.setColor(Color.PINK);
            font.draw(batch, "Wave: " + wave, 25, 550);
            font.setColor(Color.CYAN);
            font.draw(batch, "Zcore: " + score, 25, 575);
            if(life <= 0) {
                batch.draw(Resources.blackout, 0, 0);
                font.getData().setScale(5f);
                font.setColor(Color.SCARLET);
                font.draw(batch, "GAME OVER", 512 - 200, 300);
                font.getData().setScale(1);
                ZTDS3A.paused = true;
            }
    }
}
