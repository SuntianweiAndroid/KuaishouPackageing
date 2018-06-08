package com.kuaishoupackaging.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpUtils {
    private static final String HOST = "192.168.1.155";//服务器地址
    private static final int PORT = 8888;//连接端口号
    private static Socket socket = null;
    private static BufferedReader in = null;
    private static PrintWriter out = null;

    //接收线程发送过来信息，并用TextView追加显示
    public static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("dddd", "handleMessage: " + (CharSequence) msg.obj);
        }
    };

    //启动线程，连接服务器，并用死循环守候，接收服务器发送过来的数据

    public static void sendMSG(final String msg) {
//        if (socket.isConnected()) {//如果服务器连接
//            if (!socket.isOutputShutdown()) {//如果输出流没有断开
                out.println(msg);//点击按钮发送消息
//            }
//        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connection();
                    out.println(msg);//点击按钮发送消息
                }
            }).start();
//        }
    }

    /**
     * 连接服务器
     */
    private static void connection() {
        try {
            socket = new Socket(HOST, PORT);//连接服务器
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));//接收消息的流对象
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);//发送消息的流对象
        } catch (IOException ex) {
            ex.printStackTrace();
            //  ShowDialog("连接服务器失败：" + ex.getMessage());
        }
    }


    /**
     * 读取服务器发来的信息，并通过Handler发给UI线程
     */
    public static void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection();// 连接到服务器
                try {
                    while (true) {//死循环守护，监控服务器发来的消息
                        if (!socket.isClosed()) {//如果服务器没有关闭
                            if (socket.isConnected()) {//连接正常
                                if (!socket.isInputShutdown()) {//如果输入流没有断开
                                    String getLine;
                                    in.read();
                                    if ((getLine = in.readLine()) != null) {//读取接收的信息
                                        getLine += "\n";
                                        Message message = new Message();
                                        message.obj = getLine;
                                        mHandler.sendMessage(message);//通知UI更新
                                    } else {

                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
