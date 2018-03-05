package com.xhrd.mobile.hybrid.framework.manager.eventbus;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lilong on 15/5/19.
 */
public class EventListener extends PluginBase {
    static final String NETWORK_STATE_CHANGED = "network_state_changed";
    static final String BATTER_STATE_CHANGED = "battery_state_changed";

    static final int NETWORK_STATE_TYPE_NONE = 0;
    static final int NETWORK_STATE_TYPE_WIFI = 1;
    static final int NETWORK_STATE_TYPE_ETHERNET = 2;
    static final int NETWORK_STATE_TYPE_MOBILE = 3;
    static final int NETWORK_STATE_TYPE_VPN = 4;
    static final int NETWORK_STATE_TYPE_WIMAX = 5;

    static final int BATTERY_STATE_TYPE_LOW = 6;
    static final int BATTERY_STATE_TYPE_OKAY = 7;
    static final int BATTERY_STATE_TYPE_CHARGING = 8;
    static final int BATTERY_STATE_TYPE_NOT_CHARGING = 9;
    static final int BATTERY_STATE_TYPE_PLUGGED_AC = 10;
    static final int BATTERY_STATE_TYPE_PLUGGED_USB = 11;
    static final int BATTERY_STATE_TYPE_PLUGGED_WIRELESS = 12;

    public Map<String, List<EventInfo>> mEventMap = new HashMap<String, List<EventInfo>>();

    private SysEventReceiver mReceiver;
    private BatteryReceiver batteryReceiver;

    @Override
    public void onCreate(HybridView view) {
        super.onCreate(view);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        filter.addAction(Intent.ACTION_BATTERY_LOW);
//        filter.addAction(Intent.ACTION_BATTERY_OKAY);
//        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        filter.addAction(Intent.ACTION_POWER_CONNECTED);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        mReceiver = new SysEventReceiver(this,view);
//        RDCloudApplication.getApp().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HybridEnv.getApplicationContext().unregisterReceiver(mReceiver);
    }

    @Override
    public String getDefaultDomain() {
        return "eventListener";
    }

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("addEventListener");
        data.addMethod("removeEventListener");
        data.addMethod("sendEvent");

        data.addProperty("NETWORK_STATE_CHANGED", NETWORK_STATE_CHANGED);
        data.addProperty("NETWORK_STATE_TYPE_NONE", NETWORK_STATE_TYPE_NONE);
        data.addProperty("NETWORK_STATE_TYPE_WIFI", NETWORK_STATE_TYPE_WIFI);
        data.addProperty("NETWORK_STATE_TYPE_ETHERNET", NETWORK_STATE_TYPE_ETHERNET);
        data.addProperty("NETWORK_STATE_TYPE_MOBILE", NETWORK_STATE_TYPE_MOBILE);
        data.addProperty("NETWORK_STATE_TYPE_VPN", NETWORK_STATE_TYPE_VPN);
        data.addProperty("NETWORK_STATE_TYPE_WIMAX", NETWORK_STATE_TYPE_WIMAX);

        data.addProperty("BATTERY_STATE_CHANGED", BATTER_STATE_CHANGED);
        data.addProperty("BATTERY_STATE_TYPE_LOW", BATTERY_STATE_TYPE_LOW);
        data.addProperty("BATTERY_STATE_TYPE_OKAY ", BATTERY_STATE_TYPE_OKAY);
        data.addProperty("BATTERY_STATE_TYPE_CHARGING", BATTERY_STATE_TYPE_CHARGING);
        data.addProperty("BATTERY_STATE_TYPE_NOT_CHARGING", BATTERY_STATE_TYPE_NOT_CHARGING);
        data.addProperty("BATTERY_STATE_TYPE_PLUGGED_AC", BATTERY_STATE_TYPE_PLUGGED_AC);
        data.addProperty("BATTERY_STATE_TYPE_PLUGGED_USB",BATTERY_STATE_TYPE_PLUGGED_USB);
        data.addProperty("BATTERY_STATE_TYPE_PLUGGED_WIRELESS", BATTERY_STATE_TYPE_PLUGGED_WIRELESS);
    }

    @JavascriptFunction
    public void addEventListener(HybridView rdView, String[] params) {
        if (params.length < 2) {
            return;
        }
        String action = params[0];
        if ("network_state_changed".equals(action)){
            if (mReceiver == null){
                IntentFilter filter = new IntentFilter();
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mReceiver = new SysEventReceiver(this,rdView);
                HybridEnv.getApplicationContext().registerReceiver(mReceiver, filter);
            }
        }

        if ("battery_state_changed".equals(action)){
            if (batteryReceiver == null){
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_BATTERY_LOW);
                filter.addAction(Intent.ACTION_BATTERY_OKAY);
                filter.addAction(Intent.ACTION_POWER_CONNECTED);
                filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
                filter.addAction(Intent.ACTION_BATTERY_CHANGED);
                batteryReceiver = new BatteryReceiver(this,rdView);
                HybridEnv.getApplicationContext().registerReceiver(batteryReceiver, filter);
            }
        }

        String func = params[1];

        EventInfo eventInfo = new EventInfo();
        eventInfo.rdCloudView = rdView;
        eventInfo.function = func;

        if(mEventMap.containsKey(action)){
            mEventMap.get(action).add(eventInfo);
        } else {
            List<EventInfo> eventInfos = new ArrayList<EventInfo>();
            eventInfos.add(eventInfo);
            mEventMap.put(action, eventInfos);
        }
    }

    @Override
    public void onDeregistered(HybridView view) {
        super.onDeregistered(view);
        for (Map.Entry<String, List<EventInfo>> entry : mEventMap.entrySet()) {
            List<EventInfo> willRemoveList = new ArrayList<EventInfo>();
            for (EventInfo info : entry.getValue()) {
                if (info.rdCloudView == view) {
                    willRemoveList.add(info);
                }
            }
            entry.getValue().removeAll(willRemoveList);
        }
    }

    @JavascriptFunction
    public void removeEventListener(HybridView rdView, String[] params) {
        String action = params[0];
        if (mEventMap.containsKey(action)) {
            mEventMap.get(action).clear();
            mEventMap.remove(action);
        }
    }

    String lastState = "11";
    @JavascriptFunction
    public void sendEvent(HybridView rdCloudViews, String[] params) {
        String action = params[0];
        String content = params[1];
        eventHandle(action, content);
    }

    private void eventHandle(String action, String content) {
        List<EventInfo> eventInfoList = mEventMap.get(action);
        if(eventInfoList == null){
            return;
        }
        if ("network_state_changed".equals(action) || "battery_state_changed".equals(action)){
            String sysJson = "{action : '%s',state : %s}";
            content = String.format(sysJson,action,content);
        }
        for (EventInfo info : eventInfoList) {
            jsonCallBack(info.rdCloudView, false, info.function, content);
        }
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.App;
    }
}
