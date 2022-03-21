package cn.wangshidai;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.wangshidai.dao.UserDao;
import cn.wangshidai.dao.impl.UserDaoImpl;
import cn.wangshidai.entity.UserInfo;

public class ServerMain {
	
	private static UserDao userDao = new UserDaoImpl();
	public static Map<String, Socket> socketMap = new HashMap<String, Socket>();
	
	//����һ���̳߳�
	private static ExecutorService  pool = Executors.newCachedThreadPool();
	
	public static void main(String[] args) {
			
			try {
				//����������
				ServerSocket server = new ServerSocket(9999);
				System.out.println("��ʱ�������ҷ���������...");
				
				
				while (true) {
					//ֻҪ�пͻ��������ӣ��ͻ�ִ��������루һ���ͻ������ӣ�����һ��Socket����
					Socket client = server.accept();
					
					//����һ�����߳�Ϊ�ͻ�����
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								//�������Կͻ��˵���Ϣ
								ObjectInputStream input = new ObjectInputStream(client.getInputStream());
								UserInfo userInfo = (UserInfo) input.readObject();
								
								UserInfo loginUserInfo = userDao.selectUserByUserAndPwd(userInfo.getUser_name(), userInfo.getUser_pwd());
								
								//�ظ��ͻ��˵���Ϣ
								if(loginUserInfo==null){//�˺Ż��������
									//�ظ��ͻ�����Ϣ
									ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
									out.writeObject(null);
									
									//��¼ʧ�ܣ��͹رո�socket��
									if(client!=null){
										client.close();
									}
									
								}else{
									//�ظ��ͻ�����Ϣ
									ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
									out.writeObject(loginUserInfo);
									
									//���ա���ȡ�û��б�������
									input.readObject();
									//��Ӧ���û��б����ͻ���
									List<UserInfo> userList = userDao.selectAll(loginUserInfo.getUser_id());
									out.writeObject(userList);
									
									/*
									 * {
									 * "zhangsan":������socket,
									 * "lisi":���ĵ�socket
									 * }
									 */
									socketMap.put(loginUserInfo.getUser_name(), client);
									
									
									
									
									
									//����һ�����̣߳�������ͻ��˵ĳ�����ͨѶ���������뱣֤socket�������٣�
									//new Thread(new RefreshMessageThread_server(client,loginUserInfo)).start();
									
									//���̳߳����һ������
									pool.execute(new RefreshMessageThread_server(client,loginUserInfo));
									
								}
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
					
					
					
					
					
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
