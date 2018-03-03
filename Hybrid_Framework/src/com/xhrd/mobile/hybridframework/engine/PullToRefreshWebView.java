package com.xhrd.mobile.hybridframework.engine;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;

/**
 * 封装了WebView的下拉刷新
 * 
 * @author Li Hong
 * @since 2013-8-22
 */
public class PullToRefreshWebView extends PullToRefreshBase<RDCloudView> {

    /**
     * 构造方法
     * 
     * @param window RDCloudWindow
     */
    public PullToRefreshWebView(RDCloudWindow window) {
        this(window, null);
    }
    
    /**
     * 构造方法
     * 
     * @param window RDCloudWindow
     * @param attrs attrs
     */
    public PullToRefreshWebView(RDCloudWindow window, AttributeSet attrs) {
        this(window, attrs, 0);
    }
    
    /**
     * 构造方法
     * 
     * @param window RDCloudWindow
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullToRefreshWebView(RDCloudWindow window, AttributeSet attrs, int defStyle) {
        super(window, attrs);
    }


    @Override
    protected RDCloudView createRefreshableView(Context context, AttributeSet attrs) {
        //用于添加window的上拉和下拉刷新
        RDCloudView view = new RDCloudOriginalView(mWindow);
        view.setRefresableParent(this);
        return view;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        RDCloudView view = getRefreshableView();
        //Log.e("event action outside:", "view.isChildrenRefreshable(event)="+view.isChildrenRefreshable(event) + ", view isChildInterceptTouchEventEnabled: " + view.isChildInterceptTouchEventEnabled(event)+",view.isChildPullLoading(event)="+view.isChildPullLoading(event)+", view.isChildPullRefreshing(event)="+view.isChildPullRefreshing(event)+",super.onInterceptTouchEvent(event)="+super.onInterceptTouchEvent(event));
        if (view.isChildrenRefreshable(event)) {
            return false;
        }
        if (view.isChildInterceptTouchEventEnabled(event)) {
            return false;
        }
        //TODO 性能问题
        if (view.isChildPullLoading(event) || view.isChildPullRefreshing(event)) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     *
            * 判断刷新的View是否滑动到顶部
    *
            * @return true表示已经滑动到顶部，否则false
    *
     */
    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0;
    }

    /**
     *判断刷新的View是否滑动到底
     *
     * @return true表示已经滑动到底部，否则false
     */
    @Override
    protected boolean isReadyForPullUp() {
    	//mRefreshableView.getContentHeight()webview当前页面显示整个页面的高度  mRefreshableView.getScale() 
    	//mRefreshableView.getScale() 获取webview的缩放比例  
    	//getContentHeight()乘以getScale() 才是真正的真个html页面的高度

        double exactContentHeight = Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());
//        boolean up=mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
        return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight() - 2);
    }
}
