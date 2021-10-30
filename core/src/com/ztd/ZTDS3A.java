package com.ztd;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class ZTDS3A extends ApplicationAdapter {
	//Lists
	public static ArrayList<Zombie> zlist = new ArrayList<Zombie>();
	public static ArrayList<Cannon> clist = new ArrayList<Cannon>();
	public static ArrayList<Bullet> blist = new ArrayList<Bullet>();
	public static ArrayList<Button> bulist = new ArrayList<Button>();
	public static ArrayList<Loot> loot_list = new ArrayList<Loot>();
	public static ArrayList<Wall> wlist = new ArrayList<Wall>();
	public static ArrayList<Saw> saw_list = new ArrayList<Saw>();
	public static ArrayList<Effect> effect_list = new ArrayList<Effect>();

	//Other Variables
	SpriteBatch batch;
	Texture bg;
	public static boolean paused = true, started = false;
	OrthographicCamera camera;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		bg = Resources.bgTexture;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 600);
		bulist.add(new Button(Resources.startbutton, "start", 512 - 300, 200));
		bulist.add(new Button(Resources.exitbutton, "exit", 512 - 300 + 400, 200));
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		update();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if(!started){
			batch.draw(Resources.title, 0 , 0);
			for(Button b : bulist) b.draw(batch);
		} else {
			batch.draw(bg, 0, 0);
			UI.draw(batch);
			for(Zombie z : zlist) z.draw(batch);
			for(Bullet b : blist) b.draw(batch);
			for(Loot l : loot_list) l.draw(batch);
			for(Wall w : wlist) w.draw(batch);
			for(Cannon c : clist) c.draw(batch);
			for(Button b : bulist) b.draw(batch);
			for(Saw s : saw_list) s.draw(batch);
			for(Effect e : effect_list) e.draw(batch);
		}
		batch.end();
	}

	public void update(){
		//internal method calls
		controls();
		if(paused) return;
		spawnZombies();
		bulletZombieCollision();
		wallZombieCollision();
		zombieCannonCollision();
		sawZombieCollision();
		coinPouchCollision();
		housekeeping();

		//update loops
		for(Zombie z : zlist) z.update();
		for(Cannon c : clist) c.update();
		for(Bullet b : blist) b.update();
		for(Wall w : wlist) w.update();
		for(Saw s : saw_list) s.update();
		for(Effect e : effect_list) e.update();
		for(Loot l : loot_list) if(l.type.equals("coin")) l.moveToTarget(loot_list.get(0));
	}

	public void housekeeping(){	//remove stuff, remove sprites
		for(int i = 0; i < zlist.size(); i++) if(!zlist.get(i).active) {
			if(zlist.get(i).drop) {
				UI.last_zombie = zlist.get(i).type;
				loot_list.add(new Loot("coin", zlist.get(i).x, zlist.get(i).y, 0, 0));
			}
			zlist.remove(i);
		}
		for(int i = 0; i < wlist.size(); i++) if(!wlist.get(i).active) {
			wlist.get(i).wall_cannons.clear();
			wlist.remove(i);
		}
		for(int i = 0; i < blist.size(); i++) if(!blist.get(i).active) blist.remove(i);
		for(int i = 0; i < clist.size(); i++) if(!clist.get(i).active) clist.remove(i);
		for(int i = 0; i < saw_list.size(); i++) if(!saw_list.get(i).active) saw_list.remove(i);
		for(int i = 0; i < loot_list.size(); i++) if(!loot_list.get(i).active) loot_list.remove(i);
		for(int i = 0; i < effect_list.size(); i++) if(!effect_list.get(i).active) effect_list.remove(i);
	}

	public void controls(){
		if(Gdx.input.justTouched()){
			float x, y;
			Vector3 touchpos = new Vector3();
			touchpos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchpos);
			x = touchpos.x;
			y = touchpos.y;

			effect_list.add(new Effect("click", (int)x, (int)y));

			for(Button b : bulist)
                if(b.t != null && !b.t.hidden && b.t.hitbox().contains(x,y)){
                    if(b.t.btn.getHitbox().contains(x, y)) b.t.hidden = true;
                    return;
                }

			for(Cannon c : clist) if(c.getHitbox().contains(x, y)) return;

			for(Button b : bulist)
				if(b.getHitbox().contains(x, y)){
				    if(b.t != null && b.t.hidden && b.locked) { hideTT(); b.t.hidden = false; return; }
					switch(b.type){
						case "cannon":
						case "fire":
						case "super":
						case "double":
						case "laser":
						case "saw":
							if(b.locked) {
								if(!(UI.money >= b.cost)) return;
								b.locked = false;
								UI.money -= b.cost;
                                if(b.t != null) b.t.hidden = true;
							} else {
								selector(b.type);
								UI.current_type = b.type;
							}
							break;
						case "mounted":
							if(UI.money >= b.cost && b.locked){
								b.locked = false;
								UI.money -= b.cost;
                                if(b.t != null) b.t.hidden = true;
								break;
							}
						case "wall":
							if(b.type.equals("wall") && UI.money >= b.cost && wlist.size() < 3) {
								wlist.add(new Wall(wlist.size() * 50, 0));
								UI.money -= b.cost;
								if(b.t != null) b.t.hidden = true;
							} else {
								if(wlist.size() < 3 && UI.money >= b.cost) {
									wlist.add(new Wall(wlist.size() * 50, 0));
									for (int i = 0; i < 10; i++) {
										wlist.get(wlist.size() - 1).wall_cannons.add(new Cannon(UI.cannon_selector.get("mounted"), wlist.size() * 50 - 50, i * 50, 4));
									}
									UI.money -= b.cost;
								}
							}
							break;
						case "play":
						case "pause":
							if(b.type.equals("pause")) b.btex = Resources.playButton; else b.btex = Resources.pauseButton;
							if(b.type.equals("pause")) b.type = "play"; else b.type = "pause";
							paused = !paused;
							break;
						case "start":
						case "restart":
							createGame();
							return;
						case "exit":
							System.exit(0);
							break;
					}
				}

			if(!started) return;

			if((y <= 200 && y >= 0) || (y <= 500 && y >= 300)) {
				if(!UI.current_type.equals("saw") && UI.money >= UI.placement_costs.get(UI.current_type)) {
					clist.add(new Cannon(UI.cannon_selector.get(UI.current_type), x, y, 11));
					UI.money -= UI.placement_costs.get(UI.current_type);
				}
			}
			if(y >= 0 && y <= 500 && UI.current_type.equals("saw") && UI.money >= UI.placement_costs.get("saw")){
				saw_list.add(new Saw(0, y - Resources.saw.getHeight() / 2));
				UI.money -= UI.placement_costs.get("saw");
			}
		}
	}

	public void selector(String type){
			for(Button b : bulist) b.selected = b.type.equals(type);
	}

	public void hideTT(){
	    for(Button b : bulist) if(b.t != null) b.t.hidden = true;
    }
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public void createGame(){
		//control variables
		started = true;
		paused = false;
		UI.money = 10000;
		UI.score = 0;
		UI.life = 20;
		UI.wave = 0;

		//clear everything
		clist.clear();
		wlist.clear();
		zlist.clear();
		blist.clear();
		bulist.clear();
		loot_list.clear();
		saw_list.clear();

		//button unlock costs
		UI.unlock_costs.put("fire", 200);
		UI.unlock_costs.put("super", 150);
		UI.unlock_costs.put("double", 200);
		UI.unlock_costs.put("laser", 1000);
		UI.unlock_costs.put("saw", 2500);
		UI.unlock_costs.put("mounted", 300);
		UI.unlock_costs.put("wall", 100);

		int button_start = 200;
		bulist.add(new Button(Resources.cannonIcon, "cannon", button_start + (bulist.size() * 75), 525));
		bulist.add(new Button(Resources.fireCannonIcon, "fire", button_start + (bulist.size() * 75), 525));
		bulist.add(new Button(Resources.superCannonIcon, "super", button_start + (bulist.size() * 75), 525));
		bulist.add(new Button(Resources.doubleCannonIcon, "double", button_start + (bulist.size() * 75), 525));
		bulist.add(new Button(Resources.laserCannonIcon, "laser", button_start + (bulist.size() * 75), 525));
		bulist.add(new Button(Resources.sawIcon, "saw", button_start + (bulist.size() * 75), 525));
		bulist.add(new Button(Resources.mountedCannonIcon, "mounted", button_start + (bulist.size() * 75), 525));
		bulist.add(new Button(Resources.wallIcon, "wall", button_start + (bulist.size() * 75), 525));

		//Pause / Play has its own spot
		bulist.add(new Button(Resources.pauseButton, "pause", 1024 - 75, 525));
		bulist.add(new Button(Resources.restartButton, "restart", 1024 - 150, 525));

		//TABLES

		//cannon resources
		UI.cannon_selector.put("cannon", Resources.cannonTexture);
		UI.cannon_selector.put("fire", Resources.fireCannonTexture);
		UI.cannon_selector.put("super", Resources.superCannonTexture);
		UI.cannon_selector.put("double", Resources.doubleCannonTexture);
		UI.cannon_selector.put("laser", Resources.laserCannonTexture);
		UI.cannon_selector.put("mounted", Resources.mountedCannonTexture);

		//tooltip information
		UI.tooltip_info.put("cannon", "Fires weak bullets at a low rate of fire.");
		UI.tooltip_info.put("fire", "Fires strong bullets at a high rate of fire.");
		UI.tooltip_info.put("super", "Fires weak bullets at a high rate of fire.");
		UI.tooltip_info.put("double", "Fires weak bullets at a low rate of fire twice.");
		UI.tooltip_info.put("laser", "Fires extremely strong bullets at a very low rate of fire.");
		UI.tooltip_info.put("mounted", "Spawns a wall with basic cannons attached.");
		UI.tooltip_info.put("saw", "Spawns a saw that cuts down anything in its path.");

		//placement costs
		UI.placement_costs.put("cannon", 10);
		UI.placement_costs.put("fire", 50);
		UI.placement_costs.put("super", 30);
		UI.placement_costs.put("double", 25);
		UI.placement_costs.put("laser", 250);
		UI.placement_costs.put("mounted", 150);
		UI.placement_costs.put("saw", 250);

		//animation columns
		UI.columns.put("laser", 16);
		UI.columns.put("speedy", 6);

		//zombie textures
		UI.zombie_selector.put("grey", Resources.greyZombieTexture);
		UI.zombie_selector.put("fast", Resources.fastZombieTexture);
		UI.zombie_selector.put("speedy", Resources.speedyZombieTexture);
		UI.zombie_selector.put("riot", Resources.riotZombieTexture);
		UI.zombie_selector.put("riotB", Resources.bigRiotZombieTexture);

		//zombie health
		UI.zombie_health.put("fast", 40);
		UI.zombie_health.put("speedy", 30);
		UI.zombie_health.put("riot", 100);
		UI.zombie_health.put("riotB", 250);

		//zombie speed
		UI.zombie_speed.put("fast", 4);
		UI.zombie_speed.put("speedy", 6);
		UI.zombie_speed.put("riot", 1);
		UI.zombie_speed.put("riotB", 1);

		//zombie score
		UI.zombie_score.put("fast", 2);
		UI.zombie_score.put("speedy", 3);
		UI.zombie_score.put("riot", 5);
		UI.zombie_score.put("riotB", 10);

		//zombie value
		UI.zombie_value.put("fast", 7);
		UI.zombie_value.put("speedy", 10);
		UI.zombie_value.put("riot", 10);
		UI.zombie_value.put("riotB", 50);

		//zombie damage
		UI.zombie_damage.put("fast", 100);

		//bullet resources
		UI.bullet_selector.put("fire", Resources.fireBulletTexture);
		UI.bullet_selector.put("super", Resources.superBulletTexture);
		UI.bullet_selector.put("laser", Resources.laserBulletTexture);

		//bullet speeds
		UI.bullet_speed.put("fire", 10);
		UI.bullet_speed.put("super", 7);
		UI.bullet_speed.put("laser", 10);

		//bullet damage
		UI.bullet_damage.put("fire", 25);
		UI.bullet_damage.put("laser", 100);


		//type tracking
		UI.current_type = "cannon";

		//loot table resources
		UI.loot_table.put("pouch", Resources.coin_pouch);

		loot_list.add(new Loot("pouch", 100, 525, 0, 0));
	}

	public void spawnZombies(){
		if(!zlist.isEmpty()) return;
		String[] types = { "zzz", "grey", "fast", "speedy", "riot", "riotB" };
		UI.wave++;
		int zombiecap = 5; // THIS IS THE NUMBER OF ZOMBIES PER WAVE
		Random r = new Random();
		for(int i = 0; i < UI.wave * zombiecap; i++){
			switch(UI.wave){
				case 1:
					zlist.add(
							new Zombie("zzz", 1024 + i * 36, r.nextInt(450)));
					break;
				case 5:
					zlist.add(new Zombie("riotB", 1024 + i * 36, r.nextInt(450)));
					return;
				default:
					zlist.add(new Zombie(types[r.nextInt(types.length - 1)], 1024 + i * 36, r.nextInt(450)));
					break;
			}
		}
	}

	public void bulletZombieCollision(){
		for(Zombie z : zlist)
			for(Bullet b : blist)
				if(z.getHitbox().contains(b.x, b.y)) {
					if(z.hp - b.damage <= 0) z.hp = 0; else z.hp -= b.damage;
					b.active = false;
				}
	}

	public void zombieCannonCollision(){
		for(Cannon c : clist)
			for(Zombie z : zlist)
				if(c.getHitbox().contains(z.x, z.y)) {
					if(c.health - z.damage < 0) c.health = 0;
					else c.health -= z.damage;
					z.active = false;
				}
	}

	public void coinPouchCollision(){
		for(int i = 1; i < loot_list.size(); i++){
			if(loot_list.get(i).type.equals("coin") && loot_list.get(0).getHitbox().contains(loot_list.get(i).x, loot_list.get(i).y)){
				UI.money += loot_list.get(i).item_value;
				loot_list.get(i).active = false;
			}
		}
	}

	public void sawZombieCollision(){
		for(Saw s : saw_list) for(Zombie z : zlist) if(s.getHitbox().contains(z.x, z.y)) z.hp = 0;
	}

	public void wallZombieCollision(){
		if(wlist.isEmpty()) return;

		for(Zombie z : zlist)
			if(wlist.get(wlist.size() - 1).getHitbox().contains(z.x, z.y)){
				wlist.get(wlist.size() - 1).durability--;
				z.drop = true;
				z.active = false;
			}
	}

}
