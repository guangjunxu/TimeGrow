package com.guangjun.timegrow;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Constant {
	final static int DIALOG_SET_SEARCH_RANGE = 1;// �����������ڷ�Χ�Ի���
	final static int DIALOG_SET_DATETIME = 2;// ��������ʱ��Ի���
	final static int DIALOG_SCH_DEL_CONFIRM = 3;// �ճ�ɾ��ȷ��
	final static int DIALOG_CHECK = 4;// �鿴�ճ�
	final static int DIALOG_ALL_DEL_CONFIRM = 5;// ɾ��ȫ�������ճ�
	final static int DIALOG_ABOUT = 6;// ���ڶԻ���

	final static int MENU_HELP = 1;// �˵�����
	final static int MENU_ABOUT = 2;// �˵�����

	public static enum WhoCall {// �ж�˭������dialogSetRange���Ծ����ĸ��ؼ���gone����visible
		SETTING_ALARM, // ��ʾ�������� ��ť
		SETTING_DATE, // ��ʾ�������ڰ�ť
		SETTING_RANGE, // ��ʾ�����ճ̲��ҷ�Χ��ť
		NEW, // ��ʾ�½��ճ̰�ť
		EDIT, // ��ʾ�޸��ճ̰�ť
		SEARCH_RESULT// ��ʾ���Ұ�ť

	}

	public static enum Layout {
		WELCOME_VIEW, MAIN, // ������
		SETTING, // �ճ�����
		TYPE_MANAGER, // ���͹���
		SEARCH, // ����
		SEARCH_RESULT, // ���ҽ������
		HELP, // ��������
		ABOUT,

		CAMERA, GALLERY, GALLERY_EDIT
	}

	public static String[] alarmFreq = new String[] { "1Сʱ", "2Сʱ", "3Сʱ",
			"12Сʱ", "1��", "2��", "3��", "5��", "1��", "2��", "1��", "2��", "6��", "1��" };

	public static long[] alarmNextMilli = { 3600000, 7200000, 10800000,
			43200000, 86400000, 172800000, 259200000, 432000000, 604800000,
			1209600000, 2592000000L, 5184000000L, 15768000000L, 31536000000L };

	// public static String getNowDateString()//��õ�ǰ���ڷ�����ת����ʽYYYY/MM/DD
	// {
	// Calendar c=Calendar.getInstance();
	// String nowDate=Person.toDateString(c.get(Calendar.YEAR),
	// c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
	// return nowDate;
	//
	// }
	// public static String getNowTimeString()//��õ�ǰʱ�䣬��ת���ɸ�ʽHH:MM
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