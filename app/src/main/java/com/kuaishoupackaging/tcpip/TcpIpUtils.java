package com.kuaishoupackaging.tcpip;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kuaishoulibrary.utils.SharedPreferencesUitl;
import com.kuaishoupackaging.been.operType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class TcpIpUtils {
    private String ip = "192.168.1.166";
    private int port = 8888;
    private String TAG = "socket thread";

    public Socket client = null;
    private PrintWriter out;
    BufferedReader in;
    boolean isRun = true;
    private Handler myHandler;
    private Context ctx;
    private String TAG1 = "===Send===";
    private Receive_Thread receiveThread;
    private final SharedPreferencesUitl preferencesUitl;

    public TcpIpUtils(Handler handler, Context context) {
        preferencesUitl = SharedPreferencesUitl.getInstance(context, "decoeBar");
        this.myHandler = handler;
        this.ctx = context;
    }

    /**
     * 连接socket服务器
     */
    public void conn() {
        try {
            initdate();
            Log.i(TAG, "连接中……");
            client = new Socket(ip, port);
            int timeout = 10000;
            client.setSoTimeout(timeout);// 设置阻塞时间
            MyLog.i(TAG, "连接成功");
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream(), "GBK"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream(), "GBK")), true);
            MyLog.i(TAG, "输入输出流获取成功");
        } catch (UnknownHostException e) {
            MyLog.i(TAG, "连接错误UnknownHostException 重新获取");
            e.printStackTrace();
            conn();
        } catch (IOException e) {
            MyLog.i(TAG, "连接服务器io错误");
            myHandler.sendMessage(myHandler.obtainMessage(4));
            e.printStackTrace();
        } catch (Exception e) {
            MyLog.i(TAG, "连接服务器错误Exception" + e.getMessage());
            myHandler.sendMessage(myHandler.obtainMessage(4));
            e.printStackTrace();
        }
    }

    public void initdate() {
        ip = preferencesUitl.read("ip", "192.168.1.155");
        port = Integer.parseInt(preferencesUitl.read("duankou", "8888"));
        myHandler.sendMessage(myHandler.obtainMessage(5,ip+port));
        MyLog.i(TAG, "获取到ip端口:" + ip + ";" + port);
    }


    public void startss() {
        receiveThread = new Receive_Thread();
        receiveThread.start();
    }

    /**
     * 接收线程
     */
    class Receive_Thread extends Thread//继承Thread
    {
        public void run() {//重写run方法
            conn();
            while (isRun) {
                try {
                    final byte[] buf = new byte[1024];
                    final int len = client.getInputStream().read(buf);
                    String result = new String(buf, 0, len);
                    myHandler.sendMessage(myHandler.obtainMessage(3, result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Timer timer;

    /**
     * tcp心跳检测是否连接
     */
    public void starterTimer() {
        int time = Integer.parseInt(preferencesUitl.read("heart", "1"));
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final Gson gson = new GsonBuilder().serializeNulls().create();
                String rusultData = gson.toJson(new operType(0));
                Send(rusultData);
            }
        }, 0, time * 60000);
    }

    /**
     * 发送数据
     *
     * @param mess
     */
    public void Send(String mess) {
        try {
            if (client != null) {
                MyLog.i(TAG1, "发送" + mess + "至"
                        + client.getInetAddress().getHostAddress() + ":"
                        + String.valueOf(client.getPort()));
                out.println(mess);
                out.flush();
                MyLog.i(TAG1, "发送成功");

            } else {
                MyLog.i(TAG, "client 不存在");
                MyLog.i(TAG, "连接不存在重新连接");
                conn();
            }

        } catch (Exception e) {
            MyLog.i(TAG1, "send error");
            e.printStackTrace();
        } finally {
            MyLog.i(TAG1, "发送完毕");

        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            isRun = false;
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (receiveThread != null) {
                receiveThread = null;
            }
            if (client != null) {
                MyLog.i(TAG, "close in");
                in.close();
                MyLog.i(TAG, "close out");
                out.close();
                MyLog.i(TAG, "close client");
                client.close();
            }
        } catch (Exception e) {
            MyLog.i(TAG, "close err");
            e.printStackTrace();
        }

    }
}
