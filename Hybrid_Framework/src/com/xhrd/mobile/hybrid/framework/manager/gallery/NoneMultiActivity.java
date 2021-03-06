package com.xhrd.mobile.hybrid.framework.manager.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xhrd.mobile.hybrid.engine.HybridResourceManager;

import java.util.ArrayList;
import java.util.List;

public class NoneMultiActivity extends Activity implements OnClickListener {
	private GridView gridView;
	private List<String> videoPathList;
	private List<String> imageList;
	ArrayList<String> filelist;
	protected ImageLoader imageLoader;
	DisplayImageOptions options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(HybridResourceManager.getInstance().getLayoutId("activity_image"));
		filelist = new ArrayList<String>();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		initView();
	}

	private void initView() {
		gridView = (GridView) findViewById(HybridResourceManager.getInstance().getId("gridView"));
		Util util = new Util(this);
		videoPathList = util.getVideoPath();
		imageList = util.listAlldir();
		NoneMultiAdapter adapter = new NoneMultiAdapter(this, videoPathList, imageList,onItemClickClass);
		gridView.setAdapter(adapter);
		
		Button back = (Button) findViewById(HybridResourceManager.getInstance().getId("back"));
		back.setOnClickListener(this);
		Button confirm = (Button) findViewById(HybridResourceManager.getInstance().getId("confirm"));
		confirm.setOnClickListener(this);
		
	}
	
	NoneMultiAdapter.OnItemClickClass onItemClickClass = new NoneMultiAdapter.OnItemClickClass() {
		@Override
		public void OnItemClick(View v, int Position, CheckBox checkBox) {
			imageList.addAll(videoPathList);
			String filapath = imageList.get(Position);
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				filelist.remove(filapath);
			} else {
				try {
					checkBox.setChecked(true);
					Log.i("img", "video choise position->" + Position);
					filelist.add(filapath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	@Override
	public void onClick(View v) {
		if (v.getId() == HybridResourceManager.getInstance().getId("back")) {
			finish();
		} else if (v.getId() == HybridResourceManager.getInstance().getId("confirm")) {
			Intent intent = new Intent();
			intent.putStringArrayListExtra("filelist", filelist);
			setResult(4, intent);
			finish();
		}
	}
}
