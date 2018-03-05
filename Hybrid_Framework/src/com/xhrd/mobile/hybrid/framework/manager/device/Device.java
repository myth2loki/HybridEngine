package com.xhrd.mobile.hybrid.framework.manager.device;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.xhrd.mobile.hybrid.annotation.JavascriptConfig;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.engine.HybridResourceManager;
import com.xhrd.mobile.hybrid.framework.HybridEnv;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.PluginBase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by wangqianyu on 15/4/24.
 */
@JavascriptConfig(domain = "device", scope = PluginData.Scope.App)
public class Device extends PluginBase {

	private Context mContext;
	private WakeLock mWakeLock = null;
	private boolean isWakeLock;
	private MediaPlayer mMediaPlayer;
	private int mLooping;
	private SharedPreferences msp;

	public Device() {
		mContext = HybridEnv.getApplicationContext();
	}


	@TargetApi(Build.VERSION_CODES.DONUT)
	@Override
	public void addMethodProp(PluginData data) {
		data.addMethod("dial",new String[]{Manifest.permission.CALL_PHONE}, new String[]{HybridResourceManager.getInstance().getString("request_device_dial_permission_msg")});
		data.addMethod("beep");
		data.addMethod("pauseBeep");
		data.addMethod("vibrate");
		data.addMethodWithReturn("setWakelock");
		data.addMethodWithReturn("isWakelock");
		data.addMethodWithReturn("setVolume");
		data.addMethodWithReturn("getVolume");
		data.addMethod("getOSInfo");
		if(Build.VERSION.SDK_INT < 23) {
			data.addProperty("imei", getImei());
			data.addObject("imsi", getImsi());
//        data.addProperty("imsi", imsi);
		}else {
			data.addProperty("imei", "");
			//不能填空字符，会导致js格式错误
			data.addObject("imsi", "null");
		}
		data.addMethodWithReturn("getIMEI", new String[]{Manifest.permission.READ_PHONE_STATE}, new String[]{HybridResourceManager.getInstance().getString("request_device_read_hpone_permission_msg")});
		data.addMethodWithReturn("getIMSI", new String[]{Manifest.permission.READ_PHONE_STATE}, new String[]{HybridResourceManager.getInstance().getString("request_device_read_hpone_permission_msg")});
		data.addProperty("model", Build.MODEL);
		data.addProperty("vendor", Build.MANUFACTURER);
		data.addProperty("uuid", UUID.randomUUID().toString());
	}

	/**
	 * 打电话
	 * @param params 参数一：电话号码,参数二：是否直接拨号,true是 false否
	 */
	@JavascriptFunction
	public void dial(HybridView view, String[] params) {
		String callTypeString = Intent.ACTION_CALL;
		boolean isConfirm = false;
		try {
			isConfirm = (params!=null&&params.length>1)?Boolean.parseBoolean(params[1]):false;
		}catch (Exception e) {
			isConfirm = false;
		}
		if (isConfirm) {
			callTypeString = Intent.ACTION_DIAL;
		}
		Intent intent = new Intent(callTypeString, Uri.parse("tel:" + params[0]));
		view.getContext().startActivity(intent);
	}
	/**
	 * 发出蜂鸣声
	 * @param params 参数一循环播放次数
	 */
	@JavascriptFunction
	public void beep(HybridView view, String[] params) {
		if(mMediaPlayer!=null)
		{
			mMediaPlayer.start();
		}else {
//			mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC);//设置音量使用
			boolean shouldPlayBeep = true;
			AudioManager audioService = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				shouldPlayBeep = false;
			}
			if (shouldPlayBeep) {
				try {
					mLooping = Integer.parseInt(params[0]);
				}catch (Exception e) {
					mLooping = 1;
				}
				mMediaPlayer = getMediaPlayer();
				if(mMediaPlayer != null)
					mMediaPlayer.start();
			}
		}
	}
	/**
	 * 暂停蜂鸣声
	 * @param params
	 */
	@JavascriptFunction
	public void pauseBeep(HybridView view, String[] params) {
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
		}
	}

	private MediaPlayer getMediaPlayer() {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer player) {
//						android.util.Log.e("播放完成",mLooping+"...");
						if(mLooping>1&&!player.isPlaying())
						{
							mLooping--;
							player.start();
						}
					}
				});
		try {
			mediaPlayer.setDataSource(mContext, getDefaultRingtoneUri(RingtoneManager.TYPE_RINGTONE));
			// mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			mediaPlayer.prepare();
		} catch (IOException ioe) {
			Log.w("Media", ioe);
			mediaPlayer = null;
		}
		return mediaPlayer;
	}
	private Uri getDefaultRingtoneUri(int type){

		return RingtoneManager.getActualDefaultRingtoneUri(mContext, type);

	}
	/**
	 * 发出震动
	 * @param params 参数一 震动时长，以毫秒为单位
	 */
	@JavascriptFunction
	public void vibrate(HybridView view, String[] params) {
		long milliseconds = 500;
		try {
			milliseconds = params != null ? Long.parseLong(params[0]) : 500;
		}catch(Exception e)
		{
			milliseconds = 500;
		}
		Vibrator vibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);
		// long [] pattern = {100,milliseconds,100,milliseconds}; // 停止 开启 停止 开启
		// vibrator.vibrate(pattern,-1);
		vibrator.vibrate(milliseconds);
	}
	/**
	 * 设置屏幕常亮
	 * @param params 参数一 是否常亮，true常亮，false不常亮。参数二回调提醒
	 */
	@JavascriptFunction
	public boolean setWakelock(HybridView view, String[] params) {

		try {
			isWakeLock = params != null ? Boolean.parseBoolean(params[0]) : false;
		}catch(Exception e) {
			isWakeLock = false;
		}
		PowerManager pm = (PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE);
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
		if (isWakeLock) {
			// 屏幕常亮
			mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "xhrd");
		} else { // 正常状态
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "xhrd");
		}
		mWakeLock.acquire();
//		Toast.makeText(mContext, "test", 1000).show();
//		jsCallback(params[1], I18n.getInstance().getString("demo_successfully"));
//    	jsCallback(params[1],mI18n.getString(new String[]{"设置成功"}));
		return true;
	}

	/**
	 * 获取屏幕是否常亮
	 * @param params 参数一回调提醒
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
	@JavascriptFunction
	public boolean isWakelock(HybridView view, String[] params) {
//    	jsCallback(params[0],isWakelock+"");
		return isWakeLock;
	}

	/**
	 * 设置系统音量
	 * @param params 参数一 音量大小(范围0-1)。参数二 回调提醒
	 */
	@JavascriptFunction
	public boolean setVolume(HybridView view, String[] params) {
		float current = 0;
		try{
			current = params != null ? Float.parseFloat(params[0]) : 0;
		}catch (Exception e){
			current = 0;
		}
		AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (current >= 1) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
		} else {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)Math.round(max * current), 0);
		}

//		jsCallback(params[1], I18n.getInstance().getString("demo_successfully"));
		return true;
	}

	/**
	 * 获取系统音量
	 * @param params 参数一回调提醒
	 * @return
	 */
	@JavascriptFunction
	public String getVolume(HybridView view, String[] params) {
		AudioManager mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		return getFloatRound((double)current / max,10)+"";//"最大音量："+max+"，当前音量："+current+"";
	}

	public float getFloatRound(double sourceData,int a)
	{
		double i = (double) Math.round(sourceData * a);     //小数点后 a 位前移，并四舍五入
		float f2 = (float) (i/(float)a);        //还原小数点后 a 位
		return f2;
	}

	/**
	 * 获取系统信息
	 * @param params 参数一回调提醒
	 */
	@JavascriptFunction
	public void getOSInfo(HybridView view, String[] params)
	{
		TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		String imei = mTelephonyMgr.getDeviceId();
		String json = "{os:{imei:'"+imei+"',imsi:'"+imsi+"',model:'"+Build.MODEL+"',vendor:'"+Build.MANUFACTURER+"',uuid:'"+UUID.randomUUID()+"'}}";
		Log.e("os info", json);
		jsonCallBack(params[0],json);
	}

	/**
	 * 此方法在onDestroy里调用回收资源
	 */
	public void clearAll()
	{
		if(mWakeLock!=null)
		{
			mWakeLock.release();
			mWakeLock = null;
		}
		if(mMediaPlayer!=null)
		{
			if (!mMediaPlayer.isPlaying())
				mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@JavascriptFunction
	public String getIMEI(HybridView view, String[] params) {
		return getImei();
	}

	@JavascriptFunction
	public String getIMSI(HybridView view, String[] params) {
		return getImsi();
	}


	private TelephonyManager getTelephonyManager() {
		return (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	}

	private String getImei() {
		TelephonyManager tm = getTelephonyManager();
		if(tm != null) {
			return tm.getDeviceId();
		}
		return "";
	}

	public String getImsi() {
		String imsi = null;
		try {   //普通方法获取imsi
			TelephonyManager tm = getTelephonyManager();
			imsi = tm.getSubscriberId();
			if (imsi==null || "".equals(imsi)) imsi = tm.getSimOperator();
			Class<?>[] resources = new Class<?>[] {int.class};
			Integer resourcesId = new Integer(1);
			boolean isExistImsi = false;
//			if (imsi==null || "".equals(imsi)) {
			try {   //利用反射获取    MTK手机
				Method addMethod = tm.getClass().getDeclaredMethod("getSubscriberIdGemini", resources);
				addMethod.setAccessible(true);
				if(!TextUtils.isEmpty(imsi)) {
					Object obj = addMethod.invoke(tm, resourcesId);
					if(obj != null ) {
						imsi += "," + (String) obj;
						isExistImsi = true;
					}
				}
			} catch (Exception e) {
//					imsi = null;
			}
//			}
			if (!isExistImsi) {//imsi==null || "".equals(imsi)
				try {   //利用反射获取    展讯手机
					Class<?> c = Class
							.forName("com.android.internal.telephony.PhoneFactory");
					Method m = c.getMethod("getServiceName", String.class, int.class);
					String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
					TelephonyManager tm1 = (TelephonyManager) mContext.getSystemService(spreadTmService);
					if(!TextUtils.isEmpty(imsi)) {
						String id = tm1.getSubscriberId();
						if(id != null) {
							imsi += "," + id;
							isExistImsi = true;
						}
					}
				} catch (Exception e) {
				}
			}
			if (!isExistImsi) {//imsi==null || "".equals(imsi)
				try {   //利用反射获取    高通手机
					Method addMethod2 = tm.getClass().getDeclaredMethod("getSimSerialNumber", resources);
					addMethod2.setAccessible(true);
					if(!TextUtils.isEmpty(imsi)) {
						Object obj = addMethod2.invoke(tm, resourcesId);
						if(obj != null) {
							imsi += "," + (String) obj;
						}
					}
				} catch (Exception e) {
				}
			}
			if (imsi==null || "".equals(imsi)) {
				imsi = "[]";
			}else {
				imsi = "["+imsi+"]";
			}
		} catch (Exception e) {
		}
		return imsi;
	}

	@Override
	public PluginData.Scope getScope() {
		return PluginData.Scope.App;
	}
}
