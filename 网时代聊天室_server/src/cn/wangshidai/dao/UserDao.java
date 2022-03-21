package cn.wangshidai.dao;

import java.util.List;

import cn.wangshidai.entity.UserInfo;

public interface UserDao {

	/**
	 * 通过账号、密码查询用户信息
	 * @param username
	 * @param pwd
	 * @return
	 */
	UserInfo selectUserByUserAndPwd(String username,String pwd);
	
	
	/**
	 * 查询所有用户列表
	 * @return
	 */
	List<UserInfo> selectAll(int user_id);
	
}
