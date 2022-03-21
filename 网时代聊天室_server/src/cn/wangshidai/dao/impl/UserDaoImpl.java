package cn.wangshidai.dao.impl;

import java.util.List;

import cn.wangshidai.dao.UserDao;
import cn.wangshidai.entity.UserInfo;
import cn.wangshidai.util.DBUtil_v5;

public class UserDaoImpl implements UserDao {

	@Override
	public UserInfo selectUserByUserAndPwd(String username, String pwd) {
		String sql = "SELECT * FROM tb_user \n" +
				"WHERE user_name=? AND user_pwd=?";
		List<UserInfo> userList = new DBUtil_v5().executeDQL_ORM(UserInfo.class, sql, username,pwd);
		return userList.size()>0 ? userList.get(0) : null;
	}

	@Override
	public List<UserInfo> selectAll(int user_id) {
		String sql = "SELECT * FROM tb_user WHERE user_id!=?";
		List<UserInfo> userList = new DBUtil_v5().executeDQL_ORM(UserInfo.class, sql,user_id);
		return userList;
	}

}
