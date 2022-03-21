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
	
	
	//指定下载目录
	public static final String MY_HOME = "/Users/wushiwei/WangshidaiDownload";
	//指定下载文件列表保存数据的文件路径
	public static final String DB_FILE = "/Users/wushiwei/WangshidaiDownload/.wangshidai.data";
	
	
	//下载文件列表
	public static List<TaskDownloadInfo>  wangshidaiList = new ArrayList<TaskDownloadInfo>();
	

	
	
	/*
	 * 将Date 转成 指定格式的字符串
	 */
	public static String getDateStr(String format,Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
}
