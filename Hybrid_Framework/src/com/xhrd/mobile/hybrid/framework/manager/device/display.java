package com.xhrd.mobile.hybrid.framework.manager.device;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.framework.PluginBase;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

/**
 * Created by wangqianyu on 15/4/24.
 */
public class display extends PluginBase {
    private Window mWindow;  
    /**当前界面根view*/  
    private View mViewBoot;
      
    /**屏幕宽度*/  
    private int mScreenWidth;  
    /**屏幕高度*/  
    private int mScreenHeight;  
      
    /**状态栏高度*/  
    private int mStatusBarHeight;  
    /**标题栏高度*/  
    private int mTitleBarHeight;  
    
    public display()
    {
        Context context = HybridEnv.getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
        mScreenWidth = dm.widthPixels;
       
    }
    /** 
     * 获取相关属性值 
     */  
    private void getRelatedAttributeValue(){
    	if(mTitleBarHeight<1)
    	{
	    	 mWindow = HybridActivity.getInstance().getWindow();
	         /*取得系统当前显示的 view根（它是一个framelayout对象）*/
	         mViewBoot = mWindow.findViewById(Window.ID_ANDROID_CONTENT);
	        /*定义一个区域*/
	        Rect frame = new Rect();
	        /*区域范围为该textview的区域范围*/
	        getTargetView().getSelfView().getWindowVisibleDisplayFrame(frame);
	        /*获取状态栏高度。因为获取的区域不包含状态栏*/
	        mStatusBarHeight = frame.top;
	        /*获取除了状态栏和标题内容的起始y坐标，也就是 状态栏+标题栏的高度*/
	        int contentTop = mViewBoot.getTop();
	        /*一减得出标题栏高度*/
	        mTitleBarHeight = contentTop - mStatusBarHeight;
    	}
    }

	@JavascriptFunction
    public int getResolutionHeight(HybridView view, String[] params)
    {
    	getRelatedAttributeValue();
    	int resolutionHeight = mScreenHeight-mStatusBarHeight-mTitleBarHeight;
    	return resolutionHeight;
    }

	@JavascriptFunction
    public int getResolutionWidth(HybridView view, String[] params)
    {
    	getRelatedAttributeValue();
    	return mScreenWidth;
    }
    
	@Override
	public void addMethodProp(PluginData data) {
        data.addMethodWithReturn("getResolutionHeight");
        data.addMethodWithReturn("getResolutionWidth");
        data.addMethod("termination");

        data.addProperty("resolutionHeight", mScreenHeight-mStatusBarHeight-mTitleBarHeight);
        data.addProperty("resolutionWidth", mScreenWidth);
	}

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.App;
    }
}
