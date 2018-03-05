package com.xhrd.mobile.hybrid.engine;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class FooterLoadingLayout extends LoadingLayout {
    /**
     * 旋转动画时间
     */
    private static final int ROTATE_ANIM_DURATION = 150;
    /**
     * 向上的动画
     */
    private Animation mRotateUpAnim;
    /**
     * 向下的动画
     */
    private Animation mRotateDownAnim;
    /**进度条*/
    private ImageView mFootImage;

    private ProgressBar progressBar;
    /** 显示的文本 */
    private TextView mHintView;
    /** 上拉可以刷新  */
    private String onPullToRefreshText ;
    /** 松开后可以刷新  */
    private String onReleaseToRefreshText ;
    /** 正在加载中  */
    private String onRefreshingText;
    /** 没有更多数据  */
    private String onNoMoreDataText;

    private LinearLayout pull_to_load_footer_hint_ll;
    /**
     * 构造方法
     * 
     * @param context context
     */
    public FooterLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public FooterLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     * 
     * @param context context
     */
    private void init(Context context) {
        //pull_to_load_footer_hint_ll

        onPullToRefreshText= HybridResourceManager.getInstance().getString("pull_to_refresh_header_hint_normal2");
        onReleaseToRefreshText= HybridResourceManager.getInstance().getString("pull_to_refresh_header_hint_ready");
        onRefreshingText= HybridResourceManager.getInstance().getString("pull_to_refresh_header_hint_loading");



        mFootImage = (ImageView)findViewById(HybridResourceManager.getInstance().getId("pull_to_load_footer"));
        mHintView = (TextView) findViewById(HybridResourceManager.getInstance().getId("pull_to_load_footer_hint_textview"));

        pull_to_load_footer_hint_ll = (LinearLayout) findViewById(HybridResourceManager.getInstance().getId("pull_to_load_footer_hint_ll"));
        progressBar = (ProgressBar)findViewById(HybridResourceManager.getInstance().getId("pull_to_refresh_header_progressbar"));
        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = -180f;     // SUPPRESS CHECKSTYLE
        // 初始化旋转动画
        mRotateUpAnim = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(toDegree, 0.0f, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
        setState(State.RESET);
    }
    
    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate( HybridResourceManager.getInstance().getLayoutId("pull_to_load_footer"), null);
        return container;
    }

    @Override
    public int getContentSize() {
        View view = findViewById( HybridResourceManager.getInstance().getId("pull_to_load_footer_content"));
        if (null != view) {
            return view.getHeight();
        }
        
        return (int) (getResources().getDisplayMetrics().density * 40);
    }

    @Override
    public void setContentColorLabel(String colorValue) {
        if (TextUtils.isEmpty(colorValue))
            return;
        mHintView.setTextColor(Color.parseColor(colorValue));
    }

    @Override
    public void setContentVisbleLabel(boolean isInvisible) {
        pull_to_load_footer_hint_ll.setVisibility(isInvisible ? View.INVISIBLE : View.VISIBLE);

    }

    @Override
    public void setContentSizeLabel(String  fontSize) {
        if (TextUtils.isEmpty(fontSize))
            return;
        mHintView.setTextSize(Integer.valueOf(fontSize));

    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
        mFootImage.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        super.onStateChanged(curState, oldState);
    }
    
    @Override
    protected void onReset() {
        mFootImage.clearAnimation();
        mHintView.setText(onPullToRefreshText);
    }

    @Override
    protected void onPullToRefresh() {
        if (State.RELEASE_TO_REFRESH == getPreState()) {
            mFootImage.clearAnimation();
            mFootImage.startAnimation(mRotateUpAnim);
        }
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(onPullToRefreshText);
    }

    @Override
    protected void onReleaseToRefresh() {
        mFootImage.clearAnimation();
        mFootImage.startAnimation(mRotateUpAnim);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(onReleaseToRefreshText);
    }

    @Override
    protected void onRefreshing() {
        mFootImage.clearAnimation();
        mFootImage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(onRefreshingText);
    }
    
    @Override
    protected void onNoMoreData() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(HybridResourceManager.getInstance().getString("pull_to_refresh_no_more_data"));
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {
        if (TextUtils.isEmpty(pullLabel))
            return;
       this.onPullToRefreshText=pullLabel.toString();
    }

    @Override
    public void seteleaseLabel(CharSequence releaseLabel) {
        if (TextUtils.isEmpty(releaseLabel))
            return;
       this.onReleaseToRefreshText=releaseLabel.toString();
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        if (TextUtils.isEmpty(refreshingLabel))
            return;
      this.onRefreshingText=refreshingLabel.toString();
    }
    @Override
    public void setLoadingDrawable(String drawableName) {
        if (TextUtils.isEmpty(drawableName)) {
            return;
        }
        Bitmap bmp = BitmapFactory.decodeFile(drawableName);
        mFootImage.setImageBitmap(bmp);
    }
}
