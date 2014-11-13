package com.guangjun.timegrow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.guangjun.timegrow.Constant.*;
import static com.guangjun.timegrow.DBUtil.*;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	static ArrayList<Album> allalbum = new ArrayList<Album>();
	static ArrayList<Picture> allpicture = new ArrayList<Picture>();

	WhoCall wcNeworEdit;
	Layout curr = null;

	Bundle bundle = null;

	Album tmpalbum;
	Picture tmppicture;
	int tmpindex;

	public int width_screen;
	public int height_screen;

	int width = 300;
	int height = 400;

	private ViewGroup layout;
	private ViewGroup layoutokcancel;
	private Camera camera = null;
	// private Camera.Parameters parameters = null;

	static String lastPicName = null;

	// Bundle bundle = null; // ����һ��Bundle���������洢����
	static String alastfile;
	static String aname;
	static String aadddate;
	static int asize;
	static int aid;

	SurfaceHolder holder;
	BaseAdapter adapter;

	ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics dm = new DisplayMetrics();
		// ��ȡ��Ļ��Ϣ
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		width_screen = dm.widthPixels;
		height_screen = dm.heightPixels;

		gotoMain();
	}

	public void gotoMain() {
		setContentView(R.layout.activity_main);
		curr = Layout.MAIN;

		final ArrayList<Boolean> alIsSelected = new ArrayList<Boolean>();// ��¼ListView������ѡ���˵ı�־λ

		GridView gv_main = (GridView) findViewById(R.id.gridView1);
		Button btnadd = (Button) findViewById(R.id.btn_add);

		allalbum.clear();
		loadAlbum(this);

		alIsSelected.clear();
		for (int i = 0; i < allalbum.size(); i++)// ȫ������Ϊfalse����û��һ��ѡ��
		{
			alIsSelected.add(false);
		}

		gv_main.setAdapter(adapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(MainActivity.this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.gv_bg));
				ll.setPadding(20, 20, 20, 20);
				// ll.setLayoutParams(new
				// LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT,
				// GridLayout.LayoutParams.WRAP_CONTENT));
				// LinearLayout.LayoutParams(
				// new LayoutParams(
				// android.widget.GridLayout.LayoutParams.MATCH_PARENT,
				// android.widget.GridLayout.LayoutParams.MATCH_PARENT)));
				int height_ll, width_ll;
				int margin = (int) getResources().getDimension(
						R.dimen.activity_horizontal_margin);
				width_ll = (width_screen - margin * 3) / 2;
				height_ll = width_ll / 3 * 4 + margin;
				ll.setLayoutParams(new GridView.LayoutParams(width_ll,
						height_ll));

				ImageView image = new ImageView(MainActivity.this);
				String tmpfile = allalbum.get(position).getLastfile();
				if (!tmpfile.equals("")) {
					Bitmap bm = BitmapFactory.decodeFile(tmpfile);
					image.setImageBitmap(bm);
				} else {
					image.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_launcher));
				}
				image.setLayoutParams(new LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						//height_ll-margin));
						RelativeLayout.LayoutParams.MATCH_PARENT));
				ll.addView(image);

				String tmpname = allalbum.get(position).getName();
				TextView tvName = new TextView(MainActivity.this);
				tvName.setText(tmpname);
				tvName.setTextSize(17);
				ll.addView(tvName);

				// ������ѡ���ˣ�����ɫ����Ϊѡ�еı���ɫ
				if (alIsSelected.get(position)) {
					ll.setBackgroundColor(Color.parseColor("#000000"));
				}

				return ll;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return allalbum.get(position);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return allalbum.size();
			}
		});

		gv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tmpalbum = allalbum.get(position);

				for (int i = 0; i < alIsSelected.size(); i++) {
					alIsSelected.set(i, false);
				}
				alIsSelected.set(position, true);
				wcNeworEdit = WhoCall.EDIT;

				gotoGallery();
				// gotoTakePicture();
			}
		});

		gv_main.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(0, 0, 0, "ɾ��");
			}
		});

		btnadd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tmpalbum = new Album();
				wcNeworEdit = WhoCall.NEW;

				final EditText et = new EditText(MainActivity.this);

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("�������������")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(et)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String name = et.getText().toString()
												.trim();
										String lastfile = "";
										String adddate = getNowDateTimeString();
										int size = 0;

										if (name.equals("")) {
											Toast.makeText(MainActivity.this,
													"���ֲ���Ϊ��",
													Toast.LENGTH_SHORT).show();
											return;
										}

										tmpalbum.setName(name);
										tmpalbum.setLastfile(lastfile);
										tmpalbum.setSize(size);
										tmpalbum.setAdddate(adddate);

										insertAlbum(MainActivity.this);

										gotoMain();
									}
								}).setNegativeButton("ȡ��", null).show();
			}
		});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		tmpalbum = allalbum.get(info.position);
		tmpindex = info.position;
		switch (item.getItemId()) {
		case 0:
			DeleteAlbumDialog();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void DeleteAlbumDialog() {
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("ɾ���Ի���").setIcon(R.drawable.ic_launcher)
				.setMessage("ȷ��ɾ����")
				// �൱�ڵ��ȷ�ϰ�ť
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Toast.makeText(MainActivity.this,
								"ɾ����" + tmpalbum.getName(), Toast.LENGTH_SHORT)
								.show();
						Log.d("deleteItem", "delete" + tmpalbum.getId());
						deleteAlbum(MainActivity.this);
						allalbum.remove(tmpindex);
						adapter.notifyDataSetChanged();
					}
				})
				// �൱�ڵ��ȡ����ť
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						return;
					}
				}).create();
		dialog.show();
	}

	public void gotoTakePicture() {
		// bundle = new Bundle();
		// bundle.putString("lastfile", tmpalbum.getLastfile());
		// bundle.putInt("id", tmpalbum.getId());
		// bundle.putInt("size", tmpalbum.getSize());
		// bundle.putString("name", tmpalbum.getName());
		// bundle.putString("adddate", tmpalbum.getAdddate());
		//
		// Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		// intent.putExtras(bundle);
		// startActivity(intent);

		setContentView(R.layout.take_picture);
		curr = Layout.CAMERA;

		// Intent intent = getIntent();
		// Bundle bd = intent.getExtras();
		// alastfile = bd.getString("lastfile");
		// asize = bd.getInt("size");
		// aid = bd.getInt("id");
		// aname = bd.getString("name");
		// aadddate = bd.getString("adddate");

		alastfile = tmpalbum.getLastfile();
		aid = tmpalbum.getId();
		aname = tmpalbum.getName();
		aadddate = tmpalbum.getAdddate();
		asize = tmpalbum.getSize();

		image = (ImageView) findViewById(R.id.scalePic);

		// setLayoutParams����滻��xml���layout���ã��޷�����
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,
				height);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		image.setLayoutParams(lp);

		if ("" != alastfile) {
			Bitmap bm = BitmapFactory.decodeFile(alastfile);
			image.setImageBitmap(bm);
			image.setAlpha(100);
		}
		// else{
		// image.setImageDrawable(getResources().getDrawable(
		// R.drawable.ic_launcher));
		// }

		layout = (ViewGroup) this.findViewById(R.id.buttonLayout);
		layoutokcancel = (ViewGroup) this.findViewById(R.id.buttonLayout2);

		SurfaceView surfaceView = (SurfaceView) this
				.findViewById(R.id.surfaceView);
		RelativeLayout.LayoutParams lp_sv = new RelativeLayout.LayoutParams(
				width, height);
		lp_sv.addRule(RelativeLayout.CENTER_HORIZONTAL);
		surfaceView.setLayoutParams(lp_sv);

		holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(height, width); // ����Surface�ֱ���
		holder.setKeepScreenOn(true);// ��Ļ����
		holder.addCallback(new SurfaceCallback());// ΪSurfaceView�ľ�����һ���ص�����

	}

	private void gotoGallery() {
		setContentView(R.layout.gallery_layout);
		curr = Layout.GALLERY;

		final ImageView gallery = (ImageView) findViewById(R.id.iv_gallery);
		SeekBar bar = (SeekBar) findViewById(R.id.bar_gallery);
		Button btn_camera = (Button) findViewById(R.id.btn_takepic_gallery);

		gallery.setLayoutParams(new LinearLayout.LayoutParams(width, height));

		Log.d("Gallery", "before LoadPicture");
		allpicture.clear();
		loadPicture(MainActivity.this, tmpalbum.getId());

		bar.setMax(tmpalbum.getSize() - 1);
		bar.setProgress(0);

		Log.d("tmpalbum", " " + tmpalbum.getId() + " " + tmpalbum.getSize());

		Bitmap bm;
		if (tmpalbum.getSize() == 0) {
			gallery.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_launcher));
		} else {
			bm = BitmapFactory.decodeFile(allpicture.get(0).getFilename());
			gallery.setImageBitmap(bm);
		}

		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Bitmap bm = BitmapFactory.decodeFile(allpicture.get(progress)
						.getFilename());
				gallery.setImageBitmap(bm);
			}
		});

		btn_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoTakePicture();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 4) {
			switch (curr) {
			case MAIN:// ��������Ļ��˳�����
				System.exit(0);
				break;
			case SETTING:// ���ճ̱༭����Ļ�����������
				gotoMain();
				break;
			case TYPE_MANAGER:// //�����͹������Ļ������ճ̱༭����
				// gotoSetting();
				break;
			case SEARCH:// ���ճ̲��ҽ���Ļ�����������
				gotoMain();
				break;
			case SEARCH_RESULT:// ���ճ̲��ҽ������Ļ������ճ̲��ҽ���
				// gotoSearch();
				break;
			case HELP:// �ڰ�������Ļ�����������
				gotoMain();
				break;
			case ABOUT:
				gotoMain();
				break;
			case GALLERY:
				gotoMain();
				break;
			case CAMERA:
				gotoGallery();
				break;
			}

			if (keyCode == KeyEvent.KEYCODE_CAMERA) { // �������հ�ť
				if (camera != null && event.getRepeatCount() == 0) {
					// ����
					// ע������takePicture()�������������Ǵ�����һ��PictureCallback���󡪡��������ȡ���������õ�ͼƬ����֮��
					// ��PictureCallback���󽫻ᱻ�ص����ö�����Ը������Ƭ���б����������
					camera.takePicture(null, null, new MyPictureCallback());
				}
			}

		}
		return true;
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
				camera.autoFocus(autoFocusCallback);
				// if (null != lastPicName) {
				// Bitmap bm = BitmapFactory.decodeFile(lastPicName);
				// //
				// image.setImageDrawable(getResources().getDrawable(R.drawable.message));
				// image.setImageBitmap(bm);
				// image.setImageAlpha(100);
				// }
				break;
			}
		}
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera arg1) {
			if (success) {
				camera.takePicture(null, null, new MyPictureCallback());
			}
		}
	};

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

			// final byte[] tmpdata = data;
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			image.setImageBitmap(bm);
			image.setAlpha(0);
			layout.setVisibility(ViewGroup.GONE);
			layoutokcancel.setVisibility(ViewGroup.VISIBLE);

			Button btnok = (Button) findViewById(R.id.btn_ok_pic);
			btnok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						bundle = new Bundle();
						bundle.putByteArray("bytes", data); // ��ͼƬ�ֽ����ݱ�����bundle���У�ʵ�����ݽ���
						saveToDBandSDCard(data); // ����ͼƬ��sd����
						Toast.makeText(getApplicationContext(), "success",
								Toast.LENGTH_SHORT).show();
						// camera.startPreview();
						gotoGallery();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			Button btncencel = (Button) findViewById(R.id.btn_cancel_pic);
			btncencel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					camera.startPreview(); // �����պ����¿�ʼԤ��
					Bitmap bm = BitmapFactory.decodeFile(alastfile);
					image.setImageBitmap(bm);
					image.setAlpha(100);
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

		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		Matrix m = new Matrix();
		m.postRotate(90);
		bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
				bm.getHeight(), m, true);
		
		CompressFormat fmt= Bitmap.CompressFormat.JPEG;
		bm.compress(fmt, 80, outputStream); 

//		outputStream.write(data); // д��sd����
		outputStream.close(); // �ر������

		alastfile = fileFolder.toString() + "/" + filename;
		asize++;
		tmpalbum.setLastfile(alastfile);
		tmpalbum.setSize(asize);
		// Album album = new Album(aid, aname, asize, alastfile, aadddate);
		updateAlbum(MainActivity.this);

		tmppicture = new Picture();
		tmppicture.setAlbumid(aid);
		tmppicture.setDatetime(format.format(date));
		tmppicture.setFilename(alastfile);
		insertPicture(MainActivity.this);

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

			List<Size> supporSizes = parameters.getSupportedPictureSizes();
			for (Size tmp : supporSizes) {
				Log.d("info", tmp.height + " * " + tmp.width);
			}
			//
			// Log.i("info", parameters.toString());
			// // parameters = camera.getParameters(); // ��ȡ�������
//			parameters.set("orientation", "portrait");
//			parameters.set("rotation", 90);

//			parameters.setRotation(90);
			parameters.setPictureFormat(PixelFormat.JPEG); // ����ͼƬ��ʽ
			parameters.setPreviewSize(width, height); // ����Ԥ����С
			parameters.setPreviewFrameRate(5); // ����ÿ����ʾ4֡
			parameters.setPictureSize(1280, 960);// width, height); // ���ñ����ͼƬ�ߴ�
			parameters.setJpegQuality(80); // ������Ƭ����

			camera.setParameters(parameters);
		}

		// ��ʼ����ʱ���ø÷���
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open(); // ������ͷ
				camera.setPreviewDisplay(holder); // ����������ʾ����Ӱ���SurfaceHolder����
				camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
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

	// /**
	// * ����ֻ���Ļ�ǣ���ʾ������ť
	// */
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// layout.setVisibility(ViewGroup.VISIBLE); // ������ͼ�ɼ�
	// break;
	// }
	// return true;
	// }

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
