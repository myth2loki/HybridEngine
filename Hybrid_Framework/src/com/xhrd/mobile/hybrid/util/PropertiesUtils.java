package com.xhrd.mobile.hybrid.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

	
	public static Properties getProperties(String path){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            return getProperties(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		return null;
	}

    public static Properties getProperties(InputStream inputStream){
        Properties pro = new Properties();
        try {
            pro.load(inputStream);
            inputStream.close(); //关闭流
            return pro;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**  
     * 保存属性到文件中  
     *   
     * @param pro  
     * @param file  
     */  
    public static void saveProperties(Properties pro, String file) {  
        if (pro == null) {  
            return;  
        }  
        FileOutputStream oFile = null;  
        try {  
            oFile = new FileOutputStream(file, false);  
            pro.store(oFile, "modify properties file");  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (oFile != null) {  
                    oFile.close();  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
	
}
