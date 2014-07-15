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

public class HorizontalListView extends ClipingRectangle {

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

	public HorizontalListView(SimpleBaseGameActivity Context, float pX, float pY, float pWidth, float pHeight,
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
		float left = 0;
		if (selectionFlag) {
			left = mDiffLeftForSelection;
			mFirstPosition = mSelection;
			final int totalItem = mAdapter.getCount();
			maxItemVisible = trackMaxItemVisible();
			if (totalItem - maxItemVisible < mFirstPosition) {
				if (mFirstPosition < maxItemVisible) {
					mFirstPosition = 0;
					left = 0;
				} else if (mFirstPosition > maxItemVisible) {
					mFirstPosition = totalItem - 1;
					left = getWidth() - mAdapter.getChildWidth();
				}
			}
		} else {
			if (mChilds.size() > 0) {
				left = mChilds.getFirst().getX();
			}
		}

		for (IAreaShape entity : mChilds) {
			mRecycler.addScrapView(entity);
			detachChild(entity);
		}
		mChilds.clear();

		// init view for first view ( could be selection)
		makeAndAddView(mFirstPosition, left, 0);
		fillGap(true);
		fillGap(false);

		dataChanged = false;
		selectionFlag = false;
	}

	private int trackMaxItemVisible() {
		return (int) (getWidth() / mAdapter.getChildWidth());
	}

	private IAreaShape makeAndAddView(int position, float left, int positionToAdd) {

		IAreaShape mView = mRecycler.getActiveView(position);
		if (mView != null && dataChanged) {
			return mView;
		}
		mView = obtainView(position);
		mView.setX(left);
		mChilds.add(positionToAdd, mView);
		attachChild(mView);
		return mView;
	}

	private IAreaShape obtainView(int position) {
		int type = mAdapter.getItemViewType(position);
		IAreaShape view = mRecycler.getScrapView(position, type);
		view = mAdapter.getView(position, view);
		view.setTag(type);
		return view;
	}

	private float initialX;
	private float currentX;

	private boolean selectionFlag;

	private float mDiffLeftForSelection;

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
			initialX = currentX = event.getX();
			stopMovingMotion();
			break;

		case TouchEvent.ACTION_MOVE:
			final float diffX = event.getX() - currentX;
			final float totalDiff = event.getX() - initialX;
			currentX = event.getX();
			if (!scrollFlag) {
				if (Math.abs(totalDiff) > MIN_DISTANCE_TO_SCROLL) {
					scrollFlag = true;
				}
			} else {
				mContext.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						scrollByX(diffX);
					}
				});
			}

			break;

		case TouchEvent.ACTION_UP:
			if (scrollFlag) {
				velocityTracker.computeCurrentVelocity(1000);
				int initialVelocity = (int) velocityTracker.getXVelocity();
				if (Math.abs(initialVelocity) > 0) {
					// start filling
					mFillinger.fling((int) event.getX(), 0, -initialVelocity);
				}
			} else {
				if (onItemClickListenner != null) {
					int position = getPositionFromLocation(event.getX(), event.getY());
					if (position != INVALID_POSTION) {
						final IAreaShape view = mChilds.get(position);
						position = mFirstPosition + position;
						onItemClickListenner.onClick(view, position);
					}
				}

			}

			// velocityTracker.recycle();
			break;
		case TouchEvent.ACTION_CANCEL:
			velocityTracker.recycle();
			break;
		default:
			break;
		}

		return true;
	}

	private int getPositionFromLocation(float x, float y) {
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
	 */
	private void scrollByX(float diffX) {
		final int childCount = mChilds.size();
		if (childCount == 0) {
			return;
		}
		final float firstLeft = mChilds.get(0).getX();
		final float lastRight = mChilds.getLast().getX() + mChilds.getLast().getWidth();
		final boolean right = diffX > 0;
		if ((mFirstPosition == 0 && firstLeft + diffX >= 0 && diffX > 0)
				|| (mFirstPosition + childCount == mAdapter.getCount() && lastRight + diffX < getWidth()) && diffX < 0) {
			// no need track mmotion
			return;
		}
		if (firstLeft + diffX >= 0 && lastRight + diffX <= getWidth()) {
			// in case first and last both in draw rectangle

			moveCurrentItem(diffX);
		} else {

			if (right) {
				for (int i = mChilds.size() - 1; i >= 0; i--) {
					final IAreaShape view = mChilds.get(i);
					if (view.getX() + diffX <= getWidth() + mAdapter.getChildWidth()) {
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

					if (view.getX() + view.getWidth() + diffX + mAdapter.getChildWidth() >= 0) {
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
			moveCurrentItem(diffX);

			// get scrap view to active
			fillGap(right);
		}

	}

	private void moveCurrentItem(float diffX) {
		for (int i = 0; i < mChilds.size(); i++) {
			final IAreaShape view = mChilds.get(i);
			view.setX(view.getX() + diffX);
		}
	}

	private void fillGap(boolean down) {
		final int childCOunt = mChilds.size();
		if (down) {
			final float startOffset = childCOunt > 0 ? mChilds.get(0).getX() : (getWidth());
			fillLeft(mFirstPosition - 1, startOffset);
		} else {
			final float startOffset = childCOunt > 0 ? (mChilds.get(childCOunt - 1).getWidth() + mChilds.get(
					childCOunt - 1).getX()) : 0;
			fillRight(mFirstPosition + childCOunt, startOffset);
		}
	}

	private void fillLeft(int position, float startOffset) {
		while (startOffset > -mAdapter.getChildWidth() && position >= 0) {
			mFirstPosition = position;
			IAreaShape view = makeAndAddView(position, startOffset - mAdapter.getChildWidth(), 0);
			startOffset -= view.getWidth();
			position -= 1;
		}
	}

	private void fillRight(int position, float startOffset) {
		while (startOffset < getWidth() + mAdapter.getChildWidth() && position < mAdapter.getCount()) {
			IAreaShape view = makeAndAddView(position, startOffset, mChilds.size());
			startOffset += view.getWidth();
			position += 1;
		}
	}

	private void addViewToRecycle(IAreaShape view) {
		mRecycler.addScrapView(view);
		detachChild(view);
		mChilds.remove(view);
	}

	public void setSelection(int selection) {
		setSelectionFromLeft(selection, 0, false);
	}

	public void setSelectionFromLeft(int selection, float diffLeft, boolean scroll) {
		if (mAdapter == null) {
			mFirstPosition = mSelection = selection;
			this.mDiffLeftForSelection = diffLeft;
			return;
		}
		if (mSelection > mAdapter.getCount()) {
			return;
		}
		mSelection = selection;
		if (diffLeft < 0) {
			diffLeft = 0;
		}
		if (diffLeft > getHeight()) {
			diffLeft = getHeight();
		}
		this.mDiffLeftForSelection = diffLeft;
		if (mFirstPosition == mSelection) {
			return;
		}
		if (mAdapter != null && mAdapter.getCount() > 0 && mChilds.size() > 0) {
			int diffPos = mSelection - mFirstPosition;
			final float diffX = diffPos * mAdapter.getChildWidth() - diffLeft;
			if (scroll && (mFirstPosition < mSelection && diffPos < maxItemVisible)) {
				mFillinger.scroll((int) mChilds.get(0).getX(), 0, (int) diffX, 0);
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
		private int lastX;

		public Fillinger() {
			mScroller = new Scroller(mContext);
		}

		public void fling(int startX, int startY, int initialVelocity) {
			lastX = startX;
			mScroller.fling(startX, startY, initialVelocity, 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			post(this);
			Log.d(tag, tag + " duration " + mScroller.getDuration() + " initialVelocity " + initialVelocity);
		}

		/**
		 * ratio by 480 * 800
		 */
		private static final int defaultDurationPerScreenHeight = 600;

		public void scroll(int startX, int startY, int dx, int dy) {
			int duration = dx / 800 * defaultDurationPerScreenHeight;
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
			int currenX = mScroller.getCurrX();
			final int diffX = lastX - currenX;
			lastX = currenX;
			mContext.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					scrollByX(diffX);
				}
			});
			if (more) {
				post(this);
			}
		}

	}

	private OnItemClickListenner onItemClickListenner;
}
