package org.sandholm.max.birdgame;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;

public class Pipe {
	public static final float WIDTH = 0.45f;
	
	private float height;
	private float xPosition;
	
	public Pipe(float height, float position) {
		this.height = height;
		xPosition = position;
	}
	
	public Float getHeight() {
		return height;
	}
	
	public float getPosition() {
		return xPosition;
	}
	
	public void setPosition(float position) {
		xPosition = position;
	}
	
}
