package cn.wangshidai.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.wangshidai.entity.TaskDownloadInfo;

public class PublicUtil {
	
	
	//ָ������Ŀ¼
	public static final String MY_HOME = "/Users/wushiwei/WangshidaiDownload";
	//ָ�������ļ��б������ݵ��ļ�·��
	public static final String DB_FILE = "/Users/wushiwei/WangshidaiDownload/.wangshidai.data";
	
	
	//�����ļ��б�
	public static List<TaskDownloadInfo>  wangshidaiList = new ArrayList<TaskDownloadInfo>();
	

	
	
	/*
	 * ��Date ת�� ָ����ʽ���ַ���
	 */
	public static String getDateStr(String format,Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
}
