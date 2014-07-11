package com.bestfunforever.andengine.uikit.listview;

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

import com.bestfunforever.andengine.uikit.entity.ClipingRectangle;

public class ListView extends ClipingRectangle {

	private static final String TAG = "ListView";

	Recycler mRecycler;
	BaseAdapter mAdapter;
	DataSetObserver mDataSetObserver;
	SimpleBaseGameActivity mContext;

	SmartList<IAreaShape> mChilds = new SmartList<IAreaShape>();

	int mSelection;
	int mFirstPosition;

	private boolean dataChanged = true;
	private VelocityTracker velocityTracker;
	private Fillinger mFillinger;

	private int maxItemVisible;

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
		this.mAdapter.registerDataSetObserver(mDataSetObserver);
		mContext.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				layoutChildrent();
			}
		});
	}

	private void layoutChildrent() {
		float top = 0;
		if (selectionFlag) {
			top = mDiffTopForSelection;
			mFirstPosition = mSelection;
			final int totalItem = mAdapter.getCount();
			maxItemVisible = trackMaxItemVisible();
			if (totalItem - maxItemVisible < mFirstPosition) {
				if (mFirstPosition < maxItemVisible) {
					mFirstPosition = 0;
					top = 0;
				} else if (mFirstPosition > maxItemVisible) {
					mFirstPosition = totalItem - 1;
					top = getHeight() - mAdapter.getChildHeight();
				}
			}
		} else {
			if (mChilds.size() > 0) {
				top = mChilds.getFirst().getY();
			}
		}

		for (IAreaShape entity : mChilds) {
			mRecycler.addScrapView(entity);
			detachChild(entity);
		}
		mChilds.clear();
		
		// init view for first view ( could be selection)
		makeAndAddView(mFirstPosition, top, 0);
		fillGap(true);
		fillGap(false);

		dataChanged = false;
		selectionFlag = false;
	}

	private int trackMaxItemVisible() {
		return (int) (getHeight() / mAdapter.getChildHeight());
	}

	private IAreaShape makeAndAddView(int position, float top, int positionToAdd) {

		IAreaShape mView = mRecycler.getActiveView(position);
		if (mView != null && dataChanged) {
			return mView;
		}
		mView = obtainView(position);
		mView.setY(top);
		mChilds.add(positionToAdd, mView);
		attachChild(mView);
		return mView;
	}

	private IAreaShape obtainView(int position) {
		int type = mAdapter.getItemViewType(position);
		IAreaShape view = mRecycler.getScrapView(position, type);
		view = mAdapter.getView(position, view);
		view.setTag(type);
		view.setWidth(mAdapter.getChildWidth());
		view.setHeight(mAdapter.getChildHeight());
		return view;
	}

	private float initialY;
	private float currentY;

	private boolean selectionFlag;

	private float mDiffTopForSelection;

	private static final float MIN_DISTANCE_TO_SCROLL = 20;

	private boolean scrollFlag = false;

	public static final int INVALID_POSTION = -1;

	@Override
	public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (velocityTracker == null) { // If we do not have velocity tracker
			velocityTracker = VelocityTracker.obtain(); // then get one
		}
		final MotionEvent motionEvent = event.getMotionEvent();
		velocityTracker.addMovement(motionEvent); // add this movement to it
		Log.d(TAG, TAG + " mFirstPos " + mFirstPosition + " childcount " + mChilds.size());

		switch (event.getAction()) {
		case TouchEvent.ACTION_DOWN:
			scrollFlag = false;
			initialY = currentY = event.getY();
			stopMovingMotion();
			break;

		case TouchEvent.ACTION_MOVE:
			final float diffY = event.getY() - currentY;
			final float totalDiff = event.getY() - initialY;
			currentY = event.getY();
			if (!scrollFlag) {
				if (Math.abs(totalDiff) > MIN_DISTANCE_TO_SCROLL) {
					scrollFlag = true;
				}
			} else {
				mContext.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						scrollByY(diffY);
					}
				});
			}

			break;

		case TouchEvent.ACTION_UP:
			if (scrollFlag) {
				velocityTracker.computeCurrentVelocity(1000);
				int initialVelocity = (int) velocityTracker.getYVelocity();
				if (Math.abs(initialVelocity) > 0) {
					// start filling
					mFillinger.fling(0, (int) event.getY(), -initialVelocity);
				}
			} else {
				if (onItemClickListenner != null) {
					int position = getPositionByY(event.getX(), event.getY());
					if (position != INVALID_POSTION) {
						final IAreaShape view = mChilds.get(position);
						position = mFirstPosition + position;
						onItemClickListenner.onClick(view, position);
					}
				}

			}

			velocityTracker.recycle();
			break;

		default:
			break;
		}

		return true;
	}

	private int getPositionByY(float x, float y) {
		final int childCount = mChilds.size();
		for (int i = 0; i < childCount; i++) {
			IAreaShape view = mChilds.get(i);
			if (view.contains(x, y)) {
				return i;
			}
		}
		return INVALID_POSTION;
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
		final boolean down = diffY > 0;
		if ((mFirstPosition == 0 && firstTop + diffY >= 0 && diffY > 0)
				|| (mFirstPosition + childCount == mAdapter.getCount() && lastBottom + diffY < getHeight())
				&& diffY < 0) {
			// no need track mmotion
			return;
		}
		if (firstTop + diffY >= 0 && lastBottom + diffY <= getHeight()) {
			// in case first and last both in draw rectangle
			moveCurrentItem(diffY);
		} else {

			if (down) {
				for (int i = mChilds.size() - 1; i >= 0; i--) {
					final IAreaShape view = mChilds.get(i);
					if (view.getY() + diffY <= getHeight() + mAdapter.getChildHeight()) {
						break;
					} else {
						// add view to recycle
						addViewToRecycle(view);
						i--;
					}
				}

			} else {

				for (int i = 0; i < childCount; i++) {
					final IAreaShape view = mChilds.get(i);

					if (view.getY() + view.getHeight() + diffY + mAdapter.getChildHeight() >= 0) {
						break;
					} else {
						// add view to recycle
						addViewToRecycle(view);
						mFirstPosition++;
						i--;
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
		while (startOffset > -mAdapter.getChildHeight() && position >= 0) {
			mFirstPosition = position;
			IAreaShape view = makeAndAddView(position, startOffset - mAdapter.getChildHeight(), 0);
			startOffset -= view.getHeight();
			position -= 1;

		}
	}

	private void fillDown(int position, float startOffset) {
		while (startOffset < getHeight() + mAdapter.getChildHeight() && position < mAdapter.getCount()) {
			IAreaShape view = makeAndAddView(position, startOffset, mChilds.size());
			startOffset += view.getHeight();
			position += 1;
		}
	}

	private void addViewToRecycle(IAreaShape view) {
		mRecycler.addScrapView(view);
		detachChild(view);
		mChilds.remove(view);
	}

	public void setSelection(int selection) {
		setSelectionFromTop(selection, 0, false);
	}

	public void setSelectionFromTop(int selection, float diffTop, boolean scroll) {
		if(mAdapter == null){
			mFirstPosition = mSelection = selection;
			this.mDiffTopForSelection = diffTop;
			return;
		}
		if (mSelection > mAdapter.getCount()) {
			return;
		}
		mSelection = selection;
		if (diffTop < 0) {
			diffTop = 0;
		}
		if (diffTop > getHeight()) {
			diffTop = getHeight();
		}
		this.mDiffTopForSelection = diffTop;
		if (mFirstPosition == mSelection) {
			return;
		}
		if (mAdapter != null && mAdapter.getCount() > 0 && mChilds.size() > 0) {
			int diffPos = mSelection - mFirstPosition;
			final float diffY = diffPos * mAdapter.getChildHeight() - diffTop;
			if (scroll&&(mFirstPosition<mSelection&& diffPos<maxItemVisible)) {
				mFillinger.scroll(0, (int) mChilds.get(0).getY(), 0, (int) diffY);
			} else {
				selectionFlag = true;
				layoutChildrent();
			}
		}
	}

	private Handler mHandler;

	public void post(Runnable runnable) {
		mHandler.post(runnable);
	}

	private void stopMovingMotion() {
		mHandler.removeCallbacks(mFillinger);
	}

	public OnItemClickListenner getOnItemClickListenner() {
		return onItemClickListenner;
	}

	public void setOnItemClickListenner(OnItemClickListenner onItemClickListenner) {
		this.onItemClickListenner = onItemClickListenner;
	}

	public class Fillinger implements Runnable {
		private static final String tag = "Fillinger";
		private Scroller mScroller;
		private int lastY;

		public Fillinger() {
			mScroller = new Scroller(mContext);
		}

		public void fling(int startX, int startY, int initialVelocity) {
			lastY = startY;
			mScroller.fling(startX, startY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			post(this);
			Log.d(tag, tag + " duration " + mScroller.getDuration() + " initialVelocity " + initialVelocity);
		}

		/**
		 * ratio by 480 * 800
		 */
		private static final int defaultDurationPerScreenHeight = 1000;

		public void scroll(int startX, int startY, int dx, int dy) {
			int duration = dy / 480 * defaultDurationPerScreenHeight;
			if (duration > defaultDurationPerScreenHeight) {
				duration = defaultDurationPerScreenHeight;
			}
			mScroller.startScroll(startX, startY, dx, dy, duration);
			post(this);
		}

		@Override
		public void run() {
			if (mAdapter == null || mAdapter.getCount() == 0 || mChilds.size() == 0) {
				return;
			}
			boolean more = mScroller.computeScrollOffset();
			int currenY = mScroller.getCurrY();
			final int diffY = lastY - currenY;
			lastY = currenY;
			mContext.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					scrollByY(diffY);
				}
			});
			if (more) {
				post(this);
			}
		}

	}

	private OnItemClickListenner onItemClickListenner;

}
