package com.xhrd.mobile.hybridframework.engine;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.xhrd.mobile.hybridframework.util.ResourceUtil;

/**
 * 管理本地资源
 * Created by wangqianyu on 15/6/5.
 */
public class RDResourceManager {
    private static RDResourceManager mInstance = new RDResourceManager();

    private RDResourceManager() {
    }

    /**
     * 通过传入context获得ResoureFinder实例以后调用方法可以不用传入context
     *
     * @return
     */
    public synchronized static RDResourceManager getInstance() {
        if (mInstance == null) {
            mInstance = new RDResourceManager();
        }
        return mInstance;
    }

    public Resources getResources(){
        return ResourceUtil.getResources();
    }
    /**
     * get animation resource id according animation resource name
     *
     * @param animName
     * @return
     */
    public int getAnimId(String animName) {
        return ResourceUtil.getResAnimID(animName);
    }

    /**
     * get animation object according animation resource name
     *
     * @param animName
     * @return
     */
    public Animation getAnimation(String animName) {
        final int animId = getAnimId(animName);
        if (animId == 0) {
            return null;
        } else {
            return AnimationUtils.loadAnimation(ResourceUtil.mContext, animId);
        }
    }

    /**
     * get attribute resource id according attr resource name
     *
     * @param attrName
     * @return
     */
    public int getAttrId(String attrName) {
        return ResourceUtil.getResAttrID(attrName);
    }

    /**
     * get color resource id according color resource name
     *
     * @param colorName
     * @return
     */
    public int getColorId(String colorName) {
        return ResourceUtil.getResColorID(colorName);
    }

    /**
     * get color value according color resource name
     *
     * @param colorName
     * @return
     */
    public int getColor(String colorName) {
        final int colorId = getColorId(colorName);
        if (colorId == 0) {
            return 0;
        } else {
            return ResourceUtil.resources.getColor(colorId);
        }
    }

    /**
     * get drawable resource id according drawable resource name
     *
     * @param drawableName
     * @return
     */
    public int getDrawableId(String drawableName) {
        return ResourceUtil.getResDrawableID(drawableName);
    }

    /**
     * get drawable object according drawable resource name
     *
     * @param drawableName
     * @return
     */
    public Drawable getDrawable(String drawableName) {
        final int drawableId = getDrawableId(drawableName);
        if (drawableId == 0) {
            return null;
        } else {
            return ResourceUtil.resources.getDrawable(drawableId);
        }
    }

    /**
     * get view id according id's name
     *
     * @param idName
     * @return
     */
    public int getId(String idName) {
        return ResourceUtil.getResIdID(idName);
    }

    /**
     * get layout resource id according layout resource name
     *
     * @param layoutName
     * @return
     */
    public int getLayoutId(String layoutName) {
        return ResourceUtil.getResLayoutID(layoutName);
    }

    /**
     * get raw resoure id according raw resource name
     *
     * @param rawName
     * @return
     */
    public int getRawId(String rawName) {
        return ResourceUtil.getResRawID(rawName);
    }

    /**
     * get String resource id according string resource name
     *
     * @param stringName
     * @return
     */
    public int getStringId(String stringName) {
        return ResourceUtil.getResStringID(stringName);
    }

    /**
     * get String value according string resource name
     *
     * @param stringName
     * @return
     */
    public String getString(String stringName) {
        final int stringId = getStringId(stringName);
        if (stringId == 0) {
            return "";
        } else {
            return ResourceUtil.resources.getString(stringId);
        }
    }

    /**
     * get style resource id according style resource name
     *
     * @param styleName
     * @return
     */
    public int getStyleId(String styleName) {
        return ResourceUtil.getResStyleID(styleName);
    }

    public int getResDimenId(String dimenName){
        return ResourceUtil.getResDimenID(dimenName);
    }

    public int getResStyleableId(String styleName){
        return ResourceUtil.getResStyleableID(styleName);
    }

    public int[] getResourceDeclareStyleableIntArray(String name){
        return ResourceUtil.getResourceDeclareStyleableIntArray(name);
    }

    public static int dipToPixels(int dip) {
        return ResourceUtil.dipToPixels(dip);
    }


    public static int px2dip(float pxValue) {
        return ResourceUtil.px2dip(pxValue);
    }

    public int getArrayId(String name){
        return ResourceUtil.getResArrayID(name);
    }

    public int getMipmapId(String name){
        return ResourceUtil.getResMipmapID(name);
    }

}
