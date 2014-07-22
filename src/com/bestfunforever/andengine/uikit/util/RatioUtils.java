package com.bestfunforever.andengine.uikit.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

public class RatioUtils {

	public static final int DEFAULT_SCREEN_WIDTH_PORTRAIT = 640;
	public static final int DEFAULT_SCREEN_HEIGHT_PORTRAIT = 960;

	public static final int DEFAULT_SCREEN_WIDTH_LANSCAPE = 960;
	public static final int DEFAULT_SCREEN_HEIGHT_LANSCAPE = 640;

	public static float calculatorRatioScreen(Activity activity, boolean portrait) {
		DisplayMetrics disp = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(disp);
		int screenWidth, screenHeight;
		// if (disp.widthPixels < disp.heightPixels) {
		// screenWidth = disp.heightPixels;
		// screenHeight = disp.widthPixels;
		// } else {
		// screenWidth = disp.widthPixels;
		// screenHeight = disp.heightPixels;
		// }
		screenWidth = disp.widthPixels;
		screenHeight = disp.heightPixels;
		Log.d("", "ratio screenWidth " + screenWidth + " screenHeight " + screenHeight);
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
