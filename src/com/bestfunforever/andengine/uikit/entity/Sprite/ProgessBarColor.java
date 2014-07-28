package com.bestfunforever.andengine.uikit.entity.Sprite;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.bestfunforever.andengine.uikit.entity.IProgress;

public class ProgessBarColor extends Rectangle implements IProgress {

	private int percent;
	private float borderWidth;
	private Rectangle progess;

	public ProgessBarColor(float pX, float pY, float pWidth, float pHeight,
			Color borderColor, float borderWidth, Color progessColor,
			Color bgColor, VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pWidth, pHeight, 0, borderColor, borderWidth,
				progessColor, bgColor, pVertexBufferObjectManager);
	}

	public ProgessBarColor(float pX, float pY, float pWidth, float pHeight,
			int percent, Color borderColor, float borderWidth,
			Color progessColor, Color bgColor,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		setColor(bgColor);
		this.borderWidth = borderWidth;
		this.percent = percent;
		progess = new Rectangle(borderWidth, borderWidth,
				(pWidth - borderWidth * 2) * percent / 100, pHeight - 2
						* borderWidth, pVertexBufferObjectManager);
		Rectangle bgRectangle = new Rectangle(borderWidth, borderWidth, pWidth
				- borderWidth * 2, pHeight - 2 * borderWidth,
				pVertexBufferObjectManager);
		progess.setColor(progessColor);
		bgRectangle.setColor(bgColor);
		attachChild(bgRectangle);
		attachChild(progess);
	}

	/**
	 * @return the percent
	 */
	public int getPercent() {
		return percent;
	}

	/**
	 * @param percent
	 *            the percent to set
	 */
	public void setPercent(int percent) {
		this.percent = percent;
		progess.setWidth((getWidth() - borderWidth * 2) * percent / 100);
	}

}
