package com.bestfunforever.andengine.uikit.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

import com.bestfunforever.andengine.uikit.entity.BaseSprite.State;
import com.bestfunforever.andengine.uikit.entity.BubbleSprite;

public abstract class ExpandableMenu extends BaseMenu {

	public enum DIRECTION {
		UP, LEFT, RIGHT, DOWN
	}

	private DIRECTION mDirection = DIRECTION.RIGHT;

	private float mDistanceItem = 30;
	private IEntityModifier mMenuItemModifierWhenShow;
	private IEntityModifier mMenuItemModifierWhenHide;

	public IEntityModifier getMenuItemModifierWhenShow() {
		return mMenuItemModifierWhenShow;
	}

	public void setMenuItemModifierWhenShow(
			IEntityModifier mMenuItemModifierWhenShow) {
		this.mMenuItemModifierWhenShow = mMenuItemModifierWhenShow;
	}

	public IEntityModifier getMenuItemModifierWhenHide() {
		return mMenuItemModifierWhenHide;
	}

	public void setMenuItemModifierWhenHide(
			IEntityModifier mMenuItemModifierWhenHide) {
		this.mMenuItemModifierWhenHide = mMenuItemModifierWhenHide;
	}

	/**
	 * ratio by 480 * 800
	 */
	private float DURATIONX_PER_SCREENSIZE = 2;
	private float DURATIONY_PER_SCREENSIZE = 3;
	private float DURATION_ROTATION_COUNT_PER_SCREENSIZE = 20;

	public float getDistanceItem() {
		return mDistanceItem;
	}

	public void setDistanceItem(float mDistanceItem) {
		this.mDistanceItem = mDistanceItem;
	}

	protected BubbleSprite mControl;

	public ExpandableMenu(SimpleBaseGameActivity context, Camera mCamera,
			float ratio) {
		super(context, mCamera, ratio);
		mDistanceItem = mDistanceItem * ratio;
	}

	@Override
	protected void show() {
		super.show();
		Log.d("", "expand show state "+stage );
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
		Log.d("", "expand hide state "+stage );
		if (stage == STAGE.SHOW) {
			if (mMenuItems.size() == 0) {
				return;
			}
			for (int i = 0; i < mMenuItems.size(); i++) {
				MenuItem menuItem = (MenuItem) mMenuItems.get(i);
				float distanceX = menuItem.getX() - mControl.getX();
				float distanceY = menuItem.getY() - mControl.getY();
				float durationX = distanceX / 480 * DURATIONX_PER_SCREENSIZE;
				float durationY = distanceY / 480 * DURATIONX_PER_SCREENSIZE;
				if (mMenuItemModifierWhenHide != null) {
					menuItem.registerEntityModifier(new ParallelEntityModifier(
							new MoveModifier(Math.max(Math.abs(durationX),
									Math.abs(durationY)), menuItem.getX(),
									mControl.getX(), menuItem.getY(), mControl
											.getY(),
									new IEntityModifierListener() {

										@Override
										public void onModifierStarted(
												IModifier<IEntity> pModifier,
												IEntity pItem) {
											stage = STAGE.ANIMATE;
										}

										@Override
										public void onModifierFinished(
												IModifier<IEntity> pModifier,
												IEntity pItem) {
											stage = STAGE.HIDE;
										}
									}), mMenuItemModifierWhenHide));
				} else {
					menuItem.registerEntityModifier(new MoveModifier(Math.max(
							Math.abs(durationX), Math.abs(durationY)), menuItem
							.getX(), mControl.getX(), menuItem.getY(), mControl
							.getY(), new IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {
							stage = STAGE.ANIMATE;
						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {
							stage = STAGE.HIDE;
						}
					}));
				}
			}
		}
	}

	private void createAnimationRight() {
		float initialX = mDistanceItem + mControl.getX() + mControl.getWidth();
		float initialY = mControl.getY();
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialX - menuItem.getX();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;
			movingItem(duration, menuItem, initialX, initialY,
					i == mMenuItems.size() - 1);
			initialX += menuItem.getWidth() + mDistanceItem;
		}
	}

	private void movingItem(float duration, MenuItem menuItem, float initialX,
			float initialY, final boolean checkState) {
		if (mMenuItemModifierWhenShow != null) {
			menuItem.registerEntityModifier(new ParallelEntityModifier(
					new MoveModifier(duration, menuItem.getX(), initialX,
							menuItem.getY(), initialY,
							new IEntityModifierListener() {

								@Override
								public void onModifierStarted(
										IModifier<IEntity> pModifier,
										IEntity pItem) {
									if (checkState)
										stage = STAGE.ANIMATE;
								}

								@Override
								public void onModifierFinished(
										IModifier<IEntity> pModifier,
										IEntity pItem) {
									if (pItem.getRotation() != 0) {
										pItem.registerEntityModifier(new RotationModifier(
												0.2f, pItem.getRotation(), 0,
												new IEntityModifierListener() {

													@Override
													public void onModifierStarted(
															IModifier<IEntity> pModifier,
															IEntity pItem) {

													}

													@Override
													public void onModifierFinished(
															IModifier<IEntity> pModifier,
															IEntity pItem) {
														((MenuItem) pItem)
																.setState(State.NOACTION);
														((MenuItem) pItem)
																.onNormalState();
														stage = STAGE.SHOW;
													}
												}));
									} else {
										((MenuItem) pItem)
												.setState(State.NOACTION);
										((MenuItem) pItem).onNormalState();
										if (checkState)
											stage = STAGE.SHOW;
									}

								}
							}), mMenuItemModifierWhenShow));
		} else {
			IEntityModifierListener listener = null;

			listener = new IEntityModifierListener() {

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier,
						IEntity pItem) {
					// TODO Auto-generated method stub
					if (checkState)
						stage = STAGE.ANIMATE;
				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier,
						IEntity pItem) {
					// TODO Auto-generated method stub
					((MenuItem) pItem).setState(State.NOACTION);
					((MenuItem) pItem).onNormalState();
					if (checkState)
						stage = STAGE.SHOW;
				}
			};
			menuItem.registerEntityModifier(new MoveModifier(duration, menuItem
					.getX(), initialX, menuItem.getY(), initialY, listener));
		}
	}

	private void createAnimationLeft() {
		float initialX = -mControl.getX() - mMenuItems.get(0).getWidth()
				- mDistanceItem;
		float initialY = mControl.getY();
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialX - menuItem.getX();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;
			movingItem(duration, menuItem, initialX, initialY,
					i == mMenuItems.size() - 1);
			initialX -= menuItem.getWidth() + mDistanceItem;
		}
	}

	private void createAnimationUp() {
		float initialY = mDistanceItem;
		float initialX = mControl.getX();
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialY - menuItem.getY();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;
			movingItem(duration, menuItem, initialX, initialY,
					i == mMenuItems.size() - 1);
			initialY -= menuItem.getHeight() + mDistanceItem;
		}

	}

	private void createAnimationDown() {
		float initialY = mControl.getY() + mControl.getHeight()
				+ mMenuItems.get(0).getHeight() - mDistanceItem;
		float initialX = mControl.getX();
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialY - menuItem.getY();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;
			movingItem(duration, menuItem, initialX, initialY,
					i == mMenuItems.size() - 1);
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
			attachChild(menuItem);
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
