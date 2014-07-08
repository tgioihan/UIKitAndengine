package com.bestfunforever.andengine.uikit.listview;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.list.SmartList;

import android.database.DataSetObserver;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

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
	private Fillinger mFillinger;

	public ListView(SimpleBaseGameActivity Context, float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		this.mContext = Context;
		mRecycler = new Recycler();
		mFillinger = new Fillinger();
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
		mContext.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler = new Handler();
			}
		});
	}

	public void setAdapter(BaseAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		this.mAdapter = adapter;
		mRecycler.clear();
		mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());
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
			IAreaShape view = makeAndAddView(position, top,mChilds.size());
			top += view.getHeight();
			position++;
		}

		dataChanged = false;
	}

	private IAreaShape makeAndAddView(int position, float top, int positionToAdd) {

		IAreaShape mView = mRecycler.getActiveView(position);
		if (mView != null && dataChanged) {
			Log.d("", " use view active at " + position + "no need recycle or initial ");
			return mView;
		}
		mView = obtainView(position);
		mView.setY(top);
		Log.d(TAG, TAG+" makeAndAddView "+position);
		mChilds.add(positionToAdd,mView);
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
	private float currentY;

	@Override
	public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (velocityTracker == null) { // If we do not have velocity tracker
			velocityTracker = VelocityTracker.obtain(); // then get one
		}
		final MotionEvent motionEvent = event.getMotionEvent();
		velocityTracker.addMovement(motionEvent); // add this movement to it

		switch (event.getAction()) {
			case TouchEvent.ACTION_DOWN :
				initialY =currentY= event.getY();
				stopMovingMotion();
				break;

			case TouchEvent.ACTION_MOVE :
				final float diffY = event.getY() - currentY;
				currentY= event.getY();
				scrollByY(diffY);
				break;

			case TouchEvent.ACTION_UP :
				velocityTracker.computeCurrentVelocity(1000);
				int initialVelocity = (int)velocityTracker.getYVelocity();
				Log.d(TAG, TAG+ " initialVelocity "+initialVelocity);
				if(Math.abs(initialVelocity)>0){
					//start filling
					mFillinger.start(0, (int) event.getY(), -initialVelocity);
				}
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
		Log.d(TAG, TAG+" scrollByY childCount "+childCount);
		if (childCount == 0) {
			return;
		}
		final float firstTop = mChilds.get(0).getY();
		final float lastBottom = mChilds.getLast().getY() + mChilds.getLast().getHeight();
		final boolean down = diffY > 0;
		Log.d(TAG, TAG+" down "+down);
		Log.d(TAG, TAG+" scrollByY firstTop "+firstTop+" diffY "+diffY +" "+(firstTop + diffY)+" "+(lastBottom + diffY));
		if (firstTop + diffY >= 0 && lastBottom + diffY <= getHeight()) {
			// in case first and last both in draw rectangle
			moveCurrentItem(diffY);
		} else {
			if ((mFirstPosition == 0 && firstTop >= 0 && diffY>0)
					|| (mFirstPosition + childCount == mAdapter.getCount() && lastBottom < getHeight()) && diffY<0) {
				// no need track mmotion
				return;
			}

			if (down) {
				for (int i = 0; i < childCount; i++) {
					final IAreaShape view = mChilds.get(i);
					if (view.getY()+ view.getHeight() +diffY >= 0) {
						break;
					} else {
						// add view to recycle
						addViewToRecycle(view);
						mFirstPosition ++;
						Log.d(TAG, TAG +" down "+down + " add view to recycle at " + i);
					}
				}

			} else {
				for (int i = mChilds.size()-1; i >= 0; i--) {
					final IAreaShape view = mChilds.get(i);
					if (view.getY() +diffY <= getHeight()) {
						break;
					} else {
						// add view to recycle
						addViewToRecycle(view);
						Log.d(TAG, TAG +" down "+down +" add view to recycle at " + i);
					}
				}
			}

			// move item acitive
			moveCurrentItem(diffY);

			// get scrap view to active
			fillGap(down);
		}

	}

	private void moveCurrentItem(float diffY) {
		for (int i = 0; i < mChilds.size(); i++) {
			final IAreaShape view = mChilds.get(i);
			view.setY(view.getY() + diffY);
		}
	}

	private void fillGap(boolean down) {
		final int childCOunt = mChilds.size();
		if (down) {
			final float startOffset = childCOunt > 0 ? mChilds.get(0).getY() : (getHeight());
			fillUp(mFirstPosition - 1, startOffset);
		} else {
			final float startOffset = childCOunt > 0 ? (mChilds.get(childCOunt - 1).getHeight() + mChilds.get(
					childCOunt - 1).getY()) : 0;
			fillDown(mFirstPosition + childCOunt, startOffset);
		}
	}

	private void fillUp(int position, float startOffset) {
		// TODO Auto-generated method stub
		Log.d(TAG, TAG+" fillUp start "+ position+ startOffset);
		while (startOffset > 0 && position>=0) {
			mFirstPosition = position;
			Log.d(TAG, TAG+" fillUp "+ position+ startOffset);
			IAreaShape view = makeAndAddView(position, startOffset-mAdapter.getHeight(),0);
			startOffset -= view.getHeight();
			position -= 1;
			
		}
	}

	private void fillDown(int position, float startOffset) {
		while (startOffset < getHeight()&& position<=mAdapter.getCount()) {
			IAreaShape view = makeAndAddView(position, startOffset,mChilds.size());
			startOffset += view.getHeight();
			position +=1;
		}
	}

	private void addViewToRecycle(IAreaShape view) {
		mRecycler.addScrapView(view);
		detachChild(view);
		mChilds.remove(view);
	}

	private Handler mHandler ;
	
	public void post(Runnable runnable){
		mHandler.post(runnable);
	}
	
	private void stopMovingMotion(){
		mHandler.removeCallbacks(mFillinger);
	}
	
	public class Fillinger implements Runnable{
		private Scroller mScroller;
		private int lastY ;
		public Fillinger(){
			mScroller = new Scroller(mContext);
		}
		
		public void start(int startX, int startY ,int initialVelocity){
			lastY = startY;
			mScroller.fling(startX, startY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			post(this);
		}

		@Override
		public void run() {
			if(mAdapter == null || mAdapter.getCount() == 0|| mChilds.size() == 0){
				return ;
			}
			boolean more = mScroller.computeScrollOffset();
			int currenY = mScroller.getCurrY();
			int diffY = lastY - currenY;
			lastY = currenY;
			scrollByY(diffY);
			if(more){
				post(this);
			}
		}
		
	}
}
