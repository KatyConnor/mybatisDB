package hx.data.mybatis.socket;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author mingliang
 * @Date 2017-09-28 11:25
 */
public class Service {

    public static void main(String[] args) throws Exception{
        //服务端在20006端口监听客户端请求的TCP连接
        ServerSocket server = new ServerSocket(8026);
        Socket client = null;
        boolean f = true;
        while(f){
            //等待客户端的连接，如果没有获取连接
            client = server.accept();
            System.out.println("与客户端连接成功！");
            //为每个客户端连接开启一个线程
            new Thread(new ServerThread(client)).start();
        }
        server.close();
    }
}
