package cn.bw.nettyprac.nettyexamples.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;


/***
 *
 * Netty Echo Example
 *
 * http://lanxinglan.cn/2017/09/13/Netty-In-Action-%E4%BA%8C/?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io
 *
 * bian.wu
 * 2019.6.3
 */


public class EchoServer {

    private final int port;

    public EchoServer(int port){
        this.port = port;
    }

    public void start() throws Exception{

        /**EventLoopGroup  处理I/O 多事件的循环，处理新的请求
         * 多种类型的EventLoop实现。
         * 本例子 使用 NioEventLoopGroup();
         * */
        EventLoopGroup group = new NioEventLoopGroup();

        /***
         * 1.server实例
         * 2.指定 NIO 协议及网络地址
         * 3.channel pipeline 添加 handler
         * 4.绑定服务器， 等待服务器关闭并释放资源
         */
        try{
            /**ServerBootstrap 类 */
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /**内部类的 lambda 写法*/
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoServerHandler()); /**添加事件处理Handler 处理业务逻辑 */
                        }
                    });
            /**ChannelFuture 类 是干嘛的 ？*/
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println(
                    EchoServer.class.getName()
                            + " started and listen on "
                            + future.channel().localAddress());
            future.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args)throws Exception{
        int port = 8007;
        new EchoServer(port).start();
    }

}
