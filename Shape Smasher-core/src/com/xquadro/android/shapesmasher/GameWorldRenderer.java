package com.xquadro.android.shapesmasher;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.xquadro.android.shapesmasher.Shape.State;


public class GameWorldRenderer {

	float aspect;
	
	GameWorld world;
	OrthographicCamera camera;
	SpriteBatch batch;
	
	AssetManager assetManager;

	TextureAtlas atlas;
	AtlasRegion bg;

	TweenManager tm;

	
	public GameWorldRenderer (SpriteBatch batch, GameWorld world, AssetManager assetManager) {
		this.world = world;
		//this.atlas = atlas;
		this.assetManager = assetManager;

		this.atlas = assetManager.get("data/atlases/ssmasher.atlas",
				TextureAtlas.class);
		
		aspect = (float) Gdx.graphics.getHeight()/Gdx.graphics.getWidth();
		camera = new OrthographicCamera(GameWorld.WORLD_WIDTH,  GameWorld.WORLD_WIDTH * aspect);
		camera.position.set(GameWorld.WORLD_WIDTH / 2, GameWorld.WORLD_WIDTH / 2, 0);
		camera.update();
		this.batch = batch;
		batch.setProjectionMatrix(camera.combined);
	}
	


	public void render (float deltaTime) {
		
		if(world.state == GameWorld.State.GAME_OVER) {
			return;
		}
		
		if (world.state == GameWorld.State.RUNNING) {
			camera.position.set(GameWorld.WORLD_WIDTH/2, world.worldHeight/2, 0);
		}
			
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		renderObjects(deltaTime);
	}

	public void renderObjects (float deltaTime) {

		batch.enableBlending();
		batch.begin();
		renderTrailFX(deltaTime);
		renderTouchFX(deltaTime);
		renderShapes();
		batch.end();
	}

	public void renderTouchFX(float deltaTime) {
		for (int i = world.touchFXs.size - 1; i >= 0; i--) {
			PooledEffect effect = world.touchFXs.get(i);
			effect.draw(batch, deltaTime);
		}
	}
	
	public void renderTrailFX(float deltaTime) {
		
		int len = world.shapes.size();
		for(int i = 0; i < len; i++){
			Shape s = world.shapes.get(i);
			PooledEffect pe = s.trailFX;
			if(pe != null && s.behaviour != Shape.Behaviour.TELEPORT){
				pe.draw(batch, deltaTime);
			}
		}
	}

	private void renderShapes() {
		int len = world.shapes.size();
		Color tempColor;
		tempColor = batch.getColor();
		
		for(int i = 0; i < len; i++){
			Shape s = world.shapes.get(i);
			
			TextureRegion t;
			TextureRegion bg;

			switch (s.type) {
			case CIRCLE:
				bg = atlas.findRegion("circlebg");		
				t = atlas.findRegion("circle");				
				break;
			case TRIANGLE:
				bg = atlas.findRegion("trianglebg");
				t = atlas.findRegion("triangle");		
				break;
				
			case RECTANGLE:
				bg = atlas.findRegion("rectanglebg");
				t = atlas.findRegion("rectangle");				
				break;
			case PENTAGON:
				bg = atlas.findRegion("pentagrambg");
				t = atlas.findRegion("pentagram");	
			case HEXAGON:
				bg = atlas.findRegion("hexagrambg");
				t = atlas.findRegion("hexagram");	
			case STAR:
				bg = atlas.findRegion("starbg");
				t = atlas.findRegion("star");				
				break;

			case PLUS:
				bg = atlas.findRegion("plusbg");
				t = atlas.findRegion("plus");				
				break;
			default:
				bg = atlas.findRegion("circlebg");
				t = atlas.findRegion("circle");	
				break;
			}
			
			float alpha = 1;
			

			
			if (s.state == State.EMERGE){
				alpha = 0.15f;
			}
			

			switch (s.colour) {
			case RED:
				batch.setColor(1, 0f, 0f, alpha);		
				break;
			case BLUE:
				batch.setColor(0, 0f, 1f, alpha);			
				break;
			case GREEN:
				batch.setColor(0, 1f, 0f, alpha);				
				break;

			case CYAN:
				batch.setColor(0, 1f, 1f, alpha);		
				break;

			case MAGENTA:
				batch.setColor(1, 0f, 1f, alpha);			
				break;
			case YELLOW:
				batch.setColor(1, 1f, 0f, alpha);			
				break;

			case WHITE:
				batch.setColor(1, 1f, 1f, alpha);			
				break;


			default:
				batch.setColor(1, 0f, 0f, alpha);	
				break;
			}
			
			batch.draw (bg, s.position.x - Shape.DIAMETER/2, s.position.y- Shape.DIAMETER/2,
					Shape.DIAMETER/2, Shape.DIAMETER/2,
					Shape.DIAMETER, Shape.DIAMETER,
					1, 1, 0);//s.angle);
			batch.setColor(1, 1f, 1f, alpha);
			
			batch.draw (t, s.position.x - Shape.DIAMETER/2, s.position.y- Shape.DIAMETER/2,
					Shape.DIAMETER/2, Shape.DIAMETER/2,
					Shape.DIAMETER, Shape.DIAMETER,
					1, 1, 0);//s.angle);
			
			batch.setColor(1, 1f, 1f, 1);
			
		}
		batch.setColor(tempColor);
	}


}
