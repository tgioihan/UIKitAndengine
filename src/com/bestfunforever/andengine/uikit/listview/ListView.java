package com.bestfunforever.andengine.uikit.listview;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.list.SmartList;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class ListView extends Rectangle {

	private static final String TAG = "ListView";

	Recycler mRecycler;
	BaseAdapter mAdapter;
	DataSetObserver mDataSetObserver;
	SimpleBaseGameActivity mContext;

	SmartList<IAreaShape> mChilds = new SmartList<IAreaShape>();

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
				layoutChildrent();
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
				mChilds.clear();
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
		mChilds.add(mView);
		attachChild(mView);
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
				scrollByY(diffY);
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
	private void scrollByY(float diffY) {
		final int childCount = mChilds.size();
		if (childCount == 0) {
			return;
		}
		final float firstTop = mChilds.get(0).getY();
		final float lastBottom = mChilds.getLast().getY() + mChilds.getLast().getHeight();
		final float spaceBelow = getHeight() - lastBottom;
		final float absDiffY = Math.abs(diffY);
		final boolean down = diffY > 0;
		if (firstTop >= absDiffY && spaceBelow >= absDiffY) {
			// in case first and last both in draw rectangle
		} else {
			if ((mFirstPosition == 0 && firstTop >= 0)
					|| (mFirstPosition + childCount == mAdapter.getCount() && lastBottom < getHeight())) {
				// no need track mmotion
				return;
			}

			if (down) {
				final float top = -diffY;
				for (int i = 0; i < childCount; i++) {
					final IAreaShape view = mChilds.get(i);
					if (view.getY() + view.getHeight() >= top) {
						break;
					} else {
						// add view to recycle
						addViewToRecycle(view);
						Log.d(TAG, TAG + " add view to recycle at " + i);
					}
				}

			} else {
				final float bottom = getHeight() + diffY;
				for (int i = 0; i < childCount; i++) {
					final IAreaShape view = mChilds.get(i);
					if (view.getY() <= bottom) {
						break;
					} else {
						// add view to recycle
						addViewToRecycle(view);
						Log.d(TAG, TAG + " add view to recycle at " + i);
					}
				}
			}

			// move item acitive
			for (int i = 0; i < mChilds.size(); i++) {
				final IAreaShape view = mChilds.get(i);
				view.setY(view.getY() + diffY);
			}

			// get scrap view to active
			fillGap(down);
		}

	}

	private void fillGap(boolean down) {
		
	}

	private void addViewToRecycle(IAreaShape view) {
		mRecycler.addScrapView(view);
		detachChild(view);
		mChilds.remove(view);
	}
}
