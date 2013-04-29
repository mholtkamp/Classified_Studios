package enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import util.Point;


public class ShyGuy extends Enemy
{
	static int maxHealth = 100;
	public ShyGuy(Point[] waypoints,float difficulty,AssetManager manager)
	{
		super(waypoints, maxHealth);
		
		name = "ShyGuy";
		width = 32;
		height = 32;
		health = maxHealth;
		speed = 2.0f;
		damage = 10;
		gold_given = 25;
		animation_speed = 6;
		
		tex = new Texture[2];
		tex[0] = manager.get("data/enemies/shy_guy_right_1.png");
		tex[1] = manager.get("data/enemies/shy_guy_right_2.png");
		collider = new Rectangle((float)waypoints[0].getX(), (float)waypoints[0].getY(), width, height);
	}
	
}