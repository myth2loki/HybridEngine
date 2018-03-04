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

import com.xhrd.mobile.hybrid.engine.RDResourceManager;
import com.xhrd.mobile.hybrid.framework.manager.gallery.ImageAdapter.OnItemClickClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageActivity extends Activity implements OnClickListener {
	private GridView gridView;
	ArrayList<String> filelist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(RDResourceManager.getInstance().getLayoutId("activity_image"));
		initView();

		initData();
	}

	private void initView() {
		gridView = (GridView) findViewById(RDResourceManager.getInstance().getId("gridView"));
		back = (Button) findViewById(RDResourceManager.getInstance().getId("back"));
		back.setOnClickListener(this);
		confirm = (Button) findViewById(RDResourceManager.getInstance().getId("confirm"));
		confirm.setOnClickListener(this);
		filelist = new ArrayList<String>();

	}

	private void initData() {
		Util util = new Util(this);
		imageList = util.LocalImgFileList();
		Collections.reverse(imageList);
		ImageAdapter adapter = new ImageAdapter(this, imageList,
				onItemClickClass);
		gridView.setAdapter(adapter);

	}

	ImageAdapter.OnItemClickClass onItemClickClass = new OnItemClickClass() {
		@Override
		public void OnItemClick(View v, int Position, CheckBox checkBox) {
			String filapath = imageList.get(Position);
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				filelist.remove(filapath);
			} else {
				try {
					checkBox.setChecked(true);
					Log.i("img", "img choise position->" + Position);
					filelist.add(filapath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	private List<String> imageList;
	private Button back;
	private Button confirm;

	@Override
	public void onClick(View v) {
		if (v.getId() == RDResourceManager.getInstance().getId("back")){
			finish();
		}else if (v.getId() == RDResourceManager.getInstance().getId("confirm")){
			Intent intent = new Intent();
			intent.putStringArrayListExtra("filelist", filelist);
			setResult(4, intent);
			finish();
		}
//		switch (v.getId()) {
//		case RDResourceManager.getInstance().getId("back"):
//			finish();
//			break;
//		case R.id.confirm:
//			Intent intent = new Intent();
//			intent.putStringArrayListExtra("filelist", filelist);
//			setResult(4, intent);
//			finish();
//		default:
//			break;
//		}
	}
}
