package com.guangjun.timegrow;

import static com.guangjun.timegrow.DBUtil.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Android手指拍照
 * 
 * @author wwj
 * @date 2013/4/29
 */
public class CameraActivity extends Activity {
	private View layout;
	private View layoutokcancel;
	private Camera camera = null;
	// private Camera.Parameters parameters = null;

	static String lastPicName = null;

	Bundle bundle = null; // 声明一个Bundle对象，用来存储数据
	static String alastfile;
	static String aname;
	static String aadddate;
	static int asize;
	static int aid;
	
	SurfaceHolder holder;

	ImageView image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 显示界面
		setContentView(R.layout.take_picture);

		Intent intent = getIntent();
		Bundle bd = intent.getExtras();
		alastfile = bd.getString("lastfile");
		asize = bd.getInt("size");
		aid = bd.getInt("id");
		aname = bd.getString("name");
		aadddate = bd.getString("adddate");
		
		image = (ImageView) findViewById(R.id.scalePic);

		if ("" != alastfile) {
			Bitmap bm = BitmapFactory.decodeFile(alastfile);
			image.setImageBitmap(bm);
			image.setImageAlpha(100);
		}
//		else{
//			image.setImageDrawable(getResources().getDrawable(
//					R.drawable.ic_launcher));
//		}

		layout = this.findViewById(R.id.buttonLayout);
		layoutokcancel = this.findViewById(R.id.buttonLayout2);

		SurfaceView surfaceView = (SurfaceView) this
				.findViewById(R.id.surfaceView);
		holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(176, 144); // 设置Surface分辨率
		holder.setKeepScreenOn(true);// 屏幕常亮
		holder.addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数

	}

	/**
	 * 按钮被点击触发的事件
	 * 
	 * @param v
	 */
	public void btnOnclick(View v) {
		if (camera != null) {
			switch (v.getId()) {
			case R.id.takepicture:
				// 拍照
				camera.takePicture(null, null, new MyPictureCallback());
//				if (null != lastPicName) {
//					Bitmap bm = BitmapFactory.decodeFile(lastPicName);
//					// image.setImageDrawable(getResources().getDrawable(R.drawable.message));
//					image.setImageBitmap(bm);
//					image.setImageAlpha(100);
//				}
				break;
			}
		}
	}

	// /**
	// * 图片被点击触发的时间
	// *
	// * @param v
	// */
	// public void imageClick(View v) {
	// if (v.getId() == R.id.scalePic) {
	// if (bundle == null) {
	// Toast.makeText(getApplicationContext(), "no picture",
	// Toast.LENGTH_SHORT).show();
	// } else {
	// Intent intent = new Intent(this, ShowPicActivity.class);
	// intent.putExtras(bundle);
	// startActivity(intent);
	// }
	// }
	// }

	private final class MyPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(final byte[] data, final Camera camera) {
			
//			final byte[] tmpdata = data;
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			image.setImageBitmap(bm);
			image.setImageAlpha(0);
			layout.setVisibility(ViewGroup.GONE);
			layoutokcancel.setVisibility(ViewGroup.VISIBLE);
			
			Button btnok = (Button)findViewById(R.id.btn_ok_pic);
			btnok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						bundle = new Bundle();
						bundle.putByteArray("bytes", data); // 将图片字节数据保存在bundle当中，实现数据交换
						saveToDBandSDCard(data); // 保存图片到sd卡中
						Toast.makeText(getApplicationContext(), "success",
								Toast.LENGTH_SHORT).show();
//						camera.startPreview();
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			});

			Button btncencel = (Button)findViewById(R.id.btn_cancel_pic);
			btncencel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					camera.startPreview(); // 拍完照后，重新开始预览
					Bitmap bm = BitmapFactory.decodeFile(alastfile);
					image.setImageBitmap(bm);
					image.setImageAlpha(100);
					layoutokcancel.setVisibility(ViewGroup.GONE);
					layout.setVisibility(ViewGroup.VISIBLE);
				}
			});
			
		}
	}

	/**
	 * 将拍下来的照片存放在SD卡中
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void saveToDBandSDCard(byte[] data) throws IOException {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
		String filename = format.format(date) + ".jpg";
		File fileFolder = new File(Environment.getExternalStorageDirectory()
				+ "/timegrow/" + aname + "/");
		if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
			fileFolder.mkdirs();
		}
		File jpgFile = new File(fileFolder, filename);
		FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
		outputStream.write(data); // 写入sd卡中
		outputStream.close(); // 关闭输出流

		alastfile = fileFolder.toString() + "/" + filename;
		asize++;
		Album album = new Album(aid, aname, asize, alastfile, aadddate);
//		updateAlbum(CameraActivity.this,album);

		Picture picture = new Picture();
		picture.setAlbumid(aid);
		picture.setDatetime(format.format(date));
		picture.setFilename(alastfile);
//		insertPicture(CameraActivity.this, picture);
		
	}

	private final class SurfaceCallback implements Callback {

		// 拍照状态变化时调用该方法
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			// NullPointerException因为没有添加Camera权限
			// Log.i("info", camera.toString());
			//
			// camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
			// camera.startPreview(); // 开始预览
			Camera.Parameters parameters = camera.getParameters();
			//
			// Log.i("info", parameters.toString());
			// // parameters = camera.getParameters(); // 获取各项参数
			parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
			parameters.setPreviewSize(width, height); // 设置预览大小
			parameters.setPreviewFrameRate(5); // 设置每秒显示4帧
			parameters.setPictureSize(width, height); // 设置保存的图片尺寸
			parameters.setJpegQuality(80); // 设置照片质量

			camera.setParameters(parameters);
		}

		// 开始拍照时调用该方法
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open(); // 打开摄像头
				camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
				camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
				camera.startPreview(); // 开始预览
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// 停止拍照时调用该方法
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.release(); // 释放照相机
				camera = null;
			}
		}
	}

	/**
	 * 点击手机屏幕是，显示两个按钮
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			layout.setVisibility(ViewGroup.VISIBLE); // 设置视图可见
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮
			if (camera != null && event.getRepeatCount() == 0) {
				// 拍照
				// 注：调用takePicture()方法进行拍照是传入了一个PictureCallback对象——当程序获取了拍照所得的图片数据之后
				// ，PictureCallback对象将会被回调，该对象可以负责对相片进行保存或传入网络
				camera.takePicture(null, null, new MyPictureCallback());
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	public static int getPreviewDegree(Activity activity) {
		// 获得手机的方向
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		}
		return degree;
	}
}
