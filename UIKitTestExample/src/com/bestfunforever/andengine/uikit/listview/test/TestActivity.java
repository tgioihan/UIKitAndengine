package com.bestfunforever.andengine.uikit.listview.test;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.bestfunforever.andengine.uikit.listview.ListView;

import android.graphics.Typeface;

public class TestActivity extends SimpleBaseGameActivity{
	
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;
	
	public static final int LIST_WIDTH = 400;
	public static final int LIST_HEIGHT = 600;
	
	private Font mFont;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();
	}

	@Override
	protected Scene onCreateScene() {
		Scene scene = new Scene();
		ListView mListView = new ListView(this, CAMERA_WIDTH/2 -LIST_WIDTH/2, CAMERA_HEIGHT/2 -LIST_HEIGHT/2, LIST_WIDTH, LIST_HEIGHT, getVertexBufferObjectManager());
		TestAdapter testAdapter = new TestAdapter(getVertexBufferObjectManager(), mFont);
		mListView.setAdapter(testAdapter);
//		mListView.setSelection(5);
		scene.attachChild(mListView);
		scene.registerTouchArea(mListView);
		return scene;
	}

}
