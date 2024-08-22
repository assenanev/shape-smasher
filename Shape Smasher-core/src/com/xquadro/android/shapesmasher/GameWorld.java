package com.xquadro.android.shapesmasher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.xquadro.android.shapesmasher.Shape.Behaviour;
import com.xquadro.android.shapesmasher.Shape.Colour;
import com.xquadro.android.shapesmasher.Shape.Type;

public class GameWorld {
	public enum State {
		RUNNING, ENDING, GAME_OVER
	}; // maybe NEXT_LEVEL

	public static final float WORLD_WIDTH = 100;

	final float worldHeight;

	public float health = 100;

	public interface WorldListener {
		public void click();

		public void clickWrong();

		public void miss();
	}

	private float accumulator = 0;
	private final static float TICK = 1 / 60f;

	State state;
	float stateTime;

	public float gravity = 10; // probably del

	public ShapePool shapePool;
	public final List<Shape> shapes;

	Vector3 touchPoint;

	public final WorldListener listener;

	public Random rand = new Random();

	int score = 0;

	ParticleEffectPool trailFXPool;
	Array<PooledEffect> trailFXs = new Array<PooledEffect>();
	
	ParticleEffectPool touchFXPool;
	Array<PooledEffect> touchFXs = new Array<PooledEffect>();

//	public float particleSize = (float)Gdx.graphics.getWidth()/(WORLD_WIDTH);
//	if (particleSize < 3.5)
	
	float bannerHeight = 0;

	public GameWorld(WorldListener listener, float aspect) {

		aspect = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();

		worldHeight = WORLD_WIDTH / aspect;
		
		bannerHeight = (float) worldHeight - 
				(60 * worldHeight ) / Gdx.graphics.getHeight() - 
				Shape.DIAMETER * 2f;

		this.listener = listener;
		state = State.RUNNING;

		this.shapePool = new ShapePool();
		this.shapes = new ArrayList<Shape>();

		ParticleEffect touchEffect = new ParticleEffect();
		touchEffect.load(Gdx.files.internal("data/pe/touch.p"),
				Gdx.files.internal("data/pe"));
		touchFXPool = new ParticleEffectPool(touchEffect, 1, 200);
		
		ParticleEffect trailEffect = new ParticleEffect();
		trailEffect.load(Gdx.files.internal("data/pe/trail.p"),
				Gdx.files.internal("data/pe"));
		trailFXPool = new ParticleEffectPool(trailEffect, 1, 200);
	}

	private void addShapes() {

		if (rand.nextFloat() > 0.97f) {
		//if (shapes.size() < 1) {
			Shape s = shapePool.obtain();
			int t = rand.nextInt(7);
			Shape.Type type;
			switch (t) {
			case 0:
				type = Type.CIRCLE;
				break;
			case 1:
				type = Type.TRIANGLE;
				break;
			case 2:
				type = Type.RECTANGLE;
				break;
			case 3:
				type = Type.STAR;
				break;
			case 4:
				type = Type.PENTAGON;
				break;
			case 5:
				type = Type.HEXAGON;
				break;
			case 6:
				type = Type.PLUS;
				break;
			default:
				type = Type.CIRCLE;
				break;
			}

			int c = rand.nextInt(7);

			Shape.Colour colour;
			switch (c) {
			case 0:
				colour = Colour.RED;
				break;
			case 1:
				colour = Colour.BLUE;
				break;
			case 2:
				colour = Colour.GREEN;
				break;
			case 3:
				colour = Colour.CYAN;
				break;
			case 4:
				colour = Colour.MAGENTA;
				break;
			case 5:
				colour = Colour.YELLOW;
				break;
			case 6:
				colour = Colour.WHITE;
				break;
			default:
				colour = Colour.RED;
				break;
			}

			Shape.Behaviour behaviour;
			int b = rand.nextInt(5);
			float angle = 270;

			switch (b) {
			case 0:
				behaviour = Behaviour.FALL;
				s.gravity = gravity;
				break;
			case 1:
				behaviour = Behaviour.ZIGZAG;
				s.gravity = gravity;
				angle = rand.nextFloat() * 90 + 45 + 180;
				break;
			case 2:
				behaviour = Behaviour.TELEPORT;
				s.gravity = gravity;
				break;
			case 3:
				behaviour = Behaviour.RAND;
				s.gravity = gravity;
				angle = rand.nextFloat() * 90 + 45 + 180;
				break;
			case 4:
				behaviour = Behaviour.SINE;
				s.gravity = gravity;
				break;

			default:
				behaviour = Behaviour.ZIGZAG;
				s.gravity = gravity;
				break;
			}
			
			PooledEffect trailFX = null;
			if(Settings2.fxEnabled){
				trailFX = trailFXPool.obtain();
			}
			s.position.set((WORLD_WIDTH - Shape.DIAMETER) * rand.nextFloat()
					+ Shape.DIAMETER / 2, worldHeight + Shape.DIAMETER / 2);
			s.init(type, colour, behaviour, gravity, angle, worldHeight,
					WORLD_WIDTH, trailFX);			
			shapes.add(s);
		}
	}

	public void update(float deltaTime) {
		stateTime += deltaTime;
		switch (state) {
		case RUNNING:
			updateRunning(deltaTime);
			break;
		case ENDING:
			updateEnding(deltaTime);
			break;
		case GAME_OVER:
			break;
		default:
			break;
		}

	}

	private void updateEnding(float deltaTime) {
		if (stateTime > 0.5f) {
			state = State.GAME_OVER;
		}
	}

	public void updateRunning(float deltaTime) {
		updatePhysics(deltaTime);
		updateTouchFX(deltaTime);
		updateTrailFX(deltaTime);
		checkGameOver();
	}

	public void touch(Vector3 touchPoint) {
		int len = shapes.size();
		for (int i = 0; i < len; i++) {
			Shape s = shapes.get(i);
			if (s.position.dst2(touchPoint.x, touchPoint.y) < (Shape.DIAMETER / 2)
					* (Shape.DIAMETER / 2)
					&& s.state == Shape.State.ALIVE) {
				if (s.type == Settings2.KILL_TYPE
						|| s.colour == Settings2.KILL_COLOUR) {
					health -= 20;
					listener.clickWrong();
				} else {
					addScore();
					listener.click();
				}
				s.state = Shape.State.EXPLODED;
				return;
			}
		}
	}

	public void updateShapes(float deltaTime) {
		for (int i = shapes.size() - 1; i >= 0; i--) {
			Shape s = shapes.get(i);

			switch (s.state) {
			case EMERGE:
				if (s.position.y < bannerHeight) {
					s.state = Shape.State.ALIVE;
				}
				s.update(deltaTime);
				break;
			case ALIVE:
				if (s.position.y < Shape.DIAMETER / 2) {
					if (s.type != Settings2.KILL_TYPE
							&& s.colour != Settings2.KILL_COLOUR) {
						health -= 10;
						listener.miss();
					} else {
						addScore();
						listener.click();
					}
					s.state = Shape.State.DEAD;
				}
				s.update(deltaTime);
				

				break;
			case EXPLODED:
				PooledEffect trailFX = s.trailFX;
				if(trailFX != null){
					trailFX.allowCompletion();
					trailFXs.add(trailFX);	
					s.removeTrailFX();
				}
				if(Settings2.fxEnabled){
					PooledEffect effect = touchFXPool.obtain();
					effect.setPosition(s.position.x, s.position.y);			
					effect.getEmitters().get(0).getTint().setColors(s.getColors());
					touchFXs.add(effect);
				}
				
				s.state = Shape.State.DEAD;
				break;

			case DEAD:
			default:
				shapes.remove(i);						
				shapePool.free(s);
				break;
			}
		}
	}

	public void updatePhysics(float deltaTime) {
		float dt = MathUtils.clamp(deltaTime, 0, 0.030f);
		accumulator += dt;
		while (accumulator > TICK) {
			accumulator -= TICK;
			updateGravity(TICK);
			addShapes();
			addHealth(TICK);
			updateShapes(TICK);
		}
	}

	public void updateTouchFX(float deltaTime) {
		for (int i = touchFXs.size - 1; i >= 0; i--) {
			PooledEffect effect = touchFXs.get(i);
			effect.update(deltaTime);
			if (effect.isComplete()) {
				effect.free();
				touchFXs.removeIndex(i);
			}
		}
	}
	
	public void updateTrailFX(float deltaTime) {
		for (int i = trailFXs.size - 1; i >= 0; i--) {
			PooledEffect effect = trailFXs.get(i);
			effect.update(deltaTime);
			if (effect.isComplete()) {
				effect.free();
				trailFXs.removeIndex(i);
			}
		}
	}

	private void addHealth(float tick) {
		if (health > 0 && health < 100) {
			health += (tick / 3);
		}
	}

	private void updateGravity(double tick) {
		gravity += (tick / 10);

	}

	private void addScore() {
		score += 10;
		if (score > 99999) {
			score = 0;
		}
	}

	private void checkGameOver() {
		if (health <= 0) {
			state = State.GAME_OVER;
			stateTime = 0;
		}
	}


}
