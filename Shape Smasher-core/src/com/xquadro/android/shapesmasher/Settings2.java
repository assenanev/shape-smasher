package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings2 {
	public static final Preferences prefs = Gdx.app.getPreferences("preferences");
	public static boolean soundEnabled = true;
	public static boolean musicEnabled = true;
	public static boolean vibrateEnabled = true;
	public static boolean fxEnabled = true;
	
	public final static int[] highscores = new int[] {0, 0, 0, 0, 0};
	
	public static final Shape.Type KILL_TYPE = Shape.Type.RECTANGLE;
	public static final Shape.Colour KILL_COLOUR = Shape.Colour.RED;

	public static void load() {
		boolean saveflag = false;
		if (!prefs.contains("soundEnabled")
				|| !prefs.contains("musicEnabled")
				|| !prefs.contains("vibrateEnabled")
				|| !prefs.contains("fxEnabled")
				) {
			saveflag = true;
		}
		for (int i = 0; i < highscores.length; i++) {
			if (!prefs.contains("highscores" + String.valueOf(i))){
				saveflag = true;
			}	        
		}
		
		if (saveflag){
			save();
		}

		soundEnabled = prefs.getBoolean("soundEnabled", true);
		musicEnabled = prefs.getBoolean("musicEnabled", true);
		vibrateEnabled = prefs.getBoolean("vibrateEnabled", true);
		fxEnabled = prefs.getBoolean("fxEnabled", true);
		
		for (int i = 0; i < highscores.length; i++) {
			highscores[i] = prefs.getInteger("highscores" + String.valueOf(i));   
		}
		
	}

	public static void save() {
		prefs.putBoolean("soundEnabled", soundEnabled);
		prefs.putBoolean("musicEnabled", musicEnabled);
		prefs.putBoolean("vibrateEnabled", vibrateEnabled);
		prefs.putBoolean("fxEnabled", fxEnabled);

		for (int i = 0; i < highscores.length; i++) {
			prefs.putInteger("highscores" + String.valueOf(i), highscores[i]);
	    }
		prefs.flush();
	}
	
	public static boolean addScore (int score) {
		boolean hasHighScore = true;
		for (int i = 0; i < 5; i++) {
			if (highscores[i] < score) {
				for (int j = 4; j > i; j--)
					highscores[j] = highscores[j - 1];
				highscores[i] = score;
				break;
			}
		}
		
		for (int i = 0; i < 5; i++) {
			if (highscores[i] > score) {
				hasHighScore = false;
			}
		}
		return hasHighScore;
	}
}
