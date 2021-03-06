package enemies;
import com.me.td.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import enums.Type;

import util.Point;


public class Onix extends Enemy
{
	static float maxHealth=55000;
 	
 	public Onix(Point[] waypoints, float difficulty, AssetManager manager)
 	{
		super(waypoints,maxHealth*difficulty);
		
		name = "Onix";
		width = 58;
		height = 63;
		health = (int)(maxHealth*difficulty);
		speed = 1.5f*difficulty;
		damage = 110;
		gold_given = 50;
		animation_speed = 10;
		type = Type.ROCK;
		
		tex = new Texture[2];
		tex[0] = manager.get("data/enemies/Onix1.png");
		tex[1] = manager.get("data/enemies/Onix2.png");
		collider = new Rectangle((float)waypoints[0].getX(), (float)waypoints[0].getY(), width, height);
	}
	
}
