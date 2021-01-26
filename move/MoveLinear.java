package com.miracles.viewlib.move;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@SuppressLint("AppCompatCustomView")
public class MoveLinear extends LinearLayout implements View.OnTouchListener {
    private int xDelta;
    private int yDelta;
    private boolean isClick = true;
    private int vHeight = 0;
    private int vWidth = 0;
    private int viewX = 0;
    private int viewY = 0;
    private long downTime = 0;
    private long upTime = 0;
    private int position = 0;
    private Runnable mRunnable = null;
    private Context mContext;
    private ViewGroup rootView;
    private onClickListener mListener = null;
    private onLongClickListener mLongListener = null;
    private Handler handler = new Handler();

    public MoveLinear(Context context) {
        super(context);
        new MoveLinear(context, null);
    }

    public MoveLinear(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setRootView(ViewGroup view) {
        this.rootView = view;
    }

    /**
     * 设置控件占屏幕的位置
     *
     * @param x X轴坐标
     * @param y Y轴坐标
     */
    public void setOrientation(int x, int y) {
        LayoutParams layoutParams = (LayoutParams) this.getLayoutParams();
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        this.viewX = x;
        this.viewY = y;
    }

    /**
     * 获取控件占屏幕X轴的坐标
     *
     * @return X坐标
     */
    public int getViewX() {
        return viewX;
    }

    /**
     * 获取控件占屏幕Y轴的坐标
     *
     * @return Y轴坐标
     */
    public int getViewY() {
        return viewY;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 50;
        layoutParams.topMargin = 50;
        this.setLayoutParams(layoutParams);
        this.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(final View view, MotionEvent event) {
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();
        DisplayMetrics screenInfo = getScreenInfo(mContext);
        final int screenHeight = screenInfo.heightPixels;
        final int screenWidth = screenInfo.widthPixels;
        if (vHeight == 0 && vWidth == 0) {
            vWidth = view.getWidth();
            vHeight = view.getHeight();
        }
        //获取当前布局方向
        int orientation = this.getOrientation();
        //计算点击位置
        int childCount = ((ViewGroup) view).getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                int childWidth = ((ViewGroup) view).getChildAt(i).getWidth();
                int childHeight = ((ViewGroup) view).getChildAt(i).getHeight();
                if (childWidth != 0 && childHeight != 0) {
                    position = getPosition(x, y, childWidth, childHeight);
                }
            }
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (isClick) {
                    handler.removeCallbacks(mRunnable);
                    upTime = System.currentTimeMillis();
                    //点击时长小于2秒则触发点击事件，反之则触发长按事件
                    if ((upTime - downTime) / 1000 < 2) {
                        if (mListener != null) {
                            mListener.onClick(view, position);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                LayoutParams params = (LayoutParams) view
                        .getLayoutParams();
                xDelta = x - params.leftMargin;
                yDelta = y - params.topMargin;
                if (isClick) {
                    downTime = System.currentTimeMillis();
                    //点击时长小于2秒则触发点击事件，反之则触发长按事件
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            //要做的事情
                            if (mLongListener != null) {
                                mLongListener.onLongClick(view, position);
                                handler.removeCallbacks(mRunnable);
                            }
                        }
                    };
                    handler.postDelayed(mRunnable, 1500);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                handler.removeCallbacks(mRunnable);
                isClick = false;
                //如果当前布局是横向就直接取1，反之则取子类数量
                int count = orientation == LinearLayout.HORIZONTAL ? 1 : ((ViewGroup) view).getChildCount();
                LayoutParams layoutParams = (LayoutParams) view
                        .getLayoutParams();
                int xDistance = x - xDelta >= screenWidth ? screenWidth : Math.max(x - xDelta, 0);
                if (x - xDelta - vWidth < screenWidth - vWidth * 2) {
                    layoutParams.leftMargin = xDistance;
                    this.viewX = xDistance;
                }
                int yDistance = Math.max(y - yDelta, 0);
                if (yDistance + vHeight / count <= screenHeight - vHeight) {
                    layoutParams.topMargin = yDistance;
                    this.viewY = yDistance;
                }
                view.setLayoutParams(layoutParams);
                break;
        }
        if (rootView != null) {
            rootView.invalidate();
        }
        return true;
    }

    /**
     * 获取点击坐标位置
     *
     * @param x      x轴
     * @param y      y轴
     * @param width  子view宽度
     * @param height 子view高度
     * @return 坐标位置
     */
    private int getPosition(int x, int y, int width, int height) {
        //获取当前布局方向
        int orientation = this.getOrientation();
        //计算点击位置
        if (width != 0 && height != 0) {
            return (orientation == LinearLayout.HORIZONTAL ? (x - viewX) / width
                    : (y - viewY - height * 2) / height);
        }
        return 0;
    }

    /**
     * 得到屏幕信息
     *
     * @param context
     * @return
     */
    private DisplayMetrics getScreenInfo(Context context) {
        Activity activity = (Activity) context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public interface onClickListener {
        void onClick(View v, int position);
    }

    /**
     * 点击事件
     *
     * @param listener
     */
    public void setOnClickListener(onClickListener listener) {
        this.mListener = listener;
    }

    public interface onLongClickListener {
        void onLongClick(View v, int position);
    }

    /**
     * 长按事件
     *
     * @param listener
     */
    public void setOnLongClickListener(onLongClickListener listener) {
        this.mLongListener = listener;
    }
}
