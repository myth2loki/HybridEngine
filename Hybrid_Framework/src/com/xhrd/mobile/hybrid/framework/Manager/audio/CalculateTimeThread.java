package com.xhrd.mobile.hybrid.framework.Manager.audio;

import android.util.Log;

import com.xhrd.mobile.hybrid.engine.RDCloudView;

/**
 * Created by ljm on 2015/12/1.
 *
 * 计时线程
 */
public class CalculateTimeThread extends Thread {

    private RDCloudView mView;
    private audio mAudio;
    private String mCalculateTimeCallback;
    private boolean isRunning;
    private int i = 0;

    public CalculateTimeThread(RDCloudView rdCloudView,audio audio,String calculateTimeCallback){
        this.mView = rdCloudView;
        this.mAudio = audio;
        this.mCalculateTimeCallback = calculateTimeCallback;
        isRunning = true;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning){
            try {
                sleep(1000);
                i += 1000;
                String time = TimeUtils.formatTime(i) + "";
                Log.e("计时：",time);
                mAudio.jsCallback(mView,false,mCalculateTimeCallback,time);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void close(){
        isRunning = false;
    }
}
