package enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import util.Point;


public class galagaEnemy2 extends Enemy
{
	static int maxHealth=400;
 	
 	public galagaEnemy2(Point[] waypoints, float difficulty, AssetManager manager)
 	{
		super(waypoints,maxHealth);
		
		name = "G Enemy 2";
		width = 28;
		height = 42;
		health = (int)(maxHealth*difficulty);
		speed = 1.5f;
		damage = (int)(20*difficulty);
		gold_given = 50;
		animation_speed = 10;
		can_attack = true;
		
		tex = new Texture[2];
		tex[0] = manager.get("data/enemies/galagaEnemy2.png");
		tex[1] = manager.get("data/enemies/galagaEnemy2.png");
		collider = new Rectangle((float)waypoints[0].getX(), (float)waypoints[0].getY(), width, height);
	}
	
}
