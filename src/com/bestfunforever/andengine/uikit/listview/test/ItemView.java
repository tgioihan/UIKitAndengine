package com.bestfunforever.andengine.uikit.listview.test;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ItemView extends Rectangle {

	private int position;
	private Font mFont;
	private Text mText;

	public ItemView(int position, Font font, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(0, 0, pWidth, pHeight, pVertexBufferObjectManager);
		this.position = position;
		this.mFont = font;

		mText = new Text(getWidth() / 2, getHeight() / 2, mFont, "", 5, getVertexBufferObjectManager());
		attachChild(mText);
	}

	public void setText(String text) {
		mText.setText(text);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
