package com.guangjun.timegrow;

import static com.guangjun.timegrow.DBUtil.getIdFromPrefs;
import static com.guangjun.timegrow.Constant.getNowDateTimeString;
import android.app.Activity;
import android.util.Log;

import com.guangjun.timegrow.MainActivity;

public class Album {
	private int id;
	private String name;
	private int size;
	private String lastfile;
	private String adddate;

	public Album(int id, String name, int size, String lastfile, String adddate) {
		this.id = id;
		this.name = name;
		this.size = size;
		this.lastfile = lastfile;
		this.adddate = adddate;
	}

	public Album() {
		id = 0;
		name = "";
		size = 0;
		lastfile = "";
		adddate = getNowDateTimeString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getLastfile() {
		return lastfile;
	}

	public void setLastfile(String lastfile) {
		this.lastfile = lastfile;
	}

	public String getAdddate() {
		return adddate;
	}

	public void setAdddate(String adddate) {
		this.adddate = adddate;
	}

	public String toInsertSql(MainActivity father)// 获取schedule对象存入数据库时的sql语句
	{
		StringBuffer sb = new StringBuffer();
		sb.append("insert into album values(");
		id = getIdFromPrefs(father);
		sb.append(id);
		sb.append(",'");
		sb.append(name);
		sb.append("',");
		sb.append(size);
		sb.append(",'");
		sb.append(lastfile);
		sb.append("','");
		sb.append(adddate);
		sb.append("')");
		Log.d("toInsertSql", sb.toString());
		return sb.toString();
	}

	public String toUpdateSql(MainActivity father)// 获取schedule对象更新时的sql语句
	{
		int preId = id;// 记录之前的sn
		StringBuffer sb = new StringBuffer();
		sb.append("update album set id=");
//		id = getIdFromPrefs(father);// 换成新的sn
		sb.append(id);
		sb.append(",name='");
		sb.append(name);
		sb.append("',size=");
		sb.append(size);
		sb.append(",lastfile='");
		sb.append(lastfile);
		sb.append("',adddate='");
		sb.append(adddate);
		sb.append("' where id=");
		sb.append(preId);
		Log.d("toUpdateSql", sb.toString());
		return sb.toString();
	}

	public String toInsertSql(Activity father)// 获取schedule对象存入数据库时的sql语句
	{
		StringBuffer sb = new StringBuffer();
		sb.append("insert into album values(");
		id = getIdFromPrefs(father);
		sb.append(id);
		sb.append(",'");
		sb.append(name);
		sb.append("',");
		sb.append(size);
		sb.append(",'");
		sb.append(lastfile);
		sb.append("','");
		sb.append(adddate);
		sb.append("')");
		Log.d("toInsertSql", sb.toString());
		return sb.toString();
	}

	public String toUpdateSql(Activity father)// 获取schedule对象更新时的sql语句
	{
		int preId = id;// 记录之前的sn
		StringBuffer sb = new StringBuffer();
		sb.append("update album set id=");
//		id = getIdFromPrefs(father);// 换成新的sn
		sb.append(id);
		sb.append(",name='");
		sb.append(name);
		sb.append("',size=");
		sb.append(size);
		sb.append(",lastfile='");
		sb.append(lastfile);
		sb.append("',adddate='");
		sb.append(adddate);
		sb.append("' where id=");
		sb.append(preId);
		Log.d("toUpdateSql", sb.toString());
		return sb.toString();
	}

}
