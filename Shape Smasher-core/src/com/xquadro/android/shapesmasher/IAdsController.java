package com.xquadro.android.shapesmasher;

public interface IAdsController {
	public void showBannerAd(boolean show);

	public void prepareInterstitialAd();

	public void showInterstitialAd(Runnable then);
}