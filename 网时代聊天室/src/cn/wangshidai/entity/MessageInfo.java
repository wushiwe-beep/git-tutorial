package cn.wangshidai.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {
	
	private String send_user;		//�����˵��û���
	private String receive_user;	//�����˵��û���
	private String msg_content;		//��Ϣ����
	
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
