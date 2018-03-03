package com.xhrd.mobile.hybridframework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.xhrd.mobile.hybridframework.engine.HybridActivity;

/**
 * @author lilong
 * */
public class SharepreFerenceUtil{
    public static final String PULLLASTUPDATATIME="pulllastupdatatime";//最后更新时间
	public static final int DEFAULT_INT = -1;
	private static Map<String, Integer> intMap;
	private static Map<String, Boolean> booleanMap;
	private static Map<String, String> stringMap;
	private static Map<String, Long> longMap;
	static Map<String, Set<String>> setMap;
	private static SharedPreferences sp;
	static {
		sp = HybridActivity.getInstance().getSharedPreferences("pulltorefresh", Context.MODE_PRIVATE);
		intMap = new HashMap<String, Integer>();
		booleanMap = new HashMap<String, Boolean>();
		stringMap = new HashMap<String, String>();
		longMap = new HashMap<String, Long>();
		setMap=new HashMap<String, Set<String>>();
	}
	public static boolean getBoolean(String key) {
		Boolean bool = booleanMap.get(key);
		if (bool == null) {
			bool = sp.getBoolean(key, false);
			booleanMap.put(key, bool);
		}
		return bool;
	}

	public static void putBoolean(String key, boolean value) {
		booleanMap.put(key, value);
		sp.edit().putBoolean(key, value).commit();
	}

	public static int getInt(String key) {
		Integer i = intMap.get(key);
		if (i == null) {
			i = sp.getInt(key, DEFAULT_INT);
			intMap.put(key, i);
		}
		return i;
	}

	public static void putInt(String key, int value) {
		intMap.put(key, value);
		sp.edit().putInt(key, value).commit();
	}

	public static String getString(String key) {
		String str = stringMap.get(key);
		if (str == null) {
			str = sp.getString(key, "");
			stringMap.put(key, str);
		}
		return str;
	}

	public static String[] getStringArray(String key) {
		return getString(key).split(",");
	}

	public static void putString(String key, String value) {
		stringMap.put(key, value);
		sp.edit().putString(key, value).commit();
	}

	public static void putLong(String key, long value) {
		longMap.put(key, value);
		sp.edit().putLong(key, value).commit();
	}

	public static long getLong(String key) {
		Long l = longMap.get(key);
		if (l == null) {
			l = sp.getLong(key, 0l);
			longMap.put(key, l);
		}
		return l;
	}
}
