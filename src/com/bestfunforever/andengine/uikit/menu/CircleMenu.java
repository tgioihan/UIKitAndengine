package com.bestfunforever.andengine.uikit.menu;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationAtModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;

import com.bestfunforever.andengine.uikit.entity.BubbleSprite;

public abstract class CircleMenu extends BaseMenu {

	protected SimpleBaseGameActivity context;
	protected float camera_height;
	protected float camera_width;
	protected float ratio;

	protected ArrayList<BitmapTextureAtlas> atlas = new ArrayList<BitmapTextureAtlas>();
	protected BubbleSprite controlBgSprite;
	protected Sprite derectionMenuSprite;
	protected Sprite holderBgSprite;

	public CircleMenu(SimpleBaseGameActivity context, Camera mCamera, float ratio) {
		super();
		this.context = context;
		this.camera_height = mCamera.getHeight();
		this.camera_width = mCamera.getWidth();
		this.stage = STAGE.HIDE;
		this.ratio = ratio;
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		this.setCamera(mCamera);
		onLoadResource();
		onCreate();
	}

	public static void clearITextureRegion(final ITextureRegion mITextureRegion) {
		mITextureRegion.setTextureWidth(mITextureRegion.getWidth() - 1);
		mITextureRegion.setTextureHeight(mITextureRegion.getHeight() - 1);
	}

	@Override
	protected void show() {
		super.show();
		if (stage == STAGE.HIDE) {
			holderBgSprite.registerEntityModifier(new MoveModifier(0.3f, holderBgSprite.getX(), 0, holderBgSprite
					.getY(), camera_height - holderBgSprite.getHeight(), new IEntityModifierListener() {

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					stage = STAGE.ANIMATE;
					pItem.setVisible(true);
				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					stage = STAGE.SHOW;
				}
			}));
			derectionMenuSprite.registerEntityModifier(new RotationAtModifier(0.5f, 0, 180, derectionMenuSprite
					.getWidth() / 2, derectionMenuSprite.getHeight() / 2));
			if (menuListenner != null) {
				menuListenner.onShow();
			}
		}
	}

	@Override
	protected void hide() {
		super.hide();
		if (stage == STAGE.SHOW) {
			holderBgSprite.registerEntityModifier(new MoveModifier(0.3f, holderBgSprite.getX(), -holderBgSprite
					.getWidth(), holderBgSprite.getY(), camera_height, new IEntityModifierListener() {

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					stage = STAGE.ANIMATE;
				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					stage = STAGE.HIDE;
					pItem.setVisible(false);
				}
			}));
			derectionMenuSprite.registerEntityModifier(new RotationModifier(0.5f, 180, 0));
			if (menuListenner != null) {
				menuListenner.onHide();
			}
		}
	}

	@Override
	public void onDestroy() {
		for (BitmapTextureAtlas atla : atlas) {
			atla.unload();
		}
		atlas.clear();
	}

	private float padding = 60;

	@Override
	public void invalidate() {
		padding = 60;
		float degreeOffset = 90 / (mMenuItems.size());
		float R = holderBgSprite.getWidth() - padding;

		for (int i = 0; i < mMenuItems.size(); i++) {
			MenuItem menuItem = (MenuItem) mMenuItems.get(i);
			float x = (float) (R * Math.cos(Math.toRadians(degreeOffset * (i) + degreeOffset / 2)));
			float y = (float) (R * Math.sin(Math.toRadians(degreeOffset * (i) + degreeOffset / 2)));
			menuItem.setPosition(x - menuItem.getWidth() / 2, holderBgSprite.getHeight() - y - menuItem.getHeight() / 2);
			holderBgSprite.attachChild(menuItem);
			unregisterTouchArea(menuItem);
			registerTouchArea(menuItem);
		}

	}

}
