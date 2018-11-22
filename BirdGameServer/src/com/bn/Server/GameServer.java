package com.bn.Server;

import com.bn.Server.ActionThread;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class GameServer
{
    private int port;

    public GameServer(int port) 
    {
        this.port = port;
    }

    public void doTask() throws Exception 
    {
    	//���������߳�
    	new ActionThread().start();
    	
    	//�������ڽ�����������Ķ��߳��¼���Ϣѭ����
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //��������ִ�о���ҵ���߼��ĵĶ��߳��¼���Ϣѭ����
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); 
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) 
             .childHandler(new GameServerInitializer())  
             //����tcp��������BACKLOG����Ϊ128
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true); 

            System.out.println("����������...");

            //�󶨶˿ڣ�������sync���������ȴ��󶨹�������
            ChannelFuture f = b.bind(port).sync(); 

            //����sync�������̣߳���֤����˹رպ�main�������˳�
            f.channel().closeFuture().sync();

        }finally 
        {
        	//���ŵعر��߳���
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
