package com.bestfunforever.andengine.uikit.entity.Sprite;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import com.bestfunforever.andengine.uikit.entity.IProgress;

import android.os.Handler;
import android.util.Log;
import android.widget.Scroller;

public class SeekBar extends Rectangle implements IProgress {

	private int percent;
	private Rectangle progess;
	private Sprite thumbSprite;
	private float borderWidth;
	private SimpleBaseGameActivity context;
	private Fillinger mFillinger;

	private boolean userTouch;

	private ISeekBarListenner mSeekBarListenner;

	public SeekBar(SimpleBaseGameActivity context, float pX, float pY,
			float pWidth, float pHeight, ITextureRegion border,
			float borderWidth, Color progessColor, ITextureRegion thumb,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		this(context, pX, pY, pWidth, pHeight, 0, border, borderWidth,
				progessColor, thumb, 1, pVertexBufferObjectManager);
	}

	public SeekBar(SimpleBaseGameActivity context, float pX, float pY,
			float pWidth, float pHeight, ITextureRegion border,
			float borderWidth, Color progessColor, ITextureRegion thumb,
			float ratio, VertexBufferObjectManager pVertexBufferObjectManager) {
		this(context, pX, pY, pWidth, pHeight, 0, border, borderWidth,
				progessColor, thumb, ratio, pVertexBufferObjectManager);
	}

	public SeekBar(SimpleBaseGameActivity context, float pX, float pY,
			float pWidth, float pHeight, int percent, ITextureRegion border,
			float borderWidth, Color progessColor, ITextureRegion thumb,
			float ratio, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		setColor(Color.TRANSPARENT);
		this.context = context;
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mHandler = new Handler();
			}
		});
		mFillinger = new Fillinger();
		this.borderWidth = borderWidth;
		this.percent = percent;

		Sprite bgSprite = new Sprite(0, 0, pWidth, pHeight, border,
				pVertexBufferObjectManager);

		float progressWidth = (pWidth - borderWidth * 2) * percent / 100;
		progess = new Rectangle(borderWidth, borderWidth, progressWidth,
				pHeight - 2 * borderWidth, pVertexBufferObjectManager);
		progess.setColor(progessColor);
		thumbSprite = new Sprite(borderWidth + progressWidth - thumb.getWidth()* ratio
				/ 2, getHeight() / 2 - thumb.getHeight()* ratio / 2, thumb.getWidth()
				* ratio, thumb.getHeight() * ratio, thumb,
				pVertexBufferObjectManager);
		attachChild(progess);
		attachChild(bgSprite);
		attachChild(thumbSprite);
	}

	@Override
	public int getPercent() {
		return percent;
	}

	@Override
	public void setPercent(int percent) {
		this.setPercent(percent, false);
	}

	public void setPercent(float percent, boolean smooth) {
		float progressWidth = (getWidth() - borderWidth * 2) * percent / 100;
		int diffX = (int) (progressWidth - progess.getWidth());
		if (smooth) {
			mFillinger.start(0, -diffX);
		} else {
			seekBy(diffX);
		}
	}

	public void seekBy(int diffX) {
		progess.setWidth(progess.getWidth() + diffX);
		thumbSprite.setX(thumbSprite.getX() + diffX);
		percent = (int) (progess.getWidth() * 100 / (getWidth() - borderWidth * 2));
		if (mSeekBarListenner != null) {
			mSeekBarListenner.onSeeking(percent, userTouch);
		}
	}

	private Handler mHandler;

	public void post(Runnable runnable) {
		mHandler.post(runnable);
	}

	private void stopMovingMotion() {
		mHandler.removeCallbacks(mFillinger);
	}

	public ISeekBarListenner getSeekBarListenner() {
		return mSeekBarListenner;
	}

	public void setSeekBarListenner(ISeekBarListenner mSeekBarListenner) {
		this.mSeekBarListenner = mSeekBarListenner;
	}

	public class Fillinger implements Runnable {
		private static final String tag = "Fillinger";
		private Scroller mScroller;
		private int lastX;

		public Fillinger() {
			mScroller = new Scroller(context);
		}

		/**
		 * ratio by 480 * 800
		 */
		private static final int defaultDurationPerScreenHeight = 10000;

		public void start(int startX, int dx) {
			int duration = Math.abs(dx) * defaultDurationPerScreenHeight / 480;
			if (duration > defaultDurationPerScreenHeight) {
				duration = defaultDurationPerScreenHeight;
			}
			lastX = startX;
			Log.d("", tag + " dx " + dx + " duration " + duration);
			if (mSeekBarListenner != null) {
				mSeekBarListenner.onStartSeeking(percent, userTouch);
			}
			mScroller.startScroll(startX, 0, dx, 0, duration);
			post(this);
		}

		@Override
		public void run() {
			boolean more = mScroller.computeScrollOffset();
			int currenX = mScroller.getCurrX();
			final int diffX = lastX - currenX;
			lastX = currenX;
			seekBy(diffX);
			if (more) {
				post(this);
			} else {
				if (mSeekBarListenner != null) {
					mSeekBarListenner.onFinishSeek(percent, userTouch);
				}
			}
		}

	}

	public interface ISeekBarListenner {
		public void onStartSeeking(float percent, boolean userTouch);

		public void onSeeking(float percent, boolean userTouch);

		public void onFinishSeek(float percent, boolean userTouch);
	}

}
