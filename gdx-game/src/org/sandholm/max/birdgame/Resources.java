package org.sandholm.max.birdgame;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;

public class Resources {
	public Texture birdTextures;
	public Texture pipeTexture;
	public Texture groundTexture;
	public Texture logoTexture;
	public Texture whitePixelTexture;
	public Texture playTexture;
	public Texture retryTexture;
	public Texture scoreBGTexture;
	
	public BitmapFont numberFont;
	public BitmapFont sampleFont;
	
	public Sound flapSound;
	public Sound crashSound;
	public Sound dieSound;
	public Sound pointSound;
	public Sound swooshSound;
	
	public Resources() {
		birdTextures = new Texture(Gdx.files.internal("birdatlas.png"));
		pipeTexture = new Texture(Gdx.files.internal("pipe.png"));
		groundTexture = new Texture(Gdx.files.internal("ground.png"));
		groundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
		logoTexture = new Texture(Gdx.files.internal("logo.png"));
		whitePixelTexture = new Texture(Gdx.files.internal("pixel.png"));
		playTexture = new Texture(Gdx.files.internal("play.png"));
		retryTexture = new Texture(Gdx.files.internal("retry.png"));
		scoreBGTexture = new Texture(Gdx.files.internal("scorebg.png"));
		
		flapSound = Gdx.audio.newSound(Gdx.files.internal("sound/sfx_wing.ogg"));
		crashSound = Gdx.audio.newSound(Gdx.files.internal("sound/sfx_hit.ogg"));
		dieSound = Gdx.audio.newSound(Gdx.files.internal("sound/sfx_die.ogg"));
		pointSound = Gdx.audio.newSound(Gdx.files.internal("sound/sfx_point.ogg"));
		swooshSound = Gdx.audio.newSound(Gdx.files.internal("sound/sfx_swooshing.ogg"));
		
		numberFont = new BitmapFont(Gdx.files.internal("numbers.fnt"));
		numberFont.setUseIntegerPositions(false);
		numberFont.setScale(0.02f);
		
	}
	
	public void dispose() {
		birdTextures.dispose();
		pipeTexture.dispose();
		groundTexture.dispose();
		logoTexture.dispose();
		whitePixelTexture.dispose();
		playTexture.dispose();
		retryTexture.dispose();
		scoreBGTexture.dispose();
		
		flapSound.dispose();
		crashSound.dispose();
		dieSound.dispose();
		pointSound.dispose();
		swooshSound.dispose();
		
		numberFont.dispose();
	}
}
