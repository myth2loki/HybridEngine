package com.xhrd.mobile.hybrid.util.log;

import com.xhrd.mobile.hybrid.Config;

/**
 * Created by maxinliang on 15/10/23.
 */
public class LogUtils implements RDLog {
	
	private static final String TAG = "Hybrid_Framework";
	
	private final String tag;

	LogUtils(Class<?> clazz) {
		tag = String.format("%s: %s", "app", clazz.getSimpleName());
	}

	@Override
	public void i(String msg) {
		if (Config.DEBUG || Config.APP_LOADER) {
			android.util.Log.i(tag, msg);
		}
	}

	@Override
	public void d(String msg) {
		if (Config.DEBUG || Config.APP_LOADER) {
			android.util.Log.d(tag, msg);
		}
	}

	@Override
	public void w(String msg) {
		if (Config.DEBUG || Config.APP_LOADER) {
			android.util.Log.w(tag, msg);
		}
	}

	@Override
	public void e(String msg) {
		if (Config.DEBUG || Config.APP_LOADER) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void d4defualtTag(String msg) {
		if (Config.DEBUG || Config.APP_LOADER) {
			android.util.Log.d(TAG, msg);
		}
	}

	public static void w4defualtTag(String msg) {
		if (Config.DEBUG || Config.APP_LOADER) {
			android.util.Log.w(TAG, msg);
		}
	}

	public static void e4defualtTag(String msg) {
		if (Config.DEBUG || Config.APP_LOADER) {
			android.util.Log.e(TAG, msg);
		}
	}
}
