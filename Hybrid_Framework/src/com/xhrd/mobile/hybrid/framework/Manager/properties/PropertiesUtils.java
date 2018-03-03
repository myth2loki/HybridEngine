package com.xhrd.mobile.hybrid.framework.Manager.properties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by lilong on 15/6/27.
 *
 */
public class PropertiesUtils {
    PropertiesHelper propertiesHelper;

    public PropertiesUtils(String parentName,String propertiesName){
        propertiesHelper=new PropertiesHelper(parentName,propertiesName);

    }

    public void putBoolean(String key,boolean value){
       int i=value==false?0:1;
       propertiesHelper.putProperty(key,i);

    }
    public void putInt(String key,int value){
        propertiesHelper.putProperty(key,value);

    }
    public void putDouble(String key,Double value){
        propertiesHelper.putProperty(key,value);

    }
    public void putFolat(String key,float value){
        propertiesHelper.putProperty(key,value);

    }
    public void putString(String key,String value){
        propertiesHelper.putProperty(key,value);

    }
    public void putChar(String key,char value){
        propertiesHelper.putProperty(key,value);

    }
    public void putLong(String key,long value){
        propertiesHelper.putProperty(key,value);

    }
    public void putByte(String key,byte value){
        propertiesHelper.putProperty(key,value);

    }

    public boolean getBoolean(String key){
        boolean isValue = false;
        int getValue=Integer.parseInt(String.valueOf( propertiesHelper.getPro(key)));
        if(getValue==1){
            isValue=true;
        }
        if(getValue==0){
            isValue=false;
        }
        return isValue;
    }
    public int getInt(String key ){

        int getValue=Integer.parseInt(String.valueOf( propertiesHelper.getPro(key)));


        return getValue;

    }
    public double getDouble(String key ){

        double  getValue=new   Double( propertiesHelper.getPro(key).toString());
        return getValue;

    }
    public float getFolat(String key ){
        float getValue=Float.parseFloat(propertiesHelper.getPro(key).toString());//b为object类型
        return getValue;

    }
    public String getString(String key ){
       String getValue= String.valueOf( propertiesHelper.getPro(key));
        return getValue;
    }
    public char getChar(String key ){
        //TODO 要想更好的实现方式
        char getValue = 0;//进行强制类型转换
        if(propertiesHelper.getPro(key) instanceof Character){//如果obj是字符类型

            getValue= (Character)propertiesHelper.getPro(key);
        }

        return getValue;
    }
    public long getLong(String key ){

       long getValue= Long.valueOf(String.valueOf( propertiesHelper.getPro(key)));

        return getValue;
    }
    public byte getByte(String key ){
        //TODO  要想更好的实现方式
        return toByteArray(propertiesHelper.getPro(key))[0];
    }

    public void delete(String key){
        propertiesHelper.deleteProperty(key);

    }

    public void clean(){
        propertiesHelper.clean();

    }

    public byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }



}
