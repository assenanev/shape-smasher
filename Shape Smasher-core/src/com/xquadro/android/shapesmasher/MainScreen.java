package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MainScreen extends AbstractScreen {

	public static TextureAtlas atlas;
	
	ImageButton btnSettings;
	
	ImageButton btnSound;
	ImageButton btnFX;
	ImageButton btnVibrate;
	
	ImageButton btnPlay;
	ImageButton btnHigh;
	ImageButton btnHelp;
	
	float btnSize = 200;
	float btnSide = (btnSize-btnSize/10)/2;	
	float btnSideSin = MathUtils.sinDeg(60) * btnSide;
	float btnSideCos = MathUtils.cosDeg(60) * btnSide;
	
	boolean showSettings = false;

	public MainScreen(ShapeSmasherGame shapeSmasherGame) {
		super(shapeSmasherGame, "data/bgmain.png");
		
		atlas = game.assetManager.get("data/atlases/ssmasher.atlas", TextureAtlas.class);
		
		
        btnPlay = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnplay")),
        		new TextureRegionDrawable(atlas.findRegion("btnplayclk")));
        btnPlay.setPosition(width/2 - btnSize/2, height/2 - btnSize/2);
        btnPlay.setSize(btnSize, btnSize);
        btnPlay.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundUtils.playSound(game.assetManager, "click.ogg");
				game.setScreen(new GameScreen(game));				
			}
		        
		});
        stage.addActor(btnPlay);
        
        
        btnSound = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnsoundoff")),
        		new TextureRegionDrawable(atlas.findRegion("btnsoundonclk")), 
        		new TextureRegionDrawable(atlas.findRegion("btnsoundon")));
        btnSound.setPosition(width/2 - btnSize/2 - btnSide - btnSideCos, 
        		height/2 - btnSize/2  + 3 * btnSideSin);       
        btnSound.setSize(btnSize, btnSize);
        btnSound.setChecked(Settings2.soundEnabled);
        btnSound.setVisible(showSettings);
        btnSound.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Settings2.soundEnabled = btnSound.isChecked();		
				Settings2.save();	
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}		        
		});
        stage.addActor(btnSound);  
        
        btnFX  = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnfxoff")),
        		new TextureRegionDrawable(atlas.findRegion("btnfxonclk")), 
        		new TextureRegionDrawable(atlas.findRegion("btnfxon")));
        btnFX.setPosition(width/2 - btnSize/2, height/2 - btnSize/2  + 4 * btnSideSin);
        btnFX.setSize(btnSize, btnSize);
        btnFX.setChecked(Settings2.fxEnabled);
        btnFX.setVisible(showSettings);
        btnFX.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Settings2.fxEnabled = btnFX.isChecked();		
				Settings2.save();
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}		        
		});
        stage.addActor(btnFX);

        btnVibrate = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnvibrateoff")),
        		new TextureRegionDrawable(atlas.findRegion("btnvibrateonclk")), 
        		new TextureRegionDrawable(atlas.findRegion("btnvibrateon")));
        btnVibrate.setPosition(width/2 - btnSize/2 + btnSide + btnSideCos, 
        		height/2 - btnSize/2  + 3 * btnSideSin);
        btnVibrate.setSize(btnSize, btnSize);
        btnVibrate.setChecked(Settings2.vibrateEnabled);
        btnVibrate.setVisible(showSettings);
        btnVibrate.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Settings2.vibrateEnabled = btnVibrate.isChecked();		
				Settings2.save();		
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}		        
		});
        stage.addActor(btnVibrate); 
        
		btnSettings = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnsettings")),
				new TextureRegionDrawable(atlas.findRegion("btnsettingsclk")),
				new TextureRegionDrawable(atlas.findRegion("btnsettingsclk")));
		btnSettings.setPosition(width/2 - btnSize/2, height/2 - btnSize/2 + 2 * btnSideSin);
		btnSettings.setSize(btnSize, btnSize);
		btnSettings.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showSettings = !showSettings;
				btnSound.setVisible(showSettings);
				btnFX.setVisible(showSettings);
				btnVibrate.setVisible(showSettings);
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}		        
		});
        stage.addActor(btnSettings);
        
		btnHelp = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnhelp")));
		btnHelp.setPosition(width/2 - btnSize/2 - btnSide - btnSideCos, 
				height/2 - btnSize/2 - btnSideSin);
		btnHelp.setSize(btnSize, btnSize);
		btnHelp.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new HelpScreen(game));	
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}
		        
		});
		stage.addActor(btnHelp);
		
		btnHigh = new ImageButton(new TextureRegionDrawable(atlas.findRegion("btnhigh")));
		btnHigh.setPosition(width/2 - btnSize/2 + btnSide + btnSideCos, 
				height/2 - btnSize/2 - btnSideSin);
		btnHigh.setSize(btnSize, btnSize);
		btnHigh.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new HighScoreScreen(game));	
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}
		        
		});
		stage.addActor(btnHigh);

	}

	@Override
	void goToPrevScreen() {
		Gdx.app.exit();		
	}
}
