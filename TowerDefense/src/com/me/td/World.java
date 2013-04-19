package com.me.td;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import enemies.Arcanine;
import enemies.BasicEnemy;
import enemies.Bowser;
import enemies.Enemy;
import enemies.Geodude;
import enemies.Goomba;
import enemies.Koopa;
import enemies.ShyGuy;
import enemies.Tentacool;
import enemies.Voltorb;
import enemies.Weedle;
import enums.EnemyEnum;
import enums.Level;

import java.util.ArrayList;
import java.util.Queue;

import maps.Map;
import maps.MarioMap;
import maps.PokemonMap;

import towers.BasicTower;
import towers.FireTower;
import towers.GrassTower;
import towers.HammerBros;
import towers.PsychicTower;
import towers.Tower;
import util.Database;


public class World
{
	
	public static float volume = 1.0f;
	public static boolean mute;
	
	// constants
//	final int MARIO_MAP = 0, POKEMON_MAP = 1;
//	final int MUSHROOM = 0, GOOMBA = 1, KOOPA = 2, BOWSER = 3, SHYGUY = 4, ARCANINE = 5, GEODUDE = 6, WEEDLE = 7, VOLTORB = 8, TENTACOOL = 9;
	final int PAUSE = 0, SELL = 1, UPGRADE = 2, CASTLE = 3, HAMMER = 4, PSYCHIC = 5, FIRE = 6, GRASS = 7;
	final int TOWER_COUNT = 4;
	final int GRID_WIDTH = 40, GRID_HEIGHT = 40;
	final int BAR_WIDTH = 300, BAR_HEIGHT = 16, BAR_X = 5, BAR_Y = 375;
	final int SAFE_HEALTH = 30;

	// variables
	int health, gold, wave_number, current_tower, time_between_waves;
	EnemyEnum current_enemy;
	float current_range;
	Map map;
	TowerSelect tower_select;
	OptionsMenu options_menu;
	Queue<Enemy> wave;
	ArrayList<Enemy> enemies;
	Tower[][] tower_grid;
	OrthographicCamera camera;
	BitmapFont font, katana_font;
	Texture health_bar_max, health_bar_unsafe, health_bar_safe, hover, coin;
//	Texture heart0, heart25, heart50, heart75, heart100;
	Sound congratulations;
	Vector3 touch_pos, circle_pos;
	boolean enable_enemy_spawn, enable_tower_spawn, gameover, timeKeeper, enable_enemy_switch,
		enable_tower_switch, enable_gold, congrats_played, enable_selling, selling_state, upgrade_state,
		pause_state, drawOptionMenu, enable_killing, DEBUG, spawn;
	long prevTime, time;
	Timer timer;
	EnableSpawn enable_spawn;

	public World(OrthographicCamera camera, Level level)
	{
		
		DEBUG = true;
		
//		Vector3 map_dimensions = new Vector3(camera.viewportWidth, camera.viewportWidth, 0);
//		camera.unproject(map_dimensions);
		
		// SELECT MAP
		if (level == Level.MARIO)
			map = new MarioMap();
		else if (level == Level.POKEMON)
			map = new PokemonMap();
		
		health = 100;
		gold = 1000;
		wave_number = 0;
		time = 0;
		prevTime = 0;
		time_between_waves = 10000;
		tower_select = new TowerSelect(camera);
		options_menu = new OptionsMenu(camera);
		wave = map.getWave(0);
		current_tower = CASTLE;
		current_range = create_tower(CASTLE).getRange();
		current_enemy = EnemyEnum.MUSHROOM;	// we are spawning this type of Enemy
		gameover = false;
		timeKeeper = true;
		spawn = false;
		enable_enemy_spawn = true;
		enable_tower_spawn = false;
		enable_enemy_switch = true;
		enable_tower_switch = true;
		enable_gold = true;
		enable_selling = true;
		enable_killing = true;
		selling_state = false;
		upgrade_state = false;
		pause_state = false;
		drawOptionMenu = false;
		enemies = new ArrayList<Enemy>();
		this.camera = camera;
		font = new BitmapFont(Gdx.files.internal("data/nint.fnt"),
				Gdx.files.internal("data/nint_0.png"), false);
		katana_font = new BitmapFont(Gdx.files.internal("data/snes.fnt"),
				Gdx.files.internal("data/snes_0.png"), false);
		health_bar_max = Database.health_bar_max;
		health_bar_unsafe = Database.health_bar_unsafe;
		health_bar_safe = Database.health_bar_safe;
		hover = Database.hover;
		coin = Database.coin;
//		heart0 = new Texture("data/textures/heart0.png");
//		heart25 = new Texture("data/textures/heart25.png");
//		heart50 = new Texture("data/textures/heart50.png");
//		heart75 = new Texture("data/textures/heart75.png");
//		heart100 = new Texture("data/textures/heart100.png");
		congratulations = Gdx.audio.newSound(Gdx.files.internal("sounds/congratulations.mp3"));
		congrats_played = false;
		touch_pos = new Vector3();
		circle_pos = new Vector3();
		timer = new Timer();
		enable_spawn = new EnableSpawn();
		timer.scheduleTask(enable_spawn, 2.5f);
//		timer.schedule(spawn_enemy(), 3, )

		tower_grid = new Tower[map.getHeight()/GRID_HEIGHT][map.getWidth()/GRID_WIDTH];
//		System.out.println("tower_grid: " + tower_grid.length + " " + tower_grid[0].length);

	}

	public void render(SpriteBatch batch, ShapeRenderer shape_renderer)
	{
		batch.begin();
		
		// render map
		map.render(batch);
		
		// render tower select
		tower_select.render(batch);

		// render enemies
		for (int i = 0; i < enemies.size(); i++)
		{
			enemies.get(i).render(batch);
		}

		// render towers and bullets
		for (int i = 0; i < tower_grid.length; i++)
		{
			for (int j = 0; j < tower_grid[i].length; j++)
			{
				if (tower_grid[i][j] != null)
					tower_grid[i][j].render(batch);
			}
		}
		

		/**
		 * UI
		 */
		if (health > 0 && !(wave_number >= map.numWaves() && enemies.size() == 0))
		{// game is not over - we still have health and there are still enemies on the field
			
			// dimensions for health bars
			int bar_width = health*BAR_WIDTH/100;

			// back black bar
			batch.draw(health_bar_max, BAR_X+7, BAR_Y, BAR_WIDTH, BAR_HEIGHT);

			// choose between green bar and red health bar
			if (health > SAFE_HEALTH)
				batch.draw(health_bar_safe, BAR_X, BAR_Y, bar_width, BAR_HEIGHT);
			else
				batch.draw(health_bar_unsafe, BAR_X, BAR_Y, bar_width, BAR_HEIGHT);
			
			font.setScale(1);
			
			font.setColor(1.0f, 1.0f, 1.0f, 1.0f); // white letters for health
			font.draw(batch, " Castle Health: "+health, BAR_X+30, BAR_Y+13);
			
			font.setColor(0.16f, 0.14f, 0.13f, 1.0f); // ivoryblack
			font.draw(batch, "Wave: "+(wave_number+1)+"/"+map.numWaves(), 350, 388);
			
//			font.draw(batch, "Enemy Count: "+enemies.size(), 450, 465);
//			font.draw(batch, "Tower: "+tower_name(current_tower), 5, 370);
//			font.draw(batch, "Enemy Health: "+(enemies.size()!=0 ? enemies.get(0).getHealth() : "-"), 1, 439);
//			font.draw(batch, "Incoming Enemy: "+(wave.isEmpty() ? "" : wave.peek().getName()), 1, 439);
//			font.draw(batch, "FPS: "+Gdx.graphics.getFramesPerSecond(), 650, 465);
			
			// gold letters for gold
			font.setColor(0.16f, 0.14f, 0.13f, 1.0f); // ivoryblack
			batch.draw(coin, 530, 330);
//			System.out.println(font.getBounds("" + gold).width);
			font.draw(batch, ""+gold, 560 - font.getBounds("" + gold).width / 2, 365);
			
			if (drawOptionMenu)
				options_menu.render(batch);
			else
			{
				// testing location to represent current tile
				// TODO change it to a box representing the current tile
				batch.draw(hover, (int)(Gdx.input.getX()/GRID_WIDTH)*GRID_WIDTH, map.getHeight()-((int)(Gdx.input.getY()/GRID_HEIGHT)*GRID_HEIGHT + GRID_HEIGHT));


				// HEARTS
				// batch.draw(blackdot, 25, 25, 200, 30);
				// batch.draw(reddot, 25, 25, 2*health, 30);
				// life(batch, 642, 450);	// batch.draw(heart0, 25, 25);

				// RANGE CIRCLE
				circle_pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(circle_pos);
				circle_pos.x = ((int)(circle_pos.x / GRID_WIDTH)) * GRID_WIDTH + GRID_WIDTH / 2;
				circle_pos.y = ((int)(circle_pos.y / GRID_HEIGHT)) * GRID_HEIGHT + GRID_HEIGHT / 2;

				shape_renderer.begin(ShapeType.Circle);
				shape_renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				shape_renderer.circle(circle_pos.x, circle_pos.y, current_range);
				shape_renderer.end();
			}
			
		}
		else
		{// NO LIFE
			map.song.stop();
			font.setScale(4);
			font.setColor(1.0f, 0.0f, 0.0f, 0.8f);
			if (health <= 0)
				font.draw(batch, "GAME OVER", (600 - font.getBounds("GAME OVER").width) / 2, (400 + font.getBounds("GAME OVER").height) / 2);
			else
			{
				font.draw(batch, "CONGRATULATIONS!", (600 - font.getBounds("CONGRATULATIONS!").width) / 2, (400 + font.getBounds("CONGRATULATIONS!").height) / 2);
				congrats_played = play_once(congratulations, congrats_played, 0.5f);
			}
		}
		
		batch.end();
	}

	public void update()
	{
		// update options menu
		// if in pause state, we want to halt operations
		if (pause_state)
		{
//			System.out.println("paused pressed");
			drawOptionMenu = true;
			pause_state = options_menu.pause();
			if (mute)
			{
				volume = 0.0f;
				map.song.setVolume(volume);
			}
			else
			{
				volume = 1.0f;
				map.song.setVolume(volume);
			}

			if (options_menu.gameover)
			{
				map.song.stop();
				gameover = true;
			}
		}
		else
		{
			drawOptionMenu = false;


			int select_number = tower_select.update();
			if (select_number != -1)
			{
				if (select_number == PAUSE)
					pause_state = !pause_state;
				else if (select_number == SELL)
					selling_state = !selling_state;
				else if (select_number == UPGRADE)
					upgrade_state = !upgrade_state;
				else
				{
					current_tower = select_number;
					current_range = create_tower(current_tower).getRange();
				}
			}
//			current_tower = (tower_number == -1) ? current_tower : tower_number;

			if (health > 0)
			{
				if (spawn)
				{
					if (wave_number < map.numWaves())
					{
						if (!wave.isEmpty())
						{
							// spawn an Enemy every at speed based on current wave
							long spawnInterval;
							time = System.currentTimeMillis();
							if (wave_number < 10)
								spawnInterval = time / (1000 - wave_number*100);
							else
								spawnInterval = time / 100;
							if (timeKeeper)
							{
								prevTime = spawnInterval;
								enemies.add(wave.poll());
								timeKeeper = false;
							}
							else
							{
								if (spawnInterval > prevTime)
								{
									timeKeeper = true;
								}
							}
						}
						else // go to next wave
						{
							long temp_time = System.currentTimeMillis();
							if ((temp_time - time) > time_between_waves)
							{
								if (++wave_number < map.numWaves())
								{
									wave = map.getWave(wave_number);
									//								time_between_waves -= 500;
								}
							}
						}
					}
				}


				// spawn a tower on the position that is clicked
				// TODO change this back to input.isTouched() so that it will work for Android
				// however, this will cause problems with the selling of Towers, since right click
				// technically falls under the isTouched() method
				// if (Gdx.input.isTouched())
				if (Gdx.input.isTouched())
				{
					touch_pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					camera.unproject(touch_pos);
					// int x = (int) (touch_pos.x/GRID_WIDTH);
					// int x = xpos()/GRID_WIDTH;
					// int x = Gdx.input.getX()*(MAP_WIDTH/SCREEN_WIDTH);
					int x = (int)(touch_pos.x/GRID_WIDTH);
					int y = tower_grid.length - (int)(touch_pos.y/GRID_HEIGHT) - 1;
					//				int y = (int)(touch_pos.y/GRID_HEIGHT) - 1;

					//				System.out.println(x + " " + y);

					if (selling_state)
					{// SELL TOWER
						if (map.check_indices(x, y) && tower_grid[y][x] != null)
						{
							gold += tower_grid[y][x].getCost()/2;
							tower_grid[y][x] = null;
							map.set_placement(x, y, true);
						}
					}
					else if (enable_tower_spawn)
					{// PLACE TOWER
						Tower t = create_tower(current_tower, x*GRID_WIDTH, camera.viewportHeight - (y+1)*GRID_HEIGHT);
						if (map.placement_valid(x, y) && gold >= t.getCost())
						{
							//						towers.add(t);
							tower_grid[y][x] = t;
							map.set_placement(x, y, false);
							gold -= t.getCost();
						}
						enable_tower_spawn = false;
					}
				}
				else
					enable_tower_spawn = true;
			}// update end (health > 0) update(); 

			/**
			 * GAME OVER
			 * game over occurs if health drains to 0 or you destroy the boss
			 * TODO when boss is dead, go to next level instead of ending the game
			 */
			if (health <= 0 || (wave_number >= map.numWaves() && enemies.size() == 0))
			{
				long temp_time = System.currentTimeMillis();
				if (((temp_time/1000) - time) > 3)
				{
					if (Gdx.input.isTouched())
					{
						gameover = true;
						enable_tower_spawn = false;
					}
				}
			}

			/**
			 * KEYBOARD INPUT
			 */
			if (DEBUG)
			{
				// if S is pressed, go into selling mode
				if (Gdx.input.isKeyPressed(Keys.S))
				{
					if (enable_selling)
					{
						selling_state = !selling_state;	// toggle selling state, initially false
						enable_selling = false;
					}
				}
				else
					enable_selling = true;

				// if space bar is pressed, spawn an Enemy
				if (Gdx.input.isKeyPressed(Keys.SPACE))
				{
					if (enable_enemy_spawn)
					{
						enemies.add(create_enemy(current_enemy));
						enable_enemy_spawn = false;
					}
				}
				else
					enable_enemy_spawn = true;

				// if E is pressed, change Enemy type
				if (Gdx.input.isKeyPressed(Keys.E))
				{
					if (enable_enemy_switch)
					{
						current_enemy = EnemyEnum.values()[(current_enemy.index + 1) % (EnemyEnum.values().length)];
						enable_enemy_switch = false;
					}
				}
				else
					enable_enemy_switch = true;

				// if T is pressed, change Tower type
				if (Gdx.input.isKeyPressed(Keys.T))
				{
					if (enable_tower_switch)
					{
						current_tower = (current_tower + 1) % TOWER_COUNT;
						enable_tower_switch = false;
					}
				}
				else
					enable_tower_switch = true;

				// if G is pressed, obtain 10000 gold
				if (Gdx.input.isKeyPressed(Keys.G))
				{
					if (enable_gold)
					{
						gold += 10000;
						enable_gold = false;
					}
				}
				else
					enable_gold = true;

				// if K is pressed, kill all active enemies
				if (Gdx.input.isKeyPressed(Keys.K))
				{
					if (enable_killing)
					{
						for (Enemy e : enemies)
							e.kill();
						enable_killing = false;
					}
				}
				else
					enable_killing = true;
			}

			/**
			 * UPDATE ARRAYLISTS
			 */
			// update the enemy array
			for (int i = 0; i < enemies.size(); i++)
			{
				Enemy e = enemies.get(i);
				e.update();
				int damage = e.dealsDamage();
				if (damage != -1)
				{
					health -= damage;
				}
				if (e.isDead())
				{
					enemies.remove(i--);
					gold += e.getGold();
				}
			}

			// update the tower array
			for (int i = 0; i < tower_grid.length; i++)
			{
				for (int j = 0; j < tower_grid[i].length; j++)
				{
					if (tower_grid[i][j] != null)
						tower_grid[i][j].update();
				}
			}
		}
		
	}

//	/**
//	 * @param x position of the first heart
//	 * @param y height of hearts
//	 */
//	private void life(SpriteBatch batch, int x, int y) 
//	{
//
//		int x2 = x;
//
//		//draw 5 hearts with no health
//		for (int i = 0; i < 5; i++)
//		{
//			heart(batch, heart0, x2+=26, y);
//		}
//		x2 = x;
//		for (int i = 0; i < Math.floor(health/20); i++)
//		{
//			heart(batch, heart100, x2+=26, y);
//		}
//
//		Texture currentHealth = null;
//		if (health < 100) // execute the following only when not in full health
//		{
//			if (health%20 > 15)	
//				currentHealth = heart100;
//			else if (health%20 > 10)	
//				currentHealth = heart75;
//			else if (health%20 > 5)	
//				currentHealth = heart50;
//			else
//				currentHealth = heart25;
//			// displays the partial heart
//			heart(batch, currentHealth, x2+26, y);
//		}
//
//	}
//
//	private void heart(SpriteBatch batch, Texture corazon, int x, int y)
//	{
//		batch.draw(corazon, x, y);
//	}

	private Enemy create_enemy(EnemyEnum current_enemy)
	{// returns an instance of Enemy class based on value of current_enemy
		// enables us to quickly switch which enemy we want to spawn
		switch (current_enemy)
		{
		case MUSHROOM: return new BasicEnemy(map.getWayPoints());
		case GOOMBA: return new Goomba(map.getWayPoints());
		case SHYGUY: return new ShyGuy(map.getWayPoints());
		case KOOPA: return new Koopa(map.getWayPoints());
		case BOWSER: return new Bowser(map.getWayPoints());
		case ARCANINE: return new Arcanine(map.getWayPoints());
		case GEODUDE: return new Geodude(map.getWayPoints());
		case WEEDLE: return new Weedle(map.getWayPoints());
		case VOLTORB: return new Voltorb(map.getWayPoints());
		case TENTACOOL: return new Tentacool(map.getWayPoints());
		default: return new BasicEnemy(map.getWayPoints());
		}
	}

	private Tower create_tower(int current_tower, float x, float y)
	{// returns an instance of Tower class based on value of current_tower
		// enables us to quickly switch which tower we want to place down
		switch (current_tower)
		{
		case CASTLE: return new BasicTower(enemies, x, y);
		case HAMMER: return new HammerBros(enemies, x, y);
		case PSYCHIC: return new PsychicTower(enemies, x, y);
		case FIRE: return new FireTower(enemies, x, y);
		case GRASS: return new GrassTower(enemies, x, y);
		default: return new BasicTower(enemies, x, y);
		}
	}

	private Tower create_tower(int current_tower)
	{// returns an instance of Tower class based on value of current_tower
		// enables us to quickly switch which tower we want to place down
		float x = 0, y = 0;
		switch (current_tower)
		{
		case CASTLE: return new BasicTower(enemies, x, y);
		case HAMMER: return new HammerBros(enemies, x, y);
		case PSYCHIC: return new PsychicTower(enemies, x, y);
		case FIRE: return new FireTower(enemies, x, y);
		case GRASS: return new GrassTower(enemies, x, y);
		default: return new BasicTower(enemies, x, y);
		}
	}

	private String tower_name(int current_tower)
	{// returns the name of the current Tower
		switch(current_tower)
		{
		case CASTLE: return "Castle";
		case HAMMER: return "HammerBros";
		case PSYCHIC: return "PsychicTower";
		case FIRE: return "FireTower";
		case GRASS: return "GrassTower";
		default: return "Error";
		}
	}
	
	private boolean play_once(Sound sound, boolean has_been_played, float volume)
	{
		if (!has_been_played)
			sound.play(volume);
		return true;
	}
	
	private void spawn_enemy()
	{
		if (wave_number < map.numWaves())
		{
			if (!wave.isEmpty())
				enemies.add(wave.poll());
			else
			{// go to next wave
				
				if (++wave_number < map.numWaves())
					wave = map.getWave(wave_number);
			}
		}
	}
	
	private void next_wave()
	{
		if (++wave_number < map.numWaves())
		{
			wave = map.getWave(wave_number);
			timer.clear();
//			timer.schedule(spawn_enemy(), 5, wave_number * )
		}
	}
	
	private class EnableSpawn extends Task
	{
		
		public EnableSpawn() {}
		
		public void run()
		{
			spawn = true;
		}
		
	}
	
}
