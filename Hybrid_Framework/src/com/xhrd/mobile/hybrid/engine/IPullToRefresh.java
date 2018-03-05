package com.xhrd.mobile.hybrid.engine;

/**
 * 定义了拉动刷新的接口
 * 
 * @author Li Hong
 * @since 2013-8-22
 * @param <T>
 */
interface IPullToRefresh<T extends HybridView> {
    
    /**
     * 设置当前下拉刷新是否可用
     * 
     * @param pullRefreshEnabled true表示可用，false表示不可用
     */
    void setPullRefreshEnabled(boolean pullRefreshEnabled);
    
    /**
     * 设置当前上拉加载更多是否可用
     * 
     * @param pullLoadEnabled true表示可用，false表示不可用
     */
    void setPullLoadEnabled(boolean pullLoadEnabled);
    
    /**
     * 滑动到底部是否自动加载更多数据
     * 
     * @param scrollLoadEnabled 如果这个值为true的话，那么上拉加载更多的功能将会禁用
     */
    void setScrollLoadEnabled(boolean scrollLoadEnabled);
    
    /**
     * 判断当前下拉刷新是否可用
     * 
     * @return true如果可用，false不可用
     */
    boolean isPullRefreshEnabled();
    
    /**
     * 判断上拉加载是否可用
     * 
     * @return true可用，false不可用
     */
    boolean isPullLoadEnabled();
    
    /**
     * 滑动到底部加载是否可用
     * 
     * @return true可用，否则不可用
     */
    boolean isScrollLoadEnabled();
    
    /**
     * 设置刷新的监听器
     * 
     * @param refreshListener 监听器对象
     */
    void setOnRefreshListener(PullToRefreshBase.OnRefreshListener<T> refreshListener);
    
    /**
     * 结束下拉刷新
     */
    void onPullDownRefreshComplete();
    
    /**
     * 结束上拉加载更多
     */
    void onPullUpRefreshComplete();
    
    /**
     * 得到可刷新的View对象
     * 
     * @return 返回调用{@link #createRefreshableView(Context, AttributeSet)} 方法返回的对象
     */
    T getRefreshableView();
    
    /**
     * 得到Header布局对象
     * 
     * @return Header布局对象
     */
    LoadingLayout getHeaderLoadingLayout();
    
    /**
     * 得到Footer布局对象
     * 
     * @return Footer布局对象
     */
    LoadingLayout getFooterLoadingLayout();
    
    /**
     * 设置最后更新的时间文本
     */
    void setLastUpdatedLabel();


    /**
     * 设置最后更新的时间文本字体的大小
     *
     * @param fontSize
     */
    void setSizeTimeLabel(String  fontSize);


    /**
     * 设置最后更新的时间文本字体的颜色
     *
     * @param colorValue
     */

    void setColorTimeLabel(String colorValue);

    /**
     * 设置最后更新的时间文本字体是否显示
     *
     * @param isVisble
     */

    void setVisbleTimeLabel(boolean  isVisble);


    /**
     * 设置头部刷新内容显示与隐藏
     *
     * @param contentVisable
     */

    void setHeaderContentVisable(boolean  contentVisable);

    /**
     * 设置头部刷新内容大小
     *
     * @param sizeLabel
     */

    void setHeaderContentSizeLabel(String sizeLabel);

    /**
     * 设置头部刷新内容颜色
     *
     * @param colorLabel
     */

    void setHeaderContentColorLabel(String colorLabel);

    /**
     * 设置头部刷新内容
     *
     * @param pullLabel
     */

    void setHeaderContentPullLabel(String pullLabel);

    /**
     * 设置头部刷新内容
     *
     * @param contentreleaseLabel
     */

    void setHeaderContentreleaseLabel(String contentreleaseLabel);

    /**
     * 设置头部刷新内容
     * @param contentrerefreshingLabel
     */

    void setHeaderContentrerefreshingLabel(String contentrerefreshingLabel);

    /**
     * 设置底部刷新内容显示与隐藏
     *
     * @param contentVisable
     */

    void setFootContentVisable(boolean  contentVisable);

    /**
     * 设置底部刷新内容大小
     *
     * @param sizeLabel
     */

    void setFootContentSizeLabel(String sizeLabel);

    /**
     * 设置底部刷新内容颜色
     *
     * @param colorLabel
     */

    void setFootContentColorLabel(String colorLabel);

    /**
     * 设置底部刷新内容
     *
     * @param pullLabel
     */

    void setFootContentPullLabel(String pullLabel);

    /**
     * 设置底部刷新内容
     *
     * @param contentreleaseLabel
     */

    void setFootContentreleaseLabel(String contentreleaseLabel);

    /**
     * 设置底部刷新内容
     * @param contentrerefreshingLabel
     */

    void setFootContentrerefreshingLabel(String contentrerefreshingLabel);

    /**
     * 设置底部刷新图片
     * @param drawableUrl
     */

    void setLoadingDrawable(String drawableUrl);
    /**
     * 设置顶部刷新图片
     * @param drawableUrl
     */

    void setPullDrawable(String drawableUrl);
}
