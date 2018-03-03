package com.xhrd.mobile.hybridframework.framework.Manager.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xhrd.mobile.hybridframework.engine.RDResourceManager;
import com.xhrd.mobile.hybridframework.engine.HybridActivity;
import com.xhrd.mobile.hybridframework.framework.Manager.ResManager;
import com.xhrd.mobile.hybridframework.framework.RDCloudApplication;

/**
 * Created by Administrator on 2015/7/13.
 */
public class Player1 implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private String mPath;
    private audio mAM1;
    private AudioManager mAM;
    private MediaPlayer mPlayer;
    private SeekBar seekBar;
    private Handler handler=new Handler();
    private int duration;
    private TextView tv_pass_time1;
    private TextView tv_total_time1;
    private String sunc;
    private String err;

    public Player1(String path, audio am, View view) {
        ResManager rm = ResManager.getInstance();
        if (rm.isLegalSchema(path)) {
            this.mPath = rm.getPath(path);
        } else {
            this.mPath = path;
        }
        this.mAM1 = am;
        mAM = (AudioManager) RDCloudApplication.getApp().getSystemService(Context.AUDIO_SERVICE);
        mPlayer = MediaPlayer.create(HybridActivity.getInstance(), Uri.parse(mPath));
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        initView(view);
    }

    //播放
    public void play(final String[] params) {
        sunc = params[0];
        err = params[1];
        duration = mPlayer.getDuration();
        //音乐文件持续时间
        seekBar.setMax(duration);
        //设置SeekBar最大值为音乐文件持续时间
        tv_total_time1.setText(formatTime(duration));
        handler.post(start);
    }

    Runnable start=new Runnable(){

        @Override
        public void run() {
            mPlayer.start();
            handler.post(updatesb);
            //用一个handler更新SeekBar
        }
    };
    Runnable updatesb =new Runnable(){

        @Override
        public void run() {
            //Log.i("ljm---->", mPlayer.getCurrentPosition()+"");
            if(mPlayer != null && mPlayer.isPlaying()){
                tv_pass_time1.setText(formatTime(mPlayer.getCurrentPosition()));
                seekBar.setProgress(mPlayer.getCurrentPosition());
                handler.postDelayed(updatesb, 1000);
                //每秒钟更新一次
            }
        }

    };

    private void initView(View view) {
        seekBar = (SeekBar) view.findViewById(RDResourceManager.getInstance().getId("sb_timeline1"));
        seekBar.setOnSeekBarChangeListener(this);
        tv_pass_time1 = (TextView) view.findViewById(RDResourceManager.getInstance().getId("tv_pass_time1"));
        tv_total_time1 = (TextView) view.findViewById(RDResourceManager.getInstance().getId("tv_total_time1"));
    }

    /**
     * 格式化时间
     * @param ms
     * @return
     */
    private CharSequence formatTime(int ms) {
        if (ms >= 0) {
            final int totalSeconds = ms / 1000;
            final int hours = totalSeconds / 3600;
            final int minutes = (totalSeconds % 3600) / 60;
            final int second = ((totalSeconds % 3600) % 60);
            final StringBuffer sb = new StringBuffer();
            if (hours > 0) {
                if (hours <= 10) {
                    sb.append("0");
                }
                sb.append(hours).append(":");
            }
            if (minutes < 10) {
                sb.append("0");
            }
            sb.append(minutes).append(":");
            if (second < 10) {
                sb.append("0");
            }
            sb.append(second);
            return sb.toString();
        }
        return "";
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            mPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mPlayer.seekTo(seekBar.getProgress());
    }

    public void stop() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            updatesb = null;
            mPlayer = null;
            seekBar.setProgress(0);
            tv_pass_time1.setText("00:00");
            tv_total_time1.setText("00:00");
        }
    }

    public void setRoute(String[] params) {
        int mode = Integer.parseInt(params[0]);
        mAM.setMode(mode);
    }

    public int getAudioMode() {
        return mAM.getMode();
    }

    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }else {
            mPlayer.start();
        }
    }

    public void resume() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
            handler.post(updatesb);
        }
    }

    public int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration()/1000;
        }
        return -1;
    }

    public int getPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition()/1000;
        }
        return 0;
    }

    public void seekTo(String[] params) {
        String seek = params[0];
        int progress = Integer.parseInt(params[0]);
        if (mPlayer != null) {
            mPlayer.seekTo(progress * 1000);
        }
    }

    public int isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying() ? 1 : 0;
        }
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mAM1.jsCallback(sunc);
        mPlayer.reset();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        mAM1.jsCallback(err, "播放出错");
        return true;
    }
}
