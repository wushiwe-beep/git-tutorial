package cn.wangshidai.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {
	
	private String send_user;		//发送人的用户名
	private String receive_user;	//接收人的用户名
	private String msg_content;		//消息内容
	
	public String getSend_user() {
		return send_user;
	}
	public void setSend_user(String send_user) {
		this.send_user = send_user;
	}
	public String getReceive_user() {
		return receive_user;
	}
	public void setReceive_user(String receive_user) {
		this.receive_user = receive_user;
	}
	public String getMsg_content() {
		return msg_content;
	}
	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}
	
	
	
	
	
}
