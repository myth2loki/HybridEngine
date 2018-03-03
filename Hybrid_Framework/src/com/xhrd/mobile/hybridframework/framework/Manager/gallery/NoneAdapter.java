package com.xhrd.mobile.hybridframework.framework.Manager.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhrd.mobile.hybridframework.R;
import com.xhrd.mobile.hybridframework.engine.RDResourceManager;

import java.util.Collections;
import java.util.List;

public class NoneAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<String> imageList;
	private List<String> videoList;
	private Bitmap bitmaps[];
	private Util util;
	private List<String> totalList;

	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(RDResourceManager.getInstance().getDrawableId("ic_rd_launcher"))
			.showImageOnFail(RDResourceManager.getInstance().getDrawableId("ic_rd_launcher"))
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	public NoneAdapter(Context mContext, List<String> imageList, List<String> videoList){
		this.mContext = mContext;
		this.imageList = imageList;
		this.videoList = videoList;
		util = new Util(mContext);
		totalList = util.getCountPath(imageList, videoList);
		Collections.reverse(totalList);
		bitmaps = new Bitmap[totalList.size()];
	}
	public NoneAdapter(Context mContext, List<String> imageList){
		this.mContext = mContext;
		this.imageList = imageList;
		this.totalList = imageList;
		util = new Util(mContext);
		bitmaps = new Bitmap[totalList.size()];
	}

	@Override
	public int getCount() {
		return totalList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Holder holder = null;
//		if (convertView == null) {
//			convertView = LayoutInflater.from(mContext).inflate(RDResourceManager.getInstance().getLayoutId("none_item"), parent, null);
////			convertView = LayoutInflater.from(mContext).inflate(R.layout.none_item, null);
//			holder = new Holder();
//			holder.imageView = (ImageView) convertView.findViewById(RDResourceManager.getInstance().getId("image"));
////			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
//			convertView.setTag(holder);
//		} else {
//			holder = (Holder)convertView.getTag();
//		}

		final ImageView imageView;
		if (convertView == null) {
			//imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.none_item, parent, false);
			imageView = (ImageView)LayoutInflater.from(mContext).inflate(RDResourceManager.getInstance().getLayoutId("none_item"),parent,false);
		} else {
			imageView = (ImageView) convertView;
		}

		//显示图片的配置
		ImageLoader.getInstance().displayImage("file:///" + totalList.get(position), imageView, options);

//		imageList.addAll(videoList);
//		if (totalList.get(position).endsWith("jpg") || totalList.get(position).endsWith("jpeg") || totalList.get(position).endsWith("png") || totalList.get(position).endsWith("bmp")) {
//			if (bitmaps[position] == null) {
//				util.imgExcute(holder.imageView,new ImgClallBackLisner(position), totalList.get(position));
//			} else {
//				holder.imageView.setImageBitmap(bitmaps[position]);
//			}
//		} else {
//			if (bitmaps[position] == null) {
//				util.videoExcute(holder.imageView,new ImgClallBackLisner(position), totalList.get(position));
//			}else {
//				holder.imageView.setImageBitmap(bitmaps[position]);
//			}
//		}
		return imageView;
	}
	
//	class Holder{
//		ImageView imageView;
//	}
//
//	public class ImgClallBackLisner implements ImgCallBack{
//		int num;
//		public ImgClallBackLisner(int num) {
//			this.num=num;
//		}
//
//		//@Override
//		public void resultImgCall(ImageView imageView, Bitmap bitmap) {
//			bitmaps[num] = bitmap;
//			imageView.setImageBitmap(bitmap);
//		}
//	}

}
