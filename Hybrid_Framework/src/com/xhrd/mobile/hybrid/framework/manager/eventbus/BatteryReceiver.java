package com.xhrd.mobile.hybrid.framework.manager.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.xhrd.mobile.hybrid.engine.HybridView;

/**
 * Created by Administrator on 2015/12/17.
 */
public class BatteryReceiver extends BroadcastReceiver{

    private EventListener mListener;
    private HybridView mView;

    public BatteryReceiver(EventListener listener,HybridView view) {
//        FrameworkManager fm = RDCloudApplication.getApp().getPluginManager();
//        this.mListener = (EventListener) fm.getPlugin("eventListener");
        this.mListener = listener;
        this.mView = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
//                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_CHARGING)});
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
//                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_NOT_CHARGING)});
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    //mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_OKAY)});
                    break;
            }
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            switch (chargePlug) {
                case BatteryManager.BATTERY_PLUGGED_AC:
//                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_PLUGGED_AC)});
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
//                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_CHARGING)});
//                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED,String.valueOf(EventListener.BATTERY_STATE_TYPE_PLUGGED_USB)});
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
//                    mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED,String.valueOf(EventListener.BATTERY_STATE_TYPE_PLUGGED_WIRELESS)});
                    break;
            }
        } else if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_LOW)});
        } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_OKAY)});
        } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_CHARGING)});
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            mListener.sendEvent(mView,new String[]{EventListener.BATTER_STATE_CHANGED, String.valueOf(EventListener.BATTERY_STATE_TYPE_NOT_CHARGING)});
        }
    }
}
