package enemies;
import com.me.td.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import util.Point;


public class Eye extends Enemy
{
	static float maxHealth=800;
 	
 	public Eye(Point[] waypoints, float difficulty, AssetManager manager)
 	{
		super(waypoints,maxHealth*difficulty);
		
		name = "Eye";
		width = 28;
		height = 42;
		health = (int)(maxHealth*difficulty);
		speed = 1.0f;
		damage = (int)(40*difficulty);
		gold_given = 50;
		animation_speed = 10;
		
		tex = new Texture[2];
		tex[0] = manager.get("data/enemies/zeye1.png");
		tex[1] = manager.get("data/enemies/zeye2.png");
		collider = new Rectangle((float)waypoints[0].getX(), (float)waypoints[0].getY(), width, height);
	}
	
}
