package com.xhrd.mobile.hybrid.framework.Manager.device;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.RDCloudView;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.framework.PluginBase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

/**
 * Created by wangqianyu on 15/4/24.
 */
public class networkInfo extends PluginBase {

	private final int CONNECTION_UNKNOW = 0;// 网络连接状态未知
	private final int CONNECTION_NONE = 1;// 未连接网络
	private final int CONNECTION_ETHERNET = 2;// 有线网络
	private final int CONNECTION_WIFI = 3;// 无线WIFI网络
	private final int CONNECTION_CELL2G = 4;// 蜂窝移动2G网络
	private final int CONNECTION_CELL3G = 5;// 蜂窝移动3G网络
	private final int CONNECTION_CELL4G = 6;// 蜂窝移动4G网络

	@JavascriptFunction
	public int getCurrentType(RDCloudView view,String[] params) {
		int type = CONNECTION_UNKNOW;
		ConnectivityManager cm = (ConnectivityManager) HybridActivity.getInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			type = CONNECTION_NONE;
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			type = CONNECTION_WIFI;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA
					|| subType == TelephonyManager.NETWORK_TYPE_GPRS
					|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				type = CONNECTION_CELL2G;
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
					|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| subType == 15 //TelephonyManager.NETWORK_TYPE_HSPAP
					|| subType == TelephonyManager.NETWORK_TYPE_IDEN
					|| subType == TelephonyManager.NETWORK_TYPE_HSPA
					|| subType == TelephonyManager.NETWORK_TYPE_HSUPA
					|| subType == 11 //TelephonyManager.NETWORK_TYPE_EHRPD
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				type = CONNECTION_CELL3G;
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
				type = CONNECTION_CELL4G;
			} else if (subType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
				type = CONNECTION_UNKNOW;
			} else {
				type = CONNECTION_CELL3G;
			}
		}
		return type;
	}

	@Override
	public void addMethodProp(PluginData data) {
        data.addMethodWithReturn("getCurrentType");
//        data.addMethod("termination");

        data.addProperty("CONNECTION_UNKNOW", CONNECTION_UNKNOW);// 网络连接状态未知
        data.addProperty("CONNECTION_NONE", CONNECTION_NONE);// 未连接网络
        data.addProperty("CONNECTION_ETHERNET", CONNECTION_ETHERNET);// 有线网络
        data.addProperty("CONNECTION_WIFI", CONNECTION_WIFI);// 无线WIFI网络
        data.addProperty("CONNECTION_CELL2G", CONNECTION_CELL2G);// 蜂窝移动2G网络
        data.addProperty("CONNECTION_CELL3G", CONNECTION_CELL3G);// 蜂窝移动3G网络
        data.addProperty("CONNECTION_CELL4G", CONNECTION_CELL4G);// 蜂窝移动4G网络
	}

	@Override
	public String getDefaultDomain() {
		return "networkinfo";//this.getClass().getName().toLowerCase();
	}

	@Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }
}
