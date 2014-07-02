package com.bestfunforever.andengine.uikit.listview;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.shape.IAreaShape;

public class Recycler {
	private IAreaShape[] mActiveViews = new IAreaShape[0];
	private ArrayList<IAreaShape>[] mScrapViews;

    private int mViewTypeCount;

    private ArrayList<IAreaShape> mCurrentScrap;
    
    public void setViewTypeCount(int viewTypeCount) {
        if (viewTypeCount < 1) {
            throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
        }
        //noinspection unchecked
        ArrayList<IAreaShape>[] scrapViews = new ArrayList[viewTypeCount];
        for (int i = 0; i < viewTypeCount; i++) {
            scrapViews[i] = new ArrayList<IAreaShape>();
        }
        mViewTypeCount = viewTypeCount;
        mCurrentScrap = scrapViews[0];
        mScrapViews = scrapViews;
    }
    
    public boolean shouldRecycleViewType(int viewType) {
        return viewType >= 0;
    }
    
    void clear() {
        if (mViewTypeCount == 1) {
            final ArrayList<IAreaShape> scrap = mCurrentScrap;
            scrap.clear();
        } else {
            final int typeCount = mViewTypeCount;
            for (int i = 0; i < typeCount; i++) {
                final ArrayList<IAreaShape> scrap = mScrapViews[i];
                scrap.clear();
            }
        }
    }
    
    /**
     * Get the view corresponding to the specified position. The view will be removed from
     * mActiveViews if it is found.
     *
     * @param position The position to look up in mActiveViews
     * @return The view if it is found, null otherwise
     */
    IAreaShape getActiveView(int position) {
        int index = position;
        final IAreaShape[] activeViews = mActiveViews;
        if (index >=0 && index < activeViews.length) {
            final IAreaShape match = activeViews[index];
            activeViews[index] = null;
            return match;
        }
        return null;
    }

    /**
     * @return A view from the ScrapViews collection. These are unordered.
     */
    IAreaShape getScrapView(int position,int itemViewType) {
        ArrayList<IAreaShape> scrapViews;
        if (mViewTypeCount == 1) {
            scrapViews = mCurrentScrap;
            int size = scrapViews.size();
            if (size > 0) {
                return scrapViews.remove(size - 1);
            } else {
                return null;
            }
        } else {
            if (itemViewType >= 0 && itemViewType < mScrapViews.length) {
                scrapViews = mScrapViews[itemViewType];
                int size = scrapViews.size();
                if (size > 0) {
                    return scrapViews.remove(size - 1);
                }
            }
        }
        return null;
    }

    /**
     * Put a view into the ScapViews list. These views are unordered.
     *
     * @param scrap The view to add
     */
    void addScrapView(IAreaShape scrap) {

        // Don't put header or footer views or views that should be ignored
        // into the scrap heap
        int viewType = scrap.getTag();
        if (!shouldRecycleViewType(viewType)) {
            return;
        }

        if (mViewTypeCount == 1) {
            mCurrentScrap.add(scrap);
        } else {
            mScrapViews[viewType].add(scrap);
        }
    }

    /**
     * Move all views remaining in mActiveViews to mScrapViews.
     */
    void scrapActiveViews() {
        final IAreaShape[] activeViews = mActiveViews;
        final boolean multipleScraps = mViewTypeCount > 1;

        ArrayList<IAreaShape> scrapViews = mCurrentScrap;
        final int count = activeViews.length;
        for (int i = 0; i < count; ++i) {
            final IAreaShape victim = activeViews[i];
            if (victim != null) {
                int whichScrap = victim.getTag();

                activeViews[i] = null;

                if (multipleScraps) {
                    scrapViews = mScrapViews[whichScrap];
                }
                scrapViews.add(victim);
            }
        }

        pruneScrapViews();
    }

    /**
     * Makes sure that the size of mScrapViews does not exceed the size of mActiveViews.
     * (This can happen if an adapter does not recycle its views).
     */
    private void pruneScrapViews() {
        final int maxViews = mActiveViews.length;
        final int viewTypeCount = mViewTypeCount;
        final ArrayList<IAreaShape>[] scrapViews = mScrapViews;
        for (int i = 0; i < viewTypeCount; ++i) {
            final ArrayList<IAreaShape> scrapPile = scrapViews[i];
            int size = scrapPile.size();
            final int extras = size - maxViews;
            size--;
            for (int j = 0; j < extras; j++) {
                scrapPile.remove(size--);
            }
        }
    }

    /**
     * Puts all views in the scrap heap into the supplied list.
     */
    void reclaimScrapViews(List<IAreaShape> views) {
        if (mViewTypeCount == 1) {
            views.addAll(mCurrentScrap);
        } else {
            final int viewTypeCount = mViewTypeCount;
            final ArrayList<IAreaShape>[] scrapViews = mScrapViews;
            for (int i = 0; i < viewTypeCount; ++i) {
                final ArrayList<IAreaShape> scrapPile = scrapViews[i];
                views.addAll(scrapPile);
            }
        }
    }

}	
