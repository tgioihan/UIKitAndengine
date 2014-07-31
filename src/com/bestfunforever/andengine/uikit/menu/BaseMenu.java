package com.bestfunforever.andengine.uikit.menu;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.bestfunforever.andengine.uikit.entity.IClick;

public abstract class BaseMenu extends BaseHUD implements IOnSceneTouchListener {

	protected ArrayList<IMenuItem> mMenuItems = new ArrayList<IMenuItem>();
	protected IMenuItem mSelectedMenuItem;
	protected IOnMenuItemClickListener mOnMenuItemClickListener;
	protected ArrayList<BitmapTextureAtlas> atlas = new ArrayList<BitmapTextureAtlas>();
	
	protected SimpleBaseGameActivity context;
	protected float camera_height;
	protected float camera_width;
	protected float ratio;

	public IOnMenuItemClickListener getOnMenuItemClickListener() {
		return mOnMenuItemClickListener;
	}

	public void setOnMenuItemClickListener(IOnMenuItemClickListener mOnMenuItemClickListener) {
		this.mOnMenuItemClickListener = mOnMenuItemClickListener;
	}
	
	public BaseMenu(SimpleBaseGameActivity context, Camera mCamera, float ratio) {
		this.setCamera(mCamera);
		this.context = context;
		this.camera_height = mCamera.getHeight();
		this.camera_width = mCamera.getWidth();
		this.stage = STAGE.HIDE;
		this.ratio = ratio;
	}

	public void init() {
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		onLoadResource();
		onCreate();
	}

	public void addMenuItem(IMenuItem menuItem) {
		mMenuItems.add(menuItem);
		menuItem.setClickListenner(new IClick() {

			@Override
			public void onCLick(IAreaShape view) {
				hide();
				if (mOnMenuItemClickListener != null) {
					mOnMenuItemClickListener.onMenuItemClicked(BaseMenu.this, (IMenuItem) view);
				}
			}
		});
		invalidate();
	}

	public void addMenuItem(ArrayList<IMenuItem> menuItems) {
		mMenuItems.addAll(menuItems);
		for (IMenuItem iMenuItem : menuItems) {
			iMenuItem.setClickListenner(new IClick() {

				@Override
				public void onCLick(IAreaShape view) {
					hide();
					if (mOnMenuItemClickListener != null) {
						mOnMenuItemClickListener.onMenuItemClicked(BaseMenu.this, (IMenuItem) view);
					}
				}
			});
		}

		invalidate();
	}

	@Override
	public void onDestroy() {
		for (BitmapTextureAtlas atla : atlas) {
			atla.unload();
		}
		atlas.clear();
	}

	public abstract void invalidate();

	public enum STAGE {
		SHOW, HIDE, ANIMATE
	};

	protected STAGE stage;

	public STAGE getStage() {
		return stage;
	}

	public void setStage(STAGE stage) {
		this.stage = stage;
	}

	protected void hide() {

	}

	protected void show() {

	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (this.mSelectedMenuItem != null) {
			this.mSelectedMenuItem.onNormalState();
			this.mSelectedMenuItem = null;
		} else if (stage == STAGE.SHOW) {
			hide();
		}
		return false;
	}

	public IMenuListenner getMenuListenner() {
		return menuListenner;
	}

	public void setMenuListenner(IMenuListenner menuListenner) {
		this.menuListenner = menuListenner;
	}

	public static interface IOnMenuItemClickListener {
		public boolean onMenuItemClicked(final HUD pMenuScene, final IMenuItem pMenuItem);
	}

	protected IMenuListenner menuListenner;

	public interface IMenuListenner {
		public void onShow();

		public void onHide();
	}

}
