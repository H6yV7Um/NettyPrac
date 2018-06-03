/**
 * Copyright (C), 2015-2018, ele me
 * FileName: EchoClient
 * Author:   bian.wu
 * Date:     2018/6/3 13:52
 * Description: ${DESCRIPTION}
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.bw.nettyprac.nettyexamples.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;

/**
 * 〈一句话功能简述〉<br> 
 * 〈${DESCRIPTION}〉
 *
 * @author bian.wu
 * @create 2018/6/3
 * @since 1.0.0
 */
public class EchoClient {

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final int PORT = Integer.parseInt(System.getProperty("port","8007"));
    private static final String HOST = System.getProperty("host","127.0.0.1");
    static final int SIZE = Integer.parseInt(System.getProperty("size","256"));

    public void start() throws Exception{

        final SslContext sslCtx;
        if(SSL){
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }else {
            sslCtx = null;
        }
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception{
                            ChannelPipeline p = ch.pipeline();
                            if(sslCtx != null){
                                p.addLast(sslCtx.newHandler(ch.alloc(),HOST,PORT));
                            }
                            p.addLast(new EchoClientHandler());
                        }
                    });
            /**ChannelFuture 保存异步处理结果 */
            ChannelFuture future = bootstrap.connect(HOST,PORT).sync();
            future.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }

    }
    public static void main(String[] args){
        EchoClient client = new EchoClient();
        try{
            client.start();;
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}