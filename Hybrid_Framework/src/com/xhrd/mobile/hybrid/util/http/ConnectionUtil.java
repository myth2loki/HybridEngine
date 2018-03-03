package com.xhrd.mobile.hybrid.util.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.xhrd.mobile.hybrid.framework.HybridEnv;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLConnection;
import java.util.Enumeration;

public class ConnectionUtil {
	
	public static boolean isNetworkAvailable() {
		return isNetworkAvailable((ConnectivityManager) HybridEnv.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
	}

	public static boolean isNetworkAvailable(ConnectivityManager connManager) {
		NetworkInfo ni = connManager.getActiveNetworkInfo();
		return ni != null && ni.isConnected();
	}
	
	public static boolean is2GNetwork() {
		return is2GNetwork((ConnectivityManager) HybridEnv.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
	}
	
	public static boolean is2GNetwork(ConnectivityManager connManager) {
		NetworkInfo ni = connManager.getActiveNetworkInfo();
		if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			if (ni.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS 
					|| ni.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
				//is 2G
				return true;
			}
		}
		return false;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	public static String getHeaderField(URLConnection conn, String key) {
		String value = conn.getHeaderField(key);
		if (value == null) {
			value = conn.getHeaderField(key.toLowerCase());
		}
		return value;
	}
	
	public static String generateUrl(String ip, int port, String path) {
		String template = "http://%s:%d/%s";
		return String.format(template, ip, port, path);
	}
	
}
