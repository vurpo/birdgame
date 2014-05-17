package org.sandholm.max.birdgame;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.*;
import aurelienribon.tweenengine.*;
import com.badlogic.gdx.math.*;

public class TitleScreen implements Screen
{

	private static float UNITS_WIDE = 2.5f;
	private static float LOGO_WIDTH = 1.5f;
	private static float TAP_LABEL_WIDTH= 1F;
	private static float GROUND_HEIGHT = 1f;

	private static float SCROLLING_SPEED = 1.1f;
	private static float FADE_TIME = 0.6f;

	private BirdGame game;
	private float fadeOut;
	private boolean startGameAfterFadeOut;
	private float titleScreenTime;
	private Resources resources;
	private SpriteBatch batch;
	private float aspectRatio;
	private OrthographicCamera camera;
	private float screenHeight;

	private float distanceTraveled;

	private Bird bird;
	
	private float ppuY;

	public TitleScreen(BirdGame game){
		fadeOut = 0f;
		
		titleScreenTime = 0f;
		this.game = game;
		batch = game.batch;
		resources = game.resources;
		aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		distanceTraveled = 0f;
	}

	@Override
	public void render(float delta) {
		update(delta);
		draw(delta);
	}

	public void update(float delta) {
		titleScreenTime += delta;
		if (startGameAfterFadeOut && fadeOut == 0f) {
			game.resetGame();
		}
		bird.preUpdate(delta);
		distanceTraveled += SCROLLING_SPEED*delta;
		bird.getPosition().add(SCROLLING_SPEED*delta, 0f);
		bird.getPosition().y = ((float)Math.sin(distanceTraveled*8f))*0.03f+(screenHeight/2f+0.015f);
		camera.position.set(distanceTraveled+UNITS_WIDE/2f, UNITS_WIDE/aspectRatio/2f, 0f);Gdx.gl.glClearColor(0.4f, 0.6f, 0.8f, 1);
	}

	public void draw(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.65f, 0.7f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);
		batch.draw(bird.getTexture(),
				   bird.getPosition().x, bird.getPosition().y,
				   bird.RADIUS, bird.RADIUS,
				   bird.RADIUS*2f, bird.RADIUS*2f,
				   1f, 1f,
				   bird.getDrawAngle());
		batch.draw(resources.groundTexture, distanceTraveled, 0f, 2.5f, GROUND_HEIGHT, distanceTraveled/0.3f, 3f+(1f/3f), UNITS_WIDE/0.3f+distanceTraveled/0.3f, 0f);
		batch.draw(resources.logoTexture, distanceTraveled+(UNITS_WIDE/2f-LOGO_WIDTH/2f), screenHeight-LOGO_WIDTH/2f-(UNITS_WIDE/2f-LOGO_WIDTH/2f), LOGO_WIDTH, LOGO_WIDTH/2f);
		batch.setColor(1f, 1f, 1f, ((float)Math.sin(titleScreenTime*4f-(float)Math.PI/2f)+1f)/2f);
		batch.draw(resources.playTexture, distanceTraveled+(UNITS_WIDE/2f-TAP_LABEL_WIDTH/2f), GROUND_HEIGHT+0.5f, TAP_LABEL_WIDTH, TAP_LABEL_WIDTH/4F);
		if (fadeOut >= 0f && startGameAfterFadeOut) {
			batch.setColor(0f, 0f, 0f, 1f-fadeOut/FADE_TIME);
			batch.draw(resources.whitePixelTexture, distanceTraveled, 0f, UNITS_WIDE, screenHeight);
			fadeOut = Math.max(0f, fadeOut-delta);
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		aspectRatio = (float)width/(float)height;
		camera.setToOrtho(false, UNITS_WIDE, UNITS_WIDE/aspectRatio);
		screenHeight = UNITS_WIDE/aspectRatio;
		ppuY = height/screenHeight;
	}

	@Override
	public void show() {
		resources.swooshSound.play();
		Gdx.input.setInputProcessor(new TapProcessor());
		aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		screenHeight = UNITS_WIDE/aspectRatio;
		bird = new Bird(new Vector2(UNITS_WIDE/2f-bird.RADIUS, (screenHeight)/2f), resources);
	}

	@Override
	public void hide() {
		// TODO: Implement this method
	}

	@Override
	public void pause() {
		// TODO: Implement this metho
	}

	@Override
	public void resume() {
		// TODO: Implement this method
	}

	@Override
	public void dispose() {
		bird.dispose();
	}

	class TapProcessor implements InputProcessor {

		@Override
		public boolean keyDown(int p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean keyUp(int p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean keyTyped(char p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean touchDown(int p1, int p2, int p3, int p4) {
			if (!startGameAfterFadeOut) {
				resources.swooshSound.play();
				fadeOut = FADE_TIME;
				startGameAfterFadeOut = true;
			}
			return false;
		}

		@Override
		public boolean touchUp(int p1, int p2, int p3, int p4) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean touchDragged(int p1, int p2, int p3) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean mouseMoved(int p1, int p2) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean scrolled(int p1) {
			// TODO: Implement this method
			return false;
		}
	}
}
