package cn.wangshidai.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import cn.wangshidai.entity.MessageInfo;
import cn.wangshidai.entity.UserInfo;

/**
 * 该线程任务类（这个类是【单例】）
 * 用于一直刷新读取服务端转发过来的消息
 * @author WsdYock
 *
 */
public class RefreshMessageThread implements Runnable {

	//当前类的对象
	private static RefreshMessageThread instance;
	
	//////////////以下2个属性，希望是在项目中全局共享//////////////////
	private Socket clientSocket;		//当前用户用于与服务器交互的Socket
	private UserInfo currentUserInfo;	//当前用户对象
	
	//这个map用于存储我当前用户跟 其他人的聊天消息
	/*
	 * 假设我当前是zhangsan
	 * 
	 * {
	 * "lisi":"消息内容",
	 * "wangwu":"消息内容"
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

	//构造方法私有化（不允许外部new对象）
	private RefreshMessageThread(Socket clientSocket,UserInfo userInfo){
		this.clientSocket = clientSocket;
		this.currentUserInfo = userInfo;
	}
	
	
	/*
	 * 外部调用这个方法来创建对象
	 */
	public static RefreshMessageThread getObject(Socket clientSocket,UserInfo userInfo){
		if(instance==null){
			instance = new RefreshMessageThread(clientSocket, userInfo);
		}
		return instance;
	}
	
	/*
	 * 这个方法可以供外部拿到当前的实例/对象（每一次调用这个方法，都是唯一的实例）
	 */
	public static RefreshMessageThread getObject(){
		return instance;
	}
	
	
	@Override
	public void run() {
		
		while (true) {
			
			
			try {
				//接收来自服务端的消息
				ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
				MessageInfo msgInfo = (MessageInfo) input.readObject();
				
				//System.out.println(msgInfo.getSend_user()+"对您说:"+msgInfo.getMsg_content());
				boolean hasKey = this.messageMap.containsKey(msgInfo.getSend_user());
				if(!hasKey){//key不存在
					StringBuffer sb = new StringBuffer();
					sb.append(msgInfo.getSend_user()+"对您说:"+msgInfo.getMsg_content()+"\n");
					this.messageMap.put(msgInfo.getSend_user(), sb);
				}else{//key存在
					StringBuffer sb = this.messageMap.get(msgInfo.getSend_user());
					sb.append(msgInfo.getSend_user()+"对您说:"+msgInfo.getMsg_content()+"\n");
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
