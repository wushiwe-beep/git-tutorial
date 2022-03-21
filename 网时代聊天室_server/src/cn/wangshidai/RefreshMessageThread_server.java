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

	//��ǰ��¼�û���socket
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
				//�������Կͻ��˷������Ϣ���������д������������Ϊ��Ҫ�ȴ��ͻ��˷����������
				ObjectInputStream input = new ObjectInputStream(this.clientSocket.getInputStream());
				MessageInfo msgInfo = (MessageInfo) input.readObject();
				
				//�ظ��ͻ��ˣ������ˣ�
				String receive_user = msgInfo.getReceive_user();
				ObjectOutputStream out = new ObjectOutputStream(ServerMain.socketMap.get(receive_user).getOutputStream());
				out.writeObject(msgInfo);
				
			} catch (SocketException e) {//ֻҪ��������쳣������ǰSocket�������жϣ����������жϣ�
				
				try {
					System.out.println(loginUserInfo.getUser_name()+"�����ˣ���");
					
					clientSocket.close(); //�ر�socket����ռ����Դ
					ServerMain.socketMap.remove(loginUserInfo.getUser_name());//��map���Ƴ����̶߳���
					break;//����ѭ����run�����ͻ�ִ����ϣ���ζ�Ų���Ҫ���̶߳����ˣ�����
					
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
