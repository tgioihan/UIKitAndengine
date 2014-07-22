package com.bestfunforever.andengine.uikit.activity;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import android.util.DisplayMetrics;
import android.util.Log;

import com.bestfunforever.andengine.uikit.util.RatioUtils;

public abstract class PortraitAdmobGameActivity extends AdmobGameActivity {

	protected float ratio;
	protected int CAMERA_WIDTH;
	protected int CAMERA_HEIGHT;
	protected Camera mCamera;

	@Override
	public EngineOptions onCreateEngineOptions() {
		ratio = RatioUtils.calculatorRatioScreen(this, true);
		Log.d("", "ratio " + ratio);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int adsHeight = getAddMobHeight();
		Log.d("", "ads height " + adsHeight);
		CAMERA_WIDTH = metrics.widthPixels;
		CAMERA_HEIGHT = metrics.heightPixels - adsHeight;
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		engineOptions.getAudioOptions().getMusicOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().getSoundOptions().setNeedsSound(true);
		return engineOptions;
	}
}
