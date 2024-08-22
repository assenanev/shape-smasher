package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class ShapeSmasherGame extends Game {
	
	public static final int VIRTUAL_WIDTH = 768;
	public static final int VIRTUAL_HEIGHT = 1280;
	
	IAdsController adsController;
	
	public AssetManager assetManager;
	
	BitmapFont font65;
	BitmapFont font45;
	
	public ShapeSmasherGame(IAdsController adsController) {
    	super();
        this.adsController = adsController;
    }
	
	public IAdsController getAdsController() {
		return adsController;
	}

	@Override
	public void create() {
		assetManager = new AssetManager();
		Settings2.load();
		Gdx.input.setCatchBackKey(true);
		setScreen(new LoadScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
		assetManager.clear();
		assetManager.dispose();
	}

	public void loadFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/arial.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 65;
		
		font65 = generator.generateFont(parameter); // font size 65 pixels

		parameter.size = 45;
		font45 = generator.generateFont(parameter); // font size 45 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
		
	}
}
