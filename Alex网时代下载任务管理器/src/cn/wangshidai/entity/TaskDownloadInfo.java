package cn.wangshidai.entity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import cn.wangshidai.util.PublicUtil;


public class TaskDownloadInfo extends Thread implements Serializable {
	
	private String file_name;			//�ļ���
	private String file_date;			//��������
	private long file_size;				//�ļ���С
	private boolean is_ok;				//�Ƿ������(Ĭ��false)
	private String file_url;			//���ص�ַ
	private boolean is_pause;			//�Ƿ���ͣ(Ĭ��false)
	private boolean is_delete;			//�Ƿ�ɾ��(Ĭ����false)

	
	
	
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getFile_date() {
		return file_date;
	}
	public void setFile_date(String file_date) {
		this.file_date = file_date;
	}
	public long getFile_size() {
		return file_size;
	}
	public void setFile_size(long file_size) {
		this.file_size = file_size;
	}
	public boolean isIs_ok() {
		return is_ok;
	}
	public void setIs_ok(boolean is_ok) {
		this.is_ok = is_ok;
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	public boolean isIs_pause() {
		return is_pause;
	}
	public void setIs_pause(boolean is_pause) {
		this.is_pause = is_pause;
	}
	public boolean isIs_delete() {
		return is_delete;
	}
	public void setIs_delete(boolean is_delete) {
		this.is_delete = is_delete;
	}

	
	@Override
	public void run() {
		download(this.file_url);
	}
	
	
	private void download(String fileUrl){
		InputStream input = null;
		OutputStream out = null;
		
		//File��ֻ��ָ�򱾵��ļ�
		//File
		try {
			URL url = new URL(fileUrl);
			//������
			input = url.openStream();
			//�����
			File file = new File(PublicUtil.MY_HOME);
			if(!file.exists()){
				file.mkdirs();
			}
			
			out = new BufferedOutputStream(new FileOutputStream(new File(file, this.file_name)));
			
			while (true) {
				//����˯�ߣ���
				if(is_pause){
					try {
						Thread.sleep(Long.MAX_VALUE);//�൱�ڡ�����˯�ߡ�
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						System.out.println("���˽������ˣ���");
					}
					
				}
				if (is_delete) {
					out.close();
					input.close();
				}
				
				
				
				//��1���ֽڣ�-128 ~ 127��
				int b = input.read();
				if(b==-1){//�Ѿ����������ֽ��ˣ�����
					break;
				}
				//д1���ֽ�
				out.write(b);
				
				//�ۼ��ֽڴ�С
				this.file_size++;
			}
			
			
			//������ϣ���
			this.is_ok = true;
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				input.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public String toString() {
		return "TaskDownloadInfo [file_name=" + file_name + ", file_date="
				+ file_date + ", file_size=" + file_size + ", is_ok=" + is_ok
				+ ", file_url=" + file_url + "]";
	}	
	
	
}
