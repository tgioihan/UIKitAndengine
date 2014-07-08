package com.bestfunforever.andengine.uikit.listview.test;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.util.Log;

import com.bestfunforever.andengine.uikit.listview.SimpleAdapter;

public class TestAdapter extends SimpleAdapter{
	
	public static final String Tag = "TestAdapter";
	
	VertexBufferObjectManager vertexBufferObjectManager;
	Font font;

	public TestAdapter(VertexBufferObjectManager vertexBufferObjectManager, Font font) {
		super();
		this.vertexBufferObjectManager = vertexBufferObjectManager;
		this.font = font;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 50;
	}

	@Override
	public IAreaShape getView(int pos, IAreaShape view) {
		Log.d(Tag,Tag+" getView "+ pos+" "+view);
		if(view == null){
			view = new ItemView(pos, font, getWidth(), getHeight(), vertexBufferObjectManager);
		}
		if(pos%2 == 0){
			((ItemView)view).setColor(Color.GREEN);
		}else{
			((ItemView)view).setColor(Color.CYAN);
		}
		((ItemView)view).setText(pos+"");
		
		return view;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 480;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}
	

}
