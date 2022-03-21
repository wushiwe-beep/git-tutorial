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
 * DB������V5�汾
 * ��SQL���Ͳ�����log4j�ķ�ʽ�����־
 * @author WsdYock
 *
 */
public class DBUtil_v5 {
	private static Logger logger = Logger.getLogger(DBUtil_v5.class);
	//���ݿ��������Ϣ
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/sys?useUnicode=true&characterEncoding=utf8";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "123456";
	
	
	//���Ӷ���
	private Connection conn = null;
	//ִ��SQL���ľ������ִ������
	//private Statement stmt = null;
	private PreparedStatement stmt = null;
	//���������
	private ResultSet resultSet = null;
	
	
	//��ĳ�ʼ���飨ֻ��ִ��һ�Σ�
	static{		
		try {
			//1>���������������������Ƿ���ڣ�
			Class.forName("com.mysql.jdbc.Driver");
			logger.debug("Driver load the success");
		} catch (ClassNotFoundException e) {
			logger.error("",e);
		}
	}
	
	
	
	/**
	 * һ�β�������Ҫִ�ж���DML��䣨����������ƣ�
	 * �ص㣺
	 * һ���ԣ�һ��ɹ���һ��ʧ�ܣ���
	 * 
	 * ������
	 * �ӹ��ﳵ�µ���ɾ�����ﳵ��ѡ�е���Ʒ�����������������
	 * ת�˲�����A�˺ſ�Ǯ��B�˺ż�Ǯ
	 */
	public void executeDML_Many(String[] sqlArray,Object[][] paramsArray){
		open();
		
		try {
			//�����Զ��ύΪfalse��ȡ���Զ��ύ��
			conn.setAutoCommit(false);
			//�ύ����
			//conn.commit();
			//�ع�����
			//conn.rollback();
			
			String string = "";
			String string2 = "";
			for (int i = 0; i < sqlArray.length; i++) {
				
				//3>׼��һ�������������Statement���󣩣�����ִ��SQL����
				stmt = conn.prepareStatement(sqlArray[i]);
				
				//stmt = conn.createStatement();
				//���?��ֵ���ʺŸ���֪�����ʺŵ���������֪���𣿣�
				if (paramsArray != null) {
					
					//ȡ����i��SQL��Ӧ��?��ֵ�б�
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
				//4>ִ��SQL���
				stmt.executeUpdate();		

			}					
			//�ύ����
			conn.commit();	
			logger.debug("Execute successfully:"+string);
		} catch (SQLException e) {
			logger.error("",e);	
			//�ع�����
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error("",e1);
			}
		} finally{
			close();
			logger.debug("��DML��release resource");
		}
		
		
	}
	
	
	
	
	
	/**
	 * ִ��DML���
	 * @param sql
	 * @return
	 */
	public int executeDML(String sql,Object... paramsArray){
		int rows = 0;
		open();
		
		//Object... paramsArray  �ɱ����
		//Object[]������
		
		try {
			//3>׼��һ�������������Statement���󣩣�����ִ��SQL����
			stmt = conn.prepareStatement(sql);
			
			//stmt = conn.createStatement();
			
			//���?��ֵ���ʺŸ���֪�����ʺŵ���������֪���𣿣�
			logger.debug(">>>>>>"+sql);
			
			
			String str = "[";
			if(paramsArray!=null){
				for (int i = 0; i < paramsArray.length; i++) {
					stmt.setObject(i+1, paramsArray[i]);
					str += paramsArray[i]+",";
				}
			}
			str += "]";
			logger.debug(">>>>>�����б�:"+str);
			
			//logger.debug(str);  //����1,����2,����3
			//4>ִ��SQL���
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
	 * ִ��DQL���
	 * ORM��Object Relational Mapping�� �����ϵӳ�䣩
	 * @param clazz			ָ��Ҫӳ���ʵ����
	 * @param sql			Ҫִ�е�SQL���
	 * @param paramsArray	���SQL��?�Ķ�̬����
	 * @return
	 */
	public <E> List<E> executeDQL_ORM(Class<E> clazz,String sql,Object... paramsArray){
		List<E> list = new ArrayList<E>();
		List<Map<String, Object>> listMap = executeDQL(sql, paramsArray);
//		logger.debug("Execute successfully:"+sql);
		try {
			//��ȡʵ�����Class�е����е�����
			Field[] fieldArray = clazz.getDeclaredFields();
			
			for (Map<String, Object> map : listMap) {
				//ʵ����һ��ʵ�������
				E instance  = clazz.newInstance();
				//ѭ��ȥѰ��ʵ���������  ��  map��key ����Щƥ������
				for (Field field : fieldArray) {
					//��ȡmap�����е�key
					Set<String> allKeyList = map.keySet();
					
					//ÿһ��ʵ�������ԣ���Ҫȥ��map������key ƥ��һ�֣�  ֱ��ƥ����Ϊֹ
					for (String key : allKeyList) {	
						//�ҵ���key��Ӧ�� ʵ�����������
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
			//3>׼��һ�������������Statement���󣩣�����ִ��SQL����
			stmt = conn.prepareStatement(sql);
			logger.debug(">>>>>"+sql);
			
			//���?��ֵ���ʺŸ���֪�����ʺŵ���������֪���𣿣�
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
			logger.debug(">>>>>�����б����£�"+str);
			
			if(!string.equals(""))
			logger.debug("Loading condition information:"+sql);
			resultSet = stmt.executeQuery();
			
			////����ȡ�������
			while (resultSet.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				//��ȡ������еĽṹ��Ϣ���ֶ������ֶ�������
				ResultSetMetaData metaData = resultSet.getMetaData();
				//��ȡ���ֶ�������
				int columnCount = metaData.getColumnCount();
				
//				//ȡ����1�е�����
//				metaData.getColumnName(1)
//				//ȡ����2�е�����
//				metaData.getColumnName(2)
				
				//getColumnName  ->  ���ܲ�ѯ����
				//getColumnLabel ->  ���Ա�����д�˱����������ñ�����û�б�������ԭ�ֶ�����
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
	 * ������
	 */
	private void open(){
		
		try {
			//2>����һ�����Ӷ����������ݿ�
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			logger.error("",e);
		}
				
	}
	
	
	/**
	 * �ͷ���Դ
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
