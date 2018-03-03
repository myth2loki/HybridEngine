package com.xhrd.mobile.hybrid.framework.Manager.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.xhrd.mobile.hybrid.engine.RDCloudView;
import com.xhrd.mobile.hybrid.engine.RDResourceManager;
import com.xhrd.mobile.hybrid.framework.Manager.ResManager;
import com.xhrd.mobile.hybrid.framework.RDCloudApplication;

import java.io.File;
import java.io.IOException;

/**
 * 音频播放器
 * Created by wangqianyu on 15/4/21.
 */
public class Player implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    public static final int ROUTE_SPEAKER = AudioManager.MODE_NORMAL;
    public static final int ROUTE_EARPIECE = AudioManager.MODE_IN_CALL;

    private String mPath;
    private MediaPlayer mPlayer;
    private AudioManager mAM;
    private audio mAM1;

    public Player(RDCloudView rdCloudView,String path, audio am) {
        ResManager rm = ResManager.getInstance();
        if (rm.isLegalSchema(path)) {
            this.mPath = rm.getPath(rdCloudView,path);
        } else {
            this.mPath = path;
        }
        this.mAM1 = am;
        mAM = (AudioManager) RDCloudApplication.getApp().getSystemService(Context.AUDIO_SERVICE);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    //播放
    public void play(final String[] params) {
        File file = new File(mPath);
        if (!file.exists()){
            mAM1.jsCallback(params[1], RDResourceManager.getInstance().getString("audio_not_file"));
            return;
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mAM1.jsCallback(params[1], RDResourceManager.getInstance().getString("audio_error"));
                return true;
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mAM1.jsCallback(params[0]);
            }
        });
        mAM.setMode(ROUTE_SPEAKER);
        try {
            mPlayer.setDataSource(mPath);
            mPlayer.prepare();
            mPlayer.start();
            mAM1.jsCallback(params[0]);
        } catch (IOException e) {
            mAM1.jsCallback(params[1], e.getMessage());
        }
    }

    //暂停
    public void pause(){
    	if (mPlayer != null && mPlayer.isPlaying()) {
    		mPlayer.pause();
    	}else {
			mPlayer.start();
		}
    }
    
    //继续播放
    public void resume(){
    	if (mPlayer !=null && !mPlayer.isPlaying()) {
    		mPlayer.start();
		}
    }
    
    //停止
    public void stop() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
    
    //获取音频流的总长度
    public String getDuration(){
    	if (mPlayer != null) {
            String duration = mPlayer.getDuration()/1000 + "";
            File file = new File(mPath);
            if (!file.exists()){
                return "NaN";
            } else {
                return duration;
            }
    	}
        return "-1";
    }

    public String getPosition(){
    	if (mPlayer != null) {
            String duration = mPlayer.getCurrentPosition()/1000 + "";
            File file = new File(mPath);
            if (!file.exists()){
                return "0";
            } else {
                return duration;
            }
//			return mPlayer.getCurrentPosition()/1000;
		}
    	return "0";
    }

    public int isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying() ? 1 : 0;
        }
        return 0;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayer.reset();
    }

    public void setRoute(String[] params) {
        int mode = Integer.parseInt(params[0]);
        if (mode == 1){
            mode = 2;
            mAM.setSpeakerphoneOn(false);
        }
        mAM.setMode(mode);
    }

    public int getAudioMode() {
        return mAM.getMode();
    }

	public void seekTo(String[] params) {
        String seek = params[0];
		int progress = Integer.parseInt(params[0]);
		if (mPlayer != null) {
			mPlayer.seekTo(progress * 1000);
		}
	}
}
