package com.example.user.helpeachother;

/**
 * Created by Administrator on 2018/2/28.
 */
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MyConnector {
    public Socket socket = null; // 声明Socket对象
    public DataInputStream din = null; // 声明数据输入流对象
    public DataOutputStream dout = null; // 声明数据输出流对象

    public MyConnector(String address, int port) {
        try {
            socket = new Socket();
            SocketAddress isa = new InetSocketAddress(address, port);
            socket.connect(isa, 20000);
            // socket = new Socket(address,port);
            // socket.setSoTimeout(2000);
            din = new DataInputStream(socket.getInputStream()); // 获得输入流
            dout = new DataOutputStream(socket.getOutputStream()); // 获得输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 方法：断开连接，释放资源
    public void sayBye() {
        try {
            dout.writeUTF("<#USER_LOGOUT#>"); // 发出断开消息
            din.close();
            dout.close();
            socket.close();
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
