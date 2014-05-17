package org.sandholm.max.birdgame;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import aurelienribon.tweenengine.*;

public class BirdGame extends Game
{
    public SpriteBatch batch;
	public Resources resources;
	public Preferences prefs;
	private BirdGameScreen birdGameScreen;
	private TitleScreen titleScreen;
	
	@Override
	public BirdGame(Preferences prefs) {
		super();
		this.prefs = prefs;
	}

    public void create() {
        batch = new SpriteBatch();
		resources = new Resources();
		titleScreen = new TitleScreen(this);
        setScreen(titleScreen);
    }
	
	public void resetGame() {
		birdGameScreen = new BirdGameScreen(this);
		setScreen(birdGameScreen);
	}

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
		resources.dispose();
    }
}
