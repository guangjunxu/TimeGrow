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
 * Android��ָ����
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

	Bundle bundle = null; // ����һ��Bundle���������洢����
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
		// ��ʾ����
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
		holder.setFixedSize(176, 144); // ����Surface�ֱ���
		holder.setKeepScreenOn(true);// ��Ļ����
		holder.addCallback(new SurfaceCallback());// ΪSurfaceView�ľ�����һ���ص�����

	}

	/**
	 * ��ť������������¼�
	 * 
	 * @param v
	 */
	public void btnOnclick(View v) {
		if (camera != null) {
			switch (v.getId()) {
			case R.id.takepicture:
				// ����
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
	// * ͼƬ�����������ʱ��
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
						bundle.putByteArray("bytes", data); // ��ͼƬ�ֽ����ݱ�����bundle���У�ʵ�����ݽ���
						saveToDBandSDCard(data); // ����ͼƬ��sd����
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
					camera.startPreview(); // �����պ����¿�ʼԤ��
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
	 * ������������Ƭ�����SD����
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void saveToDBandSDCard(byte[] data) throws IOException {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // ��ʽ��ʱ��
		String filename = format.format(date) + ".jpg";
		File fileFolder = new File(Environment.getExternalStorageDirectory()
				+ "/timegrow/" + aname + "/");
		if (!fileFolder.exists()) { // ���Ŀ¼�����ڣ��򴴽�һ����Ϊ"finger"��Ŀ¼
			fileFolder.mkdirs();
		}
		File jpgFile = new File(fileFolder, filename);
		FileOutputStream outputStream = new FileOutputStream(jpgFile); // �ļ������
		outputStream.write(data); // д��sd����
		outputStream.close(); // �ر������

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

		// ����״̬�仯ʱ���ø÷���
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			// NullPointerException��Ϊû�����CameraȨ��
			// Log.i("info", camera.toString());
			//
			// camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
			// camera.startPreview(); // ��ʼԤ��
			Camera.Parameters parameters = camera.getParameters();
			//
			// Log.i("info", parameters.toString());
			// // parameters = camera.getParameters(); // ��ȡ�������
			parameters.setPictureFormat(PixelFormat.JPEG); // ����ͼƬ��ʽ
			parameters.setPreviewSize(width, height); // ����Ԥ����С
			parameters.setPreviewFrameRate(5); // ����ÿ����ʾ4֡
			parameters.setPictureSize(width, height); // ���ñ����ͼƬ�ߴ�
			parameters.setJpegQuality(80); // ������Ƭ����

			camera.setParameters(parameters);
		}

		// ��ʼ����ʱ���ø÷���
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open(); // ������ͷ
				camera.setPreviewDisplay(holder); // ����������ʾ����Ӱ���SurfaceHolder����
				camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
				camera.startPreview(); // ��ʼԤ��
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// ֹͣ����ʱ���ø÷���
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.release(); // �ͷ������
				camera = null;
			}
		}
	}

	/**
	 * ����ֻ���Ļ�ǣ���ʾ������ť
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			layout.setVisibility(ViewGroup.VISIBLE); // ������ͼ�ɼ�
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA: // �������հ�ť
			if (camera != null && event.getRepeatCount() == 0) {
				// ����
				// ע������takePicture()�������������Ǵ�����һ��PictureCallback���󡪡��������ȡ���������õ�ͼƬ����֮��
				// ��PictureCallback���󽫻ᱻ�ص����ö�����Ը������Ƭ���б����������
				camera.takePicture(null, null, new MyPictureCallback());
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// �ṩһ����̬���������ڸ����ֻ����������Ԥ��������ת�ĽǶ�
	public static int getPreviewDegree(Activity activity) {
		// ����ֻ��ķ���
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// �����ֻ��ķ���������Ԥ������Ӧ��ѡ��ĽǶ�
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
