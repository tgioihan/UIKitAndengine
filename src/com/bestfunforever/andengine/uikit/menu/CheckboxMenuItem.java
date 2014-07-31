package com.bestfunforever.andengine.uikit.menu;

import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.bestfunforever.andengine.uikit.entity.Sprite.CheckBox;

public class CheckboxMenuItem extends CheckBox implements IMenuItem {

	public CheckboxMenuItem(int Id,float pX, float pY, boolean checked, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, checked, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		setId(Id);
	}

	public CheckboxMenuItem(int Id,float pX, float pY, float ratio, boolean checked, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, ratio, checked, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		setId(Id);
	}

	public CheckboxMenuItem(int Id,float pX, float pY, float width, float height, boolean checked,
			ITiledTextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, width, height, checked, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		setId(Id);
	}

}
