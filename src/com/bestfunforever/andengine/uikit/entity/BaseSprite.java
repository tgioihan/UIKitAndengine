package com.bestfunforever.andengine.uikit.entity;

import org.andengine.audio.sound.SoundManager;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

public abstract class BaseSprite extends Sprite implements ISelector {

	public BaseSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
	}

	private IClick mClickListenner;

	public static final float MINACTIONMOVE = 20;
	protected float startX, startY;
	protected float distanceX, distanceY;
	private boolean isEnabled = true;
	private boolean isdown = false;
	
	private float[] touchChecker = new float[2];

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (!isEnabled) {
			return true;
		}
		final int action = pSceneTouchEvent.getAction();
		Log.d("", "onAreaTouched x "+pSceneTouchEvent.getX()+" y "+pSceneTouchEvent.getY()+"  lcX "+pTouchAreaLocalX+" lcY "+pTouchAreaLocalY);
		switch (action) {
		case TouchEvent.ACTION_DOWN:
			isdown = true;
			startX = pSceneTouchEvent.getX();
			startY = pSceneTouchEvent.getY();
			onPressState();
			break;
		case TouchEvent.ACTION_MOVE:
			touchChecker = convertSceneToLocalCoordinates(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			if (checkActionOutSide(touchChecker)) {
				onPressState();
			} else {
				onNormalState();
			}

			break;

		case TouchEvent.ACTION_UP:
			if (isdown) {
				touchChecker = convertSceneToLocalCoordinates(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				if (checkActionOutSide(touchChecker)) {
					onSelectedState();
					if (mClickListenner != null) {
						mClickListenner.onCLick(this);
					}
				} else {
					onNormalState();
				}
			}

			break;

		case TouchEvent.ACTION_CANCEL:
			Log.d("", "acttion cancel");
			onNormalState();
			break;

		case TouchEvent.ACTION_OUTSIDE:
			Log.d("", "acttion out side");
			onNormalState();
			break;

		default:
			break;
		}

		return true;
	}

	private boolean checkActionOutSide(float[] touchChecker) {
		if (touchChecker[0] < 0 || touchChecker[0] > getWidth() || touchChecker[1] < 0
				|| touchChecker[1] > getHeight()){
			Log.d("", "checkActionOutSide ouside");
			return false;
		}
		Log.d("", "checkActionOutSide inside");
			return true;
	}

	private boolean checkActionOutSide(float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pTouchAreaLocalX < 0 || pTouchAreaLocalX > getWidth() || pTouchAreaLocalY < 0
				|| pTouchAreaLocalY > getHeight()){
			Log.d("", "checkActionOutSide ouside");
			return false;
		}
		Log.d("", "checkActionOutSide inside");
			return true;
	}

	public IClick getClickListenner() {
		return mClickListenner;
	}

	public void setClickListenner(IClick mClickListenner) {
		this.mClickListenner = mClickListenner;
	}

}
