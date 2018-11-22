package com.bn.Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;
import javax.xml.stream.events.StartDocument;

/**
 * @ClassName: DBUtil
 * @Description: ���ݿ������
 * @author JD
 * @date 2018��7��28��
 *
 */
public class DBUtil {
	
	/**
	 * @Title: getConnection
	 * @Description: �������ݿ�
	 * @return Connection 
	 */
	private static Connection getConnection()					// ���������ݿ����ӵķ���
	{
		Connection con = null;									// ��������
		try
		{
			Class.forName("com.mysql.jdbc.Driver");				// ��������
			// ���Եı���URL
//			String url = "jdbc:mysql://localhost:3306/birdgame?useUnicode=true" +
//	    	 		"&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&failOverReadOnly=false";
//			con = DriverManager.getConnection(url,"root","initial123");  //��ȡ����
			
			String url = "jdbc:mysql://chuangyh.com:3306/ChuangYi?useUnicode=true" +
	    	 		"&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&failOverReadOnly=false";
			con = DriverManager.getConnection(url,"p1p1us","p1p1us");  //��ȡ����
		}
		catch(Exception e)
		{
			e.printStackTrace();								// �쳣����
		}
		return con;
	}
	
	/**
	 * @Title: getUserNameAndPic
	 * @Description: ��ѯ�û��ǳ���ͼƬ·��
	 * @param user_id
	 * @return String[]  
	 */
	public static String[] getUserNameAndPic(String user_id){
		
		Connection con = getConnection();
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		
		String[] str = new String[2];
		try
		{
			st = con.createStatement();
			sql="select name,icon from user where id='"+user_id+"';";
			rs = st.executeQuery(sql);
		
			if(rs.next())
			{
				str[0] = rs.getString(1);
				str[1] = rs.getString(2);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{rs.close();}catch(SQLException e){e.printStackTrace();}
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	
		return str;
	}
	
	/**
	 * @Title: getMeetingRoom
	 * @Description: ���������IDģ����ѯ��Ӧ�Ļ��鷿�䣬��ʱ�������Ļ��鷿��������ʼ�����ʱ��֮�ڵ�
	 * @param search_id
	 * @return ArrayList<String>  
	 */
	public static ArrayList<String> getMeetingRoom(String search_id) {
		
		Connection con = getConnection();
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		
		ArrayList<String> al = new ArrayList<String>();
		try
		{
			st = con.createStatement();
			sql="select meeting_id,meeting_name,start_time,end_time from game_meeting_data "+ 
					"where meeting_id like '%"+search_id+"%' "+
					"and unix_timestamp(start_time) < unix_timestamp(NOW()) "+
					"and unix_timestamp(end_time) > unix_timestamp(NOW());";
			rs = st.executeQuery(sql);
		
			while(rs.next())
			{
				al.add(rs.getString(1) + "|" + rs.getString(2));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{rs.close();}catch(SQLException e){e.printStackTrace();}
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	
		return al;
	}
	
	
	/**
	 * @Title: getInvitationRoom 
	 * @Description: ģ��������Լ����
	 * @param search_id
	 * @return ArrayList<String>  
	 */
	public static ArrayList<String> getInvitationRoom(String search_id) {
		
		Connection con = getConnection();
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		
		ArrayList<String> al = new ArrayList<String>();
		try
		{
			st = con.createStatement();
//			sql="select invitation_id,invitation_name from invitation_data "+ 
//					"where invitation_id REGEXP '[0-9]{4}"+search_id+"';";
			sql="select invitation_id,invitation_name from game_invitation_data "+ 
			"where invitation_id like '%"+search_id+"%';";
			rs = st.executeQuery(sql);
		
			while(rs.next())
			{
				al.add(rs.getString(1) + "|" + rs.getString(2));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{rs.close();}catch(SQLException e){e.printStackTrace();}
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	
		return al;
	}
	
	/**
	 * @Title: insertMeetingRoom
	 * @Description: ������鷿��
	 * @param meeting_id
	 * @param meeting_name
	 * @param start_time
	 * @param end_time
	 * @param admin_id void  
	 */
	public static void insertMeetingRoom(String meeting_id,String meeting_name,Timestamp start_time,Timestamp end_time,String admin_id) {
		
		Connection con = getConnection();
		Statement st = null;
		String sql = null;
		
		try
		{
			st = con.createStatement();
			sql="insert into game_meeting_data(meeting_id,meeting_name,start_time,end_time,admin_id) value('"
					+ meeting_id+"','"+meeting_name+"','"+start_time+"','"+end_time+"','"+admin_id+"');";
			st.execute(sql);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	}
	
	
	/**
	 * @Title: getScore
	 * @Description: ��ȡuser_id�û���room_id�����ڵ���ߵ÷�
	 * @param user_id �û�ID
	 * @param room_id ����ID
	 * @return String �÷�
	 */
	public static String getScore(String user_id, String room_id) {
		
		Connection con = getConnection();
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		
		String ans = null;
		try
		{
			st = con.createStatement();
			sql="select score,user_id,room_id from game_score "+ 
					"where user_id = '"+ user_id +"' "+
					"and room_id = '"+ room_id + "'; ";
			rs = st.executeQuery(sql);
		
			if(rs.next())
			{
				ans = rs.getInt(1)+"";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{rs.close();}catch(SQLException e){e.printStackTrace();}
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	
		return ans;
	}
	
	
	/**
	 * @Title: insertScore
	 * @Description: ��÷ֱ�����ֵ
	 * @param user_id �û�ID
	 * @param room_id ����ID
	 * @param score �÷�
	 */
	public static void insertScore(String user_id, String room_id, int score) {
		Connection con = getConnection();
		Statement st = null;
		String sql = null;
		
		try
		{
			st = con.createStatement();
			sql="insert into game_score(user_id, room_id, score) value('"
					+ user_id+"','"+room_id+"','"+score+"');";
			st.execute(sql);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	}
	
	/**
	 * @Title: updateScore
	 * @Description: ���µ÷ֱ�
	 * @param user_id
	 * @param room_id
	 * @param score void  
	 */
	public static void updateScore(String user_id, String room_id, int score) {
		Connection con = getConnection();
		Statement st = null;
		String sql = null;
		
		try
		{
			st = con.createStatement();
			sql="update game_score set score = '"+ score +"'where user_id='"+user_id+"' "
					+"and room_id = '"+ room_id +"';";
			st.execute(sql);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	}
	

	/**
	 * @Title: getRank
	 * @Description: ��ȡ�����ڵĵ÷�����
	 * @param room_id
	 * @return String  
	 */
	public static ArrayList<String> getRankList(String room_id) {
		
		Connection con = getConnection();
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		
		ArrayList<String> al = new ArrayList<String>();
		try
		{
			st = con.createStatement();
			sql="select user_id,score,room_id from game_score "+ 
					"where room_id = '"+ room_id + "' order by score desc; ";
			rs = st.executeQuery(sql);
		
			while(rs.next())
			{
				al.add(rs.getString(1)+"|"+rs.getString(2));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{rs.close();}catch(SQLException e){e.printStackTrace();}
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	
		return al;
	}
	
	/**
	 * @Title: getSponsorLogo
	 * @Description: ��ȡ������Logo
	 * @param room_id
	 * @return ArrayList<String>  
	 */
	public static ArrayList<String> getSponsorLogo(String room_id){
		
		Connection con = getConnection();
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		
		ArrayList<String> al = new ArrayList<String>();
		try
		{
			st = con.createStatement();
			sql="select logo from game_logo where room_id='"+room_id+"';";
			rs = st.executeQuery(sql);
		
			while(rs.next())
			{
				al.add(rs.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{rs.close();}catch(SQLException e){e.printStackTrace();}
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	
		return al;
	}
	
	/**
	 * @Title: insertSponsorLogo
	 * @Description: ����������
	 * @param room_id �û�ID
	 * @param logo_name Logo����
	 */
	public static void insertSponsorLogo(String room_id, String logo_name) {
		Connection con = getConnection();
		Statement st = null;
		String sql = null;
		
		try
		{
			st = con.createStatement();
			sql="insert into game_logo(room_id, logo) value('"
					+ room_id+"','"+logo_name+"');";
			st.execute(sql);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	}
	
	
	
	
	/**
	 * @Title: havaSameInviteRoom
	 * @Description: �������ݿ����Ƿ�����ͬ���Ƶ���Լ����
	 * @param room_name
	 * @return boolean  
	 */
	public static boolean havaSameInviteRoom(String room_name) {
		
		Connection con = getConnection();
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		
		boolean answer = false;
		try
		{
			st = con.createStatement();
			sql="select * from game_invitation_data where invitation_name='"+room_name+"';";
			rs = st.executeQuery(sql);
		
			if(rs.next())
			{
				answer = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{rs.close();}catch(SQLException e){e.printStackTrace();}
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
		return answer;
	}
	

	/**
	 * @Title: insertInviteRoom
	 * @Description: �����ݿ��в�����Լ����
	 * @param user_id
	 * @param room_id
	 * @param room_name  
	 */
	public static void insertInviteRoom(String user_id, String room_id, String room_name) {
		Connection con = getConnection();
		Statement st = null;
		String sql = null;
		
		try
		{
			st = con.createStatement();
			sql="insert into game_invitation_data(invitation_id, invitation_name, homeowner_id) value('"
					+ room_id+"','"+room_name+"','"+user_id+"');";
			st.execute(sql);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{st.close();}catch(SQLException e){e.printStackTrace();}
			try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
	}
	
}
