package com.xquadro.android.shapesmasher;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;


public class Shape{
	public final static float DIAMETER = 8f;
	public enum State {
		EMERGE, ALIVE, EXPLODED, DEAD
	};

	public enum Type {
		CIRCLE, TRIANGLE, RECTANGLE, STAR, PENTAGON, HEXAGON, PLUS
	};
	
	public enum Colour {
		RED, BLUE, GREEN, CYAN, MAGENTA, YELLOW, WHITE
	};

	
	public enum Behaviour {
		FALL, ZIGZAG, SINE, TELEPORT, RAND
	}
	
	public enum BehaviourState {
		FALLING, WAITING
	}

	public Type type = Type.CIRCLE;
	public State state = State.DEAD;
	public Colour colour = Colour.WHITE;
	public Behaviour behaviour = Behaviour.FALL;
	public BehaviourState behaviourState = BehaviourState.FALLING;
	public float stateTime;
	public float behaviourStateTime;

	public final Vector2 position = new Vector2();
	public final Vector2 acceleration = new Vector2();
	public final Vector2 velocity = new Vector2();
	public float angle;
	public float gravity;
	
	public int teleportCount = 6;
	public float teleportWaitTime = 3f;
	
	public float randFallTime = 2.5f;
	
	float worldHeight;
	float worldWidth;
	
	float sineDirection;

	Random rand = new Random();
	PooledEffect trailFX;

	public void init(Type type, Colour colour, Behaviour behaviour, float gravity, float angle, float worldHeight, float worldWidth, PooledEffect trailFX) {
		this.type = type;
		this.colour = colour;
		this.behaviour = behaviour;
		state = State.EMERGE;
		stateTime = 0;

		
		behaviourState = BehaviourState.FALLING;
		behaviourStateTime = 0;
		
		sineDirection = rand.nextFloat() > 0.5f ? (-1) : 1;
		
		this.worldHeight = worldHeight;
		this.worldWidth = worldWidth;
		
		this.gravity = gravity;
		this.angle = angle;
		this.trailFX = trailFX;
		if(trailFX != null) {
			this.trailFX.reset();
			this.trailFX.setPosition(this.position.x, this.position.y);
			this.trailFX.getEmitters().get(0).getTint().setColors(this.getColors());
		}
		updateVelocity(angle);
	}

	public float[] getColors(){
		float[] colors = {1, 1, 1};
		switch(this.colour){
		case RED:
			colors[0] = 1f;
			colors[1] = 0f;
			colors[2] = 0f;
			break;
		case BLUE:
			colors[0] = 0f;
			colors[1] = 0f;
			colors[2] = 1f;
			break;
		case GREEN:
			colors[0] = 0f;
			colors[1] = 1f;
			colors[2] = 0f;
			break;
		case CYAN:
			colors[0] = 0f;
			colors[1] = 1f;
			colors[2] = 1f;
			break;
		case MAGENTA:
			colors[0] = 1f;
			colors[1] = 0f;
			colors[2] = 1f;
			break;
		case YELLOW:
			colors[0] = 1f;
			colors[1] = 1f;
			colors[2] = 0f;
			break;
		case WHITE:
			colors[0] = 1f;
			colors[1] = 1f;
			colors[2] = 1f;
			break;		
		default:
			break;
		}

		return colors;
	}

	private void updateVelocity(float angle) {
		velocity.set(gravity*MathUtils.cosDeg(angle), gravity*MathUtils.sinDeg(angle));
//		velocity.set(0, gravity);
//		velocity.setAngle(angle);
	}

	public void update(float deltaTime) {
		stateTime += deltaTime;	
		if(trailFX != null) {
			trailFX.update(deltaTime);
			trailFX.setPosition(this.position.x, this.position.y);
		}
		switch (behaviour) {
		case FALL:
			updateFall(deltaTime);
			break;
		case ZIGZAG:
			updateZigzag(deltaTime);
			break;
		case TELEPORT:
			updateTeleport(deltaTime);
			break;
		case RAND:
			undateRand(deltaTime);
			break;
		case SINE:
			updateSine(deltaTime);
			break;
		default:
			break;
		}
			
		
	}
	
	private void updateSine(float deltaTime) {

		angle = angle + deltaTime*30*sineDirection;
		if (angle > 320) {
			sineDirection *= -1;
			angle = 320;
		} else if(angle < 220) {
			sineDirection *= -1;
			angle = 220;
		}
		updateVelocity(angle);
		updateZigzag(deltaTime);		
		
	}


	private void undateRand(float deltaTime) {
		behaviourStateTime+=deltaTime;
		switch (behaviourState) {
		case FALLING:
			updateZigzag(deltaTime);
			if(behaviourStateTime > randFallTime) {
				behaviourState = BehaviourState.WAITING;
				behaviourStateTime = 0;
			}
			break;
		case WAITING:
			angle = rand.nextFloat()*120 + 30 + 180;
			updateVelocity(angle);
			behaviourState = BehaviourState.FALLING;
			behaviourStateTime = 0;			
			break;
		default:
		}		
		
	}

	
	private void updateTeleport(float deltaTime) {
		behaviourStateTime+=deltaTime;
		switch (behaviourState) {
		case FALLING:
			position.set((worldWidth - DIAMETER) * rand.nextFloat() + DIAMETER/2, 
					position.y - (worldHeight/teleportCount)*rand.nextFloat());
			behaviourState = BehaviourState.WAITING;
			behaviourStateTime = 0;
			break;
		case WAITING:
		default:
			if (behaviourStateTime > teleportWaitTime){
				behaviourState = BehaviourState.FALLING;
				behaviourStateTime = 0;
			}
			
			break;
		}
		
	}



	void updateZigzag(float deltaTime){
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		if (position.x < DIAMETER/2 || position.x > GameWorld.WORLD_WIDTH - DIAMETER/2) {
			velocity.x *= -1;
			angle = velocity.angle();
		}	
	}
	
	void updateFall(float deltaTime){
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
	}
	
	void removeTrailFX(){
		trailFX = null;
	}

}
