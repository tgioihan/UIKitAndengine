package com.bestfunforever.andengine.uikit.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

import com.bestfunforever.andengine.uikit.entity.Sprite.BubbleSprite;
import com.bestfunforever.andengine.uikit.entity.Sprite.BaseSprite.State;

public abstract class ExpandableMenu extends BaseMenu {

	public enum DIRECTION {
		UP, LEFT, RIGHT, DOWN
	}

	private DIRECTION mDirection = DIRECTION.RIGHT;

	private float mDistanceItem = 30;

	/**
	 * ratio by 480 * 800
	 */
	private float DURATIONX_PER_SCREENSIZE = 0.7f;

	public float getDistanceItem() {
		return mDistanceItem;
	}

	public void setDistanceItem(float mDistanceItem) {
		this.mDistanceItem = mDistanceItem;
	}

	protected BubbleSprite mControl;
	protected Rectangle mItemLayer;

	public ExpandableMenu(SimpleBaseGameActivity context, Camera mCamera, float ratio) {
		super(context, mCamera, ratio);
		mDistanceItem = mDistanceItem * ratio;
	}

	@Override
	protected void show() {
		super.show();
		Log.d("", "expand show state " + stage);
		if (stage == STAGE.HIDE) {
			if (mMenuItems.size() == 0) {
				return;
			}
			switch (mDirection) {
			case LEFT:
				createAnimationLeft();
				break;
			case RIGHT:
				createAnimationRight();
				break;
			case UP:
				createAnimationUp();
				break;
			case DOWN:
				createAnimationDown();
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void hide() {
		super.show();
		Log.d("", "expand hide state " + stage);
		if (stage == STAGE.SHOW) {
			if (mMenuItems.size() == 0) {
				return;
			}

			float totalDisTanceX = mControl.getX();
			float totalDisTanceY = mControl.getY();
			for (int i = 0; i < mMenuItems.size(); i++) {
				MenuItem menuItem = (MenuItem) mMenuItems.get(i);
				if (i != mMenuItems.size() - 1) {
					totalDisTanceX += menuItem.getWidth() + mDistanceItem;
					totalDisTanceY += menuItem.getHeight() + mDistanceItem;
				}

			}

			float distanceX = totalDisTanceX - mControl.getX();
			float distanceY = totalDisTanceY - mControl.getY();
			float durationX = distanceX / 480 * DURATIONX_PER_SCREENSIZE;
			float durationY = distanceY / 480 * DURATIONX_PER_SCREENSIZE;
			float duration = Math.max(durationX, durationY);

			for (int i = 0; i < mMenuItems.size(); i++) {
				final int position = i;
				MenuItem menuItem = (MenuItem) mMenuItems.get(i);
				menuItem.registerEntityModifier(new ParallelEntityModifier(new MoveModifier(duration, menuItem.getX(),
						mControl.getX(), menuItem.getY(), mControl.getY(), new IEntityModifierListener() {

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								((MenuItem) pItem).setEnabled(false);
							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								((MenuItem) pItem).unregisterEntityModifier((IEntityModifier) pModifier);
								((MenuItem) pItem).setEnabled(false);
								((MenuItem) pItem).setVisible(false);
								if (position == mMenuItems.size() - 1)
									stage = STAGE.HIDE;
							}
						}), new LoopEntityModifier(new RotationModifier(duration / (i + 1), 360, 0), i + 1)));
			}
		}
	}

	private void createAnimationRight() {
		float initialX = mDistanceItem + mControl.getX() + mControl.getWidth();
		float initialY = mControl.getY();
		float totalDisTance = initialX;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			if (i != mMenuItems.size() - 1)
				totalDisTance += menuItem.getWidth() + mDistanceItem;
		}

		float distance = totalDisTance - mControl.getX();
		float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;

		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			movingItem(i, duration, menuItem, initialX, initialY, i == mMenuItems.size() - 1);
			initialX += menuItem.getWidth() + mDistanceItem;
		}
	}

	private void movingItem(int position, float durationTranslate, MenuItem menuItem, float initialX, float initialY,
			final boolean checkState) {
		menuItem.registerEntityModifier(new ParallelEntityModifier(new MoveModifier(durationTranslate, menuItem.getX(),
				initialX, menuItem.getY(), initialY, new IEntityModifierListener() {

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						((MenuItem) pItem).setVisible(true);
						((MenuItem) pItem).setEnabled(false);
						if (checkState)
							stage = STAGE.ANIMATE;
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						((MenuItem) pItem).unregisterEntityModifier((IEntityModifier) pModifier);
						((MenuItem) pItem).setVisible(true);
						((MenuItem) pItem).setEnabled(true);
						// ((MenuItem) pItem).setState(State.NOACTION);
						((MenuItem) pItem).setState(State.NORMAL);
						if (checkState)
							stage = STAGE.SHOW;

					}
				}), new LoopEntityModifier(new RotationModifier(durationTranslate / (position + 1), 0, 360),
				position + 1)));
	}

	private void createAnimationLeft() {
		float initialX = -mControl.getX() - mMenuItems.get(0).getWidth() - mDistanceItem;
		float initialY = mControl.getY();

		float totalDisTance = initialX;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			if (i != mMenuItems.size() - 1)
				totalDisTance -= menuItem.getWidth() + mDistanceItem;
		}
		float distance = totalDisTance - mControl.getX();
		float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;

		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			movingItem(i, duration, menuItem, initialX, initialY, i == mMenuItems.size() - 1);
			initialX -= menuItem.getWidth() + mDistanceItem;
		}
	}

	private void createAnimationUp() {
		float initialY = mDistanceItem;
		float initialX = mControl.getX();

		float totalDisTance = initialY;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			if (i != mMenuItems.size() - 1)
				totalDisTance -= menuItem.getHeight() + mDistanceItem;
		}
		float distance = totalDisTance - mControl.getY();
		float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;

		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			movingItem(i, duration, menuItem, initialX, initialY, i == mMenuItems.size() - 1);
			initialY -= menuItem.getHeight() + mDistanceItem;
		}

	}

	private void createAnimationDown() {
		float initialY = mControl.getY() + mControl.getHeight() + mMenuItems.get(0).getHeight() - mDistanceItem;
		float initialX = mControl.getX();

		float totalDisTance = initialY;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			if (i != mMenuItems.size() - 1)
				totalDisTance += menuItem.getHeight() + mDistanceItem;
		}
		float distance = totalDisTance - mControl.getY();
		float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;

		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			movingItem(i, duration, menuItem, initialX, initialY, i == mMenuItems.size() - 1);
			initialY += menuItem.getHeight() + mDistanceItem;
		}
	}

	@Override
	public void invalidate() {
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			float x = mControl.getX();
			float y = mControl.getY();
			menuItem.setPosition(x, y);
			mItemLayer.attachChild(menuItem);
			unregisterTouchArea(menuItem);
			registerTouchArea(menuItem);
			menuItem.setEnabled(false);
		}
	}

	public DIRECTION getDirection() {
		return mDirection;
	}

	public void setDirection(DIRECTION mDirection) {
		this.mDirection = mDirection;
	}

}
