package com.guangjun.timegrow;

//import static com.guangjun.timegrow.MainActivity.*;
import static com.guangjun.timegrow.DBUtil.getPicIdFromPrefs;
import static com.guangjun.timegrow.DBUtil.getIdFromPrefs;
import static com.guangjun.timegrow.Constant.getNowDateTimeString;
import android.app.Activity;
import android.util.Log;

import com.guangjun.timegrow.MainActivity;

public class Picture {
	private int id;
	private int albumid;
	private String filename;
	private String datetime;

	public Picture(int id, int albumid, String filename, String datetime) {
		this.id = id;
		this.albumid = albumid;
		this.filename = filename;
		this.datetime = datetime;
	}

	public Picture() {
		id = 0;
		albumid = 0;
		filename = "";
		datetime = getNowDateTimeString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAlbumid() {
		return albumid;
	}

	public void setAlbumid(int albumid) {
		this.albumid = albumid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String toInsertSql(MainActivity father)// 获取schedule对象存入数据库时的sql语句
	{
		StringBuffer sb = new StringBuffer();
		sb.append("insert into picture values(");
		id = getPicIdFromPrefs(father);
		sb.append(id);
		sb.append(",'");
		sb.append(albumid);
		sb.append("','");
		sb.append(filename);
		sb.append("','");
		sb.append(datetime);
		sb.append("')");
		Log.d("toInsertSql", sb.toString());
		return sb.toString();
	}

	public String toUpdateSql(MainActivity father)// 获取schedule对象更新时的sql语句
	{
		int preId = id;// 记录之前的sn
		StringBuffer sb = new StringBuffer();
		sb.append("update picture set id=");
//		id = getPicIdFromPrefs(father);// 换成新的sn
		sb.append(id);
		sb.append(",albumid='");
		sb.append(albumid);
		sb.append("',filename=");
		sb.append(filename);
		sb.append(",datetime=");
		sb.append(datetime);
		sb.append("' where id=");
		sb.append(preId);
		Log.d("toUpdateSql", sb.toString());
		return sb.toString();
	}

	public String toInsertSql(Activity father)// 获取schedule对象存入数据库时的sql语句
	{
		StringBuffer sb = new StringBuffer();
		sb.append("insert into picture values(");
		id = getPicIdFromPrefs(father);
		sb.append(id);
		sb.append(",'");
		sb.append(albumid);
		sb.append("','");
		sb.append(filename);
		sb.append("','");
		sb.append(datetime);
		sb.append("')");
		Log.d("toInsertSql", sb.toString());
		return sb.toString();
	}

	public String toUpdateSql(Activity father)// 获取schedule对象更新时的sql语句
	{
		int preId = id;// 记录之前的sn
		StringBuffer sb = new StringBuffer();
		sb.append("update picture set id=");
//		id = getPicIdFromPrefs(father);// 换成新的sn
		sb.append(id);
		sb.append(",albumid='");
		sb.append(albumid);
		sb.append("',filename=");
		sb.append(filename);
		sb.append(",datetime=");
		sb.append(datetime);
		sb.append("' where id=");
		sb.append(preId);
		Log.d("toUpdateSql", sb.toString());
		return sb.toString();
	}
}
