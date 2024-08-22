package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public abstract class AbstractScreen implements Screen {
	ShapeSmasherGame game;
	
	int virtualWigth;
	int virtualHeight;
	float virtualAspect = (float) virtualWigth/virtualHeight;
	int width;
	int height;
	float aspect;
	
	Stage stage;
	
	OrthographicCamera camera;
	SpriteBatch batch;
	public static Texture bg;
	public static TextureRegion bgRegion;

	public AbstractScreen(ShapeSmasherGame shapeSmasherGame, String bgTextureFileName) {
		this.game = shapeSmasherGame;
		virtualHeight = ShapeSmasherGame.VIRTUAL_HEIGHT;
		virtualWigth = ShapeSmasherGame.VIRTUAL_WIDTH;
		
		aspect = (float) Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
		if(aspect < virtualAspect){
			width = virtualWigth;
	        height = (int) (virtualWigth / aspect);
		} else {
			width = (int) (virtualHeight * aspect);
	        height = virtualHeight;
		}
		
		bg = game.assetManager.get(bgTextureFileName, Texture.class);
		bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgRegion = new TextureRegion(bg, 0, 1024-height, width, height);

		camera = new OrthographicCamera(width, height);
		camera.position.set(width / 2, height / 2, 0);

		batch = new SpriteBatch();		
		stage = new Stage(new StretchViewport(width, height));
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage,
			new InputAdapter() {
				@Override
				public boolean keyUp(int keycode) {
					if (keycode == Keys.BACK){
						SoundUtils.playSound(game.assetManager, "click.ogg");
						goToPrevScreen();
					}
					return true;
				}
			}));
        //stage.setViewport(width, height, true);
        //stage.getViewport().update(width, height, true);
	}
	
	abstract void goToPrevScreen();

	@Override
	public void render(float delta) {
		//TODO limit delta
        stage.act(delta);
		GL20 gl = Gdx.gl;
		gl.glClearColor(0f, 0f, 0.15f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		batch.disableBlending();
		batch.begin();
		batch.draw(bgRegion, 0, 0, width, height);
		batch.end();
		batch.enableBlending();
		
		stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		stage.getViewport().update(width, height, true); //TODO - check
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();		
		batch.dispose();
	}

}
