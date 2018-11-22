package com.bn.Server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import io.netty.channel.Channel;

/**
 * @ClassName: MeetingRoom
 * @Description: ���鷿��ʵ��
 * @author JD
 * @date 2018��7��27��
 */
public class MeetingRoom extends Room{

	/**
	 * @Fields start_time : ������Ϸ��ʼʱ��
	 */
	Timestamp start_time;
	
	/**
	 * @Fields end_time : ������Ϸ����ʱ��
	 */
	Timestamp end_time;
	
	/**
	 * @Fields roomList : ���鷿���б�������еĻ��鷿��ʵ��
	 */
	public static ArrayList<MeetingRoom> roomList = new ArrayList<MeetingRoom>();

	/**
	 * @Fields roomIDList : ���鷿��ID�б�������л��鷿���ID
	 */
	public static ArrayList<String> roomIDList = new ArrayList<String>();
	
	/**
	 * ����һ���µ�ʵ�� MeetingRoom.
	 *
	 * @param room_id ���鷿��ID
	 * @param room_name	��������
	 * @param admin_id ����ԱID
	 * @param start_time ������Ϸ��ʼʱ��
	 * @param end_time ������Ϸ����ʱ��
	 */
	public MeetingRoom(String room_id,String room_name,String admin_id,Timestamp start_time,Timestamp end_time) {
		super(room_id, room_name, admin_id);
		this.start_time = start_time;
		this.end_time = end_time;
		new TimeThread(room_id,end_time).start();
	}
	
	/**
	 * @Title: getRoom
	 * @Description: ��ȡ���鷿��ʵ��
	 * @param room_id ���鷿��ID
	 * @return Room  
	 */
	public static MeetingRoom getRoom(String room_id) {
		
		for(int i=0;i<roomList.size();i++){		
			if(roomList.get(i).room_id.equals(room_id)){
				return roomList.get(i);
			}
		}
		return null;
	}

}
