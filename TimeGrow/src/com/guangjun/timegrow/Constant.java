package com.guangjun.timegrow;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Constant {
	final static int DIALOG_SET_SEARCH_RANGE = 1;// 设置搜索日期范围对话框
	final static int DIALOG_SET_DATETIME = 2;// 设置日期时间对话框
	final static int DIALOG_SCH_DEL_CONFIRM = 3;// 日程删除确认
	final static int DIALOG_CHECK = 4;// 查看日程
	final static int DIALOG_ALL_DEL_CONFIRM = 5;// 删除全部过期日程
	final static int DIALOG_ABOUT = 6;// 关于对话框

	final static int MENU_HELP = 1;// 菜单帮助
	final static int MENU_ABOUT = 2;// 菜单关于

	public static enum WhoCall {// 判断谁调用了dialogSetRange，以决定哪个控件该gone或者visible
		SETTING_ALARM, // 表示设置闹钟 按钮
		SETTING_DATE, // 表示设置日期按钮
		SETTING_RANGE, // 表示设置日程查找范围按钮
		NEW, // 表示新建日程按钮
		EDIT, // 表示修改日程按钮
		SEARCH_RESULT// 表示查找按钮

	}

	public static enum Layout {
		WELCOME_VIEW, MAIN, // 主界面
		SETTING, // 日程设置
		TYPE_MANAGER, // 类型管理
		SEARCH, // 查找
		SEARCH_RESULT, // 查找结果界面
		HELP, // 帮助界面
		ABOUT,

		CAMERA, GALLERY, GALLERY_EDIT
	}

	public static String[] alarmFreq = new String[] { "1小时", "2小时", "3小时",
			"12小时", "1天", "2天", "3天", "5天", "1周", "2周", "1月", "2月", "6月", "1年" };

	public static long[] alarmNextMilli = { 3600000, 7200000, 10800000,
			43200000, 86400000, 172800000, 259200000, 432000000, 604800000,
			1209600000, 2592000000L, 5184000000L, 15768000000L, 31536000000L };

	// public static String getNowDateString()//获得当前日期方法并转换格式YYYY/MM/DD
	// {
	// Calendar c=Calendar.getInstance();
	// String nowDate=Person.toDateString(c.get(Calendar.YEAR),
	// c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
	// return nowDate;
	//
	// }
	// public static String getNowTimeString()//获得当前时间，并转换成格式HH:MM
	// {
	// Calendar c=Calendar.getInstance();
	// int nowh=c.get(Calendar.HOUR_OF_DAY);
	// int nowm=c.get(Calendar.MINUTE);
	// String nowTime=(nowh<10?"0"+nowh:""+nowh)+":"+(nowm<10?"0"+nowm:""+nowm);
	// return nowTime;
	// }

	public static String getDateTimeStr(String datetime) {
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat ft_out = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String date_pic = "[Date]";
		try {
			date_pic = ft_out.format(ft.parse(datetime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date_pic;
	}

	public static String getNowDateTimeString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(new java.util.Date());
	}
}