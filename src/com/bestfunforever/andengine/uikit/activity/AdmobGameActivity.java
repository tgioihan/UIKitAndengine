package com.bestfunforever.andengine.uikit.activity;

import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.bestfunforever.andengine.uikit.activity.BaseFacebookAcivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public abstract class AdmobGameActivity extends BaseFacebookAcivity {

	private AdView adView;

	@Override
	protected void onSetContentView() {
		// TODO Auto-generated method stub
		addAdmob("ca-app-pub-2714906120093430/5441596905", Gravity.BOTTOM);
	}

	@SuppressLint("NewApi")
	public void addAdmob(String keyAdmob, int positionView) {

		final FrameLayout frameLayout = new FrameLayout(this);
		final FrameLayout.LayoutParams frameLayoutLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.FILL);
		final FrameLayout.LayoutParams adViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, positionView);

		adView = new AdView(this);
		adView.setAdUnitId(keyAdmob);
		adView.setAdSize(AdSize.BANNER);
		adView.setVisibility(AdView.VISIBLE);
		adView.refreshDrawableState();

		AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
		adView.loadAd(adRequest);
 
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
 

		this.mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setRenderer(mEngine, this);

	 

		final FrameLayout.LayoutParams surfaceViewLayoutParams = new FrameLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		surfaceViewLayoutParams.gravity = Gravity.TOP;

		frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
		frameLayout.addView(adView, adViewLayoutParams);
		this.setContentView(frameLayout, frameLayoutLayoutParams);
	}
	
	public int getAddMobHeight(){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
	}

}
