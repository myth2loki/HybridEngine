package com.xhrd.mobile.hybridframework.framework.Manager.audio;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xhrd.mobile.hybridframework.engine.RDCloudView;
import com.xhrd.mobile.hybridframework.framework.HybridEnv;
import com.xhrd.mobile.hybridframework.framework.Manager.ResManager;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 录音功能类。
 * Created by wangqianyu on 15/4/21.
 */
public class Recorder {
    /**
     * AMR NB file format
     */
    public static final String AMR_NB = "amr_nb";

    /**
     * AMR WB file format
     */
    public static final String AMR_WB = "amr_wb";

    /**
     * @hide AAC file format
     */
    public static final String AAC = "aac";

    /**
     * 3GPP media file format
     */
    public static final String THREE_GPP = "3gpp";

    /**
     * AMR file format
     */
    public static final String AMR = "amr";

    public static final String WAV = "wav";

    private MediaRecorder mRecorder;
    private static MediaPlayer player;

    private static String mFilename;
    private String mFormat;
    private String mSucFunc;
    private String mErrFunc;

    private Handler mHandler;
    private RDCloudView view;
    private CountDownThread thread;

    private static audio mAM;
    private String averagePowercallback;
    private String countDownCallback;
    private String calculateTimeCallback;
    private CalculateTimeThread calculateTimeThread;
    private static int duration;
    private static boolean isEnd = true;
    /**
     * 录音开始的时间
     */
    private static long recordStartTime;

    public Recorder(audio am) {
        this.mAM = am;
    }

    /**
     * 开始录音。
     *
     * @param params 0. {filename:文件名, samplerate:256000, format:amr} 1. 成功函数 2. 失败函数
     */
    public void record(final RDCloudView rdCloudView, String[] params) {
        recordStartTime = System.currentTimeMillis();
        this.view = rdCloudView;
        if (params == null || params.length < 5) {
            return;
        }
        isEnd = false;
        if (mHandler == null) {
            mHandler = new Handler(HybridEnv.getApplicationContext().getMainLooper());
        }

        String jsonStr = params[2];
        mSucFunc = params[3];
        mErrFunc = params[4];

        try {
            JSONObject jo = new JSONObject(jsonStr);
            mFilename = jo.optString("filename", "");
            mFormat = jo.optString("format", "amr");
            if (!"aac".equals(mFormat) && !"amr".equals(mFormat) && !"wav".equals(mFormat)) {
                mFormat = "amr";
            }
            ResManager rm = ResManager.getInstance();
            if (TextUtils.isEmpty(mFilename)) {
                mFilename = rm.getPath(ResManager.CACHE_URI) + "/record/" + new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + "." + mFormat;
            } else {
                if (rm.isLegalSchema(mFilename)) {
                    mFilename = rm.getPath(rdCloudView, mFilename);
                    if (mFilename.endsWith("/")) {
                        mFilename = mFilename + new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + "." + mFormat;
                    }
                } else if (mFilename.endsWith("/")) {
                    mFilename = mFilename + new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + "." + mFormat;
                } else if (mFilename.startsWith("/")) {
                    mFilename = mFilename + "." + mFormat;
                } else {
                    mFilename = rm.getPath(ResManager.CACHE_URI) + "/record/" + mFilename + "." + mFormat;
                }
            }
//            mFilename = "/sdcard/ljm/ljm.aac";
            File parentFile = new File(mFilename).getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            int samplerate = jo.optInt("samplerate", 256000);
            String format = jo.optString("format", AMR);

            mRecorder = new MediaRecorder();
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    mAM.jsCallback(mErrFunc, what);
                }
            });
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置输出格式
            mRecorder.setOutputFormat(getOutputFormat(format));
//            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置audio频率
            mRecorder.setAudioSamplingRate(samplerate > 0 ? samplerate : 256000);

            duration = jo.optInt("duration", -1);
            //设置最长时间
            if (duration != -1) {
                mRecorder.setMaxDuration(duration * 1000);
                mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                    @Override
                    public void onInfo(MediaRecorder mr, int what, int extra) {
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            mAM.jsCallback(rdCloudView, false, mSucFunc, mFilename);
                            mRecorder.stop();
                            mRecorder.release();
                            mRecorder = null;
                            if (mUpdateMicStatusTimer != null) {
                                mHandler.removeCallbacks(mUpdateMicStatusTimer);
                                mUpdateMicStatusTimer = null;
                            }
                            if (thread != null) {
                                thread.close();
                                thread = null;
                            }
                            if (calculateTimeThread != null) {
                                calculateTimeThread.close();
                                calculateTimeThread = null;
                            }
                            isEnd = true;
                        }
                    }
                });
            }
            mRecorder.setAudioEncoder(getAudioEncoder(format));
            mRecorder.setOutputFile(mFilename);
            mRecorder.prepare();
            mRecorder.start();
            // 开始分贝的回调
            if (!TextUtils.isEmpty(averagePowercallback)) {
                updateMicStatus(rdCloudView);
            }
            // 开始倒计时的回调
            if (duration != -1 && !TextUtils.isEmpty(countDownCallback)) {
                thread = new CountDownThread(rdCloudView, mAM, countDownCallback, duration, 0, true);
                thread.start();
            }
            // 开始计时的回调
            if (!TextUtils.isEmpty(calculateTimeCallback)) {
                calculateTimeThread = new CalculateTimeThread(rdCloudView, mAM, calculateTimeCallback);
                calculateTimeThread.start();
            }
        } catch (Exception e) {
            mAM.jsCallback(mErrFunc, e.getMessage());
        }
    }

    /**
     * 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600：我是怎么制定基准值的呢？ 当20
     * * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值
     * 当我不对着麦克风说任何话的时候，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准值。
     * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话
     */
    private int BASE = 600;
    private int SPACE = 200;// 间隔取样时间

    /**
     * 更新分贝值
     */
    private void updateMicStatus(RDCloudView rdCloudView) {
        if (mRecorder != null) {
            int ratio = mRecorder.getMaxAmplitude() / BASE;
            int db = 0;// 分贝
            if (ratio > 1) {
                db = (int) (20 * Math.log10(ratio));
                Log.i("分贝值：", db + "");
                mAM.jsCallback(rdCloudView, false, averagePowercallback, db + "");
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus(view);
        }
    };

    public void stop(RDCloudView rdCloudView) {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                isEnd = true;
                if (mUpdateMicStatusTimer != null) {
                    mHandler.removeCallbacks(mUpdateMicStatusTimer);
                    mUpdateMicStatusTimer = null;
                }
                if (thread != null) {
                    thread.close();
                }
                if (calculateTimeThread != null) {
                    calculateTimeThread.close();
                }
                mAM.jsCallback(rdCloudView, false, mSucFunc, mFilename);
            } else {
                mAM.jsCallback(mErrFunc, "record " + mFilename + " failed.");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(rdCloudView.getContext(),"录音失败",Toast.LENGTH_SHORT).show();
        }

    }

    static String func = "function errcallback(err){alert(err)}";

    /**
     * 播放录制的音频
     */
    public static void play() {
        if (TextUtils.isEmpty(mFilename)) {
            mAM.jsCallback(func, "file is not exist");
            return;
        }
        if (player == null) {
            player = new MediaPlayer();
        }
        try {
            player.reset();
            player.setDataSource(mFilename);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前时长
     */
    public static String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - recordStartTime;
        String dutationTime = TimeUtils.formatTime((int) time) + "";
        if (isEnd) {
            return 0 + "";
        } else {
            return "'" + dutationTime + "'";
        }
    }

    /**
     * 获取总时长
     */
    public static String getDuration() {
        if (duration != -1) {
            return duration + "";
        } else {
            return "-1";
        }
    }

    /**
     * 音量回调
     */
    public void addAveragePowerCallback(RDCloudView rdCloudView, String[] params) {
        if (params.length > 1) {
            averagePowercallback = params[2];
            updateMicStatus(rdCloudView);
        }
    }

    /**
     * 倒计时回调
     */
    public void addCountDownCallback(RDCloudView rdCloudView, String[] params) {
        if (params.length > 1) {
            countDownCallback = params[2];
            if (mRecorder != null && duration != -1 && !TextUtils.isEmpty(countDownCallback)) {
                long currentTime = System.currentTimeMillis();
                long durationTime = currentTime - recordStartTime;
                thread = new CountDownThread(rdCloudView, mAM, countDownCallback, duration, durationTime, false);
                thread.start();
            }
        }
    }

    /**
     * 计时回调
     */
    public void addCalculateTimeCallback(RDCloudView rdCloudView, String[] params) {
        if (params.length > 1) {
            calculateTimeCallback = params[2];
            if (mRecorder != null && !TextUtils.isEmpty(calculateTimeCallback)) {
                calculateTimeThread = new CalculateTimeThread(rdCloudView, mAM, calculateTimeCallback);
                calculateTimeThread.start();
            }
        }
    }

    /**
     * 格式化输出格式
     */
    private int getOutputFormat(String format) {
        if (AMR_NB.equals(format)) {
            return MediaRecorder.OutputFormat.AMR_NB;
        } else if (AMR_WB.equals(format)) {
            return MediaRecorder.OutputFormat.AMR_WB;
        } else if (AAC.equals(format)) {
            return MediaRecorder.OutputFormat.AAC_ADTS;
        } else if (THREE_GPP.startsWith(format)) {
            return MediaRecorder.OutputFormat.THREE_GPP;
        } else if (WAV.equals(format)){
            return MediaRecorder.OutputFormat.THREE_GPP;
        }
        return MediaRecorder.OutputFormat.DEFAULT;
    }

    /**
     * 格式化错误码
     */
    private int getAudioEncoder(String format) {
        if (AMR_NB.equals(format)) {
            return MediaRecorder.AudioEncoder.AMR_NB;
        } else if (AMR_WB.equals(format)) {
            return MediaRecorder.AudioEncoder.AMR_WB;
        } else if (AAC.equals(format)) {
            return MediaRecorder.AudioEncoder.AAC;
        }
        return MediaRecorder.AudioEncoder.DEFAULT;
    }


}
