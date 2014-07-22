package com.bestfunforever.andengine.uikit.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;

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

	public void setMenuItemModifierWhenShow(IEntityModifier mMenuItemModifierWhenShow) {
		this.mMenuItemModifierWhenShow = mMenuItemModifierWhenShow;
	}

	public IEntityModifier getMenuItemModifierWhenHide() {
		return mMenuItemModifierWhenHide;
	}

	public void setMenuItemModifierWhenHide(IEntityModifier mMenuItemModifierWhenHide) {
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

	public ExpandableMenu(SimpleBaseGameActivity context, Camera mCamera, float ratio) {
		super(context, mCamera, ratio);
		mDistanceItem = mDistanceItem * ratio;
	}

	@Override
	protected void show() {
		super.show();
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
		if (stage == STAGE.SHOW) {
			if (mMenuItems.size() == 0) {
				return;
			}
			for (int i = 0; i < mMenuItems.size(); i++) {
				MenuItem menuItem = (MenuItem) mMenuItems.get(i);
				float durationX = menuItem.getX() - mControl.getX();
				float durationY = menuItem.getY() - mControl.getY();
				if (mMenuItemModifierWhenHide != null) {
					menuItem.registerEntityModifier(new ParallelEntityModifier(
							new MoveModifier(
									Math.max(Math.abs(durationX), Math.abs(durationY)), menuItem.getX(), mControl.getX(),
									menuItem.getY(), mControl.getY(),new IEntityModifierListener() {
										
										@Override
										public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
											
										}
										
										@Override
										public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
											
										}
									}),
									mMenuItemModifierWhenHide
							));
				} else {
					menuItem.registerEntityModifier(new MoveModifier(
							Math.max(Math.abs(durationX), Math.abs(durationY)), menuItem.getX(), mControl.getX(),
							menuItem.getY(), mControl.getY()));
				}
			}
		}
	}

	private void createAnimationRight() {
		float initialX = mDistanceItem;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialX - menuItem.getX();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;

			if (mMenuItemModifierWhenShow != null) {
				menuItem.registerEntityModifier(new ParallelEntityModifier(new MoveXModifier(duration, menuItem.getX(),
						initialX, new IEntityModifierListener() {

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								if (pItem.getRotation() != 0) {
									pItem.registerEntityModifier(new RotationModifier(0.2f, pItem.getRotation(), 0,
											new IEntityModifierListener() {

												@Override
												public void onModifierStarted(IModifier<IEntity> pModifier,
														IEntity pItem) {

												}

												@Override
												public void onModifierFinished(IModifier<IEntity> pModifier,
														IEntity pItem) {
													((MenuItem) pItem).setState(State.NOACTION);
													((MenuItem) pItem).onNormalState();
												}
											}));
								} else {
									((MenuItem) pItem).setState(State.NOACTION);
									((MenuItem) pItem).onNormalState();
								}

							}
						}), mMenuItemModifierWhenShow));
			} else {
				menuItem.registerEntityModifier(new MoveXModifier(duration, menuItem.getX(), initialX));
			}
			initialX += menuItem.getWidth() + mDistanceItem;
		}
	}

	private void createAnimationLeft() {
		float initialX = -mControl.getX() - mMenuItems.get(0).getWidth() - mDistanceItem;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialX - menuItem.getX();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;
			if (mMenuItemModifierWhenShow != null) {
				menuItem.registerEntityModifier(new ParallelEntityModifier(new MoveXModifier(duration, menuItem.getX(),
						initialX, new IEntityModifierListener() {

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								if (pItem.getRotation() != 0) {
									pItem.registerEntityModifier(new RotationModifier(0.2f, pItem.getRotation(), 0,
											new IEntityModifierListener() {

												@Override
												public void onModifierStarted(IModifier<IEntity> pModifier,
														IEntity pItem) {

												}

												@Override
												public void onModifierFinished(IModifier<IEntity> pModifier,
														IEntity pItem) {
													((MenuItem) pItem).setState(State.NOACTION);
													((MenuItem) pItem).onNormalState();
												}
											}));
								} else {
									((MenuItem) pItem).setState(State.NOACTION);
									((MenuItem) pItem).onNormalState();
								}

							}
						}), mMenuItemModifierWhenShow));
			} else {
				menuItem.registerEntityModifier(new MoveXModifier(duration, menuItem.getX(), initialX));
			}
			initialX -= menuItem.getWidth() + mDistanceItem;
		}
	}

	private void createAnimationUp() {
		float initialY = mDistanceItem;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialY - menuItem.getY();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;
			if (mMenuItemModifierWhenShow != null) {
				menuItem.registerEntityModifier(new ParallelEntityModifier(new MoveXModifier(duration, menuItem.getY(),
						initialY, new IEntityModifierListener() {

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								if (pItem.getRotation() != 0) {
									pItem.registerEntityModifier(new RotationModifier(0.2f, pItem.getRotation(), 0,
											new IEntityModifierListener() {

												@Override
												public void onModifierStarted(IModifier<IEntity> pModifier,
														IEntity pItem) {

												}

												@Override
												public void onModifierFinished(IModifier<IEntity> pModifier,
														IEntity pItem) {
													((MenuItem) pItem).setState(State.NOACTION);
													((MenuItem) pItem).onNormalState();
												}
											}));
								} else {
									((MenuItem) pItem).setState(State.NOACTION);
									((MenuItem) pItem).onNormalState();
								}

							}
						}), mMenuItemModifierWhenShow));
			} else {
				menuItem.registerEntityModifier(new MoveXModifier(duration, menuItem.getX(), initialY));
			}
			initialY -= menuItem.getHeight() + mDistanceItem;
		}

	}

	private void createAnimationDown() {
		float initialY = -mControl.getY() - mMenuItems.get(0).getHeight() - mDistanceItem;
		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			menuItem.clearEntityModifiers();
			float distance = initialY - menuItem.getY();
			float duration = distance / 480 * DURATIONX_PER_SCREENSIZE;
			if (mMenuItemModifierWhenShow != null) {
				menuItem.registerEntityModifier(new ParallelEntityModifier(new MoveXModifier(duration, menuItem.getY(),
						initialY, new IEntityModifierListener() {

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								if (pItem.getRotation() != 0) {
									pItem.registerEntityModifier(new RotationModifier(0.2f, pItem.getRotation(), 0,
											new IEntityModifierListener() {

												@Override
												public void onModifierStarted(IModifier<IEntity> pModifier,
														IEntity pItem) {

												}

												@Override
												public void onModifierFinished(IModifier<IEntity> pModifier,
														IEntity pItem) {
													((MenuItem) pItem).setState(State.NOACTION);
													((MenuItem) pItem).onNormalState();
												}
											}));
								} else {
									((MenuItem) pItem).setState(State.NOACTION);
									((MenuItem) pItem).onNormalState();
								}

							}
						}), mMenuItemModifierWhenShow));
			} else {
				menuItem.registerEntityModifier(new MoveXModifier(duration, menuItem.getX(), initialY));
			}
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
