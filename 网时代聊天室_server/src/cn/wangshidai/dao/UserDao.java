package cn.wangshidai.dao;

import java.util.List;

import cn.wangshidai.entity.UserInfo;

public interface UserDao {

	/**
	 * ͨ���˺š������ѯ�û���Ϣ
	 * @param username
	 * @param pwd
	 * @return
	 */
	UserInfo selectUserByUserAndPwd(String username,String pwd);
	
	
	/**
	 * ��ѯ�����û��б�
	 * @return
	 */
	List<UserInfo> selectAll(int user_id);
	
}
