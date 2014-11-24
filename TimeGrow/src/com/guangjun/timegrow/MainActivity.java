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
import java.util.Date;
import java.util.List;

import static com.guangjun.timegrow.Constant.*;
import static com.guangjun.timegrow.DBUtil.*;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
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
import android.media.ExifInterface;
import android.net.MailTo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
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

	public void notifyClick() {

		Intent intent = new Intent("TimegrowAlarm");
		intent.putExtra("name", tmpalbum.getName());
		intent.putExtra("lastfile", tmpalbum.getLastfile());
		// sendBroadcast(intent);
		sendBroadcast(intent);
		Log.i("info", "broadcast send.");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setBackgroundDrawable(
				this.getBaseContext().getResources()
						.getDrawable(R.drawable.bg_actionbar));
		getActionBar().show();

		DisplayMetrics dm = new DisplayMetrics();
		// ��ȡ��Ļ��Ϣ
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
		boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		Editor editor = sharedPreferences.edit();
		if (isFirstRun) {
			Log.d("debug", "��һ������");
			editor.putBoolean("isFirstRun", false);
			editor.commit();

			try {				initDBbyPath("grass");

				initDBbyPath("��");
				initDBbyPath("̩��");
				initDBbyPath("��·");
				initDBbyPath("����");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.d("debug", "���ǵ�һ������");
		}

		gotoMain();
	}

	public void gotoMain() {
		setContentView(R.layout.activity_main);
		curr = Layout.MAIN;

		// notify���԰�ť
		// Button btn_notify = (Button) findViewById(R.id.btn_notify);
		// btn_notify.bringToFront();
		// btn_notify.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
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

		final ArrayList<Boolean> alIsSelected = new ArrayList<Boolean>();// ��¼ListView������ѡ���˵ı�־λ

		GridView gv_main = (GridView) findViewById(R.id.gridView1);
		ImageButton btnadd = (ImageButton) findViewById(R.id.btn_add);
		// ImageView im_downbar = (ImageView)findViewById(R.id.im_downbar);
		//
		// im_downbar.bringToFront();
		btnadd.bringToFront();

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
					Toast.makeText(MainActivity.this, "���ڴ�ӡ�쳣",
							Toast.LENGTH_SHORT).show();
				}

				tvDate.setText(lastdate);
				tvDate.setTextSize(8);

				// ������ѡ���ˣ�����ɫ����Ϊѡ�еı���ɫ
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
				.setTitle("ɾ���Ի���").setIcon(R.drawable.ic_camera)
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
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);

		// image.setScaleType(ScaleType.FIT_CENTER);
		image.setLayoutParams(lp);

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
		holder.setFixedSize(height, width); // ����Surface�ֱ���
		holder.setKeepScreenOn(true);// ��Ļ����
		holder.addCallback(new SurfaceCallback());// ΪSurfaceView�ľ�����һ���ص�����

	}

	private void gotoGallery() {
		setContentView(R.layout.gallery_layout);
		curr = Layout.GALLERY;

		final ImageView gallery = (ImageView) findViewById(R.id.iv_gallery);
		final SeekBar bar = (SeekBar) findViewById(R.id.bar_gallery);
		ImageButton btn_camera = (ImageButton) findViewById(R.id.btn_takepic_gallery);
		RelativeLayout gallery_ll = (RelativeLayout) findViewById(R.id.gallery_ll);
		TextView tv_name = (TextView) findViewById(R.id.tv_gallery_name);
		final TextView tv_date = (TextView) findViewById(R.id.tv_gallery_date);
		ImageButton btn_edit = (ImageButton) findViewById(R.id.btn_gallery_edit);
		ImageButton btn_delete = (ImageButton) findViewById(R.id.btn_gallery_delete);
		final ImageButton btn_play = (ImageButton) findViewById(R.id.btn_play);

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
									Thread.sleep(100);
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
							.setTitle(tmpalbum.getName() + " ɾ���Ի���")
							.setIcon(R.drawable.ic_camera)
							.setMessage(
									"ȷ��ɾ��"
											+ getDateTimeStr(tmppicture
													.getDatetime()) + "�� "
											+ tmpalbum.getName() + " ��")
							// �൱�ڵ��ȷ�ϰ�ť
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											Toast.makeText(
													MainActivity.this,
													"ɾ����"
															+ getDateTimeStr(tmppicture
																	.getDatetime())
															+ "�� "
															+ tmpalbum
																	.getName(),
													Toast.LENGTH_SHORT).show();
											Log.d("deleteItem", "delete"
													+ tmppicture.getFilename());
											// ����picture
											deletePicture(MainActivity.this);
											allpicture.remove(tmppicindex);
											tmppicindex = tmppicindex == 0 ? 0
													: --tmppicindex;
											if (allpicture.size() > 0) {
												tmppicture = allpicture
														.get(tmppicindex);
											}
											// ����album
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
											// ���½���
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
							// �൱�ڵ��ȡ����ť
							.setNegativeButton("ȡ��",
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
					Toast.makeText(MainActivity.this, "û�п�ɾ������Ƭ",
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

				// ��װ��String getDateTimeStr(String)
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
				.setTitle(tmpalbum.getName() + " ɾ���Ի���")
				.setIcon(R.drawable.ic_camera)
				.setMessage(
						"ȷ��ɾ��" + getDateTimeStr(tmppicture.getDatetime())
								+ "�� " + tmpalbum.getName() + " ��")
				// �൱�ڵ��ȷ�ϰ�ť
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Toast.makeText(
								MainActivity.this,
								"ɾ����"
										+ getDateTimeStr(tmppicture
												.getDatetime()) + "�� "
										+ tmpalbum.getName(),
								Toast.LENGTH_SHORT).show();
						Log.d("deleteItem", "delete" + tmppicture.getFilename());
						deleteAlbum(MainActivity.this);
						allpicture.remove(tmppicindex);
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

	}

	protected void gotoSetAlarm() {
		Intent intent = new Intent("TimegrowAlarm");
		intent.putExtra("name", tmpalbum.getName());
		intent.putExtra("lastfile", tmpalbum.getLastfile());

		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this,
				tmpalbum.getId(), intent, 0);
		// ��ʼʱ��
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

			// 5��һ�����ڣ���ͣ�ķ��͹㲥
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
			case GALLERY_EDIT:
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

			ImageButton btnok = (ImageButton) findViewById(R.id.btn_ok_pic);
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

			ImageButton btncencel = (ImageButton) findViewById(R.id.btn_cancel_pic);
			btncencel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					camera.startPreview(); // �����պ����¿�ʼԤ��
					Bitmap bm = BitmapFactory.decodeFile(alastfile);
					image.setImageBitmap(bm);
					image.setAlpha(alpha);
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

		// Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		// Matrix m = new Matrix();
		// m.postRotate(90);
		// bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m,
		// true);
		//
		// CompressFormat fmt = Bitmap.CompressFormat.JPEG;
		// bm.compress(fmt, 80, outputStream);

		outputStream.write(data); // д��sd����
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

	// ��ȡassets�ļ������ļ���ַ
	public String initDBbyPath(String dir) throws IOException {

		String dirname = dir;
		String lastfile = "";
		String adddate = getNowDateTimeString();
		int size = 0;

		loadAlbum(MainActivity.this);

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

		loadPicture(MainActivity.this, aid);
		Log.d("asmList", "after loadPicture");

		AssetManager asm = getAssets();

		String[] filenames = null;
		try {
			filenames = asm.list("");//dir);// asm.list("cloud/");
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
				saveToDBandSDCard(baos.toByteArray());

				// ExifInterface exif = new ExifInterface(imagefilename);
				//
				// String time = exif.getAttribute(ExifInterface.TAG_DATETIME);
				// Log.d("imagetime", time);

			}
		}

		return "";

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
			// parameters.set("orientation", "portrait");
			// parameters.set("rotation", 90);

			parameters.setRotation(90);
			parameters.setPictureFormat(PixelFormat.JPEG); // ����ͼƬ��ʽ
			parameters.setPreviewSize(width, height); // ����Ԥ����С
			// parameters.setPreviewFrameRate(5); // ����ÿ����ʾ4֡
			parameters.setPictureSize(width, height);// width, height); //
														// ���ñ����ͼƬ�ߴ�
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
