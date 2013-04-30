package enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import enums.Type;

import util.Point;


public class Voltorb extends Enemy
{
	static int maxHealth = 325;
	public Voltorb(Point[] waypoints,float difficulty,AssetManager manager)
	{
		super(waypoints,maxHealth);
		
		name = "Voltorb";
		width = 29;
		height = 29;
		health = (int)(maxHealth*difficulty);
		speed = 1.5f;
		damage = (int)(20*difficulty);
		gold_given = 40;
		animation_speed = 20;
		type = Type.ELECTRIC;
		
		tex = new Texture[2];
		tex[0] = manager.get("data/enemies/voltorb_right_1.png");
		tex[1] = manager.get("data/enemies/voltorb_right_2.png");
		collider = new Rectangle((float)waypoints[0].getX(), (float)waypoints[0].getY(), width, height);
	}
	
}
