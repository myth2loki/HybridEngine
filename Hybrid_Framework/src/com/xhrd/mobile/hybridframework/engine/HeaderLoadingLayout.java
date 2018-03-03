package com.xhrd.mobile.hybridframework.engine;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xhrd.mobile.hybridframework.util.SharepreFerenceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class HeaderLoadingLayout extends LoadingLayout {
    /**
     * 旋转动画时间
     */
    private static final int ROTATE_ANIM_DURATION = 150;
    /**
     * Header的容器
     */
    private RelativeLayout mHeaderContainer;
    /**
     * 箭头图片
     */
    private ImageView mArrowImageView;
    /**
     * 进度条
     */
    private ProgressBar mProgressBar;
    /**
     * 状态提示TextView
     */
    private TextView mHintTextView;
    /**
     * 最后更新时间的TextView
     */
    private TextView mHeaderTimeView;
    /**
     * 向上的动画
     */
    private Animation mRotateUpAnim;
    /**
     * 向下的动画
     */
    private Animation mRotateDownAnim;
    private String onPullToRefreshText;
    private String onReleaseToRefreshText;
    private String onRefreshingText;
    private LinearLayout pull_to_refresh_header_hint_ll,pull_to_refresh_last_update_time_ll;
    /**时间转换*/
    private SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd HH:mm");
    /**
     * 构造方法
     *
     * @param context context
     */
    public HeaderLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public HeaderLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        onPullToRefreshText=RDResourceManager.getInstance().getString("pushmsg_center_pull_down_text");
        onReleaseToRefreshText=RDResourceManager.getInstance().getString("pull_to_refresh_header_hint_ready");
        onRefreshingText=RDResourceManager.getInstance().getString("pull_to_refresh_header_hint_loading");


        //pull_to_refresh_header_hint_ll
        //pull_to_refresh_last_update_time_ll


        mHeaderContainer = (RelativeLayout) findViewById(RDResourceManager.getInstance().getId("pull_to_refresh_header_content"));

        pull_to_refresh_header_hint_ll =(LinearLayout)findViewById(RDResourceManager.getInstance().getId("pull_to_refresh_header_hint_ll"));
        pull_to_refresh_last_update_time_ll =(LinearLayout)findViewById(RDResourceManager.getInstance().getId("pull_to_refresh_last_update_time_ll"));


        mArrowImageView = (ImageView) findViewById(RDResourceManager.getInstance().getId("pull_to_refresh_header_arrow"));
        mHintTextView = (TextView) findViewById(RDResourceManager.getInstance().getId("pull_to_refresh_header_hint_textview"));
        mProgressBar = (ProgressBar) findViewById(RDResourceManager.getInstance().getId("pull_to_refresh_header_progressbar"));
        mHeaderTimeView = (TextView) findViewById(RDResourceManager.getInstance().getId("pull_to_refresh_header_time"));


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
    }

    @Override
    public void setLastUpdatedLabel() {
        // 如果最后更新的时间的文本是空的话，隐藏前面的标题

        String time=  dateFormat.format(new Date(System.currentTimeMillis()));
        String last= SharepreFerenceUtil.getString(SharepreFerenceUtil.PULLLASTUPDATATIME);
        if(!TextUtils.isEmpty(last)){
            mHeaderTimeView.setText("最后更新的时间："+last);
        }else{
            mHeaderTimeView.setText("最后更新的时间："+time);
        }
        SharepreFerenceUtil.putString(SharepreFerenceUtil.PULLLASTUPDATATIME,time);
    }

    @Override
    public void setVisbleTimeLabel(boolean isVisble) {
        pull_to_refresh_last_update_time_ll.setVisibility(isVisble ? View.INVISIBLE : View.VISIBLE);

    }

    @Override
    public void setSizeTimeLabel(String fontSize) {
        if(TextUtils.isEmpty(fontSize))
            return;

        mHeaderTimeView.setTextSize(Integer.valueOf(fontSize));

    }

    @Override
    public void setColorTimeLabel(String colorValue) {
        if(TextUtils.isEmpty(colorValue))
            return;
        mHeaderTimeView.setTextColor(Color.parseColor(colorValue));

    }

    @Override
    public void setContentColorLabel(String colorValue) {
        if(TextUtils.isEmpty(colorValue)) {
            return;
        }
        mHintTextView.setTextColor(Color.parseColor(colorValue));
    }

    @Override
    public void setContentVisbleLabel(boolean isInvisible) {
        pull_to_refresh_header_hint_ll.setVisibility(isInvisible ? View.INVISIBLE : View.VISIBLE);
    }


    @Override
    public void setContentSizeLabel(String fontSize) {
        if(TextUtils.isEmpty(fontSize))
            return;
        mHintTextView.setTextSize(Integer.valueOf(fontSize));

    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            return mHeaderContainer.getHeight();
        }
        return (int) (getResources().getDisplayMetrics().density * 60);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {


        View container = LayoutInflater.from(context).inflate(RDResourceManager.getInstance().getLayoutId("pull_to_refresh_header"), null);
        return container;
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        mArrowImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);

        switch (curState) {
            case RESET:
                onReset();
                break;

            case RELEASE_TO_REFRESH:
                onReleaseToRefresh();
                break;

            case PULL_TO_REFRESH:
                onPullToRefresh();
                break;

            case REFRESHING:
                onRefreshing();
                break;

            case NO_MORE_DATA:
                onNoMoreData();
                break;

            default:
                break;
        }


        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        mArrowImageView.clearAnimation();
        mHintTextView.setText(onPullToRefreshText);
    }

    @Override
    protected void onPullToRefresh() {
        if (State.RELEASE_TO_REFRESH == getPreState()) {
            mArrowImageView.clearAnimation();
            mArrowImageView.startAnimation(mRotateDownAnim);
        }
        mHintTextView.setText(onPullToRefreshText);
    }

    @Override
    protected void onReleaseToRefresh() {
        mArrowImageView.clearAnimation();
        mArrowImageView.startAnimation(mRotateUpAnim);
        mHintTextView.setText(onReleaseToRefreshText);
    }

    @Override
    protected void onRefreshing() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mHintTextView.setText(onRefreshingText);
        setLastUpdatedLabel();
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {
        if(TextUtils.isEmpty(pullLabel))
            return;
        this.onPullToRefreshText = pullLabel.toString();
    }

    @Override
    public void seteleaseLabel(CharSequence releaseLabel) {
        if(TextUtils.isEmpty(releaseLabel))
            return;
        this.onReleaseToRefreshText = releaseLabel.toString();
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        if(TextUtils.isEmpty(refreshingLabel))
            return;
        this.onRefreshingText = refreshingLabel.toString();
    }

    @Override
    public void setLoadingDrawable(String drawableName) {
        if (TextUtils.isEmpty(drawableName)) {
            return;
        }
        Bitmap bmp = BitmapFactory.decodeFile(drawableName);
        mArrowImageView.setImageBitmap(bmp);
    }
}
