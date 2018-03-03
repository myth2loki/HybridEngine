package com.xhrd.mobile.hybridframework.framework.Manager.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xhrd.mobile.hybridframework.R;
import com.xhrd.mobile.hybridframework.engine.RDResourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoneActivity extends Activity {
	private GridView gridView;
	private String filter;
	protected ImageLoader imageLoader;
	DisplayImageOptions options;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(RDResourceManager.getInstance().getLayoutId("activity_none_image"));
		filter = getIntent().getStringExtra("filter");
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(NoneActivity.this));
		initView();
		
	}

	private void initView() {
		gridView = (GridView) findViewById(RDResourceManager.getInstance().getId("grid"));
		final Util util = new Util(this);
		final List<String> imageList = util.listAlldir();
		final List<String> videoList = util.getVideoPath();
		NoneAdapter adapter = null;
		if ("image".equals(filter) || "video".equals(filter)){
			Collections.reverse(imageList);
			adapter = new NoneAdapter(this, imageList);
		} else if ("none".equals(filter)){
			adapter = new NoneAdapter(this, imageList, videoList);
		}

		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//imageList.addAll(videoList);
				List<String> totalList = util.getCountPath(imageList, videoList);
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				if ("image".equals(filter)){
					bundle.putString("path", imageList.get(position));
				} else if ("video".equals(filter)){
					bundle.putString("path", videoList.get(position));
				} else if ("none".equals(filter)){
					bundle.putString("path", totalList.get(position));
				}
				resultIntent.putExtras(bundle);
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});
	}
}
