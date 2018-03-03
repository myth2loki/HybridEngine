package com.xhrd.mobile.hybridframework.framework.Manager.device;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.engine.RDCloudView;
import com.xhrd.mobile.hybridframework.framework.PluginData;
import com.xhrd.mobile.hybridframework.engine.HybridActivity;
import com.xhrd.mobile.hybridframework.framework.PluginBase;
import com.xhrd.mobile.hybridframework.framework.RDCloudApplication;

/**
 * Screen模块管理设备屏幕信息.
 * Created by wangqianyu on 15/4/24.
 */
public class screen extends PluginBase {
    private int mResolutionHeight;
    private int mResolutionWidth;
    private float mScale;
    private float mDpiX;
    private float mDpiY;
	private SharedPreferences msp;

    public screen() {
        Context context = RDCloudApplication.getApp();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mResolutionHeight = dm.heightPixels;
        mResolutionWidth = dm.widthPixels;
        mScale = dm.density;
        mDpiX = dm.xdpi;
        mDpiY = dm.ydpi;
    }

    @JavascriptFunction
    public void setBrightness(RDCloudView view,String[] params) {
        final float brightness = Float.parseFloat(params[0]);
        final Activity act = HybridActivity.getInstance();
        if (act != null) {
        	
        	 act.runOnUiThread(new Runnable() {
     			
     			@Override
     			public void run() {
     	            Window window = act.getWindow();
     	            WindowManager.LayoutParams lp = window.getAttributes();
     	            float b = 0.1f;
					if (brightness >= 1) {
						b = 1;//brightness/255f;
					}else {
						if(brightness<0) {
							b = 0.009f;
						}else {
							b = brightness;
						}
					}
					lp.screenBrightness = b;
     	            
//     	            if (brightness == 0) {
//     	            	lp.screenBrightness = 0.009f;
//					}else {
//						lp.screenBrightness = brightness;
//						if (brightness >= 1) {
//							msp.edit().putString("brightness", "1").commit();
//						}else {
//							msp.edit().putString("brightness", brightness + "").commit();
//						}
//					}
     	            window.setAttributes(lp);
     			}
     		});
        }
    }

    @JavascriptFunction
    public String getBrightness(RDCloudView view,String[] params) {
        return HybridActivity.getInstance().getWindow().getAttributes().screenBrightness+"";
//        int screenBrightness = -1;
//        try{
//            screenBrightness = Settings.System.getInt(RDCloudApplication.getApp().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,-1);
//        } catch (Exception e){
//        }
//        return getFloatRound((double)screenBrightness/255, 2)+"";
    }

	public float getFloatRound(double sourceData,int a)  
    {  
        int i = (int) Math.round(sourceData*a);     //小数点后 a 位前移，并四舍五入  
        float f2 = (float) (i/(float)a);        //还原小数点后 a 位  
        return f2;  
    }

    @JavascriptFunction
    public void lockOrientation(RDCloudView view,String[] params) {

        Activity act = HybridActivity.getInstance();
        if (act != null) {
        	if(isPad())
        	{
        		if(mResolutionWidth>mResolutionHeight)
        		{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    }else {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
        		}else {
        			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
        	}else {
        		if(mResolutionWidth>mResolutionHeight)
        		{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    }else {
                        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

        		}else {
        			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			}
        }
    }

    /** 
     * 判断是否为平板 
     *  
     * @return 
     */  
    private boolean isPad() {  
        WindowManager wm = (WindowManager) HybridActivity.getInstance().getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();  
        display.getMetrics(dm);
        this.mResolutionHeight = dm.heightPixels;
        this.mResolutionWidth = dm.widthPixels;
        double x = Math.pow(dm.widthPixels , 2);  
        double y = Math.pow(dm.heightPixels , 2);  
        // 屏幕尺寸  
        //double screenInches = Math.sqrt(x + y);  
        double screenInches = Math.sqrt(x + y) / (160 * dm.density);  
        // 大于6尺寸则为Pad  
        if (screenInches >= 6.0) {  
            return true;  
        }  
        return false;  
    }
    @JavascriptFunction
    public void unlockOrientation(RDCloudView view,String[] params) {
    	  Activity act = HybridActivity.getInstance();
          if (act != null) {
//              int orientation = Integer.parseInt(params[0]);
//            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
              act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
          }
    }

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("setBrightness");
        data.addMethodWithReturn("getBrightness");
        data.addMethod("lockOrientation");
        data.addMethod("unlockOrientation");
        data.addMethod("termination");

        data.addProperty("portrait_primary", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        data.addProperty("portrait_second", ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        data.addProperty("landscape_primary", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        data.addProperty("landscape_second", ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        data.addProperty("portrait", ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        data.addProperty("landscape", ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        data.addProperty("resolutionHeight", mResolutionHeight);
        data.addProperty("resolutionWidth", mResolutionWidth);
        data.addProperty("scale", mScale);
        data.addProperty("dpiX", mDpiX);
        data.addProperty("dpiY", mDpiY);
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }
}
