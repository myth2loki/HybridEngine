package com.xhrd.mobile.hybridframework.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.xhrd.mobile.hybridframework.TestNative;

import java.lang.reflect.Field;

/**
 * Created by wangqianyu on 15/6/5.
 */
public class ResourceUtil {

    public static String packageName;
    public static String attrPackageName;
    public static Resources resources;
    public static Context mContext;

    public static final String anim = "anim";
    public static final String animator = "animator";
    public static final String interpolator = "interpolator";
    public static final String menu = "menu";
    public static final String mipmap = "mipmap";
    public static final String array = "array";
    public static final String bool = "bool";
    public static final String stringArray = "string-array";
    public static final String attr = "attr";
    public static final String color = "color";
    public static final String dimen = "dimen";
    public static final String drawable = "drawable";
    public static final String id = "id";
    public static final String layout = "layout";
    public static final String raw = "raw";
    public static final String string = "string";
    public static final String style = "style";
    public static final String xml = "xml";
    public static final String styleable = "styleable";

    /**
     * 初始化ResourceUtil
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        packageName = context.getPackageName();
        attrPackageName =TestNative.class.getPackage().getName();
        //packageName = TestNative.class.getPackage().getName();
        resources = context.getResources();
        mContext = context.getApplicationContext();
    }

    public static Resources getResources(){
        return resources;
    }

    public static int getResDrawableID(String resName) {

        return resources.getIdentifier(resName, drawable, packageName);
    }

    public static int getResLayoutID(String resName) {

        return resources.getIdentifier(resName, layout, packageName);
    }

    public static int getResAnimID(String resName) {

        return resources.getIdentifier(resName, anim, packageName);
    }

    public static int getResAnimatorID(String resName) {

        return resources.getIdentifier(resName, animator, packageName);
    }

    public static int getResAttrID(String resName) {

        return resources.getIdentifier(resName, attr, packageName);
    }

    public static int getResColorID(String resName) {

        return resources.getIdentifier(resName, color, packageName);
    }

    public static int getResDimenID(String resName) {

        return resources.getIdentifier(resName, dimen, packageName);
    }

    public static int getResIdID(String resName) {

        return resources.getIdentifier(resName, id, packageName);
    }

    public static int getResRawID(String resName) {

        return resources.getIdentifier(resName, raw, packageName);
    }

    public static int getResStringID(String resName) {

        return resources.getIdentifier(resName, string, packageName);
    }

    public static int getResStyleID(String resName) {
        return resources.getIdentifier(resName, style, packageName);
    }

    public static int getResStyleableID(String name) {
        return resources.getIdentifier(name, styleable, attrPackageName);
    }

    /**
     * ******************************************************************************
     * Returns the resource-IDs for all attributes specified in the
     * given <declare-styleable>-resource tag as an int array.
     *
     * @param name    The name of the <declare-styleable>-resource-tag to pick.
     * @return All resource-IDs of the child-attributes for the given
     * <declare-styleable>-resource or <code>null</code> if
     * this tag could not be found or an error occured.
     * *******************************************************************************
     */
    public static final int[] getResourceDeclareStyleableIntArray(String name) {
        try {
            //use reflection to access the resource class
            Field[] fields2 = Class.forName(attrPackageName + ".R$styleable").getFields();

            //browse all fields
            for (Field f : fields2) {
                //pick matching field
                if (f.getName().equals(name)) {
                    //return as int array
                    int[] ret = (int[]) f.get(null);
                    return ret;
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }

    public static int getResXmlID(String resName) {

        return resources.getIdentifier(resName, xml, packageName);
    }

    public static int getResInterpolatorID(String resName) {

        return resources.getIdentifier(resName, interpolator, packageName);
    }

    public static int getResMenuID(String resName) {

        return resources.getIdentifier(resName, menu, packageName);
    }

    public static int getResMipmapID(String resName) {

        return resources.getIdentifier(resName, mipmap, packageName);
    }

    public static int getResArrayID(String resName) {

        return resources.getIdentifier(resName, array, packageName);
    }

    public static int getResBoolID(String resName) {

        return resources.getIdentifier(resName, bool, packageName);
    }

    public static int getResStringArrayID(String resName) {

        return resources.getIdentifier(resName, stringArray, packageName);
    }

    public static String getString(String resName) {
        int id = getResStringID(resName);
        return resources.getString(id);
    }

    public static String getCertificatePsw(Context context, String appid) {
        //        return EUtil.getCertificatePsw(context, appid);
        return null;
    }

    public static int dipToPixels(int dip) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, dm);
    }


    public static int px2dip(float pxValue) {
        final float scale = resources.getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



}
