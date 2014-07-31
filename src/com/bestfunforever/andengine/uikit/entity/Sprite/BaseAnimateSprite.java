package com.bestfunforever.andengine.uikit.entity.Sprite;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.bestfunforever.andengine.uikit.entity.IClick;
import com.bestfunforever.andengine.uikit.entity.ISelector;
import com.bestfunforever.andengine.uikit.entity.State;

public abstract class BaseAnimateSprite extends AnimatedSprite implements ISelector {

	public BaseAnimateSprite(float pX, float pY, float pWidth, float pHeight, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager);
	}

	public int ID = 0;

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return ID;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.ID = id;
	}

	protected State mState = State.NOACTION;

	public State getState() {
		return mState;
	}

	public void setState(State mState) {
		if (mState != this.mState) {
			this.mState = mState;
			switch (this.mState) {
			case NORMAL:
				onNormalState();
				break;
			case PRESS:
				onPressState();
				break;
			case SELECTED:
				onSelectedState();
				break;
			case NOACTION:

				break;

			default:
				break;
			}
		}
	}

	private IClick mClickListenner;

	public static final float MINACTIONMOVE = 20;
	protected float startX, startY;
	protected float distanceX, distanceY;
	private boolean isEnabled = true;

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnable(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	private boolean isdown = false;

	private float[] touchChecker = new float[2];

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (!isEnabled) {
			return true;
		}
		final int action = pSceneTouchEvent.getAction();
		Log.d("", "onAreaTouched x " + pSceneTouchEvent.getX() + " y " + pSceneTouchEvent.getY() + "  lcX "
				+ pTouchAreaLocalX + " lcY " + pTouchAreaLocalY);
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
		if (touchChecker[0] < 0 || touchChecker[0] > getWidth() || touchChecker[1] < 0 || touchChecker[1] > getHeight()) {
			Log.d("", "checkActionOutSide ouside");
			return false;
		}
		Log.d("", "checkActionOutSide inside");
		return true;
	}

	private boolean checkActionOutSide(float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pTouchAreaLocalX < 0 || pTouchAreaLocalX > getWidth() || pTouchAreaLocalY < 0
				|| pTouchAreaLocalY > getHeight()) {
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