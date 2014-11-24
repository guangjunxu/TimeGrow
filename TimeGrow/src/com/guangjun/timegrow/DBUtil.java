package com.guangjun.timegrow;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import static com.guangjun.timegrow.MainActivity.*;

public class DBUtil {
	static SQLiteDatabase sld;

	public static void loadAlbum(MainActivity father)// ���ճ����ݿ��ȡ�ճ�����
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.guangjun.timegrow/myDb", null,
					SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			String sql = "create table if not exists album("
					+ "id integer primary key," + "name char(50),"
					+ "size integer," + "lastfile varchar(50),"
					+ "adddate varchar(50),"
					+ "hasalarm boolean,"
					+ "alarmfreq integer" + ")";
			sld.execSQL(sql);
			Cursor cursor = sld.query("album", null, null, null, null, null,
					"adddate asc");// ��datetime1������

			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				int size = cursor.getInt(2);
				String lastfile = cursor.getString(3);
				String adddate = cursor.getString(4);
				String hasalarm = cursor.getString(5);
				int alarmfreq = cursor.getInt(6);
				Album tmpalbum = new Album(id, name, size, lastfile, adddate,
						hasalarm, alarmfreq);
				allalbum.add(tmpalbum);
				Log.d("schdata", "" + cursor.getPosition() + ":id=" + id + ":"
						+ name + "," + size + "," + lastfile + "," + adddate + "," + hasalarm + "," + alarmfreq);
			}
			sld.close();
			cursor.close();
		} catch (Exception e) {
			Toast.makeText(father, "Album���ݿ�򿪴�������" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception", e.toString());
		}
	}

	public static void insertAlbum(MainActivity father)// �����ճ�
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.guangjun.timegrow/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql = father.tmpalbum.toInsertSql(father);
			sld.execSQL(sql);

			sld.close();

		} catch (Exception e) {
			Toast.makeText(father, "Album���ݿ���´���" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception!!", e.toString());
		}
	}

	public static void updateAlbum(MainActivity father)// �����ճ�
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.guangjun.timegrow/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql = father.tmpalbum.toUpdateSql(father);
			sld.execSQL(sql);
			sld.close();
		} catch (Exception e) {
			Toast.makeText(father, "Album���ݿ���´���" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception!!", e.toString());
		}
	}

	public static void deleteAlbum(MainActivity father)// ɾ���ճ�
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.guangjun.timegrow/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			int id = father.tmpalbum.getId();
			String sql = "delete from album where id=" + id;
			sld.execSQL(sql);
			sld.close();
			Toast.makeText(father, "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(father, "Albumɾ������" + e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	// public static void loadAlbum(Activity father, ArrayList<Album>
	// allalbum)// ���ճ����ݿ��ȡ�ճ�����
	// {
	// try {
	// sld = SQLiteDatabase.openDatabase(
	// "/data/data/com.guangjun.timegrow/myDb", null,
	// SQLiteDatabase.OPEN_READWRITE
	// | SQLiteDatabase.CREATE_IF_NECESSARY);
	// String sql = "create table if not exists album("
	// + "id integer primary key," + "name char(50),"
	// + "size integer," + "lastfile varchar(50),"
	// + "adddate varchar(50)" + ")";
	// sld.execSQL(sql);
	// Cursor cursor = sld.query("album", null, null, null, null, null,
	// "adddate asc");// ��datetime1������
	//
	// while (cursor.moveToNext()) {
	// int id = cursor.getInt(0);
	// String name = cursor.getString(1);
	// int size = cursor.getInt(2);
	// String lastfile = cursor.getString(3);
	// String adddate = cursor.getString(4);
	// Album tmpalbum = new Album(id, name, size, lastfile, adddate);
	// allalbum.add(tmpalbum);
	// Log.d("schdata", "" + cursor.getPosition() + ":id=" + id + ":"
	// + name + "," + size + "," + lastfile + "," + adddate);
	// }
	// sld.close();
	// cursor.close();
	// } catch (Exception e) {
	// Toast.makeText(father, "Album���ݿ�򿪴�������" + e.toString(),
	// Toast.LENGTH_LONG).show();
	// Log.d("exception", e.toString());
	// }
	// }
	//
	// public static void insertAlbum(Activity father, Album tmpalbum)// �����ճ�
	// {
	// try {
	// sld = SQLiteDatabase.openDatabase(
	// "/data/data/com.guangjun.timegrow/myDb", null,
	// SQLiteDatabase.OPEN_READWRITE);
	// String sql = tmpalbum.toInsertSql(father);
	// sld.execSQL(sql);
	//
	// sld.close();
	//
	// } catch (Exception e) {
	// Toast.makeText(father, "Album���ݿ���´���" + e.toString(),
	// Toast.LENGTH_LONG).show();
	// Log.d("exception!!", e.toString());
	// }
	// }
	//
	// public static void updateAlbum(Activity father, Album tmpalbum)// �����ճ�
	// {
	// try {
	// sld = SQLiteDatabase.openDatabase(
	// "/data/data/com.guangjun.timegrow/myDb", null,
	// SQLiteDatabase.OPEN_READWRITE);
	// String sql = tmpalbum.toUpdateSql(father);
	// sld.execSQL(sql);
	// sld.close();
	// } catch (Exception e) {
	// Toast.makeText(father, "Album���ݿ���´���" + e.toString(),
	// Toast.LENGTH_LONG).show();
	// Log.d("exception!!", e.toString());
	// }
	// }

	// Picture���ݿ����=================================================================
	public static void loadPicture(MainActivity father, int curalbumid)// ���ճ����ݿ��ȡ�ճ�����
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.guangjun.timegrow/myDb", null,
					SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			String sql = "create table if not exists picture("
					+ "id integer primary key," + "albumid integer,"
					+ "filename varchar(50)," + "datetime varchar(50)" + ")";
			sld.execSQL(sql);
			Log.d("createPicture", sql);
			String str = "albumid=" + curalbumid;
			Cursor cursor = sld.query("picture", null, str, null, null, null,
					"datetime asc");// ��datetime1������

			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				int albumid = cursor.getInt(1);
				String filename = cursor.getString(2);
				String datetime = cursor.getString(3);
				Picture tmpalbum = new Picture(id, albumid, filename, datetime);
				allpicture.add(tmpalbum);
				Log.d("loadPicture", "" + cursor.getPosition() + ":id=" + id
						+ ":" + albumid + "," + filename + "," + datetime);
			}
			sld.close();
			cursor.close();
		} catch (Exception e) {
			Toast.makeText(father, "Picture���ݿ�򿪴�������" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception", e.toString());
		}
	}

	public static void insertPicture(MainActivity father)// �����ճ�
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.guangjun.timegrow/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql = father.tmppicture.toInsertSql(father);
			sld.execSQL(sql);

			sld.close();

		} catch (Exception e) {
			Toast.makeText(father, "Picture���ݿ���´���" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception!!", e.toString());
		}
	}

	public static void deletePicture(MainActivity father)// ɾ���ճ�
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/com.guangjun.timegrow/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			int id = father.tmppicture.getId();
			String sql = "delete from picture where id=" + id;
			sld.execSQL(sql);
			sld.close();
			Toast.makeText(father, "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(father, "Pictureɾ������" + e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	// public static void loadPicture(Activity father, int curalbumid)//
	// ���ճ����ݿ��ȡ�ճ�����
	// {
	// try {
	// sld = SQLiteDatabase.openDatabase(
	// "/data/data/com.guangjun.timegrow/myDb", null,
	// SQLiteDatabase.OPEN_READWRITE
	// | SQLiteDatabase.CREATE_IF_NECESSARY);
	// String sql = "create table if not exists picture("
	// + "id integer primary key," + "albumid integer,"
	// + "filename varchar(50)," + "datetime varchar(50)" + ")";
	// sld.execSQL(sql);
	// String str = "albumid=" + curalbumid;
	// Cursor cursor = sld.query("album", null, str, null, null, null,
	// "datetime desc");// ��datetime1������
	//
	// while (cursor.moveToNext()) {
	// int id = cursor.getInt(0);
	// int albumid = cursor.getInt(1);
	// String filename = cursor.getString(2);
	// String datetime = cursor.getString(3);
	// Picture tmpalbum = new Picture(id, albumid, filename, datetime);
	// allpicture.add(tmpalbum);
	// Log.d("schdata", "" + cursor.getPosition() + ":id=" + id + ":"
	// + albumid + "," + filename + "," + datetime);
	// }
	// sld.close();
	// cursor.close();
	// } catch (Exception e) {
	// Toast.makeText(father, "Picture���ݿ�򿪴�������" + e.toString(),
	// Toast.LENGTH_LONG).show();
	// Log.d("exception", e.toString());
	// }
	// }
	//
	// public static void insertPicture(Activity father, Picture tmppicture)//
	// �����ճ�
	// {
	// try {
	// sld = SQLiteDatabase.openDatabase(
	// "/data/data/com.guangjun.timegrow/myDb", null,
	// SQLiteDatabase.OPEN_READWRITE);
	// String sql = tmppicture.toInsertSql(father);
	// sld.execSQL(sql);
	//
	// sld.close();
	//
	// } catch (Exception e) {
	// Toast.makeText(father, "Picture���ݿ���´���" + e.toString(),
	// Toast.LENGTH_LONG).show();
	// Log.d("exception!!", e.toString());
	// }
	// }

	// public static void updatePicture(MainActivity father)// �����ճ�
	// {
	// try {
	// sld = SQLiteDatabase.openDatabase(
	// "/data/data/com.guangjun.timegrow/myDb", null,
	// SQLiteDatabase.OPEN_READWRITE);
	// String sql = father.tmppicture.toUpdateSql(father);
	// sld.execSQL(sql);
	// sld.close();
	// } catch (Exception e) {
	// Toast.makeText(father, "Picture���ݿ���´���" + e.toString(),
	// Toast.LENGTH_LONG).show();
	// Log.d("exception!!", e.toString());
	// }
	// }

	//
	// public static void deletePassedSchedule(RcActivity father)//ɾ�����й����ճ�
	// {
	// try
	// {
	// sld=SQLiteDatabase.openDatabase
	// (
	// "/data/data/com.bn.rcgl/myDb",
	// null,
	// SQLiteDatabase.OPEN_READWRITE
	// );
	// String nowDate=getNowDateString();
	// String nowTime=getNowTimeString();
	// String
	// sql="date1<'"+nowDate+"' or date1='"+nowDate+"' and time1<'"+nowTime+"'";
	// sql="delete from schedule where date1<'"+nowDate+"' or date1='"+nowDate+"' and time1<'"+nowTime+"'";
	// sld.execSQL(sql);
	// sld.close();
	// Toast.makeText(father, "�ɹ�ɾ�������ճ�", Toast.LENGTH_SHORT).show();
	// }
	// catch(Exception e)
	// {
	// Toast.makeText(father, "�ճ�ɾ������"+e.toString(), Toast.LENGTH_LONG).show();
	// Log.d("error", e.toString());
	// }
	// }
	//
	// public static void searchSchedule(RcActivity father,ArrayList<String>
	// allKindsType)//�����ճ�
	// {
	// ArrayList<Boolean> alSelectedType=father.alSelectedType;
	// try
	// {
	// sld=SQLiteDatabase.openDatabase
	// (
	// "/data/data/com.bn.rcgl/myDb",
	// null,
	// SQLiteDatabase.OPEN_READONLY
	// );
	// String[] args=new String[2];
	// args[0]=father.rangeFrom;
	// args[1]=father.rangeTo;
	// String sql="select * from schedule where date1 between ? and ?";
	// StringBuffer sbtmp=new StringBuffer();
	// sbtmp.append(" and (type=");
	// for(int i=0;i<alSelectedType.size();i++)
	// {
	// if(alSelectedType.get(i))
	// {
	// sbtmp.append("'");
	// sbtmp.append(allKindsType.get(i));
	// sbtmp.append("' or type=");
	// }
	// }
	// String strSelectedType=sbtmp.toString();
	// strSelectedType=strSelectedType.substring(0,
	// strSelectedType.length()-9);//���ȥ�������" or type="
	// sql+=strSelectedType+")";
	//
	// Log.d("search sql:", sql);
	//
	// Cursor cursor=sld.rawQuery(sql,args);
	// Toast.makeText(father, "������"+cursor.getCount()+"���ճ�",
	// Toast.LENGTH_SHORT).show();
	// alSch.clear();
	// while(cursor.moveToNext())
	// {
	// int sn=cursor.getInt(0);
	// String date1=cursor.getString(1);
	// String time1=cursor.getString(2);
	// String date2=cursor.getString(3);
	// String time2=cursor.getString(4);
	// String title=cursor.getString(5);
	// String note=cursor.getString(6);
	// String type=cursor.getString(7);
	// String timeSet=cursor.getString(8);
	// String alarmSet=cursor.getString(9);
	// Person schTemp=new
	// Person(sn,date1,time1,date2,time2,title,note,type,timeSet,alarmSet);
	// alSch.add(schTemp);
	// }
	// sld.close();
	// cursor.close();
	// }
	// catch(Exception e)
	// {
	// Toast.makeText(father, e.toString(), Toast.LENGTH_SHORT).show();
	// }
	// }
	// ============================���д����ճ����ݿ�ķ���end==============================

	public static int getIdFromPrefs(Activity father)// ��ȡpreferences������ճ�sn
	{
		SharedPreferences sp = father.getSharedPreferences("id", MODE_PRIVATE);
		int id = sp.getInt("id", 0);
		Editor editor = sp.edit();
		editor.putInt("id", id + 1);
		editor.commit();
		return id;
	}

	public static int getPicIdFromPrefs(Activity father)// ��ȡpreferences������ճ�sn
	{
		SharedPreferences sp = father.getSharedPreferences("id", MODE_PRIVATE);
		int id = sp.getInt("pic_id", 0);
		Editor editor = sp.edit();
		editor.putInt("pic_id", id + 1);
		editor.commit();
		return id;
	}
}