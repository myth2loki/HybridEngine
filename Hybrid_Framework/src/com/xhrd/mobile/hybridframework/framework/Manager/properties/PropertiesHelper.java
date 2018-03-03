package com.xhrd.mobile.hybridframework.framework.Manager.properties;


import com.xhrd.mobile.hybridframework.framework.Manager.ResManager;
import com.xhrd.mobile.hybridframework.framework.Manager.ResManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

class PropertiesHelper {
   private Properties mProps;
   private String url;

   public PropertiesHelper(String parentName,String propertiesName){
       openProperties( parentName,propertiesName);
   }

    /**
      * 返回ture代表成功 返回flase代表添加失败
      * */
    public boolean putProperty(String param,Object value){
        mProps.put(param, value);
        return mProps.containsKey(param);
     }

    public boolean save(){
        try {
            OutputStream out = new FileOutputStream(url);
            try {
                mProps.store(out, null);
            } catch (IOException e) {
                e.printStackTrace();
                return  false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //值获取不到 可以获取domain路径的domain.properties的值
    public String getProperty(String param){
        String value = mProps.getProperty(param, "null");
        return  value;
    }

    public Object getPro(String param){
        Object value = mProps.get(param);
        return  value;
    }

    public boolean deleteProperty( String param){
        if(!new File(url).exists()){
            return true;
        }
        mProps.remove(param);
        if (!mProps.containsKey(param)){
            return true;
        }
        return false;
    }
    /**
     *
     * parentName 文件夹名字
     * */
    public boolean clean( ) {
        mProps.clear();
        return mProps.isEmpty();
    }

    /**
     *
     * 打开properties文件，得到properties操作类。
     * */
    public Properties openProperties(String parentName,String propertiesName){
        url = ResManagerFactory.getResManager().getPath(ResManager.APP_URI) + "/properties" + "/" + parentName + "/" + propertiesName + ".properties";
        mProps = new Properties();
        try {
            InputStream in = new FileInputStream(getSettingFile(url));
            mProps.load(in);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return mProps;
    }


    /**
     *
     * 获取properties文件
     * */
    private File getSettingFile(String path){
        File setting = new File(path);
        if(!setting.exists()) {
            try {
                setting.getParentFile().mkdirs();
                setting.createNewFile();
//             Runtime.getRuntime().exec("chmod 777 " + setting);
//             Runtime.getRuntime().exec("chmod 777 " + setting.getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return setting;
    }
}
