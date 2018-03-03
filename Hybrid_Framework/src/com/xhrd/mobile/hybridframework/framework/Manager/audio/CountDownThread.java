package com.xhrd.mobile.hybridframework.framework.Manager.audio;

import android.util.Log;

import com.xhrd.mobile.hybridframework.engine.RDCloudView;


/**
 * Created by ljm on 2015/12/1.
 * 倒计时线程
 */
public class CountDownThread extends Thread{

    private RDCloudView mView;
    private audio mAudio;
    private String mCountDownCallback;
    private boolean isRunning;
    private int mDuration;
    private long mDurationTime;
    private boolean isStart;
    private long myTime;

    public CountDownThread(RDCloudView rdCloudView,audio audio,String countDownCallback,int duration,long currentTime,boolean isStart){
        this.mView = rdCloudView;
        this.mAudio = audio;
        this.mCountDownCallback = countDownCallback;
        this.mDuration = duration * 1000;
        this.mDurationTime = currentTime;
        this.isStart = isStart;
        isRunning = true;
        myTime = mDuration - mDurationTime;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning){
            try {
                sleep(1000);
                if (isStart){
                    mDuration = mDuration - 1000;
//                    String time = formatTime(mDuration) + "";
                    String time = TimeUtils.formatTime(mDuration) + "";
                    Log.d("倒计时：",time);
                    mAudio.jsCallback(mView,false,mCountDownCallback,time);
                }else {
//                    long i = mDuration - mDurationTime;
                    myTime = myTime - 1000;
//                    int countDownTime = (int)(i - 1000);
//                    String time = formatTime((int)myTime) + "";
                    String time = TimeUtils.formatTime((int)myTime) + "";
                    Log.d("倒计时：",time);
                    mAudio.jsCallback(mView,false,mCountDownCallback,time);
                }
//                long i = mDurationTime - mDuration;
//                int countDownTime = (int)(i - 1000);
////                mDuration = mDuration - 1000;
//                String time = formatTime(countDownTime) + "";
//                Log.e("倒计时：",time);
//                mAudio.jsCallback(mView,false,mCountDownCallback,time);
            } catch (InterruptedException e) {
            }
        }
    }

    public void close(){
        isRunning = false;
    }
}
