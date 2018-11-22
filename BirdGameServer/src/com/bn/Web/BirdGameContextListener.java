
package com.bn.Web;

import javax.servlet.*;

import com.bn.Server.GameServer;

import java.io.*;

public class BirdGameContextListener implements ServletContextListener
{
	/*
	 * �����������������ڱ���Ŀ��
	 * WebRoot-WEB-INF-web.xmlĿ¼����ӵ�
	 */
	
	// ���������WebӦ�÷������ý��������ʱ�򱻵��á�
	public void contextInitialized(ServletContextEvent sce)
	{
		new Thread(){
			@Override
			public void run(){
				try{
					new GameServer(9988).doTask();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	// ���������WebӦ�÷����Ƴ���û�������ٽ��������ʱ�򱻵��á�
	public void contextDestroyed(ServletContextEvent sce)
	{
		
	}	
}