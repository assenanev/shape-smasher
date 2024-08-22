package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HelpScreen extends AbstractScreen {
	public static TextureAtlas atlas;

	float btnSize = 200;

	public HelpScreen(ShapeSmasherGame shapeSmasherGame) {
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

		Table hlp = new Table();

		LabelStyle lstyle = new LabelStyle();
		lstyle.font = game.font45;
		lstyle.fontColor = new Color(0, 1f, 1f, 0.8f);
		
		float pad = 15;
		
		LabelStyle redStyle = new LabelStyle();
		redStyle.font = game.font45;
		redStyle.fontColor = new Color(1, 0f, 0f, 0.8f);
		
		Label circle = new Label("+10 points", lstyle);
		Image circleImg = new Image(atlas.findRegion("circlebg"));
		circleImg.setColor(new Color(0, 0f, 1f, 1));
		hlp.add(circleImg).pad(pad);
		hlp.add(circle).pad(pad);
		hlp.row();
		
		Label triangle = new Label("+10 points", lstyle);
		Image trianglemg = new Image(atlas.findRegion("trianglebg"));
		trianglemg.setColor(0, 1f, 0f, 1);	
		hlp.add(trianglemg).pad(pad);
		hlp.add(triangle).pad(pad);
		hlp.row();
		
		Label star = new Label("+10 points", lstyle);
		Image starImg = new Image(atlas.findRegion("starbg"));
		starImg.setColor(0, 1f, 1f, 1);
		hlp.add(starImg).pad(pad);
		hlp.add(star).pad(pad);
		hlp.row();
		
		Label pentagram = new Label("+10 points", lstyle);
		Image pentagramImg = new Image(atlas.findRegion("pentagrambg"));
		pentagramImg.setColor(1, 0f, 1f, 1);	
		hlp.add(pentagramImg).pad(pad);
		hlp.add(pentagram).pad(pad);
		hlp.row();
		
		Label hexagram = new Label("+10 points", lstyle);
		Image hexagramImg = new Image(atlas.findRegion("hexagrambg"));
		hexagramImg.setColor(1, 1f, 0f, 1);	
		hlp.add(hexagramImg).pad(pad);
		hlp.add(hexagram).pad(pad);
		hlp.row();
		
		Label plus = new Label("+10 points", lstyle);
		Image plusImg = new Image(atlas.findRegion("plusbg"));
		plusImg.setColor(0, 0f, 1f, 1);
		hlp.add(plusImg).pad(pad);
		hlp.add(plus).pad(pad);
		hlp.row();
		
		Label rectangle = new Label("-20 health", redStyle);
		Image rectangleImg = new Image(atlas.findRegion("rectanglebg"));
		hlp.add(rectangleImg).pad(pad);
		hlp.add(rectangle).pad(pad);
		hlp.row();
		
		Label red = new Label("RED", redStyle);
		hlp.add(red).pad(pad);
		Label reddesc = new Label("-20 health", redStyle);
		hlp.add(reddesc).pad(pad);

		hlp.setPosition(width / 2 - hlp.getWidth() / 2,
				height / 2 - hlp.getHeight() / 2);
		stage.addActor(hlp);
	}

	@Override
	void goToPrevScreen() {
		game.setScreen(new MainScreen(game));
	}

}
