package com.bn.Server;

import java.util.Arrays;
import io.netty.channel.*;
import io.netty.channel.group.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.*;
import net.sf.json.JSONObject;

import com.bn.Util.CMD;
import com.bn.Database.DBUtil;
import com.bn.Server.Action;
import com.bn.Server.Room;

import static com.bn.Server.Room.*;

public class GameServerHandler extends SimpleChannelInboundHandler<Object> 
{
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	//����������ȡ���ͻ��˵���Ϣ
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception 
	{ 
		
		if (msg instanceof BinaryWebSocketFrame) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", msg.getClass().getName()));
        }
		
        if(msg instanceof TextWebSocketFrame){
            // ����Ӧ����Ϣ
            String request = ((TextWebSocketFrame) msg).text();
            System.out.println(request);
            
            JSONObject jsonObject = JSONObject.fromObject(request);
            int cmd = jsonObject.optInt("cmd");
            String user_id = jsonObject.optString("user_id");
            String room_id = jsonObject.optString("room_id");
            String search_id = jsonObject.optString("search_id");
            String score = jsonObject.optString("score");
            String room_name = jsonObject.optString("room_name");
            
            //���¶����������
			synchronized(lock){
				aq.offer(new Action(ctx.channel(),cmd,room_id,user_id,search_id,score,room_name));
			}
        }
	}
	
	//���������յ��ͻ��˶Ͽ���Ϣʱ����
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception 
	{  
		//��ȡ��ǰ��Channel
		Channel incoming = ctx.channel();
		
		//���������û��б�֪ͨ������Ϣ
		for (Channel channel : channels) 
		{
		    //channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " �뿪"));
		}
		//�����û����б���ɾ��
		channels.remove(ctx.channel());
		//��ӡ��������־
		System.out.println("�ͻ���:"+incoming.remoteAddress() +"�뿪");		
	}
	
	//�������������ͻ��˻
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{ 
		//��ӡ�ͻ���IP��ַ
        //System.out.println("Active:"+ctx.channel().remoteAddress());
	}
	
	
	//�����쳣
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception 
	{
		//Channel incoming = ctx.channel();
		//System.out.println("�ͻ���:"+incoming.remoteAddress()+"�쳣");
		// �������쳣�͹ر�����
		cause.printStackTrace();
		ctx.close();

	}
}