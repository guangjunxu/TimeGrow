package com.guangjun.timegrow;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;


import static com.guangjun.timegrow.Constant.*;
import static com.guangjun.timegrow.DBUtil.*;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gotoMain();
	}

	public void gotoMain() {
		setContentView(R.layout.activity_main);
		curr = Layout.MAIN;

		final ArrayList<Boolean> alIsSelected = new ArrayList<Boolean>();// 记录ListView中哪项选中了的标志位

		GridView gv_main = (GridView) findViewById(R.id.gridView1);
		Button btnadd = (Button) findViewById(R.id.btn_add);

		allalbum.clear();
		loadAlbum(this);

		alIsSelected.clear();
		for (int i = 0; i < allalbum.size(); i++)// 全部设置为false，即没有一项选中
		{
			alIsSelected.add(false);
		}

		gv_main.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(MainActivity.this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setPadding(5, 5, 5, 5);

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
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.MATCH_PARENT));
				ll.addView(image);

				String tmpname = allalbum.get(position).getName();
				TextView tvName = new TextView(MainActivity.this);
				tvName.setText(tmpname);
				tvName.setTextSize(17);
				ll.addView(tvName);

				// 如果该项被选中了，背景色设置为选中的背景色
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
//				gotoTakePicture();
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

	public void gotoTakePicture() {
		bundle = new Bundle();
		bundle.putString("lastfile", tmpalbum.getLastfile());
		bundle.putInt("id", tmpalbum.getId());
		bundle.putInt("size", tmpalbum.getSize());
		bundle.putString("name", tmpalbum.getName());
		bundle.putString("adddate", tmpalbum.getAdddate());

		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void gotoGallery() {
		setContentView(R.layout.gallery_layout);
		curr = Layout.GALLERY;

		final ImageView gallery = (ImageView)findViewById(R.id.iv_gallery);
		SeekBar bar = (SeekBar)findViewById(R.id.bar_gallery);
		Button btn_camera = (Button)findViewById(R.id.btn_takepic_gallery);
		
		Log.d("Gallery", "before LoadPicture");
		loadPicture(MainActivity.this, tmpalbum.getId());
		
		bar.setMax(tmpalbum.getSize()-1);
		
		Log.d("tmpalbum", " " + tmpalbum.getId()+" " + tmpalbum.getSize());
		
		Bitmap bm;
		if(tmpalbum.getSize()==0){
			gallery.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
		}
		else {
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
				Bitmap bm = BitmapFactory.decodeFile(allpicture.get(progress).getFilename());
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
				case MAIN:// 在主界面的话退出程序
					System.exit(0);
					break;
				case SETTING:// 在日程编辑界面的话返回主界面
					gotoMain();
					break;
				case TYPE_MANAGER:// //在类型管理界面的话返回日程编辑界面
//					gotoSetting();
					break;
				case SEARCH:// 在日程查找界面的话返回主界面
					gotoMain();
					break;
				case SEARCH_RESULT:// 在日程查找结果界面的话返回日程查找界面
//					gotoSearch();
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
			}
		}
		return true;
	}
}
