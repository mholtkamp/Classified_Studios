package enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.me.td.World;

import enums.Type;

import util.Point;


public class Arcanine extends Enemy
{
	static float maxHealth=500;
 	
 	public Arcanine(Point[] waypoints, float difficulty, AssetManager manager)
 	{
		super(waypoints,maxHealth*difficulty);
		
 		
 		name = "Arcanine";
 		width = 36;
 		height = 34;
		health = (int)(maxHealth*difficulty);
		speed = 1.5f;
		damage = (int)(20*difficulty);
		gold_given = 50;
		animation_speed = 10;
		type = Type.FIRE;
		
		tex = new Texture[2];
		tex[0] = manager.get("data/enemies/arcanine_right_1.png");
		tex[1] = manager.get("data/enemies/arcanine_right_2.png");
		collider = new Rectangle((float)waypoints[0].getX(), (float)waypoints[0].getY(), width, height);
	}
	
}
