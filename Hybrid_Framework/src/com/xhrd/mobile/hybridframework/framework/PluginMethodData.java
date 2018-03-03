package com.xhrd.mobile.hybridframework.framework;

import android.text.TextUtils;

import com.xhrd.mobile.hybridframework.engine.RDResourceManager;

import java.util.Arrays;

/**
 * Created by az on 16/1/4.
 */
public class PluginMethodData {

    private String name;

//    private String[] preParams;
//    private boolean convertJS;
//    private boolean hasReturn;

    private String jsFun;
    private boolean isAddWhitelist;
    private String[] permissions;
    private String[] permissionRationales;


    public PluginMethodData(String name, String[] permissions,String[] permissionRationales) {
        this(name, null, permissions, permissionRationales, false);
    }

    public PluginMethodData(String name, String jsFun, String[] permissions,String[] permissionRationales,boolean isAddWhitelist) {
        this.name = name;
        this.jsFun = jsFun;
        this.permissions = permissions;
        setPermissionRationales(permissionRationales);
        this.isAddWhitelist = isAddWhitelist;
    }

    public String getName() {
        return name;
    }

    public String getJsFun() {
        return jsFun;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public boolean isAddWhitelist() {
        return isAddWhitelist;
    }

    public String[] getPermissionRationales() {
        return permissionRationales;
    }


    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public void setPermissionRationales(String[] permissionRationales) {
        String[] tempRationales = null;
        if(permissionRationales != null) {
            int rationalesLength = permissionRationales.length;
            tempRationales = new String[rationalesLength];
            for(int i=0; i<rationalesLength; i++) {
                String rationale = permissionRationales[i];
                //解析资源
                if(!TextUtils.isEmpty(rationale) && rationale.startsWith("@string/")) {
                    rationale = RDResourceManager.getInstance().getString(rationale.replace("@string/", ""));
                }
                tempRationales[i] = rationale;
            }
        }
        this.permissionRationales = tempRationales;
    }

    @Override
    public String toString() {
        return "PluginMethodData{" +
                "name='" + name + '\'' +
                ", jsFun='" + jsFun + '\'' +
                ", isAddWhitelist=" + isAddWhitelist +
                ", permissions=" + Arrays.toString(permissions) +
                ", permissionRationales=" + Arrays.toString(permissionRationales) +
                '}';
    }
}
