package com.xhrd.mobile.hybridframework.framework.Manager.storage;


import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.framework.PluginData;
import com.xhrd.mobile.hybridframework.framework.PluginBase;
import com.xhrd.mobile.hybridframework.framework.RDCloudApplication;

public class storage extends PluginBase {
	
	private Context mContext;
	private SharedPreferences msp;
	public storage(){
		mContext = RDCloudApplication.getApp();

		msp = mContext.getSharedPreferences("storage", Context.MODE_PRIVATE);
	}
	
	/**
	 * 获取storage中保存的键值对的数量
	 */
	@JavascriptFunction
	public int getLength(String windowName,String[] param){
		Map<String, ?> all = msp.getAll();
		int size = all.size();
		return size;
	}
	
	/**
	 * 通过key值检索键值
	 * @param param
	 * @return
	 */
	@JavascriptFunction
	public String getItem(String windowName,String[] param){
//		String key = param[0];
//		String value = msp.getString(key, "");
//		if (value.length() > 0) {
//			return value;
//		}
//		return null;
		return msp.getString(param[0], null);

	}
	
	/**
	 * 存储key-value
	 * @param param
	 */
	@JavascriptFunction
	public void setItem(String windowName,String[] param){
		String key = param[0];
		String value = param[1];
		Editor editor = msp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * 通过key值删除键值对
	 * @param param
	 */
	@JavascriptFunction
	public void removeItem(String windowName,String[] param){
		String key = param[0];
		Editor editor = msp.edit();
		editor.remove(key);
		editor.commit();
	}
	
	/**
	 * 清除应用所有的键值对
	 */
	@JavascriptFunction
	public void clear(String windowName,String[] param){
		Editor editor = msp.edit();
		editor.clear();
		editor.commit();
	}
	
	/**
	 * 获取键值对中指定索引值的key值
	 * @param param
	 * @return
	 */
	@JavascriptFunction
	public String key(String windowName,String[] param){
		String key = param[0];
		int index = Integer.parseInt(key);
		Map<String, ?> all = msp.getAll();
		// TODO
		return (String) new ArrayList<Object>(all.values()).get(index);
//		int size = all.size()-1;
//		Map<Integer, String> mapKey = new HashMap<Integer, String>();
//		for (Map.Entry<String, ?> entry : all.entrySet()) {
//			mapKey.put(size, entry.getKey());
//			size--;
//		}
//		String key_index = mapKey.get(index);
//		return key_index;
		
	}

	@Override
	public void addMethodProp(PluginData data) {
        data.addMethod("getLength", null, true , false);
        data.addMethod("getItem", null, true , false);
        data.addMethod("setItem", null, false , false);
        data.addMethod("removeItem", null, false , false);
        data.addMethod("clear", null, false , false);
        data.addMethod("key", null, true , false);
    }
	
	public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }

}
