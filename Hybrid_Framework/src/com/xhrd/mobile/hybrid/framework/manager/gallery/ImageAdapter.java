package com.xhrd.mobile.hybrid.framework.manager.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.xhrd.mobile.hybrid.engine.RDResourceManager;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<String> imageList;
	private Bitmap bitmaps[];
	private Util util;
	private OnItemClickClass onItemClickClass;
	public ImageAdapter(Context mContext,List<String> imageList,OnItemClickClass onItemClickClass){
		this.mContext = mContext;
		this.imageList = imageList;
		bitmaps = new Bitmap[imageList.size()];
		util = new Util(mContext);
		this.onItemClickClass = onItemClickClass;
	}

	@Override
	public int getCount() {
		return imageList.size();
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
		if (bitmaps[position] == null) {
			util.imgExcute(holder.imageView,new ImgClallBackLisner(position), imageList.get(position));
		} else {
			holder.imageView.setImageBitmap(bitmaps[position]);
		}
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
			bitmaps[num] = bitmap;
			imageView.setImageBitmap(bitmap);
		}
	}
	
	public interface OnItemClickClass{
		public void OnItemClick(View v,int Position,CheckBox checkBox);
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
			if (imageList!=null && onItemClickClass!=null ) {
				onItemClickClass.OnItemClick(v, position, checkBox);
			}
		}
	}
}
