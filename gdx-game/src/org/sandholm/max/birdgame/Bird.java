package org.sandholm.max.birdgame;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.math.*;

public class Bird {
	private static final float GRAVITY = -9.81f;
	private static final float UPWARDS_SPEED = 3f;
	
	public static final float RADIUS = 0.15f;
	
	private Vector2 velocity;
	private Vector2 position;
	private Circle bounds;
	
	private float drawAngle;
	private float targetDrawAngle;
	
	private TextureRegion[] textures;
	private Animation anim;
	private float stateTime;  //NOT suitable for measuring game time
	private float deltaMultiplier;
	
	private Resources resources;
	
	private boolean dieSfxPlayed;
	private float dieSfxTimeout;
	
	private boolean dead;

	private boolean grounded;
	
	public Bird(Vector2 position, Resources resources) {
		this.resources = resources;
		this.position = position;
		bounds = new Circle(position.cpy().add(0.15f, 0.15f), 0.1125f);
		velocity = new Vector2(0f, 0f);
		textures = new TextureRegion[4];
		TextureRegion[][] tmp = TextureRegion.split(resources.birdTextures, 16, 16);
		int textureCount = 0;
		for (TextureRegion[] y : tmp) {
			for (TextureRegion x : y) {
				textures[textureCount++] = x;
			}
		}
		anim = new Animation(0.15f, textures);
		deltaMultiplier = 0f;
		drawAngle = 0f;
		dead = false;
		dieSfxTimeout = -1f;
		dieSfxPlayed = false;
	}
	
	public void flap() {
		if (!dead) {
			resources.flapSound.play();
			velocity.y = UPWARDS_SPEED;
			grounded = false;
			setDrawAngle(20f);
			dieSfxPlayed = false;
		}
	}
	
	public void ground() {
		grounded = true;
		crash(false);
	}
	
	public void crash(boolean playDieSfx) {
		if (!dead) {
			resources.crashSound.play();
			if (playDieSfx) {
				dieSfxTimeout = 0.2f;
			}
			else {
				dieSfxTimeout = -1f;
			}
			dead = true;
			velocity.y = 0f;
			setDrawAngle(-90f);
		}
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void playDieSfx() {
		resources.crashSound.play();
	}
	
	public void preUpdate(float delta) {
		stateTime += delta;
	}
	
	public void update(float delta) {
		velocity.y += GRAVITY*delta;
		velocity.y = Math.max(-4f, velocity.y);
		position.y += velocity.y*delta;
		
		if (velocity.y < -1.3f && !isDead()) {
			setDrawAngle(Math.max(velocity.y*30f, -90f));
		}
		
		if (drawAngle < targetDrawAngle) {
			drawAngle = Math.min(drawAngle+550*delta, targetDrawAngle);
		}
		else if (drawAngle > targetDrawAngle) {
			if (!dead) {
				drawAngle = Math.max(drawAngle-200*delta, targetDrawAngle);
			}
			else {
				drawAngle = Math.max(drawAngle-360*delta, targetDrawAngle);
			}
		}
		
		deltaMultiplier = (getDrawAngle()+90)/90f;
		stateTime += delta*deltaMultiplier;
		
		if (isDead()) {
			if (dieSfxTimeout > 0f) {
				dieSfxTimeout -= delta;
			}
			else if (dieSfxTimeout != -1f && dieSfxTimeout <= 0f) {
				resources.dieSound.play();
				dieSfxTimeout = -1f;
			}
		}
	}
	
	public void dispose() {
		resources.flapSound.dispose();
	}
	
	public boolean isGrounded() {
		return grounded;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public void setPosition(Vector2 position) {
		this.position = position;
	}
	
	public Circle getBounds() {
		bounds.x = position.x+0.15f;
		bounds.y = position.y+0.15f;
		return bounds;
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}
	
	public float getDrawAngle() {
		return drawAngle;
	}
	
	public void setDrawAngle(float angle) {
		targetDrawAngle = angle;
	}
	
	public TextureRegion getTexture() {
		return anim.getKeyFrame(stateTime, true);
	}
}
