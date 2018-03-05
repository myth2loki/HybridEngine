package com.xhrd.mobile.hybrid.framework.manager.actionsheet;

import android.text.TextUtils;
import android.view.View;

import com.xhrd.mobile.hybrid.annotation.JavascriptConfig;
import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.annotation.JavascriptProperty;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

import org.json.JSONArray;
import org.json.JSONObject;

@JavascriptConfig(domain = "actionSheet", scope = PluginData.Scope.App, properties = {
            @JavascriptProperty(name = "Test", value = "'test'")
        })
public class ActionSheet extends PluginBase {
    private String sunc;

    public ActionSheet(){
    }


    @JavascriptFunction(name = "show")
    public void show(HybridView rdCloudView, String[] params){
        String option = params[0];
        sunc = params[1];
        try {
            JSONObject jsonObject = new JSONObject(option);
            String title = jsonObject.optString("title", "");
            String cancelTitle = jsonObject.optString("cancelTitle", "取消");
            String destructiveTitle = jsonObject.optString("destructiveTitle", "");
            JSONArray array = jsonObject.optJSONArray("buttons");
            showActionSheet(rdCloudView,title, cancelTitle, destructiveTitle, array);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 显示ActionSheet
     * @param title
     * @param cancelTitle
     * @param destructiveTitle
     * @param array
     */
    private void showActionSheet(final HybridView rdCloudView, String title, String cancelTitle, String destructiveTitle, JSONArray array) {
        try {
            ActionSheetDialog actionSheetDialog = new ActionSheetDialog(HybridActivity.getInstance());
            actionSheetDialog = actionSheetDialog.builder();
            if (!TextUtils.isEmpty(title)){
                actionSheetDialog.setTitle(title);
            }
            actionSheetDialog.setCancelable(false);
            actionSheetDialog.setCanceledOnTouchOutside(true);
            for (int i = 0; i < array.length(); i++){
                actionSheetDialog.addSheetItem(array.getString(i), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        jsCallback(rdCloudView,false,sunc,which+"");
                    }
                });
            }
            if (!TextUtils.isEmpty(destructiveTitle)){
                actionSheetDialog.addSheetItem(destructiveTitle, ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        jsCallback(rdCloudView,false,sunc, which + "");
                    }
                });
            }

            final ActionSheetDialog finalActionSheetDialog = actionSheetDialog;
            actionSheetDialog.txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jsCallback(rdCloudView,false,sunc, "0");
                    finalActionSheetDialog.dialog.dismiss();
                }
            });
            actionSheetDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
