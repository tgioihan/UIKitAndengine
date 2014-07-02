package com.bestfunforever.andengine.uikit.listview;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class ListView extends Rectangle {

	Recycler mRecycler;
	BaseAdapter mAdapter;
	DataSetObserver mDataSetObserver;
	SimpleBaseGameActivity mContext;

	ArrayList<IAreaShape> mChilds = new ArrayList<IAreaShape>();

	private float childWidth, childHeight;

	int mSelection;
	int mFirstPosition;

	int mScrollY;

	private boolean dataChanged = true;
	private VelocityTracker velocityTracker;

	public ListView(SimpleBaseGameActivity Context, float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		this.mContext = Context;
		mRecycler = new Recycler();
		mDataSetObserver = new DataSetObserver() {

			@Override
			public void onChanged() {
				super.onChanged();
				layoutChildrent();
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
			}

		};
	}

	public void setAdateper(BaseAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}

		this.mAdapter = adapter;
		childWidth = mAdapter.getWidth();
		childHeight = mAdapter.getHeight();
		this.mAdapter.registerDataSetObserver(mDataSetObserver);
		removeAllView(true);
	}

	private void removeAllView(boolean layout) {
		mContext.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				for (IEntity entity : mChilds) {
					detachChild(entity);
				}
				mChilds.clear();
				mRecycler.clear();
				layoutChildrent();
			}
		});
	}

	private void layoutChildrent() {
		float top = 0;
		int position = mFirstPosition;
		while (top < getHeight() && position < mAdapter.getCount()) {
			IAreaShape view = makeAndAddView(position, top);
			top += view.getHeight();
			position++;
		}

		dataChanged = false;
	}

	private IAreaShape makeAndAddView(int position, float top) {

		IAreaShape mView = mRecycler.getActiveView(position);
		if (mView != null && dataChanged) {
			Log.d("", " use view active at " + position + "no need recycle or initial ");
			return mView;
		}
		mView = obtainView(position);
		mView.setY(top);
		return mView;
	}

	private IAreaShape obtainView(int position) {
		int type = mAdapter.getItemViewType(position);
		IAreaShape view = mRecycler.getScrapView(position, type);
		if (view == null) {
			view = mAdapter.getView(position, view);
		}
		view.setWidth(mAdapter.getWidth());
		view.setHeight(mAdapter.getHeight());
		TouchEvent event = null;
		event.getMotionEvent();
		return view;
	}
	
	private float initialY;

	@Override
	public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (velocityTracker == null) { // If we do not have velocity tracker
			velocityTracker = VelocityTracker.obtain(); // then get one
		}
		final MotionEvent motionEvent = event.getMotionEvent();
		velocityTracker.addMovement(motionEvent); // add this movement to it
		

		switch (event.getAction()) {
			case TouchEvent.ACTION_DOWN :
				initialY = event.getY();
				break;

			case TouchEvent.ACTION_MOVE :
				final float diffY = event.getY() - initialY;
				scrollBy(0,diffY);
				break;

			case TouchEvent.ACTION_UP :

				break;

			default :
				break;
		}

		return super.onAreaTouched(event, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	/**
	 * @param diffX
	 * @param diffY
	 */
	private void scrollBy(int diffX, float diffY) {
		
	}

}
