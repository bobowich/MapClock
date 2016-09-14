package com.zy.mapclock.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bobowich
 * Time: 2016/9/14.
 */
public class SlideLayout extends ViewGroup {
    public static final String TAG = SlideLayout.class.getSimpleName();
    View menuLayout;
    View contentLayout;
    private ViewDragHelper mDragHelper;
    float mOffset = 0;
    GestureDetector mGestureDetector;
    public SlideLayout(Context context) {
        super(context,null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                return true;
            }
            return false;
        }
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        Log.d(TAG, "SlideLayout: constractor");
        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener());

        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == menuLayout;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //super.onViewReleased(View releasedChild, float xvel, float yvel);
                if (releasedChild == menuLayout) {
                    int width = releasedChild.getMeasuredWidth();
                    Log.d(TAG, "onViewReleased: " + releasedChild.getLeft());
                    float offset = (width + releasedChild.getLeft()) * 1.0f / width;
                    if (xvel > 0 || (xvel == 0 && offset > 0.5f)) {
                        mDragHelper.settleCapturedViewAt(0, 0);
                    } else {
                        mDragHelper.settleCapturedViewAt(-width, 0);
                    }
                    invalidate();
                }
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                    mDragHelper.captureChildView(menuLayout, pointerId);
                }
            }


            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                int width = menuLayout.getWidth();
                int newLeft = Math.max(-width
                        , Math.min(0, left));
                return newLeft;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                //super.onViewPositionChanged(changedView, left, top, dx, dy);
                int width = changedView.getMeasuredWidth();
                float offset = (width + changedView.getLeft()) * 1.0f / width;
                mOffset = offset;
                changedView.setVisibility(offset == 0 ? INVISIBLE : VISIBLE);
                invalidate();
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return child == menuLayout ? menuLayout.getWidth() : 0;
            }
        });
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(width, height);
        View mMenuLayout = getChildAt(0);
        Log.d(TAG, "onMeasure: getChildAt(0)");
        MarginLayoutParams menulp = (MarginLayoutParams) mMenuLayout.getLayoutParams();
        int menuWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec
                , menulp.leftMargin + menulp.rightMargin, menulp.width);
        int menuHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec
                , menulp.topMargin + menulp.bottomMargin, menulp.height);
        menuLayout.measure(menuWidthMeasureSpec, menuHeightMeasureSpec);


        View mContentLayout = getChildAt(1);
        Log.d(TAG, "onMeasure: getChildAt(1)");
        MarginLayoutParams contentlp = (MarginLayoutParams) mContentLayout.getLayoutParams();

        int contentWidthSpec = MeasureSpec.makeMeasureSpec(width - contentlp.leftMargin
                - contentlp.rightMargin, MeasureSpec.EXACTLY);
        int contentHeightSpec = MeasureSpec.makeMeasureSpec(height - contentlp.topMargin
                - contentlp.bottomMargin, MeasureSpec.EXACTLY);
        contentLayout.measure(contentWidthSpec, contentHeightSpec);

        menuLayout = mMenuLayout;
        contentLayout = mContentLayout;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View menu = menuLayout;
        View content = contentLayout;
        MarginLayoutParams menulp = (MarginLayoutParams) menu.getLayoutParams();
        int menuLeft = (int) (-menuLayout.getMeasuredWidth() * (1.0f - mOffset));
        menu.layout(menuLeft, menulp.topMargin, menuLeft + menu.getMeasuredWidth()
                , menulp.topMargin + menu.getMeasuredHeight());

        MarginLayoutParams contentlp = (MarginLayoutParams) content.getLayoutParams();
        content.layout(contentlp.leftMargin, contentlp.topMargin
                , contentlp.leftMargin + content.getMeasuredWidth()
                , contentlp.topMargin + content.getMeasuredHeight());
    }

    @Override
    public void computeScroll() {
        //Log.d(TAG,Log.getStackTraceString(new Throwable()));
        if (mDragHelper !=null &&mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(menuLayout);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = mDragHelper.shouldInterceptTouchEvent(ev);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate: getChildAt");
        menuLayout = getChildAt(0);
        contentLayout = getChildAt(1);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }
}
