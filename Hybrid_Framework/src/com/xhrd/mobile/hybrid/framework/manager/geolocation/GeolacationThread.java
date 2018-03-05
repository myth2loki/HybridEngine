package com.xhrd.mobile.hybrid.framework.manager.geolocation;

import com.xhrd.mobile.hybrid.engine.HybridView;

/**
 * Created by ljm on 2015/11/18.
 */
public class GeolacationThread extends Thread{

    private geolocation mGeolocation;
    private long mMaximumAge;
    private HybridView mRDCloudView;
    private String mSunc;
    private boolean isRunning;

    public GeolacationThread(geolocation Geolacation, HybridView rdCloudView, String sunc, long maximumAge){
        this.mGeolocation = Geolacation;
        this.mRDCloudView = rdCloudView;
        this.mSunc = sunc;
        this.mMaximumAge = maximumAge;
        isRunning = true;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning){
            String timestamp = System.currentTimeMillis() + "";
            String coordsObj = "coords:{accuracy:'" + mGeolocation.getAccuracy() + "',altitude:'" + mGeolocation.getAltitude() + "',altitudeAccuracy:'" + mGeolocation.getAltitudeAccuracy() + "',heading:'" + mGeolocation.getHeading()+ "',latitude:'" + mGeolocation.getLatitude() + "',longitude:'" + mGeolocation.getLongitude()
                    + "',speed:'" + mGeolocation.getSpeed() + "'}";
            String jsObj = "{" + coordsObj + ",coordsType:'" + mGeolocation.getmCoordsType() + "',timestamp:'" + timestamp + "'}";
            mGeolocation.jsonCallBack(mRDCloudView, false, mSunc, jsObj);
            try {
                sleep(mMaximumAge);
            } catch (InterruptedException e) {
            }
        }
    }

    public void close(){
        isRunning = false;
    }

    public HybridView getRDCloudView(){
        return mRDCloudView;
    }
}
