package com.bestfunforever.andengine.uikit.entity.Sprite;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.bestfunforever.andengine.uikit.entity.IClick;

public class CheckBox extends BubbleAnimateSprite {

	private boolean isChecked;

	private ICheckedChange onCheckedChangeListenner;

	public CheckBox(float pX, float pY, float width, float height, boolean checked, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, width, height, null, null, pTextureRegion, pVertexBufferObjectManager);
		isChecked = checked;
		setCurrentTileIndex(isChecked ? 0 : 1);
		setClickListenner(new IClick() {

			@Override
			public void onCLick(IAreaShape view) {
				// TODO Auto-generated method stub
				setChecked(!isChecked);
			}
		});
	}

	public CheckBox(float pX, float pY, float ratio, boolean checked, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, ratio, null, null, pTextureRegion, pVertexBufferObjectManager);
		isChecked = checked;
		setCurrentTileIndex(isChecked ? 0 : 1);
		setClickListenner(new IClick() {

			@Override
			public void onCLick(IAreaShape view) {
				// TODO Auto-generated method stub
				setChecked(!isChecked);
			}
		});
	}

	public CheckBox(float pX, float pY, boolean checked, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, null, null, pTextureRegion, pVertexBufferObjectManager);
		isChecked = checked;
		setCurrentTileIndex(isChecked ? 0 : 1);
		setClickListenner(new IClick() {

			@Override
			public void onCLick(IAreaShape view) {
				// TODO Auto-generated method stub
				setChecked(!isChecked);
			}
		});
	}

	public void setChecked(boolean checked) {
		if (isChecked != checked) {
			isChecked = checked;
			setCurrentTileIndex(isChecked ? 0 : 1);
			if (onCheckedChangeListenner != null) {
				onCheckedChangeListenner.onCheckedChange(isChecked);
			}
		}
	}

	public ICheckedChange getOnCheckedChangeListenner() {
		return onCheckedChangeListenner;
	}

	public void setOnCheckedChangeListenner(ICheckedChange onCheckedChangeListenner) {
		this.onCheckedChangeListenner = onCheckedChangeListenner;
	}

	public interface ICheckedChange {
		public void onCheckedChange(boolean checked);
	}

}
