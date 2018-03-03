package com.xhrd.mobile.hybridframework.framework;

import android.os.Environment;

import com.xhrd.mobile.hybridframework.BuildConfig;
import com.xhrd.mobile.hybridframework.Config;

import java.io.File;

/**
 * Created by lang on 15/4/14.
 */
public class Constants {

    public static final String DIR_HYBRID="hybrid";
    public static final String DIR_APP=DIR_HYBRID+File.separator+"app";
    public static final String DIR_COMPONENT=DIR_HYBRID+File.separator+"component";


    public static final String NAME_APP_XML = "application.xml";
    public static final String NAME_COMPONENT_XML= "component.xml";
    public static final String NAME_PLUGINS_XML= "plugins.xml";

    public static final String PATH_APP_XML = DIR_APP+File.separator+NAME_APP_XML;

    //public static final String BASE_URL = Config.DEBUG?"http://test-dev.369cloud.com":"http://dev.369cloud.com";//"http://192.168.1.102/";"http://192.168.1.109:8080";//
    public static final String SUCCESS_CODE = "00000000";
    public static final String SDPATH = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)?Environment.getExternalStorageDirectory().getPath():HybridEnv.getApplicationContext().getFilesDir().getParent();


    public static final int WINDOW_TYPE_COMPONENT=0;
    public static final int WINDOW_TYPE_PAGE=1;

}
