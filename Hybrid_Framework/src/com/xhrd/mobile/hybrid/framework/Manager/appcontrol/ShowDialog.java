package com.xhrd.mobile.hybrid.framework.Manager.appcontrol;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhrd.mobile.hybrid.engine.RDCloudView;
import com.xhrd.mobile.hybrid.engine.RDResourceManager;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.framework.Manager.I18n;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowDialog {

	public final static int F_ALERT = 0;
	public final static int F_CONFIRM = 1;
	public final static int F_PROMPT = 2;

	public interface IDialogListener
	{
		public void confirm(View v);
	}

	private ProgressDialog progressDialog;
	/**
	 * 显示进度对话框
	 * 
	 * @param message
	 * @param cancelable
	 */
	public void showProgressDialog(String message, boolean cancelable,Context context) {
		if (progressDialog == null)
			progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancelable);
		progressDialog.show();
	}
	/**
	 * 显示进度对话框
	 * 
	 * @param message
	 */
	@SuppressLint("NewApi")
	public void showProgressDialog(String message,Context content) {		    
		showProgressDialog(message, false, content);
	}

	/**
	 * 隐藏进度对话框
	 */
	public void hideProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
	}

	/**
	 * 显示自定义对话框
	 * 
	 * @param view
	 * @return
	 */
	public Dialog showDialog(View view,Context context) {
		Dialog dialog = createDialog(view,context);
		dialog.show();
		return dialog;
	}

	/**
	 * 创建一个自定义对话框
	 * 
	 * @param view
	 * @return
	 */
	public Dialog createDialog(View view,Context context) {
		Dialog dialog = new Dialog(context, RDResourceManager.getInstance().getStyleId("custom_dialog"));
		dialog.setContentView(view);
		dialog.setCancelable(false);
		return dialog;
	}
	/**
	 * 创建一个自定义对话框
	 * 
	 * @param viewId
	 * @return
	 */
	public Dialog createDialog(int viewId,Context context) {
		return createDialog(View.inflate(context, viewId, null),context);
	}

	public Dialog createDialog(final RDCloudView pView,String[] params,final int pDialogType,final Window win) {
		return createDialog(pView,params,win,pDialogType,null);
	}

	public Dialog createDialog(String[] params,final int pDialogType,final IDialogListener pConfrimListener) {
		return createDialog(null,params,null,pDialogType,pConfrimListener);
	}
	/**
	 * 创建一个默认样式的对话框
	 * 带输入文本的弹框提醒
	 * @param params params[0] json对象  {
			title:"标题",
			msg:"内容",
			text:"文本内容",
			type:"text",// 文本类型 如text、password
			buttons:{"确认","取消"}//显示的是按钮
			}
			params[1] 回调函数
	 * @param win 当前window用来执行回调
	 * @return
	 */
	public Dialog createDialog(final RDCloudView pView,String[] params,final Window win,final int pDialogType,final IDialogListener pConfrimListener) {
		View view =  View.inflate(HybridActivity.getInstance(), RDResourceManager.getInstance().getLayoutId("default_layout"), null);
		TextView tv_title = (TextView) view.findViewById(RDResourceManager.getInstance().getId("tv_dialog_title")); // 获取标题控件
		TextView tv_content = (TextView) view.findViewById(RDResourceManager.getInstance().getId("tv_dialog_content")); // 获取标题控件
		final EditText edit_content = (EditText)view.findViewById(RDResourceManager.getInstance().getId("edit_content"));
		LinearLayout ll_edit_content = (LinearLayout)view.findViewById(RDResourceManager.getInstance().getId("ll_edit_content"));
		LinearLayout ll_content = (LinearLayout) view.findViewById(RDResourceManager.getInstance().getId("ll_content")); // 获取标题控件
		LinearLayout ll_button = (LinearLayout)view.findViewById(RDResourceManager.getInstance().getId("ll_button"));

		final Dialog dialog = createDialog(view, HybridActivity.getInstance());
		String title = null;
		String content = null;
		String editContent = null;
		String editType = null;
		String callback = null;
		if (params.length == 2) {
			callback = params[1];
		}
		try {
			JSONObject json = new JSONObject(params[0]);
			title = json.optString("title");
			content = json.optString("msg");
			if(json.has("text")) {
				editContent = json.optString("text");
				if(json.has("type")) {
					editType = json.optString("type");
					if(!TextUtils.isEmpty(editType)) {
						switch (editType) {
							case "text":
								edit_content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
								break;
							case "password":
								edit_content.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
								break;
							case "number":
								edit_content.setInputType(InputType.TYPE_CLASS_NUMBER);
								break;
							case "email":
								edit_content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
								break;
							case "url":
								edit_content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
								break;
						}
					}
				}
			}
			final String EDIT_CONTENT = editContent;
			JSONArray buttons = json.optJSONArray("buttons");
//			if(buttons!=null) {
				int btnLen = 0;
				if(buttons != null) {
					btnLen = buttons.length();
				}
				String[] defaultStr = new String[] {I18n.getInstance().getString("confirm"),I18n.getInstance().getString("cancel")};
				int bl = 0;
				if(pDialogType == F_ALERT) {
					if(btnLen>1) {
						btnLen = 1;
					}else {
						bl = 1-btnLen;
					}
				}else if(pDialogType == F_CONFIRM) {
					if(btnLen>3) {
						btnLen = 3;
					}else if(btnLen<2){
						bl = 2 - btnLen;
					}
				}else if(pDialogType == F_PROMPT) {
					if(btnLen>3) {
						btnLen = 3;
					}else if(btnLen<2){
						bl = 2-btnLen;
					}
				}
				int ind = 0;
				for(int i=0;i<btnLen;i++ ) {
					final int index = i+1;
					ind = index;
					String name = buttons.getString(i);
					Button btn = createButton(name);// new Button(App.getInstance());
//					btn.setText(name);
//					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1);
//					btn.setLayoutParams(lp);
					final String cb = callback;
					btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (!TextUtils.isEmpty(cb)) {
								String param = "";
								if (!TextUtils.isEmpty(EDIT_CONTENT)) {
									param = ",text:'" + edit_content.getText() + "'";
								}
								win.jsonCallBack(pView,true, cb, "{buttonIndex:" + index + "" + param + "},''");
							}else {
								if(pConfrimListener!=null) {
									pConfrimListener.confirm(v);
								}
							}
							dialog.dismiss();
						}
					});
					ll_button.addView(btn);
				}

				if(bl>0) {
					ind ++;
					for(int i=0; i<bl; i++) {
						final int index = i+ind;
						String name = defaultStr[1];
						if(bl>1) {
							name = defaultStr[i];
						}
						Button btn = createButton(name);
						final String cb = callback;
						btn.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (!TextUtils.isEmpty(cb)) {
									String param = "";
									if (!TextUtils.isEmpty(EDIT_CONTENT)) {
										param = ",text:'" + edit_content.getText() + "'";
									}
									win.jsonCallBack(pView,true, cb, "{buttonIndex:" + index + "" + param + "},''");
								}else {
									if(pConfrimListener!=null) {
										pConfrimListener.confirm(v);
									}
								}
								dialog.dismiss();
							}
						});
						ll_button.addView(btn);
//					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(!TextUtils.isEmpty(content)){
			tv_content.setText(content);

			ll_content.setVisibility(View.VISIBLE);
		}
		if(!TextUtils.isEmpty(title)){
			tv_title.setText(title);
			tv_title.setVisibility(View.VISIBLE);
		}
		if(!TextUtils.isEmpty(editContent)) {
			ll_edit_content.setVisibility(View.VISIBLE);
			edit_content.setHint(editContent);
		}
		return dialog;
	}

	private Button createButton(String pName) {
		Button btn = new Button(HybridActivity.getInstance());
		String name = pName;
		btn.setText(name);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1);
		btn.setLayoutParams(lp);
		return btn;
	}
}
