package com.xhrd.mobile.hybrid.framework.manager.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.engine.HybridResourceManager;
import com.xhrd.mobile.hybrid.engine.HybridActivity;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class gallery extends PluginBase {
	
	private HybridActivity mApp;
	private static int RESULT_LOAD_IMAGE = 1;
	private static int RESULT_LOAD_VIDEO = 2;
	private static int RESULT_LOAD_NONE = 3;
	private static int RESULT_LOAD_MULTI_IMAGE = 4;
	private static int RESULT_LOAD_MULTI_VIDEO = 5;
	private static int RESULT_LOAD_MULTI_NONE = 6;
	private String picturePath;
	private SharedPreferences msp;
	private String windowName;

	public gallery(){
		mApp = HybridActivity.getInstance();
	}
	
	@JavascriptFunction
	public void pick(String windowName,String[] params){
		String sucFunc2 = params[0];//function GalleryPickSuccessCallback(p){alert(p.path);}
		String errFunc = params[1];
		String option = params[2];
		this.windowName = windowName;
		msp = mApp.getSharedPreferences("gallery", Context.MODE_PRIVATE);
		msp.edit().putString("sucFunc2", sucFunc2).commit();
		msp.edit().putString("errFunc", errFunc).commit();
		Util util = new Util(mApp);
		try {
			JSONObject optObject = new JSONObject(option);
			boolean multiple = optObject.optBoolean("multiple");
			if (!multiple){
				String filter = optObject.optString("filter");
				if (!TextUtils.isEmpty(filter) && filter.equals("image")) {
					List<String> videoList = util.listAlldir();
					if (videoList.size() == 0){
						jsCallback(errFunc, "没有图片");
						return;
					}
					Intent intent = new Intent(mApp,NoneActivity.class);
					intent.putExtra("filter", filter);
					startActivityForResult(intent, RESULT_LOAD_IMAGE);
//					Intent intent = new Intent(
//							Intent.ACTION_PICK,
//							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//					startActivityForResult(intent, RESULT_LOAD_IMAGE);
				} else if (!TextUtils.isEmpty(filter) && filter.equals("video")) {
					List<String> videoList = util.getVideoPath();
					if (videoList.size() == 0){
						jsCallback(errFunc, "没有视频");
						return;
					}
					Intent intent = new Intent(mApp,NoneActivity.class);
					intent.putExtra("filter", filter);
					startActivityForResult(intent, RESULT_LOAD_VIDEO);
//					Intent intent = new Intent(
//							Intent.ACTION_PICK,
//							android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//					startActivityForResult(intent, RESULT_LOAD_VIDEO);
				} else if(!TextUtils.isEmpty(filter) && filter.equals("none")){
					Intent intent = new Intent(mApp,NoneActivity.class);
					intent.putExtra("filter", filter);
					startActivityForResult(intent, RESULT_LOAD_NONE);
				}
			}else{
				String filter = optObject.optString("filter");
				if (!TextUtils.isEmpty(filter) && filter.equals("image")) {
					List<String> videoList = util.listAlldir();
					if (videoList.size() == 0){
						jsCallback(errFunc, "没有图片");
						return;
					}
					Intent intent = new Intent(mApp,ImageActivity.class);
					startActivityForResult(intent, RESULT_LOAD_MULTI_IMAGE);
				} else if (!TextUtils.isEmpty(filter) && filter.equals("video")) {
					List<String> videoList = util.getVideoPath();
					if (videoList.size() == 0){
						jsCallback(errFunc, "没有视频");
						return;
					}
					Intent intent = new Intent(mApp,VideoActivity.class);
					startActivityForResult(intent, RESULT_LOAD_MULTI_VIDEO);
				} else if (!TextUtils.isEmpty(filter) && filter.equals("none")) {
					Intent intent = new Intent(mApp,NoneMultiActivity.class);
					intent.putExtra("filter", filter);
					startActivityForResult(intent, RESULT_LOAD_MULTI_NONE);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		if (sucFunc2.contains("GalleryPickSuccessCallback")) {
//			try {
//				JSONObject optObject = new JSONObject(option);
//				String filter = optObject.optString("filter");
//				if (!TextUtils.isEmpty(filter) && filter.equals("image")) {
//					Intent intent = new Intent(
//							Intent.ACTION_PICK,
//	                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//					startActivityForResult(intent, RESULT_LOAD_IMAGE);
//				} else if (!TextUtils.isEmpty(filter) && filter.equals("video")) {
//					Intent intent = new Intent(
//							Intent.ACTION_PICK,
//	                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//					startActivityForResult(intent, RESULT_LOAD_VIDEO);
//				} else if(!TextUtils.isEmpty(filter) && filter.equals("none")){
//					 Intent intent = new Intent(Intent.ACTION_PICK);
//					 startActivityForResult(intent, RESULT_LOAD_NONE);
//				}
//
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		} else if (sucFunc2.contains("GalleryMultiplePickSuccessCallback")) {
//			try {
//				JSONObject optObject = new JSONObject(option);
//				String filter = optObject.optString("filter");
//				if (!TextUtils.isEmpty(filter) && filter.equals("image")) {
//					Intent intent = new Intent(mApp,ImageActivity.class);
//					startActivityForResult(intent, RESULT_LOAD_MULTI_IMAGE);
//				} else if (!TextUtils.isEmpty(filter) && filter.equals("video")) {
//					Intent intent = new Intent(mApp,VideoActivity.class);
//					startActivityForResult(intent, RESULT_LOAD_MULTI_VIDEO);
//				} else if (!TextUtils.isEmpty(filter) && filter.equals("none")) {
//					//TODO
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	}
	
	
	/**
	 * 保存图片或视频到系统相册中
	 * @param windowName
	 * @param params
	 */
	@JavascriptFunction
	public void save1(String windowName,String[] params){
		String path = params[0];
		String sunc = params[1];
		String err = params[2];
		File fromFile = new File(path);
		if (!fromFile.exists()) {
			jsCallback(err, "指定的文件不存在");
			return;
		}
		String dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Camera";
		Log.i("-------->", dcimFolder);
		File toFile = new File(dcimFolder);
		copyfile(fromFile, toFile);
		jsCallback(sunc, "success");
	}


	@JavascriptFunction
	public void save(HybridView view, String[] params){
		String inPath = params[0];
		final String sunc = params[1];
		final String err = params[2];

		try {
			inPath = URLDecoder.decode(inPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (inPath == null || inPath.length() == 0) {
			jsCallback(err,"path error");
			return;
		}

		final String finalPath = inPath;
		File destPath = null;
		try {
			destPath = new File(Environment.getExternalStorageDirectory(), "/DCIM/");
			if (!destPath.exists()) {
				destPath.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		final File savePath = destPath;
		boolean isSunc = copyfile1(savePath, finalPath);
		if (isSunc){
			jsCallback(sunc, "save success");
		}else{
			jsCallback(err, "save fail");
		}

//		MyAsyncTask myAsyncTask = new MyAsyncTask();
//		myAsyncTask.execute();
//		new MyAsyncTask(){
//
//			public void handleOnPreLoad(MyAsyncTask task) {
//				if (savePath == null) {
//					jsCallback(err,"can not save");
//					this.cancel(true);
//				}
//			};
//
//			protected Object doInBackground(Object... params) {
//				if (savePath == null) {
//					return false;
//				}
//				boolean isSuc = false;
//				File inFile = new File(finalPath);
//				if (URLUtil.isFileUrl(finalPath)) {// 本地路径
//					FileInputStream fis = null;
//					FileOutputStream fos = null;
//					try {
//						fis = new FileInputStream(inFile);
//						byte[] buffer = new byte[8192];
//						File outFile = new File(savePath.getAbsolutePath()
//								+ "/" + inFile.getName());
//						if (!outFile.exists()) {
//							outFile.createNewFile();
//						}
//						fos = new FileOutputStream(outFile);
//						int actualSize = -1;
//						while ((actualSize = fis.read(buffer)) != -1) {
//							fos.write(buffer, 0, actualSize);
//						}
//						isSuc = true;
//						// 更新媒体库数据
//						UpdateMediaData.getInstance(mApp).updateFile(
//								outFile.getAbsolutePath());
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} finally {
//						if (fis != null) {
//							try {
//								fis.close();
//							} catch (IOException e) {
//
//								e.printStackTrace();
//							}
//						}
//						if (fos != null) {
//							try {
//								fos.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//
//					}
//
//				}
//				return isSuc;
//			};
//
//			public void handleOnCompleted(MyAsyncTask task, Object result) {
//				boolean flag = (Boolean) result;
//				if (flag) {
//					jsCallback(sunc, "save success");
//				} else {
//					jsCallback(err, "save fail");
//				}
//			};
//
//		}.execute(new Object[]{});

	}

	private boolean copyfile1(File savePath, String finalPath){
		if (savePath == null) {
			return false;
		}
		boolean isSuc = false;
		File inFile = new File(finalPath);
		//if (URLUtil.isFileUrl(finalPath)) {// 本地路径
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = new FileInputStream(inFile);
				byte[] buffer = new byte[8192];
				File outFile = new File(savePath.getAbsolutePath()
						+ "/" + inFile.getName());
				if (!outFile.exists()) {
					outFile.createNewFile();
				}
				fos = new FileOutputStream(outFile);
				int actualSize = -1;
				while ((actualSize = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, actualSize);
				}
				isSuc = true;
				// 更新媒体库数据
				UpdateMediaData.getInstance(mApp).updateFile(
						outFile.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		//}
		return isSuc;
	}

	/**
	 * 拷贝文件
	 * @param fromFile
	 * @param toFile
	 */
	private void copyfile(File fromFile, File toFile) {
		try {  
            FileInputStream fosfrom = new FileInputStream(fromFile);  
            FileOutputStream fosto = new FileOutputStream(toFile);  
              
            byte[] bt = new byte[1024];  
            int c;  
            while((c=fosfrom.read(bt)) > 0){  
                fosto.write(bt,0,c);  
            }  
            //关闭输入、输出流  
            fosfrom.close();  
            fosto.close();  
              
              
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		msp = mApp.getSharedPreferences("gallery", Context.MODE_PRIVATE);
		String suc = msp.getString("sucFunc2", "");
		String err = msp.getString("errFunc", "");
		if (requestCode == RESULT_LOAD_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				String path = data.getStringExtra("path");
//				String json = "{path:'" + path + "'}";
				jsCallback(suc, path);
//				Uri selectedImage = data.getData();
//	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//	            Cursor cursor = mApp.getContentResolver().query(selectedImage,
//	                    filePathColumn, null, null, null);
//	            cursor.moveToFirst();
//
//	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//	            picturePath = cursor.getString(columnIndex);
//	            String json = "{path:'" + picturePath + "'}";
//	            cursor.close();
//	            jsonCallBack(suc, json);
			}else{
				jsCallback(err, "get image failed");
			}
			
		} else if (requestCode == RESULT_LOAD_VIDEO) {
			if (resultCode == Activity.RESULT_OK) {
				String path = data.getStringExtra("path");
//				String json = "{path:'" + path + "'}";
				jsCallback(suc, path);
//				Uri selectedImage = data.getData();
//	            String[] filePathColumn = { MediaStore.Video.Media.DATA };
//
//	            Cursor cursor = mApp.getContentResolver().query(selectedImage,
//	                    filePathColumn, null, null, null);
//	            cursor.moveToFirst();
//
//	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//	            picturePath = cursor.getString(columnIndex);
//	            String json = "{path:'" + picturePath + "'}";
//	            cursor.close();
//	            jsonCallBack(suc, json);
			}else {
				jsCallback(err, "get video failed");
			}
			
		} else if(requestCode == RESULT_LOAD_NONE){
			if (resultCode == Activity.RESULT_OK) {
				String path = data.getStringExtra("path");
				String json = "{path:'" + path + "'}";
				jsonCallBack(suc, json);
			}
		} else if (requestCode == RESULT_LOAD_MULTI_IMAGE) {
			if (resultCode == 4) {
				ArrayList<String> filelist = data.getStringArrayListExtra("filelist");
				Log.i("ljm=====>", filelist.toString());
				String picturePath = filelist.toString();
//				String json = "{path:'" + picturePath + "'}";
//				jsonCallBack(getTargetView(windowName), false, suc, json);
				jsCallback(suc, picturePath.substring(1,picturePath.length()-1));
			} else {
				jsCallback(err, "get image failed");
			}
		} else if (requestCode == RESULT_LOAD_MULTI_VIDEO) {
			if (resultCode == 4) {
				ArrayList<String> filelist = data.getStringArrayListExtra("filelist");
				Log.i("ljm=====>", filelist.toString());
				String picturePath = filelist.toString();
//				String json = "{path:'" + picturePath + "'}";
				jsCallback(suc, picturePath.substring(1, picturePath.length() - 1));
			}else {
				jsCallback(err, "get video failed");
			}
		} else if (requestCode == RESULT_LOAD_MULTI_NONE){
			if (resultCode == 4) {
				ArrayList<String> filelist = data.getStringArrayListExtra("filelist");
				Log.i("ljm=====>", filelist.toString());
				String picturePath = filelist.toString();
//				String json = "{path:'" + picturePath + "'}";
				jsCallback(suc, picturePath.substring(1, picturePath.length() - 1));
			}else {
				jsCallback(err, "get video failed");
			}
		}
	}

	@Override
	public void addMethodProp(PluginData data) {
        data.addMethod("pick",new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, new String[]{HybridResourceManager.getInstance().getString("request_gallery_permission_msg")});
        data.addMethod("save",new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, new String[]{HybridResourceManager.getInstance().getString("request_gallery_permission_msg")});
	}
	
	public PluginData.Scope getScope() {
        return PluginData.Scope.App;
    }
	
}
