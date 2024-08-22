package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HighScoreScreen extends AbstractScreen {
	public static TextureAtlas atlas;

	float btnSize = 200;

	public HighScoreScreen(ShapeSmasherGame shapeSmasherGame) {
		super(shapeSmasherGame, "data/bgmain.png");

		atlas = game.assetManager.get("data/atlases/ssmasher.atlas",
				TextureAtlas.class);

		ImageButton btnMenu = new ImageButton(new TextureRegionDrawable(
				atlas.findRegion("btnmenu")), new TextureRegionDrawable(
				atlas.findRegion("btnmenuclk")));
		btnMenu.setPosition(width / 2 - btnSize / 2, btnSize / 4);
		btnMenu.setSize(btnSize, btnSize);
		btnMenu.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new MainScreen(game));
				SoundUtils.playSound(game.assetManager, "click.ogg");
			}

		});
		stage.addActor(btnMenu);

		Table hs = new Table();

		LabelStyle lstyle = new LabelStyle();
		lstyle.font = game.font65;
		lstyle.fontColor = new Color(0, 1f, 1f, 0.8f);
		Label score;

		for (int i = 0; i < 5; i++) {
			score = new Label(Integer.toString(Settings2.highscores[i]), lstyle);
			hs.add(score).height(120f);
			hs.row();
		}

		hs.setPosition(width / 2 - hs.getWidth() / 2,
				height / 2 - hs.getHeight() / 2);
		stage.addActor(hs);
	}

	@Override
	void goToPrevScreen() {
		game.setScreen(new MainScreen(game));
	}

}
