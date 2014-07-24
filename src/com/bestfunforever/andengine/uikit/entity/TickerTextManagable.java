package com.bestfunforever.andengine.uikit.entity;

import org.andengine.entity.text.TickerText;
import org.andengine.entity.text.exception.OutOfCharactersException;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TickerTextManagable extends TickerText {

	private boolean tickComplete = false;
	private ITickerTextListenner mTickerTextListenner;

	public ITickerTextListenner getTickerTextListenner() {
		return mTickerTextListenner;
	}

	public void setTickerTextListenner(ITickerTextListenner mTickerTextListenner) {
		this.mTickerTextListenner = mTickerTextListenner;
	}

	public TickerTextManagable(float pX, float pY, IFont pFont, String pText,
			TickerTextOptions pTickerTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pTickerTextOptions,
				pVertexBufferObjectManager);
	}

	@Override
	public void setText(CharSequence pText) throws OutOfCharactersException {
		// TODO Auto-generated method stub
		super.setText(pText);
		tickComplete = false;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		super.onManagedUpdate(pSecondsElapsed);
		if (!tickComplete) {
			if (this.getCharactersVisible() >= this.mCharactersToDraw) {
				tickComplete = true;
				if (mTickerTextListenner != null) {
					mTickerTextListenner.onTickerTextComplete();
				}
			}
		}
	}

	public interface ITickerTextListenner {
		public void onTickerTextComplete();
	}

}
