package com.bestfunforever.andengine.uikit.entity;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

import android.util.Log;

import com.bestfunforever.andengine.uikit.entity.BaseSprite;

public class BubbleSprite extends BaseSprite {
	public BubbleSprite(float pX, float pY, String text, Font font, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), text, font, pTextureRegion,
				pVertexBufferObjectManager);
	}
	
	public BubbleSprite(float pX, float pY,float ratio, String text, Font font, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pTextureRegion.getWidth()*ratio, pTextureRegion.getHeight()*ratio, text, font, pTextureRegion,
				pVertexBufferObjectManager);
	}

	public BubbleSprite(float pX, float pY, float width, float height, String text, Font font,
			ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, width, height, pTextureRegion, pVertexBufferObjectManager);
		this.mFont = font;
		setLabel(text);
		onNormalState();
	}

	public void setLabel(String message) {
		if (message == null || message.equals("")) {
			if (mLabel != null)
				mLabel.setVisible(false);
		} else {
			if (mLabel == null) {
				mLabel = new Text(0, getHeight() / 2 - mFont.getLineHeight() / 2, mFont, message, 11, new TextOptions(
						AutoWrap.LETTERS, getWidth(), HorizontalAlign.CENTER), getVertexBufferObjectManager());
				attachChild(mLabel);
			}
			mLabel.setText(message);
			mLabel.setVisible(true);
		}
	}
	

	private static final float duration_animate = 0.3f;

	private static final float dis_small = 0.05f;

	private Text mLabel;
	private Font mFont;

	private static final LoopEntityModifier loopNormalEntityModifier = new LoopEntityModifier(
			new SequenceEntityModifier(new ScaleModifier(duration_animate, 1.0f, 1 - dis_small, 1, 1 + dis_small),
					new ScaleModifier(duration_animate, 1 - dis_small, 1.0f, 1 + dis_small, 1.0f)));

	private static final float initialSmall = 0.85f;

	private static final LoopEntityModifier loopPressEntityModifier = new LoopEntityModifier(
			new SequenceEntityModifier(new ScaleModifier(duration_animate, initialSmall, initialSmall - dis_small,
					initialSmall, initialSmall + dis_small), new ScaleModifier(duration_animate, initialSmall
					- dis_small, initialSmall, initialSmall + dis_small, initialSmall)));

	@Override
	public void onSelectedState() {
//		if (mState != State.SELECTED) {
//			mState = State.SELECTED;
//			unregisterEntityModifier(loopNormalEntityModifier);
//			registerEntityModifier(loopPressEntityModifier.deepCopy());
//		}
		mState = State.SELECTED;
		Log.d("", "BubbleSprite onSelectedState");
		onNormalState();
		
	}

	@Override
	public void onNormalState() {
		if (mState != State.NORMAL) {
			mState = State.NORMAL;
			clearEntityModifiers();
			unregisterEntityModifier(loopPressEntityModifier);
			registerEntityModifier(loopNormalEntityModifier.deepCopy());
			Log.d("", "BubbleSprite onNormalState");
		}
		
	}

	@Override
	public void onPressState() {
		if (mState != State.PRESS) {
			mState = State.PRESS;
			clearEntityModifiers();
			unregisterEntityModifier(loopNormalEntityModifier);
			registerEntityModifier(loopPressEntityModifier.deepCopy());
			Log.d("", "BubbleSprite onPressState");
		}
	}

}
