package com.bn.Server;
import java.util.*;

import io.netty.channel.Channel;
public class ClientAgent {
		
	//�˿ͻ��˵��ŵ�
	Channel channel;
	//�û�ID
	String user_id;
	//�ǳ�
	String user_name;
	//ͼƬ����
	String user_photo;
	//�Ƿ�����
	boolean isOnLine;
	
	public ClientAgent(Channel channel,String user_id,String user_name,String user_photo){
		this.channel=channel;
		this.user_id = user_id;
		this.user_name = user_name;
		this.user_photo = user_photo;
		this.isOnLine = true;
	}
	
	public ClientAgent(String user_id,String user_name,String user_photo) {
		this.user_id = user_id;
		this.user_name = user_name;
		this.user_photo = user_photo;
		this.isOnLine = false;
	}
}
