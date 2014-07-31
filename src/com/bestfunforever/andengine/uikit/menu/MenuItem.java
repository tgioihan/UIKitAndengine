package com.bestfunforever.andengine.uikit.menu;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.bestfunforever.andengine.uikit.entity.Sprite.BubbleSprite;

public class MenuItem extends BubbleSprite implements IMenuItem {

	public MenuItem(int ID, float width, float height, String text, Font font, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(0, 0, width, height, text, font, pTextureRegion, pVertexBufferObjectManager);
		this.setId(ID);
	}

}