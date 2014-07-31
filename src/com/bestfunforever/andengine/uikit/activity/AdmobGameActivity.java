package com.bestfunforever.andengine.uikit.activity;

import org.andengine.opengl.view.RenderSurfaceView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.example.uikitandengine.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public abstract class AdmobGameActivity extends BaseFacebookAcivity {

	private AdView adView;
	ImageView defaultBanner;
	@Override
	protected void onSetContentView() {
		// TODO Auto-generated method stub
		addAdmob(getAdmobKey(), Gravity.BOTTOM);
	}

	protected abstract String getAdmobKey();

	@SuppressLint("NewApi")
	public void addAdmob(String keyAdmob, int positionView) {

		final FrameLayout frameLayout = new FrameLayout(this);
		final FrameLayout.LayoutParams frameLayoutLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.FILL);
		final FrameLayout.LayoutParams adViewLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, positionView);

		adView = new AdView(this);
		adView.setAdUnitId(keyAdmob);
		adView.setAdSize(AdSize.BANNER);
		adView.setVisibility(AdView.VISIBLE);
		adView.refreshDrawableState();
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				super.onAdLoaded();
				defaultBanner.setVisibility(View.GONE);
			}
			
			@Override
			public void onAdFailedToLoad(int errorCode) {
				// TODO Auto-generated method stub
				super.onAdFailedToLoad(errorCode);
				defaultBanner.setVisibility(View.VISIBLE);
			}
		});
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

		defaultBanner = new ImageView(getApplicationContext());
		defaultBanner.setScaleType(ScaleType.FIT_XY);
		defaultBanner.setImageResource(R.drawable.default_banner);
		final FrameLayout.LayoutParams defaultBannerParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, getAddMobHeight(), positionView);
		defaultBanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openGooglePlay();
			}
		});

		frameLayout.addView(defaultBanner, defaultBannerParams);
		frameLayout.addView(adView, adViewLayoutParams);
		this.setContentView(frameLayout, frameLayoutLayoutParams);
	}

	public int getAddMobHeight() {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
	}

	private void openGooglePlay() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:bestfunforever")));
		} catch (android.content.ActivityNotFoundException anfe) {
			Intent intent1 = new Intent();
			intent1.setAction(Intent.ACTION_VIEW);
			intent1.addCategory(Intent.CATEGORY_BROWSABLE);
			intent1.setData(Uri.parse("http://play.google.com/store/search?q=pub:bestfunforever"));
			startActivity(intent1);
		}
	}

}
