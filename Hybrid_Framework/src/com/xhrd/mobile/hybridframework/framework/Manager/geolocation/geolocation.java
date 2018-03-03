package com.xhrd.mobile.hybridframework.framework.Manager.geolocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.engine.RDCloudView;
import com.xhrd.mobile.hybridframework.engine.RDResourceManager;
import com.xhrd.mobile.hybridframework.framework.HybridEnv;
import com.xhrd.mobile.hybridframework.framework.PluginData;
import com.xhrd.mobile.hybridframework.framework.PluginBase;
import com.xhrd.mobile.hybridframework.framework.Manager.I18n;

public class geolocation extends PluginBase implements LocationListener{
	private LocationManager mLocationManager;
	private AtomicInteger mAtomicInteger = new AtomicInteger();
	private Handler mHandler;
	private SharedPreferences mSp;
	private String mCoordsType = "gps";
	private String mProvider;
	private Context mContext;
	private String sucFunc;
	private boolean mIsTimeout = true;
	private String windowName;
	private List<GeolacationThread> list = new ArrayList<GeolacationThread>();

	public geolocation() {
		mContext = HybridEnv.getApplicationContext();
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mHandler = new Handler(mContext.getMainLooper());
		mSp = mContext.getSharedPreferences("sp_geolocation", Context.MODE_PRIVATE);
	}

	@JavascriptFunction
	public void getCurrentPosition(RDCloudView rdCloudView,String[] params) {
		String sucFunc = params[0];
		String errFunc = params[1];
		String option = params[2];
		try {
			JSONObject optObject = new JSONObject(option);
			long maximumAge = optObject.optLong("maximumAge");
			mCoordsType = optObject.optString("coordsType", "gps");
			boolean isEnableHighAccuracy = optObject.optBoolean("enableHighAccuracy", false);
			Location location;
			if (isEnableHighAccuracy){
				location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}else {
				location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if (location != null) {
				long currentTime = System.currentTimeMillis();
				long lastTime = mSp.getLong("timestamp", currentTime);
				long intervalTime = currentTime - lastTime;
				if (intervalTime > maximumAge) {
					watchPosition(rdCloudView,params);
				} else {
					String accuracy = location.getAccuracy() + "";
					String altitude = location.getAltitude() + "";
					String latitude = location.getLatitude() + "";
					String longitude = location.getLongitude() + "";
					String speed = location.getSpeed() + "";
					String altitudeAccuracy = "";
					String heading = "";

//					String coordsObj = "coords:{accuracy:'" + accuracy + "',altitude:'" + altitude + "',latitude:'" + latitude + "',longitude:'" + longitude
//							+ "',speed:'" + speed + "'}";
					String coordsObj = "coords:{accuracy:'" + accuracy + "',altitude:'" + altitude + "',altitudeAccuracy:'" + altitudeAccuracy + "',heading:'" + heading+ "',latitude:'" + latitude + "',longitude:'" + longitude
							+ "',speed:'" + speed + "'}";
					String jsObj = "{" + coordsObj + ",coordsType:'" + mCoordsType + "',timestamp:'" + currentTime + "'}";
					jsonCallBack(rdCloudView, false, sucFunc, jsObj);
				}
			} else {
				watchPosition(rdCloudView,params);
			}
		} catch (JSONException e) {
			jsCallback(errFunc, e.getMessage());
		}

	}

	@JavascriptFunction
	public void watchPosition(RDCloudView rdCloudView,String[] params) {
		for (GeolacationThread thread : list){
			if (thread.getRDCloudView() == rdCloudView){
				return;
			}
		}
		sucFunc = params[0];
		final String errFunc = params[1];
		this.windowName = windowName;
		String option = params[2];
		try {
			JSONObject optObject = new JSONObject(option);
			long timeout = optObject.optLong("timeout");
			String provider = optObject.optString("provider");
			long maximumAge = optObject.optLong("maximumAge",500);
			//TODO
			if (!"system".equals(provider)) {
				jsCallback(errFunc, I18n.getInstance().getString("demo_geolocation_type_invalid")+ provider );
			}
			//mCoordsType = optObject.optString("coordsType");
			boolean enableHighAccuracy = optObject.optBoolean("enableHighAccuracy");
//			boolean enableHighAccuracy = false;
			Criteria criteria = new Criteria();
			if (enableHighAccuracy) {
//				criteria.setAccuracy(Criteria.ACCURACY_HIGH);
				criteria.setAltitudeRequired(true);//要求海拔信息
				criteria.setBearingRequired(true);//要求方位信息
				criteria.setPowerRequirement(Criteria.POWER_HIGH);//对电量要求
			} else {
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				criteria.setAltitudeRequired(false);//要求海拔信息
				criteria.setBearingRequired(false);//要求方位信息
				criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量要求
			}
			criteria.setCostAllowed(true);//是否允许付费
			criteria.setSpeedRequired(false);
			String bestProvider = mLocationManager.getBestProvider(criteria, true);
			Log.i("Test", "bestProvider: " + bestProvider);
			if (TextUtils.isEmpty(bestProvider) || "passive".equals(bestProvider)) {
				jsCallback(errFunc, I18n.getInstance().getString("demo_geolocation_service_unusable"));
			} else {
				mProvider = bestProvider;
			}

			mLocationManager.requestLocationUpdates(mProvider, 50, 5, this);

			GeolacationThread thread = new GeolacationThread(this,rdCloudView,sucFunc,maximumAge);
			thread.start();
			list.add(thread);

			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mIsTimeout) {
						mLocationManager.removeUpdates(geolocation.this);
						jsCallback(errFunc, I18n.getInstance().getString("demo_geolocation_timeout") );
					}
				}
			}, timeout);

		} catch (JSONException e) {
			jsCallback(errFunc, e.getMessage());
		}
	}

	@JavascriptFunction
	public void clearWatch(RDCloudView view,String[] params) {
		mLocationManager.removeUpdates(this);
		for (GeolacationThread thread : list){
			if (thread.getRDCloudView() == view){
				thread.close();
				list.remove(thread);
			}
		}
	}

	private  String accuracy = "59.338259";
	private  String altitude = "0.0";
	private  String latitude = "47.078898";
	private  String longitude = "116.24419";
	private  String speed = "0.0";
	private  String altitudeAccuracy = "";
	private  String heading = "";

	public String getAltitudeAccuracy() {
		return altitudeAccuracy;
	}

	public String getHeading() {
		return heading;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public String getAltitude() {
		return altitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getSpeed() {
		return speed;
	}

	public String getmCoordsType() {
		return mCoordsType;
	}

	@Override
	public void onLocationChanged(Location location) {
		synchronized (geolocation.this) {
			Log.i("Test", location.toString());
			mIsTimeout = false;
			accuracy = location.getAccuracy() + "";
			altitude = location.getAltitude() + "";
			latitude = location.getLatitude() + "";
			longitude = location.getLongitude() + "";
			speed = location.getSpeed() + "";
//			String timestamp = System.currentTimeMillis() + "";
//			String coordsObj = "coords:{accuracy:'" + accuracy + "',altitude:'" + altitude + "',latitude:'" + latitude + "',longitude:'" + longitude
//					+ "',speed:'" + speed + "'}";
////			String jsObj = "{" + coordsObj + ",timestamp:'" + timestamp + "'}";
//			String jsObj = "{" + coordsObj + ",coordsType:'" + mCoordsType + "',timestamp:'" + timestamp + "'}";
//			jsonCallBack(getTargetView(), false, sucFunc, jsObj);
//			mSp.edit().putString("provider", mProvider).putLong("timestamp", Long.parseLong(timestamp)).commit();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	public void clearAll() {
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void addMethodProp(PluginData data) {
        data.addMethod("getCurrentPosition",new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, new String[]{RDResourceManager.getInstance().getString("request_geolocation_permission_msg")});
        data.addMethod("watchPosition");
        data.addMethod("clearWatch");
	}
	
	public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }

	@Override
	public void onDeregistered(RDCloudView view) {
		super.onDeregistered(view);
		for (GeolacationThread thread : list){
			if (thread.getRDCloudView() == view){
				thread.close();
				list.remove(thread);
			}
		}
	}
}
