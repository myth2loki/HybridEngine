package com.xhrd.mobile.hybrid.framework.Manager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.RDCloudView;
import com.xhrd.mobile.hybrid.engine.RDResourceManager;
import com.xhrd.mobile.hybrid.framework.PluginData;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.RDCloudApplication;
import com.xhrd.mobile.hybrid.util.XmlUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 国际化设置管理
 *
 * @author liutengteng
 */
// @TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class I18n extends PluginBase {

	private final static String F_ASSET_PATH = "i18n";
	private final static String F_RES_NAME = "strings.xml";
	private final static String F_SEPARATOR = File.separator;
	private final static String F_LANGUAGE_SEPARATOR = "_";
	private final static String F_LANGUAGE_DEFAULT = "default";

	private HashMap<String, String> mResMap;
	private HashMap<String, String> mLanguageMap;
	private HashMap<String, String> mLikeLanguageMap;

	private Context mContext;
	private Locale mLocale;
	private InputStream mInputStream;

	private String mRealPath = Environment.getExternalStorageDirectory()
			+ F_SEPARATOR + F_ASSET_PATH;
	private final static I18n mI18nManager = new I18n();
	public I18n() {
		this.mContext = RDCloudApplication.getApp();
		this.init();
	}

	@Override
	public String getDefaultDomain() {
//		String low = this.getClass().getSimpleName().toLowerCase();
//		Log.e("i18n",low);
		return this.getClass().getSimpleName().toLowerCase();
	}

	public static I18n getInstance()
	{
		return mI18nManager;
	}

	/**
	 * 初始化相关配置
	 */
	private void init() {
		this.mLocale = Locale.getDefault();
		readLanguageFile();
		readResFile();
	}

	/**
	 * 设置语言
	 *
	 * @param pLocales
	 *            国家代码如：zh_CN(中文)、en_US(英文)
	 */
	@JavascriptFunction
	public void setLanguage(RDCloudView view, String[] pLocales) {
		Locale locale = Locale.SIMPLIFIED_CHINESE;
		if (pLocales[0].startsWith("en_")) {
			locale = Locale.US;
		}
		mResMap = null;
		setLanguage(locale);
	}

	/**
	 * 设置语言
	 *
	 * @param pLocale
	 */
	private void setLanguage(Locale pLocale) {
		Resources resources = mContext.getResources();
		// 获得设置对象
		Configuration config = resources.getConfiguration();
		// 获得屏幕参数：主要是分辨率，像素等。
		DisplayMetrics dm = resources.getDisplayMetrics();

		// 只要是汉语都设为简体中文
		// if (pLocale.getLanguage().equals("zh"))//
		// ==Locale.TRADITIONAL_CHINESE||pLocale==Locale.TAIWAN)
		// config.locale = Locale.SIMPLIFIED_CHINESE;
		// else
		config.locale = pLocale;
		mLocale = pLocale;
		resources.updateConfiguration(config, dm);
		// 重读资源文件
		readResFile();
	}

	@JavascriptFunction
	public String getLanguage(RDCloudView view, String[] pParams) {
		if(mLocale == null) {
			this.mLocale = Locale.getDefault();
		}

		return this.mLocale.getLanguage()+this.F_LANGUAGE_SEPARATOR+this.mLocale.getCountry();
	}

	/**
	 * 进入系统语言设置界面
	 *
	 * @param pParams
	 */
	@JavascriptFunction
	public void gotoSystemLanguageSettings(RDCloudView view, String[] pParams) {
		Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
		this.mContext.startActivity(intent);
	}

	/**
	 * 解析语言配置目录
	 */
	private void readLanguageFile() {
		if (mLanguageMap == null && mLikeLanguageMap == null) {
			mLanguageMap = new HashMap<String, String>();
			mLikeLanguageMap = new HashMap<String, String>();
			String[] rootFile = null;
			try {

//				String test = ResManager.getInstance().getPath(ResManager.I18N_URI);//"cpt://android/a.b/i.png"
//				Log.e("test", test+"...");
				mRealPath = ResManager.getInstance()
						.getPath("res://i18n/string");
				File file = new File(mRealPath);

				int len = 0;
				if (file != null && file.isDirectory()) {
					rootFile = file.list();
					if (rootFile != null) {
						len = rootFile.length;
					}
				}
				if (len < 1) {
					rootFile = mContext.getAssets().list(
							F_ASSET_PATH );//+ "/string"
					if (rootFile != null)
						mRealPath = F_ASSET_PATH + "/string";
				}
				if (rootFile != null)
					addLanguage(rootFile);
			} catch (Exception e) {
				Log.e("解析目录", e.getMessage());
			}
		}
	}

	private void addLanguage(String[] pParams) {
		for (String derectory : pParams) {
			System.out.println(derectory);

			if (derectory.indexOf(".") < 0 || derectory.equals(F_LANGUAGE_DEFAULT)) {

				int index = derectory.indexOf(F_LANGUAGE_SEPARATOR);
				if (index > -1) {
					String path = // F_ASSET_PATH
					mRealPath + F_SEPARATOR + derectory + F_SEPARATOR
							+ F_RES_NAME;
					mLanguageMap.put(derectory, path);
					mLikeLanguageMap.put(derectory.substring(0, index), path);
				}
			}
		}
	}

	/**
	 * 读取资源文件
	 */
	private void readResFile() {
		if (mResMap == null) {
			String language = mLocale.getLanguage();
			String country = mLocale.getCountry();
			String path = mLanguageMap.get(language + F_LANGUAGE_SEPARATOR
					+ country);
			if (path == null) {
				path = mLikeLanguageMap.get(language);
				if (path == null) {
//					path = mRealPath + F_SEPARATOR + "zh"
//							+ F_LANGUAGE_SEPARATOR + "CN" + F_SEPARATOR
//							+ F_RES_NAME;
					path = mRealPath + F_SEPARATOR + F_LANGUAGE_DEFAULT + F_SEPARATOR
							+ F_RES_NAME;
				}
			}

			// Log.e("path", "path:" +
			// mContext.getFilesDir().getAbsolutePath());
			try {
				// 获得XmlPullParser解析器
				XmlPullParser xmlParser = XmlUtil.getXmlPullParser(path);// this.getXmlPullParser(path);// Xml.newPullParser();
				if (xmlParser != null) {

					mResMap = new HashMap<String, String>();
					int evtType = xmlParser.getEventType();
					while (evtType != XmlPullParser.END_DOCUMENT) {
						switch (evtType) {
						case XmlPullParser.START_TAG:
							String tag = xmlParser.getName();
							if (tag != null && tag.equals("string")) {
								String name = xmlParser.getAttributeValue(0);
								String value = xmlParser.nextText();
								// int id =
								// mContext.getResources().getIdentifier(name,
								// "string", mContext.getPackageName());
								mResMap.put(name, value);
								if (xmlParser.getEventType() != XmlPullParser.END_TAG) {
									xmlParser.nextTag();
								}
							}
						}
						evtType = xmlParser.next();
						// System.out.println(evtType+"...");
					}
//					mInputStream.close();
//					mInputStream = null;
				}

			} catch (Exception e) {
				Log.e("解析" + path + "失败", e.getMessage());
			}
		}
	}

	private XmlPullParser getXmlPullParser(String pPath) {

		XmlPullParser xmlParser = null;
		try {
			if (pPath.startsWith("/"))
				mInputStream = new FileInputStream(new File(pPath));// mContext.getAssets().open(pPath);//
																	// "www/strings.xml"
			else {
				mInputStream = mContext.getAssets().open(pPath);// "www/strings.xml"
			}
			// 获得XmlPullParser解析器
			xmlParser = Xml.newPullParser();
			if (mInputStream != null) {
				xmlParser.setInput(mInputStream, "utf-8");
				// mInputStream.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("解析失败"+this.getClass().getPackage().getName(), e.getMessage());
		}
		return xmlParser;
	}

	/**
	 * 替换html关键词
	 *
	 * @param pContents
	 * @return
	 */
	@JavascriptFunction
	public String replaceString(RDCloudView view, String[] pContents) {
		return replaceAll(view, new String[] { pContents[0] });
	}

	/**
	 * 替换html关键词
	 *
	 * @param pParams
	 *            参数一替换内容,参数二替换正则（正则要和html内容的关键词对应上）
	 * @return
	 */
	@JavascriptFunction
	public String replaceAll(RDCloudView view, String[] pParams) {
		int len = pParams != null ? pParams.length : 0;
		if (len < 1)
			return "";

		String html = pParams[0].replace("&lt;", "<").replace("&gt;", ">");

		String regx = "<%xhrd(.*?)%>";
		if (len > 1)
			regx = pParams[1];
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(html);
		while (m.find()) {
			String key = m.group(1);
			html = html.replace("<%xhrd" + key + "%>",
					this.getString(key.replace("(", "").replace(")", "")));
		}
		return html;
	}

	/**
	 * 获取资源值,支持处理特殊符号，第一个参数必须是key，其他参数按顺序传递 示例：普通键值对处理：<string
	 * name="key">帐号</string>，js调用方式 window.xxx.getString(['key'])
	 * 带特殊符号调用<string name="key">帐号：%s
	 * 性别：%s</string>，注：（除了特殊符号外，其他value不能有单个%号，需要用两个%号转义后显示），js调用方式
	 * window.xxx.getString(['key','liutengteng','男'])
	 *
	 * @param pParams
	 *            必须以字符串数组方式传参
	 * @return
	 */
	@JavascriptFunction
	public String getString(String view, String[] pParams) {

		int len = pParams != null ? pParams.length : 0;
		if (len < 1) {
			return "invalid key!";
		}
		String key = pParams[0];
		String value = getString(key);

		if (len > 1 && value != null) {
			Object[] strs = new String[len - 1];
			for (int i = 0; i < len - 1; i++) {
				strs[i] = pParams[i + 1];
			}
			// 特殊符号处理
			try {
				value = String.format(value, strs);
			} catch (Exception e) {
				return e.getMessage();
			}
		}
		return value == null ? key : value;
	}

	/**
	 * 获取资源值
	 *
	 * @param pKey
	 *            接收对应的key参数
	 * @return
	 */
	public String getString(String pKey) {

		if (mResMap == null) {
			readResFile();
		}
		if (mResMap == null)
			return RDResourceManager.getInstance().getString(pKey);
		String value = mResMap.get(pKey);

		return value == null ? RDResourceManager.getInstance().getString(pKey) : value;
	}

	public boolean termination() {
		return false;
	}
	public boolean isGlobal() {
        return true;
    }

	@Override
	public void addMethodProp(PluginData data) {
        //data.addMethod("gotoSystemLanguageSettings");
        data.addMethodWithReturn("getString");
		data.addMethodWithReturn("getLanguage");
        data.addMethodWithReturn("replaceAll");
        data.addMethod("setLanguage");
        //data.addMethod("termination");
	}

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }
}
