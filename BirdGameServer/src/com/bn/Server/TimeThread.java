package com.bn.Server;

import static com.bn.Server.Room.aq;
import static com.bn.Server.Room.lock;

import java.sql.Timestamp;

import com.bn.Util.CMD;

import net.sf.json.JSONObject;

/**
 * @ClassName: TimeThread
 * @Description: ��ʱ�߳�
 * @author JD
 * @date 2018��7��27��
 */

public class TimeThread extends Thread{
	
	/**
	 * @Fields SLEEP : ����ʱ����1s��
	 */
	static final int SLEEP = 1000;
	
	/**
	 * @Fields start : �߳�ִ�б�־
	 */
	boolean start = true;
	
	/**
	 * @Fields end_time : ������Ϸ��ֹʱ��
	 */
	Timestamp end_time;
	
	/**
	 * @Fields room_id : ���鷿��ID
	 */
	String room_id;
	
	/**
	 * ����һ���µ�ʵ�� TimeThread.
	 *
	 * @param room_id ���鷿��ID
	 * @param end_time ������Ϸ��ֹʱ��
	 */
	public TimeThread(String room_id,Timestamp end_time){
		this.room_id = room_id;
		this.end_time = end_time;
	}
	
	
	public void run(){
		
		while(start){
			
			//���������Ϸ������ʱ��
			long time = (new Timestamp(end_time.getTime() - System.currentTimeMillis()).getTime())/1000; 
			
			//����ʱ����
			if(time == 0) {
				System.out.println("����ʱ����");
				start = false;
				
				JSONObject json = new JSONObject();
				json.put("cmd", CMD.TIME_OVER);
				//���¶����������
				synchronized(lock){
					aq.offer(new Action(CMD.TIME_OVER,room_id,json.toString()));
				}
			}
			
			int day = (int)(time/86400);
			int hour = (int)((time%86400)/3600);
			int minute = (int)(((time%86400)%3600)/60);
			int second = (int)(((time%86400)%3600)%60);
			
			JSONObject json = new JSONObject();
			json.put("cmd", CMD.TIME);
			json.put("day", day);
			json.put("hour", hour);
			json.put("minute", minute);
			json.put("second", second);
			
			//���¶����������
			synchronized(lock){
				aq.offer(new Action(CMD.TIME,room_id,json.toString()));
			}
			
			try{
				Thread.sleep(SLEEP);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
