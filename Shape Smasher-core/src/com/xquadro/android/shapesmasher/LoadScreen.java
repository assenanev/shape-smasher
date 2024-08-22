package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LoadScreen implements Screen {
	
	private ShapeSmasherGame shapeSmasherGame;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private static Texture splashTexture;
	public static Texture bg;
	public static TextureRegion bgRegion;
	
	private int virtualWigth;
	private int virtualHeight;
	private float virtualAspect = (float) virtualWigth/virtualHeight;
	private int width, height;
	private float aspect, splashAspect;
	private int splashWidth, splashHeight;
	private float activeTime;
	private float minActiveTime = 1.5f;
	AssetManager manager;

	public LoadScreen(ShapeSmasherGame game) {
		super();
		this.shapeSmasherGame = game;
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
		
		manager = game.assetManager;
		
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Keys.BACK){
					Gdx.app.exit();
				}
				return true;
			}
		});
		
		TextureParameter param = new TextureParameter();
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;

		manager.load("data/bgmain.png", Texture.class, param);
		manager.load("data/atlases/ssmasher.atlas", TextureAtlas.class);
		manager.load("data/sounds/click.ogg", Sound.class);
		manager.load("data/sounds/boom3.ogg", Sound.class);
		manager.load("data/sounds/coin1.ogg", Sound.class);

//		manager.load("data/sounds/pirates.ogg", Music.class);

		batch = new SpriteBatch();
		camera = new OrthographicCamera(width, height);
		camera.position.set(width / 2, height / 2, 0);
		
		splashTexture = new Texture(Gdx.files.internal("data/splash.png"));
		splashTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		splashAspect = (float) splashTexture.getWidth()/splashTexture.getHeight();
		splashWidth = (int) (width * 0.9f);
		splashHeight = (int) (splashWidth / splashAspect);
		activeTime = 0;
		
		bg = new Texture(Gdx.files.internal("data/bgmain.png"));

		bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgRegion = new TextureRegion(bg, 0, 1024-height, width, height);
		
	}

	@Override
	public void render(float delta) {
		activeTime += delta;
		
		if(manager.update() && activeTime > minActiveTime){
			shapeSmasherGame.loadFonts();
			//SoundUtils.playMusic(manager);
			shapeSmasherGame.setScreen(new MainScreen(shapeSmasherGame));
		}
		
		GL20 gl = Gdx.gl;

		gl.glClearColor(0f, 0f, 0.15f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		batch.draw(bgRegion, 0, 0, width, height);
		batch.setColor(1f, 1f, 1f, 0.7f);
		//batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		batch.draw(splashTexture, width/2 - splashWidth/2, height/2 - splashHeight/2 + height/30, splashWidth, splashHeight);
		//batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
		batch.dispose();		
	}

}
