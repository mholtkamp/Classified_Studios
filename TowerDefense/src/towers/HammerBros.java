package towers;

import bullets.Bullet;
import bullets.Hammer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import enemies.Enemy;

import java.util.ArrayList;


public class HammerBros extends Tower
{

	private Texture tex_left, tex_right;
	
	public HammerBros(ArrayList<Enemy> enemies, float x, float y)
	{
		super(enemies, x, y);
		
		// attributes - change for each new Tower
		name = "HammerBros";
		width = 40;
		height = 48;
		range = 150;
		cost = 500;
		firing_speed = 1.0f;	// shoot every x seconds
		
		center_x = x + width/2;
		center_y = y + height/2;
		collider = new Rectangle(x, y, width, height);
		
		tex_left = new Texture("data/towers/hammer_bros_left_idle.png");
		tex_right = new Texture("data/towers/hammer_bros_right_idle.png");
	}
	
	public void update()
	{
		for (int i = 0; i < bullets.size(); i++)
		{
			bullets.get(i).update();
			if (!bullets.get(i).isActive())
				bullets.remove(i--);
		}
		time_since_last_shot += Gdx.graphics.getDeltaTime();
		if (time_since_last_shot > firing_speed)
		{
			for (int i = 0; i < enemies.size(); i++)
			{
				float xE = (center_x - enemies.get(i).getX());
				float yE = (center_y - enemies.get(i).getY());
				if (Math.sqrt(xE*xE + yE*yE) < range)
				{
					target = enemies.get(i);
					bullets.add(new Hammer(target, center_x, center_y));
					time_since_last_shot = 0;
					break;
				}
			}
		}
	}
	
	public void render(SpriteBatch batch)
	{
		// render Tower
		if (face_left())
			batch.draw(tex_left, collider.x, collider.y);
		else
			batch.draw(tex_right, collider.x, collider.y);
		
		// render Bullets
		for (Bullet bullet : bullets)
			bullet.render(batch);
	}
}
