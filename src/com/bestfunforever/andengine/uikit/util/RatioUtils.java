package com.bestfunforever.andengine.uikit.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

public class RatioUtils {

	public static final int DEFAULT_SCREEN_WIDTH_PORTRAIT = 480;
	public static final int DEFAULT_SCREEN_HEIGHT_PORTRAIT = 800;

	public static final int DEFAULT_SCREEN_WIDTH_LANSCAPE = 800;
	public static final int DEFAULT_SCREEN_HEIGHT_LANSCAPE = 480;

	public static float calculatorRatioScreen(Activity activity, boolean portrait) {
		float ratio = -1;
		DisplayMetrics disp = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(disp);
		int screenWidth, screenHeight;
//		if (disp.widthPixels < disp.heightPixels) {
//			screenWidth = disp.heightPixels;
//			screenHeight = disp.widthPixels;
//		} else {
//			screenWidth = disp.widthPixels;
//			screenHeight = disp.heightPixels;
//		}
		 screenWidth = disp.widthPixels;
		 screenHeight = disp.heightPixels;
		Log.d("", "ratio screenWidth "+screenWidth+" screenHeight "+screenHeight);
		int defaultWidth = 480;
		int defaultHeight = 320;
		if (portrait) {
			defaultWidth = DEFAULT_SCREEN_WIDTH_PORTRAIT;
			defaultHeight = DEFAULT_SCREEN_HEIGHT_PORTRAIT;
		} else {
			defaultWidth = DEFAULT_SCREEN_WIDTH_LANSCAPE;
			defaultHeight = DEFAULT_SCREEN_HEIGHT_LANSCAPE;
		}
		float ratioByWidth = (float) screenWidth / defaultWidth;
		float ratioByHeight = (float) screenHeight / defaultHeight;
		float ratioReturn = Math.min(ratioByWidth, ratioByHeight);
		// isAlReady = true;
		return ratioReturn;
	}

}
