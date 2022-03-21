package cn.wangshidai;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import cn.wangshidai.entity.MessageInfo;
import cn.wangshidai.entity.UserInfo;

//class MyThread extends Thread
public class RefreshMessageThread_server implements Runnable {

	//当前登录用户的socket
	private Socket clientSocket;
	private UserInfo loginUserInfo;
	
	public RefreshMessageThread_server(Socket clientSocket,UserInfo loginUserInfo){
		this.clientSocket = clientSocket;
		this.loginUserInfo = loginUserInfo;
	}
	
	
	@Override
	public void run() {
		while (true) {
			
			try {
				//接收来自客户端发起的消息（下面这行代码会阻塞，因为他要等待客户端发起输出流）
				ObjectInputStream input = new ObjectInputStream(this.clientSocket.getInputStream());
				MessageInfo msgInfo = (MessageInfo) input.readObject();
				
				//回复客户端（接收人）
				String receive_user = msgInfo.getReceive_user();
				ObjectOutputStream out = new ObjectOutputStream(ServerMain.socketMap.get(receive_user).getOutputStream());
				out.writeObject(msgInfo);
				
			} catch (SocketException e) {//只要捕获到这个异常，代表当前Socket出现了中断！！（连接中断）
				
				try {
					System.out.println(loginUserInfo.getUser_name()+"下线了！！");
					
					clientSocket.close(); //关闭socket，不占用资源
					ServerMain.socketMap.remove(loginUserInfo.getUser_name());//从map中移除该线程对象
					break;//跳出循环，run方法就会执行完毕（意味着不需要该线程对象了！！）
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}
