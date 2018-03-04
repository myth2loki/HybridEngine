package com.xhrd.mobile.hybrid.framework.manager.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;

import com.xhrd.mobile.hybrid.engine.RDResourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoActivity extends Activity implements OnClickListener {
	private GridView gridView;
	ArrayList<String> filelist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(RDResourceManager.getInstance().getLayoutId("activity_image"));
		filelist = new ArrayList<String>();
		initView();
	}

	private void initView() {
		gridView = (GridView) findViewById(RDResourceManager.getInstance().getId("gridView"));
		Util util = new Util(this);
		videoPathList = util.getVideoPath();
		Collections.reverse(videoPathList);
		VideoAdapter adapter = new VideoAdapter(this,videoPathList,onItemClickClass);
		gridView.setAdapter(adapter);
		
		Button back = (Button) findViewById(RDResourceManager.getInstance().getId("back"));
		back.setOnClickListener(this);
		Button confirm = (Button) findViewById(RDResourceManager.getInstance().getId("confirm"));
		confirm.setOnClickListener(this);
	}
	
	VideoAdapter.OnItemClickClass onItemClickClass = new VideoAdapter.OnItemClickClass() {
		@Override
		public void OnItemClick(View v, int Position, CheckBox checkBox) {
			String filapath = videoPathList.get(Position);
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
	private List<String> videoPathList;

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.back:
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
