package it.visionapps.storevisionapps.widgets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import it.visionapps.storevisionapps.Utils;

/**
 * Created by iGio90 on 15/11/14.
 */
public class AutoGridRecyclerView extends RecyclerView {
    private int mPreviousFirstVisibleItem;

    public AutoGridRecyclerView(Context context) {
        super(context);
    }

    public AutoGridRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoGridRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * <code>newScrollY</code> position might not be correct if:
     * <ul>
     * <li><code>firstVisibleItem</code> is different than <code>mPreviousFirstVisibleItem</code></li>
     * <li>list has rows of different height</li>
     * </ul>
     * <p/>
     * It's necessary to track if row did not change, so events
     *
     * @see #estimateScrollY()
     */
    private boolean isSameRow(int firstVisibleItem) {
        boolean rowsChanged = firstVisibleItem == mPreviousFirstVisibleItem;
        mPreviousFirstVisibleItem = firstVisibleItem;
        return rowsChanged;
    }

    /**
     * Will be incorrect if rows has changed and if list has rows of different heights
     * <p/>
     * So when measuring scroll direction, it's necessary to ignore this value
     * if first visible row is different than previously calculated.
     *
     * @deprecated because it should be used with caution
     */
    private int estimateScrollY() {
        if (this.getChildAt(0) == null) return 0;
        View topChild = getChildAt(0);
        return getFirstVisibleItem() * topChild.getHeight() - topChild.getTop();
    }

    public int getFirstVisibleItem() {
        LayoutManager mLayoutManager = getLayoutManager();
        if (mLayoutManager == null)
            throw new IllegalStateException("Your RecyclerView does not have a LayoutManager.");
        if (mLayoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else {
            throw new RuntimeException("Currently only LinearLayoutManager is supported for the RecyclerView.");
        }
    }

    public int getLastVisibleItem() {
        LayoutManager mLayoutManager = getLayoutManager();
        if (mLayoutManager == null)
            throw new IllegalStateException("Your RecyclerView does not have a LayoutManager.");
        if (mLayoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else {
            throw new RuntimeException("Currently only LinearLayoutManager is supported for the RecyclerView.");
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int width = MeasureSpec.getSize(widthSpec);
        if (width != 0) {
            int spans = width / Utils.dpToPx(150, getContext().getResources());
            if (spans > 0 && getLayoutManager() instanceof GridLayoutManager) {
                ((GridLayoutManager)getLayoutManager()).setSpanCount(spans);
            }
        }
    }
}
