package com.hongbao5656.util;
/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.easemob.util.PathUtil;
import com.example.aaron.library.MLog;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
	//	public static String getThumbnailImagePath(String imagePath) {
	//		String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
	//		path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
	//		MLog.i("msg", "original image path:" + imagePath);
	//		MLog.i("msg", "thum image path:" + path);
	//		return path;
	//	}

	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ imageName;
		MLog.i("msg", "image path:" + path);
		return path;

	}


	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
		MLog.i("msg", "thum image path:" + path);
		return path;
	}

	public static int getSampleBitmap(byte[] in,int defaultW,int defualtH){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(in, 0, in.length, options);
		int bitmapW = options.outWidth;
		int bitmapH = options.outHeight;
		int sample = 1;
		while((bitmapW/defaultW > sample) || (bitmapH/defualtH > sample)){
			sample *= 2;
		}
		return sample;
	}

	public static Bitmap subSampleBitmap(byte[] in,int sample){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sample;
		Bitmap bitmap = BitmapFactory.decodeByteArray(in, 0, in.length, options);
		return bitmap;
	}

	/**
	 * 拍照获取图片
	 */
	public static final int GET_IMAGE_BY_CAMERA = 5001;
	/**
	 * 手机相册获取图片
	 */
	public static final int GET_IMAGE_FROM_PHONE = 5002;
	/**
	 * 裁剪图片后结果
	 */
	public static final int CROP_IMAGE = 5003;
	/**
	 * 创建一条图片地址uri,用于保存拍照后的照片
	 */
	public static Uri imageUriFromCamera;
	public static Uri cropImageUri;

	public static void openCameraImage(final Fragment activity) {
		ImageUtils.imageUriFromCamera = ImageUtils.createImagePathUri(activity);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// MediaStore.EXTRA_OUTPUT参数不设置时,系统会自动生成一个uri,但是只会返回一个缩略图
		// 返回图片在onActivityResult中通过以下代码获取
		// Bitmap bitmap = (Bitmap) data.getExtras().get("data"); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
		activity.startActivityForResult(intent, ImageUtils.GET_IMAGE_BY_CAMERA);
	}

	public static void openCameraImage(final Activity activity) {
		ImageUtils.imageUriFromCamera = ImageUtils.createImagePathUri(activity);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
		activity.startActivityForResult(intent, ImageUtils.GET_IMAGE_BY_CAMERA);
	}
	public static void openLocalImage(final Fragment activity) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, ImageUtils.GET_IMAGE_FROM_PHONE);
	}

	/**
	 * 对图片进行裁剪
	 * @param fragment
	 * @param srcUri
	 */
	public static void cropImage(Fragment activity, Uri srcUri) {
		ImageUtils.cropImageUri = ImageUtils.createImagePathUri(activity);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(srcUri, "image/*");
		intent.putExtra("crop", "true");
		////////////////////////////////////////////////////////////////
		// 1.宽高和比例都不设置时,裁剪框可以自行调整(比例和大小都可以随意调整)
		////////////////////////////////////////////////////////////////
		// 2.只设置裁剪框宽高比(aspect)后,裁剪框比例固定不可调整,只能调整大小
		////////////////////////////////////////////////////////////////
		// 3.裁剪后生成图片宽高(output)的设置和裁剪框无关,只决定最终生成图片大小
		////////////////////////////////////////////////////////////////
		// 4.裁剪框宽高比例(aspect)可以和裁剪后生成图片比例(output)不同,此时,
		//	会以裁剪框的宽为准,按照裁剪宽高比例生成一个图片,该图和框选部分可能不同,
		//  不同的情况可能是截取框选的一部分,也可能超出框选部分,向下延伸补足
		////////////////////////////////////////////////////////////////
		// aspectX aspectY 是裁剪框宽高的比例
		//	intent.putExtra("aspectX", 1);
		//	intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪后生成图片的宽高
		//	intent.putExtra("outputX", 300);
		//	intent.putExtra("outputY", 300);

		// return-data为true时,会直接返回bitmap数据,但是大图裁剪时会出现问题,推荐下面为false时的方式
		// return-data为false时,不会返回bitmap,但需要指定一个MediaStore.EXTRA_OUTPUT保存图片uri
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.cropImageUri);
		intent.putExtra("return-data", false);
		intent.putExtra("scale",true);
		intent.putExtra("scaleUpIfNeeded", true);//防止黑边
		activity.startActivityForResult(intent, CROP_IMAGE);
	}

	/**
	 * 对图片进行裁剪
	 * @param activity
	 * @param srcUri
	 */
	public static void cropImage(Activity activity, Uri srcUri) {
		ImageUtils.cropImageUri = ImageUtils.createImagePathUri(activity);

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(srcUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.cropImageUri);
		intent.putExtra("return-data", false);
		intent.putExtra("scale",true);
		intent.putExtra("scaleUpIfNeeded", true);//防止黑边
		activity.startActivityForResult(intent, CROP_IMAGE);
	}

	/**
	 * 创建一条图片地址uri,用于保存拍照后的照片
	 *
	 * @param context
	 * @return 图片的uri
	 */
	public static Uri createImagePathUri(Fragment context) {
		Uri imageFilePath = null;
		String status = Environment.getExternalStorageState();
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
		long time = System.currentTimeMillis();
		String imageName = timeFormatter.format(new Date(time));
		// ContentValues是我们希望这条记录被创建时包含的数据信息
		ContentValues values = new ContentValues(3);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
		values.put(MediaStore.Images.Media.DATE_TAKEN, time);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
			imageFilePath = context.getActivity().getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		} else {
			imageFilePath = context.getActivity().getContentResolver().insert(
					MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
		}
		Log.i("", "生成的照片输出路径：" + imageFilePath.toString());
		return imageFilePath;
	}

	/**
	 * 创建一条图片地址uri,用于保存拍照后的照片
	 * @param context
	 * @return 图片的uri
	 */
	public static Uri createImagePathUri(Activity context) {
		Uri imageFilePath = null;
		String status = Environment.getExternalStorageState();
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
		long time = System.currentTimeMillis();
		String imageName = timeFormatter.format(new Date(time));
		// ContentValues是我们希望这条记录被创建时包含的数据信息
		ContentValues values = new ContentValues(3);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
		values.put(MediaStore.Images.Media.DATE_TAKEN, time);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
			imageFilePath = context.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		} else {
			imageFilePath = context.getContentResolver().insert(
					MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
		}
		Log.i("", "生成的照片输出路径：" + imageFilePath.toString());
		return imageFilePath;
	}

	/**
	 * 删除图片缩略图
	 *
	 * @param context
	 * @return 图片的uri
	 */
	public static void deleteImagePathUri(Activity context,Uri imageFilePath) {
		context.getContentResolver().delete(imageFilePath, "", null);
	}

	public static Bitmap getBitmapFromUri(Fragment context,Uri uri){
		try{
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getActivity().getContentResolver(), uri);
			return bitmap;
		} catch (Exception e){
			Log.e("[Android]", e.getMessage());
			Log.e("[Android]", "目录为：" + uri);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从URI获取本地路径
	 *
	 * @param selectedVideoUri
	 * @param contentResolver
	 * @return
	 */
	public static  String getImagePathFromUri(Activity activity,Uri contentUri) {
		//如果是对媒体文件，在android开机的时候回去扫描，然后把路径添加到数据库中。
		//由打印的contentUri可以看到：2种结构。正常的是：content://那么这种就要去数据库读取path。
		//另外一种是Uri是 file:///那么这种是 Uri.fromFile(File file);得到的 
		String[] projection = { MediaStore.Images.Media.DATA };
		String urlpath;
		CursorLoader loader = new CursorLoader(activity,contentUri, projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		try
		{
			int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			urlpath =cursor.getString(column_index);
			//如果是正常的查询到数据库。然后返回结构
			return urlpath;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		//如果是文件。Uri.fromFile(File file)生成的uri。那么下面这个方法可以得到结果
		urlpath = contentUri.getPath();
		return urlpath;
	}

	public static void deleteCameraCrop(Activity activity){
		if(ImageUtils.imageUriFromCamera != null ){
			File file = new File(getImagePathFromUri(activity, ImageUtils.imageUriFromCamera ) );
			if(file.exists()){
				file.delete();
			}
			ImageUtils.deleteImagePathUri(activity, ImageUtils.imageUriFromCamera);

		}
		if(ImageUtils.cropImageUri != null ){
			File file = new File(getImagePathFromUri(activity, ImageUtils.cropImageUri ) );
			if(file.exists()){
				file.delete();
			}
			ImageUtils.deleteImagePathUri(activity, ImageUtils.cropImageUri);
		}

	}

	public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	public static final int PHOTO_REQUEST_CUT = 3;// 结果

	/**
	 * 打开相册
	 */
	public static void openAlbum(Activity activity) {
		Intent intent = new Intent();
		/* 开启Pictures画面Type设定为image */
		intent.setType("image/*");
		/* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_GET_CONTENT);
		/* 取得相片后返回本画面 */
		activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/**
	 * 裁剪图片
	 *
	 * @param activity
	 * @param uri
	 * @param size
	 */
	public static void startPhotoZoom(Activity activity, Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/**
	 * 根据InputStream获取图片实际的宽度和高度
	 *
	 * @param imageStream
	 * @return
	 */
	public static ImageSize getImageSize(InputStream imageStream) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(imageStream, null, options);
		return new ImageSize(options.outWidth, options.outHeight);
	}

	public static class ImageSize {
		int width;
		int height;

		public ImageSize() {
		}

		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString() {
			return "ImageSize{" + "width=" + width + ", height=" + height + '}';
		}
	}

	public static int calculateInSampleSize(ImageSize srcSize,
											ImageSize targetSize) {
		// 源图片的宽度
		int width = srcSize.width;
		int height = srcSize.height;
		int inSampleSize = 1;

		int reqWidth = targetSize.width;
		int reqHeight = targetSize.height;

		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) height / (float) reqHeight);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 根据ImageView获适当的压缩的宽和高
	 *
	 * @param view
	 * @return
	 */
	public static ImageSize getImageViewSize(View view) {

		ImageSize imageSize = new ImageSize();

		imageSize.width = getExpectWidth(view);
		imageSize.height = getExpectHeight(view);

		return imageSize;
	}

	/**
	 * 根据view获得期望的高度
	 *
	 * @param view
	 * @return
	 */
	private static int getExpectHeight(View view) {

		int height = 0;
		if (view == null)
			return 0;

		final ViewGroup.LayoutParams params = view.getLayoutParams();
		// 如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
		if (params != null
				&& params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
			height = view.getWidth(); // 获得实际的宽度
		}
		if (height <= 0 && params != null) {
			height = params.height; // 获得布局文件中的声明的宽度
		}

		if (height <= 0) {
			height = getImageViewFieldValue(view, "mMaxHeight");// 获得设置的最大的宽度
		}

		// 如果宽度还是没有获取到，憋大招，使用屏幕的宽度
		if (height <= 0) {
			DisplayMetrics displayMetrics = view.getContext().getResources()
					.getDisplayMetrics();
			height = displayMetrics.heightPixels;
		}

		return height;
	}

	/**
	 * 根据view获得期望的宽度
	 *
	 * @param view
	 * @return
	 */
	private static int getExpectWidth(View view) {
		int width = 0;
		if (view == null)
			return 0;

		final ViewGroup.LayoutParams params = view.getLayoutParams();
		// 如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
		if (params != null
				&& params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
			width = view.getWidth(); // 获得实际的宽度
		}
		if (width <= 0 && params != null) {
			width = params.width; // 获得布局文件中的声明的宽度
		}

		if (width <= 0)

		{
			width = getImageViewFieldValue(view, "mMaxWidth");// 获得设置的最大的宽度
		}
		// 如果宽度还是没有获取到，憋大招，使用屏幕的宽度
		if (width <= 0)

		{
			DisplayMetrics displayMetrics = view.getContext().getResources()
					.getDisplayMetrics();
			width = displayMetrics.widthPixels;
		}

		return width;
	}

	/**
	 * 通过反射获取imageview的某个属性值
	 *
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;

	}



}
