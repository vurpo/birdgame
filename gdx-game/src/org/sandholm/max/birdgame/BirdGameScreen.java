package org.sandholm.max.birdgame;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.audio.*;
import java.util.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import aurelienribon.tweenengine.*;
import com.badlogic.gdx.physics.box2d.joints.*;

public class BirdGameScreen implements Screen {
	
	private static float UNITS_WIDE = 2.5f;
	private static float GROUND_HEIGHT = 1f;
	private static float TAP_LABEL_WIDTH = 1f;
	
	private static float PIPE_INTERVAL = 1.3f;
	private static float FADE_TIME = 0.6f;
	
	private static float SCROLLING_SPEED = 1.1f;
	
	private BirdGame game;
	private Resources resources;
	private boolean started;
	private boolean gameOver;
	private float gameOverCountdown;
	private float gameOverTime;
	private GameOverSquare gameOverSquare;
	
	private float fadeIn;
	private float fadeOut;
	private boolean resetAfterFadeOut;
	
	private SpriteBatch batch;
	private Preferences prefs;
	private TweenManager tweens;
	private float aspectRatio;
	private OrthographicCamera camera;
	private float screenHeight;

	private float distanceTraveled;
	
	private Bird bird;
	
	private Queue<Pipe> pipes;
	private float pipeSpawnTimeCounter;
	private Random random;
	
	private int points;
	private int highScore;

	private float ppuY;
	
	private float whiteFill;
	
	public BirdGameScreen(BirdGame game){
		started = false;
		whiteFill = 0f;
		gameOverCountdown = 0f;
		fadeIn = FADE_TIME;
		fadeOut = 0f;
		resetAfterFadeOut = false;
		this.game = game;
		batch = game.batch;
		tweens = new TweenManager();
		resources = game.resources;
		prefs = game.prefs;
		aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		distanceTraveled = 0f;
		pipeSpawnTimeCounter = -2f;
		pipes = new LinkedList<Pipe>();
		random = new Random();
		points = 0;
		highScore = prefs.getInteger("highscore", 0);
		gameOver = false;
		gameOverSquare = new GameOverSquare(new Vector2(0f, 0f));
		Tween.registerAccessor(GameOverSquare.class, new GameOverSquareAccessor());
	}
	
	@Override
	public void render(float delta) {
		update(delta, started);
		draw(delta);
	}
	
	public void update(float delta, boolean started) {
		tweens.update(delta);
		if (!started) {
			bird.preUpdate(delta);
			distanceTraveled += SCROLLING_SPEED*delta;
			bird.getPosition().add(SCROLLING_SPEED*delta, 0f);
			bird.getPosition().y = ((float)Math.sin(distanceTraveled*8f))*0.03f+(screenHeight/2f+0.015f);
			camera.position.set(distanceTraveled+UNITS_WIDE/2f, UNITS_WIDE/aspectRatio/2f, 0f);Gdx.gl.glClearColor(0.4f, 0.6f, 0.8f, 1);
		}
		else {
			if (gameOverCountdown > 0f) {
				gameOverCountdown -= delta;
			}
			if (!bird.isGrounded() && !bird.isDead()) {
				distanceTraveled += SCROLLING_SPEED*delta;
				bird.getPosition().add(SCROLLING_SPEED*delta, 0f);
			}
			else if (bird.isGrounded() && bird.isDead() && !gameOver) {
				gameOver = true;
				gameOverCountdown = 1f;
				if (points > prefs.getInteger("highscore", 0)) {
					prefs.putInteger("highscore", points);
					prefs.flush();
					highScore = prefs.getInteger("highscore", 0);
				}
				gameOverSquare.getPosition().set(distanceTraveled+(UNITS_WIDE/2f-gameOverSquare.WIDTH/2f), screenHeight);
				Tween.to(gameOverSquare, GameOverSquareAccessor.POS_Y, 1f)
					.target(2f)
					.ease(TweenEquations.easeOutCubic)
					.start(tweens);
				resources.swooshSound.play();
			}
			if (gameOver) {
				gameOverTime += delta;
			}
			
			if (resetAfterFadeOut && fadeOut == 0f) {
				game.resetGame();
			}
			camera.position.set(distanceTraveled+UNITS_WIDE/2f, UNITS_WIDE/aspectRatio/2f, 0f);

			bird.update(delta);
			if (bird.getPosition().y < GROUND_HEIGHT-0.05625f) {
				bird.getPosition().y = GROUND_HEIGHT-0.05625f;
				if (!bird.isGrounded() && !bird.isDead()) {
					whiteFill = 0.75f;
				}
				bird.ground();
			}
			if (!bird.isGrounded()) {
				pipeSpawnTimeCounter += delta;
			}
			if (pipeSpawnTimeCounter >= PIPE_INTERVAL) {
				pipeSpawnTimeCounter -= PIPE_INTERVAL;
				removeFirstPipe();
				spawnPipes();
			}
			for (Pipe pipe : pipes) {
				float secondPipeHeight = screenHeight-GROUND_HEIGHT-pipe.getHeight()-0.875f;
				if (distanceTraveled+0.85f >= pipe.getPosition()+0.225f
					&& distanceTraveled+0.85f-SCROLLING_SPEED*delta <= pipe.getPosition()+0.225f && !bird.isDead()) {
					score();
				}

				Rectangle collisionRectBottom = new Rectangle(pipe.getPosition()+0.0140625f, GROUND_HEIGHT, pipe.WIDTH-0.028125f, pipe.getHeight());
				Rectangle collisionRectTop = new Rectangle(pipe.getPosition()+0.0140625f, screenHeight-secondPipeHeight, pipe.WIDTH-0.028125f, secondPipeHeight+1f);

				if (Intersector.overlaps(bird.getBounds(), collisionRectBottom) | Intersector.overlaps(bird.getBounds(), collisionRectTop) && !bird.isDead()) {
					bird.crash(true);
					whiteFill = 0.75f;
				}
			}
		}
	}

	public void draw(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.65f, 0.7f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);
		for (Pipe pipe : pipes) {
			float secondPipeHeight = screenHeight-GROUND_HEIGHT-pipe.getHeight()-0.875f;
			batch.draw(resources.pipeTexture, pipe.getPosition(), screenHeight-secondPipeHeight, pipe.WIDTH, secondPipeHeight, 0f, 0f, 1f, secondPipeHeight/0.225f);
			batch.draw(resources.pipeTexture, pipe.getPosition(), GROUND_HEIGHT, pipe.WIDTH, pipe.getHeight(), 0f, pipe.getHeight()/0.225f, 1f, 0f);
		}
		batch.draw(bird.getTexture(),
				bird.getPosition().x, bird.getPosition().y,
				bird.RADIUS, bird.RADIUS,
				bird.RADIUS*2f, bird.RADIUS*2f,
				1f, 1f,
				   bird.getDrawAngle());
		
		batch.draw(resources.groundTexture, distanceTraveled, 0f, 2.5f, GROUND_HEIGHT, distanceTraveled/0.3f, 3f+(1f/3f), UNITS_WIDE/0.3f+distanceTraveled/0.3f, 0f);
		if (!gameOver) {
			BitmapFont.TextBounds pointsBounds = resources.numberFont.getBounds(points+"");
			resources.numberFont.draw(batch, points+"", distanceTraveled+(UNITS_WIDE/2f)-(pointsBounds.width/2f), screenHeight-0.75f-(gameOver ? 0.5f-gameOverCountdown*0.5f : 0f));
		}
		if (gameOver) {
			//batch.setColor(1f, 1f, 1f, 1f-Math.max(gameOverCountdown, 0f));
			batch.draw(resources.scoreBGTexture, gameOverSquare.getX(), gameOverSquare.getY(), gameOverSquare.WIDTH, gameOverSquare.WIDTH);
			//resources.numberFont.setColor(1f, 1f, 1f, 1f-Math.max(gameOverCountdown, 0f));
			resources.numberFont.draw(batch, points+"", gameOverSquare.getX()+gameOverSquare.WIDTH/2f, gameOverSquare.getY()+1.225f);
			resources.numberFont.draw(batch, highScore+"", gameOverSquare.getX()+gameOverSquare.WIDTH/2f, gameOverSquare.getY()+0.65f);
			resources.numberFont.setColor(1f, 1f, 1f, 1f);
			if (gameOverCountdown <= 0f) {
				batch.setColor(1f, 1f, 1f, ((float)Math.sin((gameOverTime-1f)*4f-(float)Math.PI/2f)+1f)/2f);
				batch.draw(resources.retryTexture, distanceTraveled+(UNITS_WIDE/2f-TAP_LABEL_WIDTH/2f), GROUND_HEIGHT+0.5f, TAP_LABEL_WIDTH, TAP_LABEL_WIDTH/4F);
			}
			batch.setColor(1f, 1f, 1f, 1f);
		}
		if (whiteFill > 0f) {
			batch.setColor(1f, 1f, 1f, whiteFill);
			batch.draw(resources.whitePixelTexture, distanceTraveled, 0f, UNITS_WIDE, screenHeight);
			whiteFill = Math.max(0f, whiteFill-4f*delta);
		}
		if (fadeIn > 0f) {
			batch.setColor(0f, 0f, 0f, fadeIn/FADE_TIME);
			batch.draw(resources.whitePixelTexture, distanceTraveled, 0f, UNITS_WIDE, screenHeight);
			fadeIn = Math.max(0f, fadeIn-delta);
		}
		if (fadeOut >= 0f && resetAfterFadeOut) {
			batch.setColor(0f, 0f, 0f, 1f-fadeOut/FADE_TIME);
			batch.draw(resources.whitePixelTexture, distanceTraveled, 0f, UNITS_WIDE, screenHeight);
			fadeOut = Math.max(0f, fadeOut-delta);
		}
		batch.end();
	}
	
	private void spawnPipes() {
		pipes.add(new Pipe(random.nextFloat()*(screenHeight-2.575f)+0.45f, distanceTraveled+UNITS_WIDE));
	}
	
	private void removeFirstPipe() {
		if (pipes.size() > 3) {
			pipes.poll();
		}
	}
	
	private void score() {
		resources.pointSound.play();
		points++;
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
		Gdx.input.setInputProcessor(new TapProcessor());
		aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		screenHeight = UNITS_WIDE/aspectRatio;
		bird = new Bird(new Vector2(0.7f, (screenHeight)/2f), resources);
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
	
	class GameOverSquare {
		private Vector2 position;
		
		public float WIDTH = 1.5f;
		
		public GameOverSquare(Vector2 pos) {position = pos;}
		
		public Vector2 getPosition() {return position;}
		
		public float getX() {return position.x;}
		
		public float getY() {return position.y;}
		
		public void setX(float x) {position.x = x;}
		
		public void setY(float y) {position.y = y;}
	}
	
	public class GameOverSquareAccessor implements TweenAccessor<GameOverSquare> {
		public static final int POS_Y = 1;
		@Override
		public int getValues(GameOverSquare target, int tweenType, float[] returnValues) {
			switch (tweenType) {
				case POS_Y:
					returnValues[0] = target.getY();
					return 2;
				default:
					assert false;
					return -1;
			}
		}
		
		@Override
		public void setValues(GameOverSquare target, int tweenType, float[] newValues) {
			switch (tweenType) {
				case POS_Y:
					target.setY(newValues[0]);
					break;
				default:
					assert false;
			}
		}
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
			if (bird.isDead() && bird.isGrounded() && !resetAfterFadeOut) {
				resources.swooshSound.play();
				fadeOut = FADE_TIME;
				resetAfterFadeOut = true;
				//game.resetGame();
			}
			else if (!started) {
				started = true;
			}
			if (bird.getPosition().y < screenHeight) {
				bird.flap();
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
