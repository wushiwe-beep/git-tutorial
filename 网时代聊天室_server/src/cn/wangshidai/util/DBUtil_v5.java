package cn.wangshidai.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * DB工具类V5版本
 * 将SQL语句和参数以log4j的方式输出日志
 * @author WsdYock
 *
 */
public class DBUtil_v5 {
	private static Logger logger = Logger.getLogger(DBUtil_v5.class);
	//数据库的连接信息
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/sys?useUnicode=true&characterEncoding=utf8";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "123456";
	
	
	//连接对象
	private Connection conn = null;
	//执行SQL语句的句柄对象（执行器）
	//private Statement stmt = null;
	private PreparedStatement stmt = null;
	//结果集对象
	private ResultSet resultSet = null;
	
	
	//类的初始化块（只会执行一次）
	static{		
		try {
			//1>加载驱动（检测该驱动类是否存在）
			Class.forName("com.mysql.jdbc.Driver");
			logger.debug("Driver load the success");
		} catch (ClassNotFoundException e) {
			logger.error("",e);
		}
	}
	
	
	
	/**
	 * 一次操作，需要执行多条DML语句（考虑事务控制）
	 * 特点：
	 * 一致性（一起成功、一起失败！）
	 * 
	 * 举例：
	 * 从购物车下单：删除购物车中选中的商品、往订单表插入数据
	 * 转账操作：A账号扣钱，B账号加钱
	 */
	public void executeDML_Many(String[] sqlArray,Object[][] paramsArray){
		open();
		
		try {
			//设置自动提交为false（取消自动提交）
			conn.setAutoCommit(false);
			//提交事务
			//conn.commit();
			//回滚事务
			//conn.rollback();
			
			String string = "";
			String string2 = "";
			for (int i = 0; i < sqlArray.length; i++) {
				
				//3>准备一个操作句柄对象（Statement对象），用来执行SQL语句的
				stmt = conn.prepareStatement(sqlArray[i]);
				
				//stmt = conn.createStatement();
				//填充?的值（问号个数知道吗？问号的数据类型知道吗？）
				if (paramsArray != null) {
					
					//取出第i条SQL对应的?的值列表
					Object[] valueArray = paramsArray[i];
					
					if(valueArray!=null){
						for (int j = 0; j < valueArray.length; j++) {
							stmt.setObject(j + 1, valueArray[j]);
							string2 += valueArray[j]+",";
						}
						string += sqlArray[i];
					}					
				}
				logger.debug("Execute SQL:"+string2);
				logger.debug("Execute SQL:"+string);
				//4>执行SQL语句
				stmt.executeUpdate();		

			}					
			//提交事务
			conn.commit();	
			logger.debug("Execute successfully:"+string);
		} catch (SQLException e) {
			logger.error("",e);	
			//回滚事务
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error("",e1);
			}
		} finally{
			close();
			logger.debug("‘DML’release resource");
		}
		
		
	}
	
	
	
	
	
	/**
	 * 执行DML语句
	 * @param sql
	 * @return
	 */
	public int executeDML(String sql,Object... paramsArray){
		int rows = 0;
		open();
		
		//Object... paramsArray  可变参数
		//Object[]的数组
		
		try {
			//3>准备一个操作句柄对象（Statement对象），用来执行SQL语句的
			stmt = conn.prepareStatement(sql);
			
			//stmt = conn.createStatement();
			
			//填充?的值（问号个数知道吗？问号的数据类型知道吗？）
			logger.debug(">>>>>>"+sql);
			
			
			String str = "[";
			if(paramsArray!=null){
				for (int i = 0; i < paramsArray.length; i++) {
					stmt.setObject(i+1, paramsArray[i]);
					str += paramsArray[i]+",";
				}
			}
			str += "]";
			logger.debug(">>>>>参数列表:"+str);
			
			//logger.debug(str);  //参数1,参数2,参数3
			//4>执行SQL语句
			rows = stmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("",e);
		} finally{
			close();
			logger.debug("release resource");
		}
		logger.debug("modify successfully  "+rows+"  rows ");
		return rows;
	}
	
	
	/**
	 * 执行DQL语句
	 * ORM（Object Relational Mapping， 对象关系映射）
	 * @param clazz			指定要映射的实体类
	 * @param sql			要执行的SQL语句
	 * @param paramsArray	填充SQL中?的动态参数
	 * @return
	 */
	public <E> List<E> executeDQL_ORM(Class<E> clazz,String sql,Object... paramsArray){
		List<E> list = new ArrayList<E>();
		List<Map<String, Object>> listMap = executeDQL(sql, paramsArray);
//		logger.debug("Execute successfully:"+sql);
		try {
			//获取实体类的Class中的所有的属性
			Field[] fieldArray = clazz.getDeclaredFields();
			
			for (Map<String, Object> map : listMap) {
				//实例化一个实体类对象
				E instance  = clazz.newInstance();
				//循环去寻找实体类的属性  与  map的key 有哪些匹配上了
				for (Field field : fieldArray) {
					//获取map的所有的key
					Set<String> allKeyList = map.keySet();
					
					//每一个实体类属性，都要去跟map的所有key 匹配一轮，  直到匹配上为止
					for (String key : allKeyList) {	
						//找到了key对应的 实体类的属性名
						if(key.equals(field.getName())){
							field.setAccessible(true);
							field.set(instance, map.get(key));
							break;
						}
					}
				}
				
				list.add(instance);
			}
			
			
		} catch (SecurityException e) {
			logger.error("",e);

		} catch (InstantiationException e) {
			logger.error("",e);

		} catch (IllegalAccessException e) {
			logger.error("",e);
		}
		
		return list;
	}
	
	
	public List<Map<String, Object>> executeDQL(String sql,Object... paramsArray){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		open();
		
		try {
			//3>准备一个操作句柄对象（Statement对象），用来执行SQL语句的
			stmt = conn.prepareStatement(sql);
			logger.debug(">>>>>"+sql);
			
			//填充?的值（问号个数知道吗？问号的数据类型知道吗？）
			String string = "";
			String str = "[";
			if(paramsArray!=null){
				for (int i = 0; i < paramsArray.length; i++) {
					stmt.setObject(i+1, paramsArray[i]);
					string += paramsArray[i]+",";
				}
			}
			str += string;
			str += "]";
			logger.debug(">>>>>参数列表如下："+str);
			
			if(!string.equals(""))
			logger.debug("Loading condition information:"+sql);
			resultSet = stmt.executeQuery();
			
			////遍历取出结果集
			while (resultSet.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				//获取结果集中的结构信息（字段名、字段数量）
				ResultSetMetaData metaData = resultSet.getMetaData();
				//获取“字段数量”
				int columnCount = metaData.getColumnCount();
				
//				//取出第1列的列名
//				metaData.getColumnName(1)
//				//取出第2列的列名
//				metaData.getColumnName(2)
				
				//getColumnName  ->  不能查询别名
				//getColumnLabel ->  可以别名（写了别名就优先用别名，没有别名就用原字段名）
				//metaData.getColumnLabel(2)
				
				for (int i = 1; i <= columnCount; i++) {
					String key = metaData.getColumnLabel(i);
					map.put(key, resultSet.getObject(i));
				}
				
				
				list.add(map);
			}
			
		} catch (SQLException e) {
			logger.error("",e);
		} finally{
			close();
			logger.debug("'executeDQL' release resource");
		}		
		return list;
	}
	
	
	
	/**
	 * 打开连接
	 */
	private void open(){
		
		try {
			//2>创建一个连接对象，连接数据库
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			logger.error("",e);
		}
				
	}
	
	
	/**
	 * 释放资源
	 */
	private void close(){
		try {
			if (resultSet!=null)
				resultSet.close();
			if (stmt!=null)
				stmt.close();
			if (conn!=null)
				conn.close();
		} catch (SQLException e) {
			logger.error("",e);
		}
	}
	
	
	
}
