package bullets;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.td.World;

import enemies.Enemy;

public class Bomb extends Bullet{
	
	// change WIDTH and HEIGHT for each new Bullet
	private final int WIDTH = 40, HEIGHT = 40;
	private Texture tex, explodeTex;
	private boolean exploding,firstHit;
	private float explodeTimer;
	private Rectangle explosionCollider;
	private ArrayList<Enemy> enemies;
	private Sound blow;
	private boolean has_been_played = false;
	
	public Bomb(Enemy target, float center_x, float center_y, int damagemultiplier, ArrayList<Enemy> enemies,AssetManager manager)
	{
		this.enemies = enemies;
		this.center_x = center_x;
		this.center_y = center_y;
		collider = new Rectangle(center_x, center_y, WIDTH, HEIGHT);
		this.target = target;
		active = true;
		
		Sound shoot = manager.get("sounds/Bomb_Drop.mp3");
		blow = manager.get("sounds/Bomb_Blow.mp3");
		shoot.play(World.volume);
		
		// attributes - change for each new Bullet
		tex = manager.get("data/bullets/zeldaBomb.png");
		explodeTex = manager.get("data/bullets/bombExplosion.png");
		damage = 200*damagemultiplier;
		speed = 10;
		exploding = false;
		firstHit = false;
		explodeTimer = 1;
		explosionCollider = new Rectangle(0,0,30,30);
	}
	
	public void render(SpriteBatch batch)
	{
		if (active)
		{
			if(!exploding)
				batch.draw(tex, collider.x, collider.y, collider.width, collider.height);
			else
			{
				batch.draw(explodeTex,explosionCollider.x,explosionCollider.y,explosionCollider.width,explosionCollider.height);
				if (!has_been_played)
				{
					blow.play(World.volume);
					has_been_played = true;
				}
			}
		}
	}
	
	public void update()
	{
		if (active && !exploding)
		{
			if (target == null)
				active = false;
			else
			{
				float xE = (target.getCollider().getWidth()/2 + target.getX()) - center_x;
				float yE = (target.getCollider().getHeight()/2 + target.getY()) - center_y;
				float hE = (float)Math.sqrt(xE*xE + yE*yE);
				collider.x += (xE/hE)*speed;
				collider.y += (yE/hE)*speed;
				if (collider.overlaps(target.getCollider()))
				{// hit target
/*					if((target.getType() == WATER) || (target.getType() == GRASS))
					{
						target.subHealth((int)(damage*0.5));
					}
					else if((target.getType() == FIRE) ||(target.getType() == ROCK))
					{
						target.subHealth(damage*2);
					}
					else 
*/					target.subHealth(damage);
					
					exploding = true;
					explosionCollider.x = collider.x + (explosionCollider.width/3);
					explosionCollider.y = collider.y + (explosionCollider.height/3);
				}
			}
		}
		else if(active && exploding)
		{
			explodeTimer = explodeTimer - Gdx.graphics.getDeltaTime();
			if(!firstHit)
			{
				for(int i = 0; i < enemies.size(); i++)
				{
					if(enemies.get(i).getCollider().overlaps(explosionCollider))
						enemies.get(i).subHealth(25);
				}
				firstHit = true;
			}
			if(explodeTimer <= 0)
				active = false;
		}
	}
	
}
