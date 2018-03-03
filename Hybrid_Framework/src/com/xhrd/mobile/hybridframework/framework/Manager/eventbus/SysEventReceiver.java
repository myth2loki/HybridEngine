package com.xhrd.mobile.hybridframework.framework.Manager.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;

import com.xhrd.mobile.hybridframework.engine.RDCloudView;
import com.xhrd.mobile.hybridframework.framework.FrameworkManager;
import com.xhrd.mobile.hybridframework.framework.RDCloudApplication;

/**
 * Created by wangqianyu on 15/11/23.
 */
public class SysEventReceiver extends BroadcastReceiver {
    private EventListener mListener;
    private RDCloudView mView;

    public SysEventReceiver(EventListener listener,RDCloudView view) {
//        FrameworkManager fm = RDCloudApplication.getApp().getFrameworkManager();
//        this.mListener = (EventListener) fm.getPlugin("eventListener");
        this.mListener = listener;
        this.mView = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_DATE_CHANGED.equals(action)) {

        } else if (Intent.ACTION_TIME_CHANGED.equals(action)) {

        } else if (Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {

        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {

        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {

        } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_CHARGING)});
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_NOT_CHARGING)});
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    //mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_OKAY)});
                    break;
            }
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            switch (chargePlug) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_PLUGGED_AC)});
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED,String.valueOf(EventListener.BATTERY_STATE_TYPE_PLUGGED_USB)});
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED,String.valueOf(EventListener.BATTERY_STATE_TYPE_PLUGGED_WIRELESS)});
                    break;
            }
        } else if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_LOW)});
        } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_OKAY)});
        } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
//            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_CHARGING)});
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_NOT_CHARGING)});
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            NetworkInfo netInfo;
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = connectivityManager.getActiveNetworkInfo();
            if(netInfo != null && netInfo.isAvailable()) {
                //网络连接
                //String name = netInfo.getTypeName();
                if(netInfo.getType()==ConnectivityManager.TYPE_WIFI) {
                    //WiFi网络
                    mListener.sendEvent(mView,new String[]{EventListener.NETWORK_STATE_CHANGED, String.valueOf(EventListener.NETWORK_STATE_TYPE_WIFI)});
                } else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET) {
                    //有线网络
                    mListener.sendEvent(mView,new String[]{EventListener.NETWORK_STATE_CHANGED, String.valueOf(EventListener.NETWORK_STATE_TYPE_ETHERNET)});
                } else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
                    //3g网络
                    mListener.sendEvent(mView,new String[]{EventListener.NETWORK_STATE_CHANGED, String.valueOf(EventListener.NETWORK_STATE_TYPE_MOBILE)});
                } else if(netInfo.getType()==ConnectivityManager.TYPE_VPN) {
                    //vpn网络
                    mListener.sendEvent(mView,new String[]{EventListener.NETWORK_STATE_CHANGED, String.valueOf(EventListener.NETWORK_STATE_TYPE_VPN)});
                } else if(netInfo.getType()==ConnectivityManager.TYPE_WIMAX) {
                    //WIMAX网络
                    mListener.sendEvent(mView,new String[]{EventListener.NETWORK_STATE_CHANGED, String.valueOf(EventListener.NETWORK_STATE_TYPE_WIMAX)});
                }
            } else {
                //网络断开
                mListener.sendEvent(mView,new String[]{EventListener.NETWORK_STATE_CHANGED, String.valueOf(EventListener.NETWORK_STATE_TYPE_NONE)});
            }
        }
    }
}
