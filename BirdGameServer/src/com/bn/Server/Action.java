package com.bn.Server;

import java.util.ArrayList;

import com.bn.Database.DBUtil;
import com.bn.Util.CMD;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Action {

	
	/**
	 * @Fields channel : �ͻ�������ͨ��
	 */
	Channel channel;
	
	/**
	 * @Fields cmd : ָ��ֵ
	 */
	int cmd;

	/**
	 * @Fields room_id : ����ID
	 */
	String room_id;

	/**
	 * @Fields user_id : �û�ID
	 */
	String user_id;
	
	/**
	 * @Fields search_id : ������ID�ţ�����ID�ĺ���λ��
	 */
	String search_id;
	
	/**
	 * @Fields score : ��Ҫ���µķ���
	 */
	String score;
	
	/**
	 * @Fields room_name : ��������
	 */
	String room_name;
	
	/**
	 * @Fields jsonString : �����͵��ͻ��˵�json�ַ���
	 */
	String jsonString = null;
	
	/**
	 * ����һ���µ�ʵ�� Action.
	 *
	 * @param channel �ͻ�������ͨ��
	 * @param cmd ָ��ֵ
	 * @param room_id ����ID
	 * @param user_id �û�ID
	 * @param search_id ������ID��
	 */
	public Action(Channel channel,int cmd,String room_id,String user_id,String search_id,String score,String room_name){
		this.channel = channel;
		this.cmd = cmd;
		this.room_id = room_id;
		this.user_id = user_id;
		this.search_id = search_id;
		this.score = score;
		this.room_name = room_name;
	}
	
	/**
	 * ����һ���µĵ���ʱAction
	 *
	 * @param cmd ָ��
	 * @param room_id ����ID
	 * @param jsonString JSON�ַ���
	 */
	public Action(int cmd,String room_id,String jsonString){
		this.cmd = cmd;
		this.room_id = room_id;
		this.jsonString = jsonString;
	}
	
	/**
	 * @Title: doAction
	 * @Description: ����ָ��ֵ����Action
	 */
	public void doAction(){
		
		if(cmd == CMD.USER_INFO) {
			//�����û�ID��ѯ���û����ǳ���ͼ��
			String[] str = DBUtil.getUserNameAndPic(user_id);
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.USER_INFO);
			json.put("user_name", str[0]);
			json.put("user_photo", str[1]);
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.SEARCH_ROOM) {
			//��������ID�ĺ���λ��������
			ArrayList<String> al = DBUtil.getMeetingRoom(search_id);
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.SEARCH_ROOM);
			json.put("count", al.size());//�����������
			
			JSONArray array = new JSONArray();//�������
			for(String s : al) {
				JSONObject temp = new JSONObject();
				String[] ss = s.split("\\|");
				temp.put("meeting_id", ss[0]);
				temp.put("meeting_name", ss[1]);
				array.add(temp);
			}
			
			json.put("meeting_list", array);
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.JOIN_ROOM) {
			//�û�������뷿��
			MeetingRoom room = MeetingRoom.getRoom(room_id);
			
			//��ȡ�÷�
			int score = 0;
			if(DBUtil.getScore(user_id, room_id)==null) {
				DBUtil.insertScore(user_id, room_id, score);
			}else {
				score = Integer.parseInt(DBUtil.getScore(user_id, room_id));
			}
			
			//��ȡ������ͷ����Ϣ
			String[] str = DBUtil.getUserNameAndPic(user_id);
			
			ClientAgent ca = new ClientAgent(channel, user_id, str[0], str[1]);
			room.clientList.put(user_id, ca);
			
			
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.SCORE);
			json.put("score", score);
			
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
			
		}else if(cmd == CMD.TIME) {
			//�򷿼��ڵ������û��㲥����ʱ
			MeetingRoom room = MeetingRoom.getRoom(room_id);
			Room.broadcastMsg(jsonString, room);
		}else if(cmd == CMD.HIGHER_SCORE) {
			//�������ݿ��еķ���
			DBUtil.updateScore(user_id, room_id, Integer.parseInt(score));
		}else if(cmd == CMD.RANK_LIST) {
			//��ȡ�����ڵ�����
			ArrayList<String> al = DBUtil.getRankList(room_id);
			
			//��ȡ���а�
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.RANK_LIST);
			json.put("count", al.size());//�����������
			Room room;
			if(room_id.charAt(room_id.length()-1)=='0') {
				room = MeetingRoom.getRoom(room_id);
			}else {
				room = InvitationRoom.getRoom(room_id);
			}
			
			JSONArray array = new JSONArray();//�������
			for(int i = 1;i<=al.size();i++) {
				JSONObject temp = new JSONObject();
				String[] ss = al.get(i-1).split("\\|");
				temp.put("rank", i);
				temp.put("user_id", ss[0]);
				temp.put("user_photo", room.clientList.get(ss[0]).user_photo);
				temp.put("user_name", room.clientList.get(ss[0]).user_name);
				temp.put("score", ss[1]);
				array.add(temp);
			}
			
			json.put("rank_list", array);
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.LUCKY_FRIEND) {
			//�Ƽ���ϵ��
			ArrayList<String> al = DBUtil.getRankList(room_id);
			
			if(al.size() <= 1) return;
			
			Room room;
			if(room_id.charAt(room_id.length()-1)=='0') {
				room = MeetingRoom.getRoom(room_id);
			}else {
				room = InvitationRoom.getRoom(room_id);
			}
			
			int index;
			String the_user_id;
			do {
				index = Room.randomIndex(0, al.size()-1);
				the_user_id = al.get(index).split("\\|")[0];
			}while(user_id.equals(the_user_id));
			
			String[] ss = al.get(index).split("\\|");
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.LUCKY_FRIEND);
			json.put("rank", index+1);
			json.put("user_id", ss[0]);
			json.put("user_photo", room.clientList.get(ss[0]).user_photo);
			json.put("user_name", room.clientList.get(ss[0]).user_name);
			json.put("score", ss[1]);
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.SPONSOR_LOGO) {
			//������Logo
			ArrayList<String> al = DBUtil.getSponsorLogo(room_id);
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.SPONSOR_LOGO);
			json.put("count", al.size());
			
			JSONArray array = new JSONArray();//�������
			for(int i = 0;i<al.size();i++) {
				JSONObject temp = new JSONObject();
				temp.put("logo", al.get(i));
				array.add(temp);
			}
			
			json.put("logo_list", array);
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.CREATE_INVITE_ROOM) {
			
			//������Լ����
			String room_id = InvitationRoom.createInvitationRoom(room_name, user_id);
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.CREATE_INVITE_ROOM);
			json.put("room_id", room_id);
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.SEARCH_INVITATION_ROOM) {
			//������Լ����
			//��������ID�ĺ���λ��������
			ArrayList<String> al = DBUtil.getInvitationRoom(search_id);
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.SEARCH_INVITATION_ROOM);
			json.put("count", al.size());//�����������
			
			JSONArray array = new JSONArray();//�������
			for(String s : al) {
				JSONObject temp = new JSONObject();
				String[] ss = s.split("\\|");
				temp.put("invitation_id", ss[0]);
				temp.put("invitation_name", ss[1]);
				array.add(temp);
			}
			
			json.put("invitation_list", array);
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.JOIN_INVITATION_ROOM) {
			//������Լ����
			//�û�������뷿��
			InvitationRoom room = InvitationRoom.getRoom(room_id);
			
			if(room.clientList.get(user_id) != null) {
				//TODO: ���û��Ѿ��ڸ÷����ڣ��ڱ𴦵�¼��
			}
			
			//��ȡ�÷�
			int score = 0;
			if(DBUtil.getScore(user_id, room_id)==null) {
				DBUtil.insertScore(user_id, room_id, score);
			}else {
				score = Integer.parseInt(DBUtil.getScore(user_id, room_id));
			}
			
			//��ȡ������ͷ����Ϣ
			String[] str = DBUtil.getUserNameAndPic(user_id);
			ClientAgent ca = new ClientAgent(channel, user_id, str[0], str[1]);
			room.clientList.put(user_id, ca);
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.SCORE);
			json.put("score", score);
			
			jsonString = json.toString();
			
			TextWebSocketFrame tws = new TextWebSocketFrame(jsonString); 
			channel.writeAndFlush(tws);
		}else if(cmd == CMD.TIME_OVER) {
			//���龺����ʱ�����
			Room room =MeetingRoom.getRoom(room_id);
			
			//�㲥����ʱ������Ϣ
			room.broadcastMsg(jsonString, room);
		}
	}
}
