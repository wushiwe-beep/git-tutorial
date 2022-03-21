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
	
	//创建一个线程池
	private static ExecutorService  pool = Executors.newCachedThreadPool();
	
	public static void main(String[] args) {
			
			try {
				//启动服务器
				ServerSocket server = new ServerSocket(9999);
				System.out.println("网时代聊天室服务已启动...");
				
				
				while (true) {
					//只要有客户端来连接，就会执行下面代码（一个客户端连接，就是一个Socket对象）
					Socket client = server.accept();
					
					//开辟一条子线程为客户服务
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								//接收来自客户端的消息
								ObjectInputStream input = new ObjectInputStream(client.getInputStream());
								UserInfo userInfo = (UserInfo) input.readObject();
								
								UserInfo loginUserInfo = userDao.selectUserByUserAndPwd(userInfo.getUser_name(), userInfo.getUser_pwd());
								
								//回复客户端的消息
								if(loginUserInfo==null){//账号或密码错误
									//回复客户端消息
									ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
									out.writeObject(null);
									
									//登录失败！就关闭该socket流
									if(client!=null){
										client.close();
									}
									
								}else{
									//回复客户端消息
									ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
									out.writeObject(loginUserInfo);
									
									//接收“获取用户列表”的请求
									input.readObject();
									//响应“用户列表”给客户端
									List<UserInfo> userList = userDao.selectAll(loginUserInfo.getUser_id());
									out.writeObject(userList);
									
									/*
									 * {
									 * "zhangsan":张三的socket,
									 * "lisi":李四的socket
									 * }
									 */
									socketMap.put(loginUserInfo.getUser_name(), client);
									
									
									
									
									
									//开启一条子线程，保持与客户端的长连接通讯！！（必须保证socket对象不销毁）
									//new Thread(new RefreshMessageThread_server(client,loginUserInfo)).start();
									
									//给线程池添加一条任务
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
