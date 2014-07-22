package com.bestfunforever.andengine.uikit.entity;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class ProgessBarColor extends Rectangle {

	private float percent;
	private float borderWidth;
	private Rectangle progess;

	public ProgessBarColor(float pX, float pY, float pWidth, float pHeight,
			Color borderColor, float borderWidth, Color progessColor,
			Color bgColor, VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pWidth, pHeight, 0, borderColor, borderWidth,
				progessColor, bgColor, pVertexBufferObjectManager);
	}

	public ProgessBarColor(float pX, float pY, float pWidth, float pHeight,
			float percent, Color borderColor, float borderWidth,
			Color progessColor, Color bgColor,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		setColor(bgColor);
		this.borderWidth = borderWidth;
		this.percent = percent;
		progess = new Rectangle(borderWidth, borderWidth,
				(pWidth - borderWidth * 2) * percent/100,
				pHeight - 2 * borderWidth, pVertexBufferObjectManager);
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
	public float getPercent() {
		return percent;
	}

	/**
	 * @param percent
	 *            the percent to set
	 */
	public void setPercent(float percent) {
		this.percent = percent;
		progess.setWidth((getWidth() - borderWidth * 2) * percent/100);
	}

}
