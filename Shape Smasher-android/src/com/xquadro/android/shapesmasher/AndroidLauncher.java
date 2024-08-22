package com.xquadro.android.shapesmasher;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


public class AndroidLauncher extends AndroidApplication implements
		IAdsController {
	
	private static final String BANNER_AD_UNIT_ID = "123";
	private static final String INTERSTITIAL_AD_UNIT_ID = "123";
	private static final boolean ENABLE_INTERSTITIAL_ADS = false;
	private static final boolean ENABLE_BANNER_ADS = false;

	private AdView bannerView;
	private View gameView;
	private InterstitialAd interstitialAd;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = false;
		cfg.useCompass = false;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		RelativeLayout layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);

		View gameView = createGameView(cfg);
		layout.addView(gameView);
		
		AdView admobView = createBannerView();
		layout.addView(admobView);
		
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
		
		setContentView(layout);
		
		if(ENABLE_BANNER_ADS) {
			startBannerAds(admobView);
			showBannerAd(true);
		}

		prepareInterstitialAd();
	}
	
	private AdView createBannerView() {
		bannerView = new AdView(this);
		bannerView.setAdSize(AdSize.BANNER);
		bannerView.setAdUnitId(BANNER_AD_UNIT_ID);
		bannerView.setId(1); // this is an arbitrary id, allows for relative positioning in createGameView()
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		adParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);		
		bannerView.setLayoutParams(adParams);
		bannerView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		return bannerView;
	}
	
	private View createGameView(AndroidApplicationConfiguration cfg) {
		gameView = initializeForView(new ShapeSmasherGame(this), cfg);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		//params.addRule(RelativeLayout.BELOW, adView.getId());
		gameView.setLayoutParams(params);
		return gameView;
	}
	
	private void startBannerAds(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice("23C074AE7DD5818701D54BFB29D3B9F8") //2nd nexus 
			.build();
		adView.loadAd(adRequest);
	}

	@Override
	public void showBannerAd(boolean show) {
		if(show) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					bannerView.setVisibility(View.VISIBLE);
				}
			});
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					bannerView.setVisibility(View.GONE);
				}
			});
		}
	}
	
	@Override
	public void prepareInterstitialAd() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(ENABLE_INTERSTITIAL_ADS && !interstitialAd.isLoaded()) {
					AdRequest ad = new AdRequest.Builder().addTestDevice(
							"23C074AE7DD5818701D54BFB29D3B9F8") // 2nd nexus
							.build();
					interstitialAd.loadAd(ad);
				}	
			}
		});
	}
	
	@Override
	public void showInterstitialAd(final Runnable then) {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(interstitialAd.isLoaded()){
						if (then != null) {
							interstitialAd.setAdListener(new AdListener() {
								@Override
								public void onAdClosed() {
									Gdx.app.postRunnable(then);
								}
							});
						}
						interstitialAd.show();
					} else {
						prepareInterstitialAd();
						if (then != null) {
							Gdx.app.postRunnable(then);
						}
					}
				}
			}); 
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		if (bannerView != null) {
			bannerView.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (bannerView != null) {
			bannerView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (bannerView != null) {
			bannerView.resume();
		}
	}

}
