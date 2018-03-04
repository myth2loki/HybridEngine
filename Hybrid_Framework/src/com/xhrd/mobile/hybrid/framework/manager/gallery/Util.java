package com.xhrd.mobile.hybrid.framework.manager.gallery;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

public class Util {

	public static Context context;

	public Util(Context context) {
		this.context = context;
	}

	/**
	 * 获取全部图片地址
	 * 
	 * @return
	 */
	public List<String> listAlldir() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Uri uri = intent.getData();
		List<String> list = new ArrayList<String>();
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, null,
				null, null);// managedQuery(uri, proj, null, null, null);
		while (cursor.moveToNext()) {
			String path = cursor.getString(0);
			list.add(new File(path).getAbsolutePath());
		}
		return list;
	}

	public List<String> LocalImgFileList() {
		List<String> allimglist = listAlldir();
		return allimglist;
	}

	/**
	 * 获取视频的路径
	 * @return
	 */
	public static List<String> getVideoList(){
		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		Uri uri = intent.getData();
		List<String> list = new ArrayList<String>();
		String[] proj = {MediaStore.Video.Media.DATA};
		Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
		while (cursor.moveToNext()) {
			String path = cursor.getString(0);
			list.add(new File(path).getAbsolutePath());
		}
		return list;
	}
	
	public String getfileinfo(String data) {
		String filename[] = data.split("/");
		if (filename != null) {
			return filename[filename.length - 2];
		}
		return null;
	}

	// 显示原生图片尺寸大小
	public Bitmap getPathBitmap(Uri imageFilePath, int dw, int dh)
			throws FileNotFoundException {
		// 获取屏幕的宽和高
		/**
		 * 为了计算缩放的比例，我们需要获取整个图片的尺寸，而不是图片
		 * BitmapFactory.Options类中有一个布尔型变量inJustDecodeBounds，将其设置为true
		 * 这样，我们获取到的就是图片的尺寸，而不用加载图片了。
		 * 当我们设置这个值的时候，我们接着就可以从BitmapFactory.Options的outWidth和outHeight中获取到值
		 */
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		// 由于使用了MediaStore存储，这里根据URI获取输入流的形式
		Bitmap pic = BitmapFactory.decodeStream(context.getContentResolver()
				.openInputStream(imageFilePath), null, op);

		int wRatio = (int) Math.ceil(op.outWidth / (float) dw); // 计算宽度比例
		int hRatio = (int) Math.ceil(op.outHeight / (float) dh); // 计算高度比例

		/**
		 * 接下来，我们就需要判断是否需要缩放以及到底对宽还是高进行缩放。 如果高和宽不是全都超出了屏幕，那么无需缩放。
		 * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》 这需要判断wRatio和hRatio的大小
		 * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。 缩放使用的还是inSampleSize变量
		 */
		if (wRatio > 1 && hRatio > 1) {
			if (wRatio > hRatio) {
				op.inSampleSize = wRatio;
			} else {
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false; // 注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了
		pic = BitmapFactory.decodeStream(context.getContentResolver()
				.openInputStream(imageFilePath), null, op);

		return pic;
	}

	public void imgExcute(ImageView imageView, ImgCallBack icb,
			String... params) {
		LoadBitAsynk loadBitAsynk = new LoadBitAsynk(imageView, icb);
		loadBitAsynk.execute(params);
	}

	public class LoadBitAsynk extends AsyncTask<String, Integer, Bitmap> {

		ImageView imageView;
		ImgCallBack icb;

		LoadBitAsynk(ImageView imageView, ImgCallBack icb) {
			this.imageView = imageView;
			this.icb = icb;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			try {
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						bitmap = getPathBitmap(
								Uri.fromFile(new File(params[i])), 200, 200);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				// imageView.setImageBitmap(result);
				icb.resultImgCall(imageView, result);
			}
		}

	}
	
	public void videoExcute(ImageView imageView, ImgCallBack icb,
			String... params) {
		VideoBitAsynk loadBitAsynk = new VideoBitAsynk(imageView, icb);
		loadBitAsynk.execute(params);
	}

	public class VideoBitAsynk extends AsyncTask<String, Integer, Bitmap> {

		ImageView imageView;
		ImgCallBack icb;

		VideoBitAsynk(ImageView imageView, ImgCallBack icb) {
			this.imageView = imageView;
			this.icb = icb;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			try {
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						bitmap = getVideoThumbnail(params[i], 60, 60,  
							    MediaStore.Images.Thumbnails.MICRO_KIND); 
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				// imageView.setImageBitmap(result);
				icb.resultImgCall(imageView, result);
			}
		}

	}

	/**
	 * 获取本地视频路径
	 * @return
	 */
	public List<String> getVideoPath() {
		List<String> list = null;
		if (context != null) {
			Cursor cursor = context.getContentResolver().query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (cursor != null) {
				list = new ArrayList<String>();
				while (cursor.moveToNext()) {
					String path = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
					list.add(path);
				}
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * 获取视频的缩略图
	 * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
	 * @param videoPath 视频的路径 
	 * @param width 指定输出视频缩略图的宽度 
	 * @param height 指定输出视频缩略图的高度度 
	 * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
	 * @return	指定大小的视频缩略图 
	 */
	public Bitmap getVideoThumbnail(String videoPath, int width, int height,  int kind) {
		Bitmap bitmap = null;  
		  // 获取视频的缩略图  
		  bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
		  System.out.println("w"+bitmap.getWidth());  
		  System.out.println("h"+bitmap.getHeight());  
		  bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
		    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
		  return bitmap;  
	}

	/**
	 * 合并集合
	 * @param imageList
	 * @param videoList
	 * @return
	 */
	public List<String> getCountPath(List<String> imageList, List<String> videoList){
		List<String> totalList = new ArrayList<String>();
		for (int i = 0; i < imageList.size(); i++){
			totalList.add(imageList.get(i));
		}
		for (int j = 0; j < videoList.size(); j++){
			totalList.add(videoList.get(j));
		}
		return totalList;
	}

}
