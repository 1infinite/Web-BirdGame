package com.bn.Server;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.bn.Database.DBUtil;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;


/**
 * @ClassName: Room
 * @Description: ͳ�������鷿������Լ����
 * @author JD
 * @date 2018��7��27��
 */
public class Room {
	
	/**
	 * @Fields room_id : ����ID(8λ����)��ĩλΪ0��ʾ���鷿�䣬ĩλΪ1��ʾ��Լ����
	 */
	public String room_id;
	
	/**
	 * @Fields room_name : ��������
	 */
	public String room_name;
	
	/**
	 * @Fields admin_id : ����/����Ա��ID
	 */
	public String admin_id;
	
	/**
	 * @Fields clientList : �����ڿͻ����б�
	 */
	public Map<String,ClientAgent> clientList;
	
	/**
	 * @Fields aq : ��������
	 */
	static Queue<Action> aq = new LinkedList<Action>();
	
	/**
	 * @Fields lock : ������������ȷ����˳��Ӷ���������ȡ������
	 */
	static Object lock = new Object();
		
	/**
	 * ����һ���µ�ʵ�� Room.
	 *
	 * @param room_id ����ID
	 * @param room_name	��������
	 * @param admin_id ����/����ԱID
	 */
	public Room(String room_id,String room_name,String admin_id){
		this.room_id = room_id;
		this.room_name = room_name;
		this.admin_id = admin_id;
		clientList = new LinkedHashMap<String,ClientAgent>();
	}
	
	/**
	 * @Title: broadcastMsg
	 * @Description: �򷿼��ڵ����������û��㲥��Ϣ
	 * @param data JSON��ʽ�������ַ���
	 * @param room �������
	 * @return void
	 * @throws
	 */
	public static void broadcastMsg(String data, Room room){
		for (ClientAgent user : room.clientList.values()) { 
			TextWebSocketFrame tws = new TextWebSocketFrame(data); 
			if(user.isOnLine) {
				user.channel.writeAndFlush(tws);
			}
		}
	}
	
	/**
	 * @Title: getUser
	 * @Description: �����û�ID��ȡ�û���ClientAgent
	 * @param room �������
	 * @param user_id �û�ID
	 * @return ClientAgent �û�������� 
	 * @throws
	 */
	public static ClientAgent getUser(Room room, String user_id){
		return room.clientList.get(user_id);
	}
	
	/**   
	 * @Title: randomIndex   
	 * @Description: ���������[min,max]  
	 * @param min ȡֵ��Χ��Сֵ
	 * @param max ȡֵ��Χ���ֵ
	 * @return int ���ɵ������ֵ
	 * @throws   
	 */  
	public static int randomIndex(int min, int max){
        Random random = new Random();
        int index = random.nextInt(max)%(max-min+1) + min;
        return index;
	}
	
	/**
	 * @Title: findVoidRoomID
	 * @Description: Ѱ��һ��δʹ�ù��ģ�����/��Լ������ID
	 * @param isMeetingRoom �Ƿ�Ϊ���鷿��
	 * @param roomIDList ������/��Լ������ID�б�
	 * @return String ����ID��8λ��
	 * @throws 
	 */
	public static String findVoidRoomID(boolean isMeetingRoom,ArrayList<String> roomIDList){
		
		int length = 8;//����IDλ�� 
		String room_id;
		
		do{
			room_id = "";
			for(int i = 1; i<length; i++){
				room_id += randomIndex(0,9);
			}
		
			//���һλΪ0��ʾ���鷿��  Ϊ1��ʾ��Լ����
			if(isMeetingRoom) room_id += "0";
			else room_id += "1";
			
		}while(roomIDList.contains(room_id));
			
		return room_id;
	}
	
	/**
	 * @Title: initMeetingRoom
	 * @Description: XXX�����Ƽ����ݲ��Դ������鷿��Ĺ���
	 * @return void
	 * @throws ParseException
	 */
	public static void initMeetingRoom() throws ParseException {
		Room.createMeetingRoom("ȫ��XXX���-����1", string2Time("2018-06-27 12:00:00"), string2Time("2018-08-06 16:59:00"), "0001");
	}
	
	/**
	 * @Title: createMeetingRoom
	 * @Description: XXX���������鷿��,�˹���Ӧ��Web�˽��й���
	 * @param meeting_name ��������
	 * @param start_time ������Ϸ��ʼʱ��
	 * @param end_time ������Ϸ����ʱ��
	 * @param admin_id ����ԱID
	 * @return String ����ID 
	 * @throws 
	 */
	public static String createMeetingRoom(String meeting_name,Timestamp start_time,Timestamp end_time,String admin_id) {
		
		//��ȡ�������ID
		String meeting_id = Room.findVoidRoomID(true, MeetingRoom.roomIDList);
		//String meeting_id = "11111110";
		//System.out.println("�������ID��"+meeting_id);
		//��ӽ�����ID�б�
		MeetingRoom.roomIDList.add(meeting_id);
		MeetingRoom room = new MeetingRoom(meeting_id, meeting_name, admin_id, start_time, end_time);
		MeetingRoom.roomList.add(room);
		
		//XXX����������
//		room.clientList.put("0001", new ClientAgent("0001","����","0001"));
//		room.clientList.put("0002", new ClientAgent("0002","����","0002"));
//		room.clientList.put("0003", new ClientAgent("0003","����","0003"));
//		room.clientList.put("0004", new ClientAgent("0004","����","0004"));
//		room.clientList.put("0005", new ClientAgent("0005","·��","0005"));
//		room.clientList.put("0006", new ClientAgent("0006","��¡","0006"));
//		room.clientList.put("0007", new ClientAgent("0007","�㼪ʿ","0007"));
		
		//��������ӽ����ݿ�
		DBUtil.insertMeetingRoom(meeting_id, meeting_name, start_time, end_time, admin_id);
		
		return meeting_id;
	}
	
	/**
	 * @Title: string2Time 
	 * @Description: ��ʱ���ַ���תΪTimestamp����,�Ա�洢�����ݿ�
	 * @param dateString ʱ���ַ�������ʽΪ"yyyy-MM-dd kk:mm:ss"
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp string2Time(String dateString) throws ParseException { 
		DateFormat dateFormat; 
		dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH); 
		dateFormat.setLenient(false); 
		java.util.Date timeDate = dateFormat.parse(dateString);
		Timestamp dateTime = new Timestamp(timeDate.getTime());
		return dateTime; 
	}
}
