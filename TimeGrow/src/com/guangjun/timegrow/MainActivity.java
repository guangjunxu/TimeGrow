package com.guangjun.timegrow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.acl.LastOwnerException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.guangjun.timegrow.jpgtogif.jpgToGif;
import com.guangjun.timegrow.ImageToMp4.VideoCapture;

import static com.guangjun.timegrow.Constant.*;
import static com.guangjun.timegrow.DBUtil.*;
import static org.opencv.calib3d.Calib3d.findHomography;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.MailTo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
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
import android.widget.Switch;
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
	int tmppicindex;

	public int width_screen;
	public int height_screen;

	int width = 300;
	int height = 400;
	int margin = 20;
	int padding = 20;

	int alpha = 150;

	public static int interval = 100;

	public static final String IMAGE_TYPE = ".jpg";
	public static final int MSG_SAVE_SUCCESS = 100;
	public static final int MSG_STATE = 101;
	public static final int DEF_INTEVAL = 100;

	public String videoSavePath;
	public String gifSavePath;

	boolean isFirstRun = false;

	private ViewGroup layout;
	private ViewGroup layoutokcancel;
	private Camera camera = null;
	// private Camera.Parameters parameters = null;

	static String lastPicName = null;

	// Bundle bundle = null; // 声明一个Bundle对象，用来存储数据
	static String alastfile;
	static String aname;
	static String aadddate;
	static int asize;
	static int aid;

	SurfaceHolder holder;
	BaseAdapter adapter;

	ImageView image, cvimage;
	RelativeLayout layout_progress;

	// OpenCV4Android=======================================================
	private static final String TAG = "OpenCV4Android";

	static String image1 = "/storage/emulated/0/kangfu2_1.jpg";
	static String image2 = "/storage/emulated/0/kangfu2_2.jpg";
	static String imageresult = "/storage/emulated/0/result.jpg";

	static ImageView iv_result;

	static Mat outImage;

	static Mat preImage;
	static Mat curImage;

	static FeatureDetector detector;

	static MatOfKeyPoint prekey;
	static MatOfKeyPoint curKey;

	static Mat preDescripter;
	static Mat curDescripter;

	static MatOfDMatch Matches;

	static Scalar mLineColor = new Scalar(0, 255, 0);

	// OpenCV库加载并初始化成功后的回调函数
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			// TODO Auto-generated method stub
			switch (status) {
			case BaseLoaderCallback.SUCCESS:
				Log.i(TAG, "成功加载");
				break;
			default:
				super.onManagerConnected(status);
				Log.i(TAG, "加载失败");
				break;
			}

		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// load OpenCV engine and init OpenCV library
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10,
				getApplicationContext(), mLoaderCallback);
		Log.i(TAG, "onResume sucess load OpenCV...");
	}

	// OpenCV4Android=======================================================

	public void notifyClick() {

		Intent intent = new Intent("TimegrowAlarm");
		intent.putExtra("name", tmpalbum.getName());
		intent.putExtra("lastfile", tmpalbum.getLastfile());
		// sendBroadcast(intent);
		sendBroadcast(intent);
		Log.i("info", "broadcast send.");
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SAVE_SUCCESS:
				Log.d("handlerMessage", "handler run");

				if (msg.obj != null) {
					videoSavePath = (String) msg.obj;
					// pathTxtView.setVisibility(View.VISIBLE);
					// pathTxtView.setText("视频保存路径："+savePath);
					// playButton.setVisibility(View.VISIBLE);
					layout_progress.setVisibility(RelativeLayout.GONE);
					Toast.makeText(MainActivity.this,
							"视频保存路径：" + videoSavePath, Toast.LENGTH_LONG)
							.show();
				}
				// start.setEnabled(true);
				break;
			case MSG_STATE:
				if (msg.obj != null) {
					// pathTxtView.setText("现在状态："+(String) msg.obj);
					layout_progress.setVisibility(RelativeLayout.VISIBLE);
					layout_progress.bringToFront();
					Toast.makeText(MainActivity.this,
							"现在状态：" + (String) msg.obj, Toast.LENGTH_LONG)
							.show();
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setBackgroundDrawable(
				this.getBaseContext().getResources()
						.getDrawable(R.drawable.bg_actionbar));
		getActionBar().show();

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		width_screen = dm.widthPixels;
		height_screen = dm.heightPixels;

		height = height_screen;
		width = width_screen;
		if (width / 3 * 4 > height) {
			width = height / 4 * 3;
		} else {
			height = width / 3 * 4;
		}

		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"share", MODE_PRIVATE);
		isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		Editor editor = sharedPreferences.edit();
		if (isFirstRun) {
			Log.d("debug", "第一次运行");
			editor.putBoolean("isFirstRun", false);
			editor.commit();

			try {
				initDBbyPath("Growing");
				initDBbyPath("Blooming");
				initDBbyPath("Rising Sun");
				// initDBbyPath("Cloud");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.d("debug", "不是第一次运行");
		}

		String[] pictures = {
				"/storage/emulated/0/timegrow/taiguo/20141124205639.jpg",
				"/storage/emulated/0/timegrow/taiguo/20141124205640.jpg",
				"/storage/emulated/0/timegrow/taiguo/20141124205641.jpg" };

		// jpgToGif.jpgToGif(pictures,"/storage/emulated/0/timegrow/tg.gif");

		gotoMain();
	}

	public void gotoMain() {
		setContentView(R.layout.activity_main);
		curr = Layout.MAIN;

		// notify测试按钮
		// Button btn_notify = (Button) findViewById(R.id.btn_notify);
		// btn_notify.bringToFront();
		// btn_notify.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// System.out.println(s.equals("any string"));
		// Log.d("info", "broadcast send.");
		// Intent intent = new Intent("TimegrowAlarm");
		// intent.putExtra("name", "test name");
		// intent.putExtra("lastfile", "test file");
		// // sendBroadcast(intent);
		// sendBroadcast(intent);
		// Toast.makeText(MainActivity.this, "broadcast send.",
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		// ////////////////////////////////////////////////////////////

		final ArrayList<Boolean> alIsSelected = new ArrayList<Boolean>();// 记录ListView中哪项选中了的标志位

		GridView gv_main = (GridView) findViewById(R.id.gridView1);
		ImageButton btnadd = (ImageButton) findViewById(R.id.btn_add);
		// ImageView im_downbar = (ImageView)findViewById(R.id.im_downbar);
		//
		// im_downbar.bringToFront();
		btnadd.bringToFront();

		allalbum.clear();
		loadAlbum(this);

		alIsSelected.clear();
		for (int i = 0; i < allalbum.size(); i++)// 全部设置为false，即没有一项选中
		{
			alIsSelected.add(false);
		}

		gv_main.setAdapter(adapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// ViewHolder holder = null;

				RelativeLayout ll = null;
				ImageView image = null;
				TextView tvName = null;
				TextView tvDate = null;
				// ll.setOrientation(LinearLayout.VERTICAL);
				if (convertView == null) {
					// holder = new ViewHolder();
					// holder.ll = new RelativeLayout(MainActivity.this);
					// holder.image = new ImageView(MainActivity.this);
					// holder.tvName = new TextView(MainActivity.this);
					// holder.tvDate = new TextView(MainActivity.this);
					ll = new RelativeLayout(MainActivity.this);
					image = new ImageView(MainActivity.this);
					tvName = new TextView(MainActivity.this);
					tvDate = new TextView(MainActivity.this);

					// convertView = new View(MainActivity.this);

					ll.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.gv_bg));

					ll.setPadding(padding, padding, padding, padding);
					// ll.setLayoutParams(new
					// LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT,
					// GridLayout.LayoutParams.WRAP_CONTENT));
					// LinearLayout.LayoutParams(
					// new LayoutParams(
					// android.widget.GridLayout.LayoutParams.MATCH_PARENT,
					// android.widget.GridLayout.LayoutParams.MATCH_PARENT)));
					int height_ll, width_ll;
					margin = (int) getResources().getDimension(
							R.dimen.activity_horizontal_margin);
					width_ll = (width_screen - margin * 3) / 2;
					height_ll = width_ll / 3 * 4 + margin;
					ll.setLayoutParams(new GridView.LayoutParams(width_ll,
							height_ll));

					int width_im, height_im;
					width_im = width_ll - padding * 2;
					height_im = width_im / 3 * 4;
					// image.setLayoutParams(new LayoutParams(
					// width_im,height_im));
					// RelativeLayout.LayoutParams.MATCH_PARENT,
					// height_ll-margin));
					// RelativeLayout.LayoutParams.MATCH_PARENT));
					RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
							width_im, height_im);
					// RelativeLayout.LayoutParams.WRAP_CONTENT,
					// RelativeLayout.LayoutParams.WRAP_CONTENT);
					lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);

					ll.addView(image, lp1);

					RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

					ll.addView(tvName, lp2);

					RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

					ll.addView(tvDate, lp3);

					// convertView.setTag(holder);
				} else {
					// holder = (ViewHolder) convertView.getTag();
					ll = (RelativeLayout) convertView;
					image = (ImageView) ll.getChildAt(0);
					tvName = (TextView) ll.getChildAt(1);

					tvDate = (TextView) ll.getChildAt(2);
				}

				String tmpfile = allalbum.get(position).getLastfile();
				if (!tmpfile.equals("")) {
					Bitmap bm = BitmapFactory.decodeFile(tmpfile);
					image.setImageBitmap(bm);
				} else {
					image.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_launcher));
				}

				String tmpname = allalbum.get(position).getName();
				tvName.setText(tmpname);
				tvName.setTextSize(13);

				SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");

				String lastdate = allalbum.get(position).getLastfile();
				if (lastdate.length() < 18) {
					lastdate = allalbum.get(position).getAdddate();
				} else {
					lastdate = lastdate.substring(lastdate.length() - 18,
							lastdate.length() - 4);
				}
				try {
					SimpleDateFormat ft_out = new SimpleDateFormat("yyyy/MM/dd");
					lastdate = ft_out.format(ft.parse(lastdate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(MainActivity.this, "日期打印异常",
							Toast.LENGTH_SHORT).show();
				}

				tvDate.setText(lastdate);
				tvDate.setTextSize(8);

				// 如果该项被选中了，背景色设置为选中的背景色
				if (alIsSelected.get(position)) {
					ll.setBackgroundColor(Color.parseColor("#000000"));
				}

				return ll;

			}

			class ViewHolder {
				RelativeLayout ll;
				ImageView image;
				TextView tvName, tvDate;
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
				menu.add(0, 0, 0, "删除");
			}
		});

		btnadd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tmpalbum = new Album();
				wcNeworEdit = WhoCall.NEW;

				final EditText et = new EditText(MainActivity.this);

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("请输入相册名称")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(et)
						.setPositiveButton("确定",
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
													"名字不能为空",
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
								}).setNegativeButton("取消", null).show();
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
				.setTitle("删除对话框").setIcon(R.drawable.ic_camera)
				.setMessage("确认删除吗？")
				// 相当于点击确认按钮
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Toast.makeText(MainActivity.this,
								"删除：" + tmpalbum.getName(), Toast.LENGTH_SHORT)
								.show();
						Log.d("deleteItem", "delete" + tmpalbum.getId());
						deleteAlbum(MainActivity.this);
						allalbum.remove(tmpindex);
						adapter.notifyDataSetChanged();
					}
				})
				// 相当于点击取消按钮
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
		// cvimage = new ImageView(MainActivity.this);
		cvimage = (ImageView) findViewById(R.id.iv_CVdraw);

		// setLayoutParams后会替换掉xml里的layout设置，无法居中
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,
				height);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);

		// image.setScaleType(ScaleType.FIT_CENTER);
		image.setLayoutParams(lp);
		cvimage.setLayoutParams(lp);

		if ("" != alastfile) {
			Bitmap bm = BitmapFactory.decodeFile(alastfile);
			image.setImageBitmap(bm);
			image.setAlpha(alpha);
		}
		// else{
		// image.setImageDrawable(getResources().getDrawable(
		// R.drawable.ic_camera));
		// }

		layout = (ViewGroup) this.findViewById(R.id.buttonLayout);
		layoutokcancel = (ViewGroup) this.findViewById(R.id.buttonLayout2);

		SurfaceView surfaceView = (SurfaceView) this
				.findViewById(R.id.surfaceView);

		RelativeLayout.LayoutParams lp_sv = new RelativeLayout.LayoutParams(
				width, height);
		lp_sv.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp_sv.addRule(RelativeLayout.CENTER_HORIZONTAL);
		surfaceView.setLayoutParams(lp_sv);

		holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(height, width); // 设置Surface分辨率
		holder.setKeepScreenOn(true);// 屏幕常亮
		holder.addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数

	}

	private void gotoGallery() {
		setContentView(R.layout.gallery_layout);
		curr = Layout.GALLERY;

		final ImageView gallery = (ImageView) findViewById(R.id.iv_gallery);
		final SeekBar bar = (SeekBar) findViewById(R.id.bar_gallery);
		final ImageButton btn_camera = (ImageButton) findViewById(R.id.btn_takepic_gallery);
		RelativeLayout gallery_ll = (RelativeLayout) findViewById(R.id.gallery_ll);
		TextView tv_name = (TextView) findViewById(R.id.tv_gallery_name);
		final TextView tv_date = (TextView) findViewById(R.id.tv_gallery_date);
		final ImageButton btn_edit = (ImageButton) findViewById(R.id.btn_gallery_edit);
		final ImageButton btn_share = (ImageButton) findViewById(R.id.btn_gallery_share);
		final ImageButton btn_delete = (ImageButton) findViewById(R.id.btn_gallery_delete);
		final ImageButton btn_play = (ImageButton) findViewById(R.id.btn_play);
		final LinearLayout ll_share = (LinearLayout) findViewById(R.id.ll_share_group);
		final ImageButton btn_gif = (ImageButton) findViewById(R.id.btn_gif);
		final ImageButton btn_mp4 = (ImageButton) findViewById(R.id.btn_mp4);
		final EditText et_interval = (EditText) findViewById(R.id.et_gallery_interval);
		layout_progress = (RelativeLayout) findViewById(R.id.gallery_progress_circular);

		btn_play.setOnClickListener(new OnClickListener() {
			boolean isIconChange = false;

			@Override
			public void onClick(View v) {
				if (isIconChange) {
					btn_play.setBackgroundResource(R.drawable.btn_play);
					isIconChange = false;
				} else {
					btn_play.setBackgroundResource(R.drawable.btn_stop);
					isIconChange = true;

					new Thread() {
						public void run() {
							while (isIconChange) {
								int curprogress = bar.getProgress();
								int total = bar.getMax() + 1;

								if (total == 0) {
									isIconChange = false;
									break;
								}

								curprogress = (curprogress + 1) % total;
								// Log.d("seekbar", " " + total);
								//
								// Log.d("seekbar", " " + curprogress);

								bar.setProgress(curprogress);

								try {
									Thread.sleep(interval);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}.start();
				}
			}
		});

		btn_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (curr == Layout.SHARE) {
					curr = Layout.GALLERY;
					gotoGallery();
				} else {
					curr = Layout.SHARE;
					btn_camera.setVisibility(Button.GONE);
					btn_edit.setVisibility(Button.GONE);
					btn_delete.setVisibility(Button.GONE);
					// btn_share.setVisibility(Button.GONE);

					ll_share.setVisibility(LinearLayout.VISIBLE);

					RelativeLayout gallery_layout = (RelativeLayout) findViewById(R.id.gallery_layout);

					gallery_layout.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (curr == Layout.SHARE) {
								et_interval.clearFocus();
								InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
								if (imm != null) {
									return imm.hideSoftInputFromWindow(
											v.getWindowToken(), 0);
								}
							}
							return false;
						}
					});

					et_interval
							.setOnFocusChangeListener(new OnFocusChangeListener() {

								@Override
								public void onFocusChange(View v,
										boolean hasFocus) {
									if (!hasFocus) {
										interval = DEF_INTEVAL;
										String ettext = et_interval.getText()
												.toString().trim();
										if (ettext.equals("")) {
											et_interval.setText(String
													.valueOf(DEF_INTEVAL));
											interval = DEF_INTEVAL;
											return;
										}
										if (ettext.matches("^[1-9]\\d*$")) {
											interval = Integer.parseInt(ettext);
											if (interval > 99999
													|| interval < 50) {
												interval = DEF_INTEVAL;
											}
											return;
										}
										Toast.makeText(MainActivity.this,
												"请输入50~100000的整数",
												Toast.LENGTH_SHORT).show();
										interval = DEF_INTEVAL;
										et_interval.setText(String
												.valueOf(DEF_INTEVAL));
									}
								}
							});
				}

				btn_gif.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						new Thread() {
							public void run() {

								handler.sendMessage(handler.obtainMessage(
										MainActivity.MSG_STATE,
										"开始将图片转成GIF，请稍后。。"));

								Date date = new Date();
								SimpleDateFormat format = new SimpleDateFormat(
										"yyyyMMddHHmmss"); // 格式化时间
								String filename = tmpalbum.getName() + "_"
										+ format.format(date) + ".gif";
								gifSavePath = Environment
										.getExternalStorageDirectory()
										+ "/timegrow/" + filename;

								ArrayList<String> picturesname = new ArrayList<String>();
								for (int i = 0; i < allpicture.size(); i++) {
									picturesname.add(allpicture.get(i)
											.getFilename());
								}
								jpgToGif.jpgToGif(
										picturesname.toArray(new String[] {}),
										gifSavePath);
								Log.d("image2gif", "jpgtogif finish");
								handler.sendMessage(handler.obtainMessage(
										MainActivity.MSG_SAVE_SUCCESS,
										gifSavePath));
							}
						}.start();
					}
				});

				btn_mp4.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Date date = new Date();
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyyMMddHHmmss"); // 格式化时间
						String filename = tmpalbum.getName() + "_"
								+ format.format(date) + ".mp4";
						videoSavePath = Environment
								.getExternalStorageDirectory()
								+ "/timegrow/"
								+ filename;

						ArrayList<String> picturesname = new ArrayList<String>();
						for (int i = 0; i < allpicture.size(); i++) {
							picturesname.add(allpicture.get(i).getFilename());
						}
						VideoCapture.genMp4(MainActivity.this,
								picturesname.toArray(new String[] {}),
								videoSavePath, allpicture.size(),
								1000.0 / interval, handler);
					}
				});
			}
		});

		btn_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoAlbumEdit();
			}
		});

		btn_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (allpicture.size() > 0) {

					Dialog dialog = new AlertDialog.Builder(MainActivity.this)
							.setTitle(tmpalbum.getName() + " 删除对话框")
							.setIcon(R.drawable.ic_camera)
							.setMessage(
									"确认删除"
											+ getDateTimeStr(tmppicture
													.getDatetime()) + "的 "
											+ tmpalbum.getName() + " 吗？")
							// 相当于点击确认按钮
							.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											Toast.makeText(
													MainActivity.this,
													"删除："
															+ getDateTimeStr(tmppicture
																	.getDatetime())
															+ "的 "
															+ tmpalbum
																	.getName(),
													Toast.LENGTH_SHORT).show();
											Log.d("deleteItem", "delete"
													+ tmppicture.getFilename());
											// 更新picture
											deletePicture(MainActivity.this);
											allpicture.remove(tmppicindex);
											tmppicindex = tmppicindex == 0 ? 0
													: --tmppicindex;
											if (allpicture.size() > 0) {
												tmppicture = allpicture
														.get(tmppicindex);
											}
											// 更新album
											if (allpicture.size() > 0) {
												tmpalbum.setSize(tmpalbum
														.getSize() - 1);
												if (tmppicindex == allpicture
														.size() - 1) {
													tmpalbum.setLastfile(tmppicture
															.getFilename());
												}
											} else {
												tmpalbum.setSize(0);
												tmpalbum.setLastfile("");
											}

											updateAlbum(MainActivity.this);
											// 更新界面
											if (tmpalbum.getSize() == 0) {
												gallery.setImageDrawable(getResources()
														.getDrawable(
																R.drawable.ic_launcher));
												tv_date.setText("[Date]");
											}
											bar.setProgress(tmppicindex);
											bar.setMax(tmpalbum.getSize() - 1);
										}
									})
							// 相当于点击取消按钮
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											return;
										}
									}).create();
					dialog.show();
				} else {
					Toast.makeText(MainActivity.this, "没有可删除的照片",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		tv_name.setText(tmpalbum.getName());

		int width_ll = width_screen - 4 * margin;
		int height_ll = width_ll / 3 * 4 + margin * 3;

		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				width_ll, height_ll);
		lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		gallery_ll.setLayoutParams(lp1);
		gallery_ll.setPadding(padding, padding, padding, padding);

		int width_im = width_ll - 2 * padding;
		int height_im = width_im / 3 * 4;
		gallery.setLayoutParams(new RelativeLayout.LayoutParams(width_im,
				height_im));

		Log.d("Gallery", "before LoadPicture");
		allpicture.clear();
		loadPicture(MainActivity.this, tmpalbum.getId());

		bar.setMax(tmpalbum.getSize() - 1);
		bar.setProgress(0);
		tmppicindex = 0;
		if (allpicture.size() > 0) {
			tmppicture = allpicture.get(0);
		}

		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat ft_out = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String date_pic = "[Date]";
		try {
			if (allpicture.size() > 0) {
				date_pic = ft_out.format(ft.parse(allpicture.get(0)
						.getDatetime()));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tv_date.setText(date_pic);

		Log.d("tmpalbum", " " + tmpalbum.getId() + " " + tmpalbum.getSize());

		Bitmap bm;
		if (tmpalbum.getSize() <= 0) {
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
				tmppicture = allpicture.get(progress);
				tmppicindex = progress;
				Bitmap bm = BitmapFactory.decodeFile(tmppicture.getFilename());
				gallery.setImageBitmap(bm);

				// 封装成String getDateTimeStr(String)
				// SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
				// SimpleDateFormat ft_out = new SimpleDateFormat(
				// "yyyy/MM/dd HH:mm:ss");
				// String date_pic = "[Date]";
				// try {
				// date_pic = ft_out.format(ft.parse(tmppicture
				// .getDatetime()));
				// } catch (ParseException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				tv_date.setText(getDateTimeStr(tmppicture.getDatetime()));
			}
		});

		btn_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoTakePicture();
			}
		});
	}

	protected void gotoDeletePic() {
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle(tmpalbum.getName() + " 删除对话框")
				.setIcon(R.drawable.ic_camera)
				.setMessage(
						"确认删除" + getDateTimeStr(tmppicture.getDatetime())
								+ "的 " + tmpalbum.getName() + " 吗？")
				// 相当于点击确认按钮
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Toast.makeText(
								MainActivity.this,
								"删除："
										+ getDateTimeStr(tmppicture
												.getDatetime()) + "的 "
										+ tmpalbum.getName(),
								Toast.LENGTH_SHORT).show();
						Log.d("deleteItem", "delete" + tmppicture.getFilename());
						deleteAlbum(MainActivity.this);
						allpicture.remove(tmppicindex);
						adapter.notifyDataSetChanged();
					}
				})
				// 相当于点击取消按钮
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						return;
					}
				}).create();
		dialog.show();
	}

	protected void gotoAlbumEdit() {
		setContentView(R.layout.gallery_edit);
		curr = Layout.GALLERY_EDIT;

		final EditText et_name = (EditText) findViewById(R.id.et_edit_name);
		final Switch sw = (Switch) findViewById(R.id.switch1);
		final SeekBar bar = (SeekBar) findViewById(R.id.bar_edit_freq);
		final TextView tv_freq = (TextView) findViewById(R.id.tv_edit_freq);
		ImageButton btn_ok = (ImageButton) findViewById(R.id.btn_ok_edit);
		ImageButton btn_cancel = (ImageButton) findViewById(R.id.btn_cancel_edit);
		final LinearLayout ll_freq = (LinearLayout) findViewById(R.id.ll_freq);
		RelativeLayout layout_gallery_edit = (RelativeLayout) findViewById(R.id.layout_gallery_edit);

		bar.setMax(alarmFreq.length - 1);
		et_name.setText(tmpalbum.getName());
		sw.setChecked(tmpalbum.isHasalarm());
		bar.setProgress(tmpalbum.getAlarmfreq());
		tv_freq.setText(alarmFreq[tmpalbum.getAlarmfreq()]);

		if (sw.isChecked()) {
			ll_freq.setVisibility(LinearLayout.VISIBLE);
		}

		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					ll_freq.setVisibility(LinearLayout.VISIBLE);
				} else {
					ll_freq.setVisibility(LinearLayout.GONE);
				}
			}
		});

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
				tv_freq.setText(alarmFreq[progress]);
			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tmpalbum.setName(et_name.getText().toString().trim());
				tmpalbum.setHasalarm(sw.isChecked());
				tmpalbum.setAlarmfreq(bar.getProgress());
				updateAlbum(MainActivity.this);

				gotoSetAlarm();

				gotoGallery();
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoGallery();
			}
		});

		layout_gallery_edit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				et_name.clearFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if (imm != null) {
					return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return false;
			}
		});

	}

	protected void gotoSetAlarm() {
		Intent intent = new Intent("TimegrowAlarm");
		intent.putExtra("name", tmpalbum.getName());
		intent.putExtra("lastfile", tmpalbum.getLastfile());

		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this,
				tmpalbum.getId(), intent, 0);
		// 开始时间
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

		if (tmpalbum.isHasalarm()) {
			Date lastdate = new Date();
			if (tmpalbum.getSize() > 0) {
				SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
				String tmplastfile = tmpalbum.getLastfile();
				try {
					lastdate = ft
							.parse(tmplastfile.substring(
									tmplastfile.length() - 18,
									tmplastfile.length() - 4));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			long firstime = lastdate.getTime()
					+ alarmNextMilli[tmpalbum.getAlarmfreq()];

			// 5秒一个周期，不停的发送广播
			// am.setRepeating(AlarmManager.RTC_WAKEUP,
			// System.currentTimeMillis(), 2 * 60 * 1000, sender);
			Log.d("info", "set alarm success");
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstime,
					alarmNextMilli[tmpalbum.getAlarmfreq()], sender);
		} else {
			am.cancel(sender);
		}

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
			case MAIN:// 在主界面的话退出程序
				System.exit(0);
				break;
			case SETTING:// 在日程编辑界面的话返回主界面
				gotoMain();
				break;
			case TYPE_MANAGER:// //在类型管理界面的话返回日程编辑界面
				// gotoSetting();
				break;
			case SEARCH:// 在日程查找界面的话返回主界面
				gotoMain();
				break;
			case SEARCH_RESULT:// 在日程查找结果界面的话返回日程查找界面
				// gotoSearch();
				break;
			case HELP:// 在帮助界面的话返回主界面
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
			case GALLERY_EDIT:
				gotoGallery();
				break;
			case SHARE:
				gotoGallery();
				break;
			}

			if (keyCode == KeyEvent.KEYCODE_CAMERA) { // 按下拍照按钮
				if (camera != null && event.getRepeatCount() == 0) {
					// 拍照
					// 注：调用takePicture()方法进行拍照是传入了一个PictureCallback对象——当程序获取了拍照所得的图片数据之后
					// ，PictureCallback对象将会被回调，该对象可以负责对相片进行保存或传入网络
					camera.takePicture(null, null, new MyPictureCallback());
				}
			}

		}
		return true;
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
				camera.autoFocus(autoFocusCallback);
				// if (null != lastPicName) {
				// Bitmap bm = BitmapFactory.decodeFile(lastPicName);
				// //
				// image.setImageDrawable(getResources().getDrawable(R.drawable.message));
				// image.setImageBitmap(bm);
				// image.setImageAlpha(alpha);
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

			// final byte[] tmpdata = data;
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inPreferredConfig = Config.RGB_565;
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			image.setImageBitmap(bm);
			image.setAlpha(0);
			layout.setVisibility(ViewGroup.GONE);
			layoutokcancel.setVisibility(ViewGroup.VISIBLE);

			int ivHeight = image.getHeight();
			int ivWidth = image.getWidth();
//			// OpenCV===========================================================
//			// outImage = new Mat();
//
//			Mat preImage = new Mat();
//			Mat curImage = new Mat();
//
//			MatOfKeyPoint prekey = new MatOfKeyPoint();
//			MatOfKeyPoint curKey = new MatOfKeyPoint();
//
//			Mat preDescripter = new Mat();
//			Mat curDescripter = new Mat();
//
//			MatOfDMatch Matches = new MatOfDMatch();
//
//			try {
//				// Utils.bitmapToMat(BitmapFactory.decodeFile(alastfile),
//				// preImage);
//				Utils.bitmapToMat(bm, curImage);
//				Imgproc.cvtColor(curImage, curImage, Imgproc.COLOR_RGB2BGR);
//				preImage = Highgui.imread(alastfile);
//
//				FeatureDetector detector = FeatureDetector
//						.create(FeatureDetector.ORB);
//
//				detector.detect(preImage, prekey);
//				detector.detect(curImage, curKey);
//
//				DescriptorExtractor extractor = DescriptorExtractor
//						.create(DescriptorExtractor.ORB);
//				extractor.compute(preImage, prekey, preDescripter);
//				extractor.compute(curImage, curKey, curDescripter);
//
//				DescriptorMatcher matcher = DescriptorMatcher
//						.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//				int lc = preDescripter.cols();
//				int rc = curDescripter.cols();
//				if (preDescripter.type() == curDescripter.type() && lc == rc) {
//					matcher.match(preDescripter, curDescripter, Matches);
//				}
//
//				// -- Quick calculation of max and min distances between
//				// keypoints
//				double max_dist = 0;
//				double min_dist = 100;
//				List<DMatch> matchesList = Matches.toList();
//				List<DMatch> matchesResult = new ArrayList<DMatch>();
//				for (DMatch tmpMatch : matchesList) {
//					double tmpdist = tmpMatch.distance;
//					if (tmpdist < min_dist)
//						min_dist = tmpdist;
//					if (tmpdist > max_dist)
//						max_dist = tmpdist;
//				}
//				Log.d(TAG, "min=" + min_dist + ",max=" + max_dist);
//
//				// -- Draw only "good" matches (i.e. whose distance is less than
//				// 3*min_dist )
//				for (DMatch tmpMatch : matchesList) {
//					if (tmpMatch.distance < 3 * min_dist) {
//						matchesResult.add(tmpMatch);
//					}
//				}
//
//				MatOfDMatch goodMatches = new MatOfDMatch();
//				goodMatches.fromList(matchesResult);
//
//				// -- Localize the object from img_1 in img_2
//				MatOfPoint2f prePoints = new MatOfPoint2f();
//				MatOfPoint2f curPoints = new MatOfPoint2f();
//				prePointsList = new ArrayList<Point>();
//				curPointsList = new ArrayList<Point>();
//				List<KeyPoint> preKeyPoints = new ArrayList<KeyPoint>();
//				List<KeyPoint> curKeyPoints = new ArrayList<KeyPoint>();
//
//				preKeyPoints = prekey.toList();
//				curKeyPoints = curKey.toList();
//
//				for (DMatch tmpmatch : matchesResult) {
//					prePointsList.add(preKeyPoints.get(tmpmatch.queryIdx).pt);
//					curPointsList.add(curKeyPoints.get(tmpmatch.trainIdx).pt);
//				}
//				prePoints.fromList(prePointsList);
//				curPoints.fromList(curPointsList);
//
//				// 绘制curImage上的特征点
//				Bitmap bmCanvas = Bitmap.createBitmap(ivWidth, ivHeight,
//						Config.ARGB_8888);
//				Canvas canvas = new Canvas(bmCanvas);
//
//				Paint paint = new Paint();
//
//				paint.setColor(Color.GREEN);
//				paint.setStrokeWidth(10);
//				paint.setAntiAlias(true);
//
//				// canvas.drawPoint(500, 500, paint);
//				// canvas.drawCircle(200, 200, 50, paint);
//				// canvas.drawBitmap(bmCanvas, 0, 0, paint);
//
//				float pointArray[] = new float[curPointsList.size() * 2];
//				int count = 0;
//				for (Point pt : curPointsList) {
//					pointArray[count] = (float) pt.x;
//					pointArray[count + 1] = (float) pt.y;
//					count += 2;
//				}
//				// Float[] pointArray = pointList.toArray(new
//				// Float[curPointsList.size()*2]);
//
//				canvas.drawPoints(pointArray, paint);
//
//				cvimage.bringToFront();
//				cvimage.setImageBitmap(bmCanvas);
//
//				cvimage.setOnTouchListener(new OnTouchListener() {
//
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						int x = (int) event.getX();
//						int y = (int) event.getY();
//						Log.d(TAG, "OnTouch: x=" + x + ", y=" + y);
//						return true;
//					}
//				});
//
//				// test=====
//				// Mat outImage = new Mat();
//				// // preImage = Highgui.imread(image1);
//				// // curImage = Highgui.imread(image2);
//				// Features2d.drawMatches(preImage, prekey, curImage, curKey,
//				// goodMatches, outImage);
//				// // Highgui.imwrite(imageresult, outImage);
//				//
//				// // test bitmap2mat
//				// // Highgui.imwrite(imageresult, curImage);
//				//
//				// Bitmap bmtest = Bitmap.createBitmap(outImage.width(),
//				// outImage.height(), Config.ARGB_8888);
//				//
//				// Imgproc.cvtColor(outImage, outImage, Imgproc.COLOR_BGR2RGB);
//				// Utils.matToBitmap(outImage, bmtest);
//				// // bmtest = BitmapFactory.decodeFile(imageresult);
//				// cvimage.setImageBitmap(bmtest);
//				// endtest=====
//
//				Mat H = new Mat();
//				try {
//					H = findHomography(curPoints, prePoints);
//				} catch (CvException e) {
//					e.printStackTrace();
//					Log.d(TAG, "FindHomography fail");
//				}
//
//				outImage = new Mat(preImage.height(), preImage.width(),
//						CvType.CV_8UC3);
//
//				Imgproc.warpPerspective(curImage, outImage, H, outImage.size());
//
//				Bitmap tmpbm = Bitmap.createBitmap(outImage.width(),
//						outImage.height(), Config.ARGB_8888);
//				Imgproc.cvtColor(outImage, outImage, Imgproc.COLOR_BGR2RGB);
//				Utils.matToBitmap(outImage, tmpbm);
//
//				// cvimage.setImageBitmap(tmpbm);
//
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				tmpbm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//				data = baos.toByteArray();
//			} catch (CvException e) {
//				e.printStackTrace();
//				Log.d(TAG, "OpenCV imageWarp fail");
//			}
//			// endOpenCV===========================================================
			
			findMatches(data);
			drawMatchesPoints();

			ImageButton btnok = (ImageButton) findViewById(R.id.btn_ok_pic);
			btnok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						// bundle = new Bundle();
						// bundle.putByteArray("bytes", data); //
						// 将图片字节数据保存在bundle当中，实现数据交换
						warpImage(data);
						saveToDBandSDCard(outdata); // 保存图片到sd卡中
						Toast.makeText(getApplicationContext(), "success",
								Toast.LENGTH_SHORT).show();
						// camera.startPreview();
						gotoGallery();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			ImageButton btncencel = (ImageButton) findViewById(R.id.btn_cancel_pic);
			btncencel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					camera.startPreview(); // 拍完照后，重新开始预览
					Bitmap bm = BitmapFactory.decodeFile(alastfile);
					image.setImageBitmap(bm);
					image.setAlpha(alpha);
					cvimage.setVisibility(ImageView.GONE);
					layoutokcancel.setVisibility(ViewGroup.GONE);
					layout.setVisibility(ViewGroup.VISIBLE);
				}
			});

		}
	}

	public void findMatches(byte[] data) {
		preImage = new Mat();
		curImage = new Mat();

		prekey = new MatOfKeyPoint();
		curKey = new MatOfKeyPoint();

		Mat preDescripter = new Mat();
		Mat curDescripter = new Mat();

		MatOfDMatch Matches = new MatOfDMatch();

		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

		try {
			// Utils.bitmapToMat(BitmapFactory.decodeFile(alastfile),
			// preImage);
			Utils.bitmapToMat(bm, curImage);
			Imgproc.cvtColor(curImage, curImage, Imgproc.COLOR_RGB2BGR);
			preImage = Highgui.imread(alastfile);

			FeatureDetector detector = FeatureDetector
					.create(FeatureDetector.ORB);

			detector.detect(preImage, prekey);
			detector.detect(curImage, curKey);

			DescriptorExtractor extractor = DescriptorExtractor
					.create(DescriptorExtractor.ORB);
			extractor.compute(preImage, prekey, preDescripter);
			extractor.compute(curImage, curKey, curDescripter);

			DescriptorMatcher matcher = DescriptorMatcher
					.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
			int lc = preDescripter.cols();
			int rc = curDescripter.cols();
			if (preDescripter.type() == curDescripter.type() && lc == rc) {
				matcher.match(preDescripter, curDescripter, Matches);
			}

			// -- Quick calculation of max and min distances between
			// keypoints
			double max_dist = 0;
			double min_dist = 100;
			List<DMatch> matchesList = Matches.toList();
			// List<DMatch> matchesResult = new ArrayList<DMatch>();
			matchesResult = new ArrayList<DMatch>();
			for (DMatch tmpMatch : matchesList) {
				double tmpdist = tmpMatch.distance;
				if (tmpdist < min_dist)
					min_dist = tmpdist;
				if (tmpdist > max_dist)
					max_dist = tmpdist;
			}
			Log.d(TAG, "min=" + min_dist + ",max=" + max_dist);

			// -- Draw only "good" matches (i.e. whose distance is less than
			// 3*min_dist )
			for (DMatch tmpMatch : matchesList) {
				if (tmpMatch.distance < 3 * min_dist) {
					matchesResult.add(tmpMatch);
				}
			}
		} catch (CvException e) {
			e.printStackTrace();
			Log.d(TAG, "findMatches fail");
		}
	}

	List<Point> prePointsList;
	List<Point> curPointsList;
	List<DMatch> matchesResult;
	Canvas canvas;
	Bitmap bmCanvas;
	int ivWidth;
	int ivHeight;
	int imwidth;
	int imheight;
	float ivimratio;
	double delta = 30;
	byte [] outdata;

	public void drawMatchesPoints() {
		// -- Localize the object from img_1 in img_2
		MatOfPoint2f prePoints = new MatOfPoint2f();
		MatOfPoint2f curPoints = new MatOfPoint2f();
		List<KeyPoint> preKeyPoints = new ArrayList<KeyPoint>();
		List<KeyPoint> curKeyPoints = new ArrayList<KeyPoint>();
		prePointsList = new ArrayList<Point>();
		curPointsList = new ArrayList<Point>();

		preKeyPoints = prekey.toList();
		curKeyPoints = curKey.toList();

		for (DMatch tmpmatch : matchesResult) {
			prePointsList.add(preKeyPoints.get(tmpmatch.queryIdx).pt);
			curPointsList.add(curKeyPoints.get(tmpmatch.trainIdx).pt);
		}
		
		Log.d("PrePointsList", "curSize="+curPointsList.size()+",preSize="+prePointsList.size());

		prePoints.fromList(prePointsList);
		curPoints.fromList(curPointsList);

		// 绘制curImage上的特征点
		ivWidth = image.getWidth();
		ivHeight = image.getHeight();
		imwidth = curImage.width();
		imheight = curImage.height();
		bmCanvas = Bitmap.createBitmap(ivWidth, ivHeight,
				Config.ARGB_8888);
		canvas = new Canvas(bmCanvas);

		final Paint paintClear = new Paint();
		paintClear.setXfermode(new PorterDuffXfermode(Mode.CLEAR));

		final Paint paint = new Paint();

		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(10);
		paint.setAntiAlias(true);

		// canvas.drawPoint(500, 500, paint);
		// canvas.drawCircle(200, 200, 50, paint);
		// canvas.drawBitmap(bmCanvas, 0, 0, paint);
		
//		float ptmax_x = 0;
//		float ptmax_y = 0;
		
		ivimratio = (float)ivWidth / (float)imwidth;

		float pointArray[] = new float[curPointsList.size() * 2];
		int count = 0;
		for (Point pt : curPointsList) {
			pointArray[count] = (float) pt.x * ivimratio;
			pointArray[count + 1] = (float) pt.y * ivimratio;
			
//			if(pointArray[count] > ptmax_x)ptmax_x = pointArray[count];
//			if(pointArray[count + 1] > ptmax_y)ptmax_y = pointArray[count + 1];
			
			count += 2;
		}
		// Float[] pointArray = pointList.toArray(new
		// Float[curPointsList.size()*2]);

//		canvas.drawPaint(paintClear);
//		canvas.drawCircle(0, 0, 20, paint);
//		canvas.drawCircle(ivWidth, ivHeight, 20, paint);
//		canvas.drawCircle(ptmax_x, ptmax_y, 20, paint);

		canvas.drawPoints(pointArray, paint);

		cvimage.setVisibility(ImageView.VISIBLE);
		cvimage.bringToFront();
		cvimage.setImageBitmap(bmCanvas);

		cvimage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				double x = event.getX();
				double y = event.getY();
				x = x/ivimratio;
				y = y/ivimratio;
				double px, py;
//				Log.d(TAG, "OnTouch: x=" + x + ", y=" + y);

				List<Integer> index = new ArrayList<Integer>();
				Collection<Point> curcollection = new ArrayList<Point>();
				Collection<Point> precollection = new ArrayList<Point>();
				
				int index_rm = 0;
				while(index_rm < curPointsList.size()){
					px = curPointsList.get(index_rm).x;
					py = curPointsList.get(index_rm).y;
//					Log.d(TAG, "MatchPoint: x="+px+", y="+py);
					if (((x - px) * (x - px) + (y - py) * (y - py)) < delta*delta) {
						curPointsList.remove(index_rm);
						prePointsList.remove(index_rm);
						
						Log.d(TAG, "prePointsList Size="+prePointsList.size());
						Log.d(TAG, "OnTouch: x=" + x + ", y=" + y);
						Log.d(TAG, "MatchPoint: x="+px+", y="+py);
					}else{
						index_rm++;
					}
				}
				
//				for (int i = 0; i < curPointsList.size(); i++) {
//					px = curPointsList.get(i).x;
//					py = curPointsList.get(i).y;
////					Log.d(TAG, "MatchPoint: x="+px+", y="+py);
//					if (((x - px) * (x - px) + (y - py) * (y - py)) < delta*delta) {
//						curcollection.add(curPointsList.get(i));
//						try{
//						precollection.add(prePointsList.get(i));
//						}
//						catch(IndexOutOfBoundsException e){
//							Log.d(TAG, "prePointsList Size="+prePointsList.size());
//						}
//						index.add(i);
//						Log.d(TAG, "OnTouch: x=" + x + ", y=" + y);
//						Log.d(TAG, "MatchPoint: x="+px+", y="+py);
//
//					}
//				}
//				
//				Log.d("PrePointsList", "curColSize="+curcollection.size()+",preColSize="+precollection.size());
//
//				curPointsList.removeAll(curcollection);
//				prePointsList.removeAll(precollection);
				
				Log.d("PrePointsList", "curSizeRm="+curPointsList.size()+",preSizeRm="+prePointsList.size());

//				for (int i = 0; i < index.size(); i++) {
//					Log.d(TAG, "remove Point Pos:" + curPointsList.get(index.get(i)).x+","+curPointsList.get(index.get(i)).y);
//					curPointsList.remove((int)index.get(i));
//					prePointsList.remove((int)index.get(i));
//					Log.d(TAG, "remove Point:" + i);
//				}

				float pointArray[] = new float[curPointsList.size() * 2];
				int count = 0;
				for (Point pt : curPointsList) {
					pointArray[count] = (float) pt.x * ivimratio;
					pointArray[count + 1] = (float) pt.y * ivimratio;
					count += 2;
				}

				bmCanvas = Bitmap.createBitmap(ivWidth, ivHeight,
						Config.ARGB_8888);
				canvas = new Canvas(bmCanvas);
//				canvas.drawPaint(paintClear);
				canvas.drawPoints(pointArray, paint);

				Paint paintcircle = new Paint();
				paintcircle.setColor(Color.RED);
				paintcircle.setStyle(Paint.Style.STROKE);
				canvas.drawCircle((float)x*ivimratio, (float)y*ivimratio, (float)delta*ivimratio, paintcircle);
				Log.d(TAG, "OnTouch: x=" + x + ", y=" + y);
				
				cvimage.setImageBitmap(bmCanvas);

				return true;
			}
		});
	}

	public void warpImage(byte [] data) {
		try {
			MatOfPoint2f prePoints = new MatOfPoint2f();
			MatOfPoint2f curPoints = new MatOfPoint2f();

			prePoints.fromList(prePointsList);
			curPoints.fromList(curPointsList);
			
			Mat H = new Mat();
			try {
				H = findHomography(curPoints, prePoints);
			} catch (CvException e) {
				e.printStackTrace();
				Log.d(TAG, "FindHomography fail");
			}

			outImage = new Mat(preImage.height(), preImage.width(),
					CvType.CV_8UC3);

			Imgproc.warpPerspective(curImage, outImage, H, outImage.size());

			Bitmap tmpbm = Bitmap.createBitmap(outImage.width(),
					outImage.height(), Config.ARGB_8888);
			Imgproc.cvtColor(outImage, outImage, Imgproc.COLOR_BGR2RGB);
			Utils.matToBitmap(outImage, tmpbm);

			// cvimage.setImageBitmap(tmpbm);

			outdata = new byte[data.length];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			tmpbm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			outdata = baos.toByteArray();
		} catch (CvException e) {
			e.printStackTrace();
			Log.d(TAG, "OpenCV imageWarp fail");
			outdata = data;
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

		// Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		// Matrix m = new Matrix();
		// m.postRotate(90);
		// bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m,
		// true);
		//
		// CompressFormat fmt = Bitmap.CompressFormat.JPEG;
		// bm.compress(fmt, 80, outputStream);

		outputStream.write(data); // 写入sd卡中
		outputStream.close(); // 关闭输出流

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
		// Log.d("assetsInsert", alastfile);
	}

	public void saveToDBandSDCard(byte[] data, String filename)
			throws IOException {
		// Date date = new Date();
		// SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); //
		// 格式化时间
		// String filename = format.format(date) + ".jpg";

		String datestring = filename.substring(0, filename.length() - 4);
		File fileFolder = new File(Environment.getExternalStorageDirectory()
				+ "/timegrow/" + aname + "/");
		if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
			fileFolder.mkdirs();
		}
		File jpgFile = new File(fileFolder, filename);
		FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流

		// Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		// Matrix m = new Matrix();
		// m.postRotate(90);
		// bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m,
		// true);
		//
		// CompressFormat fmt = Bitmap.CompressFormat.JPEG;
		// bm.compress(fmt, 80, outputStream);

		outputStream.write(data); // 写入sd卡中
		outputStream.close(); // 关闭输出流

		alastfile = fileFolder.toString() + "/" + filename;
		asize++;
		tmpalbum.setLastfile(alastfile);
		tmpalbum.setSize(asize);
		// Album album = new Album(aid, aname, asize, alastfile, aadddate);
		updateAlbum(MainActivity.this);

		tmppicture = new Picture();
		tmppicture.setAlbumid(aid);
		tmppicture.setDatetime(datestring);
		tmppicture.setFilename(alastfile);
		insertPicture(MainActivity.this);
		Log.d("assetsInsert", alastfile);
	}

	// 获取assets文件夹下文件地址
	public String initDBbyPath(String dir) throws IOException {

		String dirname = dir;
		String lastfile = "";
		String adddate = getNowDateTimeString();
		int size = 0;

		if (isFirstRun) {
			loadAlbum(MainActivity.this);
		}

		tmpalbum = new Album();
		tmpalbum.setName(dirname);
		tmpalbum.setLastfile(lastfile);
		tmpalbum.setSize(size);
		tmpalbum.setAdddate(adddate);

		insertAlbum(MainActivity.this);

		aid = tmpalbum.getId();
		aname = tmpalbum.getName();
		aadddate = tmpalbum.getAdddate();
		asize = tmpalbum.getSize();
		alastfile = tmpalbum.getLastfile();

		if (isFirstRun) {
			loadPicture(MainActivity.this, aid);
			// Log.d("asmList", "after loadPicture");
		}

		AssetManager asm = getAssets();

		String[] filenames = null;
		try {
			filenames = asm.list(dir);// asm.list("cloud/");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.d("asmList", "fail");
		}
		for (String name : filenames) {
			Log.d("assetsName", name);
		}

		for (String name : filenames) {
			if (name.endsWith(".jpg")) {
				String imagefilename = dir + "/" + name;
				Log.d("imagefilename", imagefilename);
				InputStream imagein = asm.open(imagefilename);
				// byte [] image = imagein.to
				Bitmap image = BitmapFactory.decodeStream(imagein);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				saveToDBandSDCard(baos.toByteArray(), name);

				// ExifInterface exif = new ExifInterface(imagefilename);
				//
				// String time = exif.getAttribute(ExifInterface.TAG_DATETIME);
				// Log.d("imagetime", time);

			}
		}

		return "";

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

			int width_pic = 1024;
			int height_pic = 768;

			List<Size> supporSizes = parameters.getSupportedPictureSizes();
			for (Size tmp : supporSizes) {
				Log.d("info", tmp.height + " * " + tmp.width);
				if (tmp.height < 1000 && tmp.height > 500
						&& (Math.abs(tmp.height * 4 / 3 - tmp.width) < 1)) {
					width_pic = tmp.width;
					height_pic = tmp.height;
					Log.d("info", "set pic size: " + tmp.height + " * "
							+ tmp.width);
					break;
				}
			}
			//
			// Log.i("info", parameters.toString());
			// // parameters = camera.getParameters(); // 获取各项参数
			// parameters.set("orientation", "portrait");
			// parameters.set("rotation", 90);

			parameters.setRotation(90);
			parameters.setPictureFormat(ImageFormat.JPEG); // 设置图片格式
			parameters.setPreviewSize(width, height); // 设置预览大小
			// parameters.setPreviewFrameRate(5); // 设置每秒显示4帧
			parameters.setPictureSize(width_pic, height_pic);// width,
																// height);//
																// width,
			// height); //
			// 设置保存的图片尺寸
			parameters.setJpegQuality(60); // 设置照片质量

			camera.setParameters(parameters);
		}

		// 开始拍照时调用该方法
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open(); // 打开摄像头
				camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
				camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
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

	// /**
	// * 点击手机屏幕是，显示两个按钮
	// */
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// layout.setVisibility(ViewGroup.VISIBLE); // 设置视图可见
	// break;
	// }
	// return true;
	// }

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
