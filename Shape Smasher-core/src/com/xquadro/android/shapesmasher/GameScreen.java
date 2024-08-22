/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.xquadro.android.shapesmasher.GameWorld.WorldListener;


public class GameScreen implements Screen {
	public enum State {LOAD, RUNNING, PAUSED, OVER};

	final ShapeSmasherGame game;
	int levelCount;
	int levelIndex;
	final int btnPauseSize = 100;
	
	final float btnSize = 200;
	final float btnSide = (btnSize-btnSize/10)/2;	
	final float btnSideSin = MathUtils.sinDeg(60) * btnSide;
	final float btnSideCos = MathUtils.cosDeg(60) * btnSide;

	int virtualWigth;
	int virtualHeight;
	float virtualAspect = (float) virtualWigth / virtualHeight;
	int width, height;
	float aspect;

	State state;
	float stateTime;

	TextureAtlas atlas;
	
	OrthographicCamera guiCam;

	Vector3 touchPoint = new Vector3();

	SpriteBatch batch;

	Stage stage;

	GameWorld gameWorld;
	WorldListener worldListener;
	GameWorldRenderer renderer;
	FPSLogger fps = new FPSLogger();
	
	ImageButton btnPlay;
	ImageButton btnMenu;
	ImageButton btnRestart;
	ImageButton btnPause;
	Label score;
	
	Label newHighScore;
	Label gameOverScore;
	int scoreText;
	Slider slider;
	
	public static Texture bg;
	public static TextureRegion bgRegion;

	boolean hasHighScore = false;
	public GameScreen(ShapeSmasherGame shapeSmasherGame) {
		this.game = shapeSmasherGame;

		atlas = game.assetManager.get("data/atlases/ssmasher.atlas",
				TextureAtlas.class);

		state = State.LOAD;
		stateTime = 0;
		
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
		
		bg = game.assetManager.get("data/bgmain.png", Texture.class);
		bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgRegion = new TextureRegion(bg, 0, 1024-height, width, height);

		guiCam = new OrthographicCamera(width, height);
		guiCam.position.set(width / 2, height / 2, 0);
		guiCam.update();

		batch = new SpriteBatch();
		stage = new Stage(new StretchViewport(width, height));
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage,
			new InputAdapter() {
				@Override
				public boolean keyUp(int keycode) {
					if (keycode == Keys.BACK){
						game.setScreen(new MainScreen(game));
					}
					return true;
				}
				@Override
				public boolean touchDragged(int x, int y, int pointer) {
					if(state == State.RUNNING) {
						clickWorld(x, y);
					}
					return true;
				}
				@Override
				public boolean touchDown(int x, int y, int pointer, int button) {
					if(state == State.RUNNING) {
						clickWorld(x, y);
					}
					return true;
				}
			}));
		//stage.setViewport(width, height, true);
		//stage.getViewport().update(width, height, true);

		batch = new SpriteBatch();
		worldListener = new WorldListener() {
			
			@Override
			public void miss() {
				SoundUtils.vibrate();
				SoundUtils.playSound(game.assetManager, "boom3.ogg");
			}
			
			@Override
			public void clickWrong() {
				SoundUtils.vibrate();
				SoundUtils.playSound(game.assetManager, "boom3.ogg");
				
			}
			
			@Override
			public void click() {
				SoundUtils.playSound(game.assetManager, "coin1.ogg");				
			}
		};
		
		gameWorld = new GameWorld(worldListener, aspect);
				
		renderer = new GameWorldRenderer(batch, gameWorld, game.assetManager);

		setupStage();
		
		game.getAdsController().prepareInterstitialAd();
	}

	private void setupStage() {

		btnPause = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnpause")),
				new TextureRegionDrawable(atlas.findRegion("btnpauseclk")));
		btnPause.setPosition(width - btnPauseSize - 20, 20);
		btnPause.setSize(btnPauseSize, btnPauseSize);
		btnPause.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				state = State.PAUSED;
				showButtons(true);
				btnPause.setVisible(false);
				slider.setVisible(false);
				score.setVisible(false);
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}

		});
		stage.addActor(btnPause);
		
		btnMenu = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnmenu")),
	        		new TextureRegionDrawable(atlas.findRegion("btnmenuclk")));
		btnMenu.setPosition(width/2 - btnSize/2 - btnSide - btnSideCos, 
				height/2 - btnSize/2 - btnSideSin / 2);
	    btnMenu.setSize(btnSize, btnSize);
	        
	    btnMenu.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundUtils.playSound(game.assetManager, "click.ogg");
				game.getAdsController().showInterstitialAd(new Runnable() {
					@Override
					public void run() {
						game.setScreen(new MainScreen(game));
					}
				});
			}      
		});
	    stage.addActor(btnMenu);

		btnPlay = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnplay")),
        		new TextureRegionDrawable(atlas.findRegion("btnplayclk")));
		btnPlay.setPosition(width/2 - btnSize/2, 
				height/2 - btnSize/2 + btnSideSin / 2);
		btnPlay.setSize(btnSize, btnSize);
		btnPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				state = State.RUNNING;
				showButtons(false);
				btnPause.setVisible(true);
				slider.setVisible(true);
				SoundUtils.playSound(game.assetManager, "click.ogg");
				
			}      
		});
		stage.addActor(btnPlay);
		
		btnRestart = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnrestart")),
        		new TextureRegionDrawable(atlas.findRegion("btnrestartclk")));
		btnRestart.setPosition(width/2 - btnSize/2 + btnSide + btnSideCos, 
				height/2 - btnSize/2 - btnSideSin / 2);
		btnRestart.setSize(btnSize, btnSize);
		
		btnRestart.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundUtils.playSound(game.assetManager, "click.ogg");
				
				game.getAdsController().showInterstitialAd(new Runnable() {
					@Override
					public void run() {
						game.setScreen(new GameScreen(game));
					}
				});
			}      
		});
		stage.addActor(btnRestart);
		showButtons(false);
		
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = new TextureRegionDrawable(atlas.findRegion("sliderbg"));
		sliderStyle.knobBefore = new TextureRegionDrawable(atlas.findRegion("sliderbf"));
		sliderStyle.knobAfter = new TextureRegionDrawable(atlas.findRegion("slideraf"));
		
		slider = new Slider(0, 100, 1, false, sliderStyle);
		slider.setSize(width - btnPauseSize - 250, 60);
		slider.setPosition(200, 20 + btnPauseSize/2 - 60/2);
		slider.setValue(100);
		slider.setTouchable(Touchable.disabled);
		stage.addActor(slider);
		
		LabelStyle lstyle = new LabelStyle();
		lstyle.font = game.font45;
		lstyle.fontColor = new Color(0, 1f, 1f, 0.8f);		
		score = new Label(Integer.toString(gameWorld.score), lstyle);
		score.setAlignment(Align.right, Align.right);
		
		score.setSize(160, btnPauseSize);
		score.setPosition(25, 20);
		stage.addActor(score);
		
		LabelStyle highScoreStyle = new LabelStyle();
		highScoreStyle.font = game.font65;
		highScoreStyle.fontColor = new Color(0, 1f, 1f, 0.8f);		
		newHighScore = new Label("Score:", lstyle);
		newHighScore.setAlignment(Align.center, Align.center);
		
		newHighScore.setSize(100, 100);
		newHighScore.setPosition(width/2 - 50, height/2 + 200 -50);
		newHighScore.setVisible(false);
		stage.addActor(newHighScore);
		
		gameOverScore = new Label(Integer.toString(gameWorld.score), lstyle);
		gameOverScore.setAlignment(Align.center, Align.center);
		
		gameOverScore.setSize(100, 100);
		gameOverScore.setPosition(width/2 - 50, height/2 + 120 -50);
		gameOverScore.setVisible(false);
		stage.addActor(gameOverScore);
	
	}
	
	private void showButtons(boolean show) {
		btnMenu.setVisible(show);
		btnRestart.setVisible(show);
		btnPlay.setVisible(show);
	}

	public void update(float deltaTime) {

		stage.act(deltaTime);

		switch (state) {
		case LOAD:
			updateLoad(deltaTime);
			break;
		case RUNNING:
			updateRunning(deltaTime);
			break;
		case PAUSED:
			break;
		case OVER:
			break;
		}
	}

	private void updateLoad(float deltaTime) {
		stateTime += deltaTime;
		if (stateTime > 0.5f) {
			state = State.RUNNING;
			stateTime = 0;
		}
	}
	
	public void clickWorld(int x, int y) {
		touchPoint.set(x, y, 0);
		renderer.camera.unproject(touchPoint);
		gameWorld.touch(touchPoint);
	}

	private void updateRunning(float deltaTime) {

		gameWorld.update(deltaTime);
		if (gameWorld.health > 0 && gameWorld.health < 100) {
			slider.setValue(gameWorld.health);
		}
		
		score.setText(Integer.toString(gameWorld.score));
		
		if(gameWorld.state == GameWorld.State.RUNNING){
			btnPause.setVisible(true);	
			slider.setVisible(true);
			score.setVisible(true);			
		}

		if (gameWorld.state == GameWorld.State.GAME_OVER) {
			state = State.OVER;
			stateTime = 0;
			showButtons(true);
			btnPlay.setVisible(false);
			btnPause.setVisible(false);
			slider.setVisible(false);
			score.setVisible(false);
			
			gameOverScore.setText(Integer.toString(gameWorld.score));
			gameOverScore.setVisible(true);
			
			hasHighScore = Settings2.addScore(gameWorld.score);
			if(hasHighScore) {
				newHighScore.setText("New High Score:");
			}
			newHighScore.setVisible(true);
			Settings2.save();
		}
	}

	public void draw(float deltaTime) {
		GL20 gl = Gdx.gl;
		gl.glClearColor(0f, 0f, 0.15f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		guiCam.update();
		batch.setProjectionMatrix(guiCam.combined);
		
		batch.disableBlending();
		batch.begin();
		batch.draw(bgRegion, 0, 0, width, height);
		batch.end();
		batch.enableBlending();

		renderer.render(deltaTime);

		stage.draw();

	}


	@Override
	public void render(float delta) {
		fps.log();
		update(delta);
		draw(delta);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
		//game.myRequestHandler.showAds(false);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {		
		stage.dispose();
		batch.dispose();			
	}
}
