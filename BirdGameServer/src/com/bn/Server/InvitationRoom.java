package com.bn.Server;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.bn.Database.DBUtil;

public class InvitationRoom extends Room{

	/**
	 * @Fields roomList : ��Լ�����б�������еĻ��鷿��ʵ��
	 */
	public static ArrayList<InvitationRoom> roomList = new ArrayList<InvitationRoom>();

	/**
	 * @Fields roomIDList : ��Լ����ID�б�������л��鷿���ID
	 */
	public static ArrayList<String> roomIDList = new ArrayList<String>();
	
	public InvitationRoom(String room_id,String room_name,String admin_id) {
		super(room_id, room_name, admin_id);
	}
	
	/**
	 * @Title: getRoom
	 * @Description: ��ȡ���鷿��ʵ��
	 * @param room_id ���鷿��ID
	 * @return Room  
	 */
	public static InvitationRoom getRoom(String room_id) {
		
		for(int i=0;i<roomList.size();i++){		
			if(roomList.get(i).room_id.equals(room_id)){
				return roomList.get(i);
			}
		}
		return null;
	}

	
	/**
	 * @Title: isSameNameRoom
	 * @Description: �ж��Ƿ�����ͬ���Ƶķ���
	 * @param room_name
	 * @return boolean  
	 */
	public static boolean isSameNameRoom(String room_name) {
		for(InvitationRoom room : roomList) {
			if(room.room_name.equals(room_name)) return true;
		}
		return false;
	}
	
	
	/**
	 * @Title: createInvitationRoom
	 * @Description: ������Լ����
	 * @param invitation_name
	 * @param admin_id void  
	 */
	public static String createInvitationRoom(String invitation_name,String admin_id) {
		
		if(isSameNameRoom(invitation_name)) return "";
		
		//��ȡ�������ID XXX:����
		String invitation_id = Room.findVoidRoomID(false, InvitationRoom.roomIDList);
		//String invitation_id = "11111111";
		//��ӽ�����ID�б�
		InvitationRoom.roomIDList.add(invitation_id);
		InvitationRoom room = new InvitationRoom(invitation_id,invitation_name,admin_id);
		InvitationRoom.roomList.add(room);
		
		//XXX����������
//		room.clientList.put("0001", new ClientAgent("0001","����","0001"));

		
		//��������ӽ����ݿ�
		DBUtil.insertInviteRoom(admin_id, invitation_id, invitation_name);
		return invitation_id;
	}
}
