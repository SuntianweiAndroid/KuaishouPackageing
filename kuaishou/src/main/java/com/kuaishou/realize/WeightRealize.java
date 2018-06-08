package com.kuaishou.realize;

import android.serialport.SerialPort;
import android.util.Log;

import com.kuaishou.KuaishouInterface.WeightInterface;
import com.speedata.libutils.DataConversionUtils;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author :孙天伟 in  2018/4/10   17:47.
 *         联系方式:QQ:420401567
 *         功能描述:
 */
public class WeightRealize implements WeightInterface {
    private SerialPort serialPort = null;
    private ReadThread readThread;
    private DisplayWeightDatasListener displayWeightDatasListener;
    private Queue<Double> queue = new ArrayDeque<>();//电子秤队列
    //#########################################################


    @Override
    public void initWeight() {
        startWeight();
    }

    @Override
    public void setWeightStatas(DisplayWeightDatasListener displayWeightDatasListener) {
        this.displayWeightDatasListener = displayWeightDatasListener;
    }

    @Override
    public void releaseWeightDev() {
        stopWeight();
    }

    class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                Log.i("aaa", "run: ");
                try {
                    int fd = serialPort.getFd();
                    if (fd == -1) {
                        displayWeightDatasListener.WeightStatas(0, 0);
                        readThread.interrupt();
                        return;
                    }
                    byte[] bytes = serialPort.ReadSerial(fd, 16);
                    Log.i("aaa", "run: " + DataConversionUtils.byteArrayToString(bytes));
                    if (bytes != null) {
                        for (int i = 0; i < bytes.length; i++) {
                            byte[] resultBytes = new byte[8];
                            if (bytes[i] == 61) {
                                System.arraycopy(bytes, i, resultBytes, 0, 8);
                                StringBuffer stringBuffer = new StringBuffer((String) DataConversionUtils.byteArrayToAscii(resultBytes));
                                double weight = Double.parseDouble(stringBuffer.reverse().toString().replace("=", ""));
                                if (weight != 0) {
                                    queue.offer(weight);
                                    Log.i("duilie", "mainEvent: 开始 ");
                                    if (queue.size() > 5) {
                                        Object[] s = queue.toArray();
                                        for (int j = 1; j < s.length; j++) {
                                            if ((double) s[s.length - 1] - (double) s[s.length - j - 1] < 0.01) {
                                                System.out.print((double) s[j] + "dafdafasdfas");
                                                displayWeightDatasListener.WeightStatas(1, (double) s[s.length - 1]);
                                                queue.poll();
                                            } else {
                                                displayWeightDatasListener.WeightStatas(2, (double) s[s.length - 1]);
                                                queue.poll();
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    displayWeightDatasListener.WeightStatas(2, weight);
                                }
                                Log.i("test", "evenbus——send");
                                break;
                            }
                        }
                    } else {
                        displayWeightDatasListener.WeightStatas(3, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("aaa", "catch为空 ");
                    displayWeightDatasListener.WeightStatas(3, 0);
                }
            }
        }
    }

    public void startWeight() {
        try {
            serialPort = new SerialPort();
            serialPort.OpenSerial("/dev/ttyMT2", 9600);
            readThread = new ReadThread();
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopWeight() {
        serialPort.CloseSerial(serialPort.getFd());
        readThread.interrupt();
    }


}
