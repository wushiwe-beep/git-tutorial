package cn.wangshidai.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import cn.wangshidai.entity.MessageInfo;
import cn.wangshidai.entity.UserInfo;

/**
 * ���߳������ࣨ������ǡ���������
 * ����һֱˢ�¶�ȡ�����ת����������Ϣ
 * @author WsdYock
 *
 */
public class RefreshMessageThread implements Runnable {

	//��ǰ��Ķ���
	private static RefreshMessageThread instance;
	
	//////////////����2�����ԣ�ϣ��������Ŀ��ȫ�ֹ���//////////////////
	private Socket clientSocket;		//��ǰ�û������������������Socket
	private UserInfo currentUserInfo;	//��ǰ�û�����
	
	//���map���ڴ洢�ҵ�ǰ�û��� �����˵�������Ϣ
	/*
	 * �����ҵ�ǰ��zhangsan
	 * 
	 * {
	 * "lisi":"��Ϣ����",
	 * "wangwu":"��Ϣ����"
	 * }
	 */
	public Map<String,StringBuffer> messageMap = new HashMap<String, StringBuffer>();
	
	
	public Socket getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	public UserInfo getCurrentUserInfo() {
		return currentUserInfo;
	}
	public void setCurrentUserInfo(UserInfo currentUserInfo) {
		this.currentUserInfo = currentUserInfo;
	}

	//���췽��˽�л����������ⲿnew����
	private RefreshMessageThread(Socket clientSocket,UserInfo userInfo){
		this.clientSocket = clientSocket;
		this.currentUserInfo = userInfo;
	}
	
	
	/*
	 * �ⲿ���������������������
	 */
	public static RefreshMessageThread getObject(Socket clientSocket,UserInfo userInfo){
		if(instance==null){
			instance = new RefreshMessageThread(clientSocket, userInfo);
		}
		return instance;
	}
	
	/*
	 * ����������Թ��ⲿ�õ���ǰ��ʵ��/����ÿһ�ε����������������Ψһ��ʵ����
	 */
	public static RefreshMessageThread getObject(){
		return instance;
	}
	
	
	@Override
	public void run() {
		
		while (true) {
			
			
			try {
				//�������Է���˵���Ϣ
				ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
				MessageInfo msgInfo = (MessageInfo) input.readObject();
				
				//System.out.println(msgInfo.getSend_user()+"����˵:"+msgInfo.getMsg_content());
				boolean hasKey = this.messageMap.containsKey(msgInfo.getSend_user());
				if(!hasKey){//key������
					StringBuffer sb = new StringBuffer();
					sb.append(msgInfo.getSend_user()+"����˵:"+msgInfo.getMsg_content()+"\n");
					this.messageMap.put(msgInfo.getSend_user(), sb);
				}else{//key����
					StringBuffer sb = this.messageMap.get(msgInfo.getSend_user());
					sb.append(msgInfo.getSend_user()+"����˵:"+msgInfo.getMsg_content()+"\n");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

	
	
}
