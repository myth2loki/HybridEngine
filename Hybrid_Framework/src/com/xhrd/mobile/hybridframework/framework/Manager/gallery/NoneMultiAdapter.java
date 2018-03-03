package com.xhrd.mobile.hybridframework.framework.Manager.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhrd.mobile.hybridframework.R;
import com.xhrd.mobile.hybridframework.engine.RDResourceManager;

import java.util.Collections;
import java.util.List;

public class NoneMultiAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> videoPathList;
	private List<String> imageList;
	private OnItemClickClass onItemClickClass;
	private Bitmap bitmaps[];
	private Util util;
	private List<String> totalList;
	public NoneMultiAdapter(Context mContext, List<String> videoPathList, List<String> imageList, OnItemClickClass onItemClickClass){
		this.mContext = mContext;
		this.videoPathList = videoPathList;
		this.imageList = imageList;
		this.onItemClickClass = onItemClickClass;
		bitmaps = new Bitmap[videoPathList.size()];
		util = new Util(mContext);
		totalList = util.getCountPath(imageList, videoPathList);
		Collections.reverse(totalList);
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(RDResourceManager.getInstance().getLayoutId("imgsitem"), null);
			holder = new Holder();
			holder.imageView = (ImageView) convertView.findViewById(RDResourceManager.getInstance().getId("imageView"));
			holder.checkBox = (CheckBox) convertView.findViewById(RDResourceManager.getInstance().getId("checkBox"));
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}

		//显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(RDResourceManager.getInstance().getDrawableId("ic_rd_launcher"))
				.showImageOnFail(RDResourceManager.getInstance().getDrawableId("ic_rd_launcher"))
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		ImageLoader.getInstance().displayImage("file:///" + totalList.get(position), holder.imageView, options);

//		imageList.addAll(videoPathList);
//		if (imageList.get(position).endsWith("jpg") || imageList.get(position).endsWith("jpeg") || imageList.get(position).endsWith("png") || imageList.get(position).endsWith("bmp")) {
//			if (bitmaps[position] == null) {
//				util.imgExcute(holder.imageView,new ImgClallBackLisner(position), imageList.get(position));
//			} else {
//				holder.imageView.setImageBitmap(bitmaps[position]);
//			}
//		} else {
//			if (bitmaps[position] == null) {
//				util.videoExcute(holder.imageView,new ImgClallBackLisner(position), videoPathList.get(position));
//			}else {
//				holder.imageView.setImageBitmap(bitmaps[position]);
//			}
//		}
		convertView.setOnClickListener(new OnPhotoClick(position, holder.checkBox));
		return convertView;
	}
	
	class Holder{
		ImageView imageView;
		CheckBox checkBox;
	}
	
	public class ImgClallBackLisner implements ImgCallBack{
		int num;
		public ImgClallBackLisner(int num) {
			this.num=num;
		}
		
		@Override
		public void resultImgCall(ImageView imageView, Bitmap bitmap) {
			bitmaps[num]=bitmap;
			imageView.setImageBitmap(bitmap);
		}
	}

	public interface OnItemClickClass{
		public void OnItemClick(View v, int Position, CheckBox checkBox);
	}
	
	class OnPhotoClick implements OnClickListener{
		int position;
		CheckBox checkBox;
		
		public OnPhotoClick(int position,CheckBox checkBox) {
			this.position=position;
			this.checkBox=checkBox;
		}
		@Override
		public void onClick(View v) {
			if (videoPathList!=null && imageList!=null && onItemClickClass!=null ) {
				onItemClickClass.OnItemClick(v, position, checkBox);
			}
		}
	}
}
