package com.kuaishoupackaging;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.honeywell.barcode.ActiveCamera;
import com.honeywell.barcode.BarcodeBounds;
import com.honeywell.barcode.HSMDecodeComponent;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.barcode.Symbology;
import com.honeywell.camera.CameraManager;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.kuaishoulibrary.KuaishouInterface.VolumeInterface;
import com.kuaishoulibrary.KuaishouInterface.WeightInterface;
import com.kuaishoulibrary.VolumeManage;
import com.kuaishoulibrary.WeightManage;
import com.kuaishoulibrary.utils.Logcat;
import com.kuaishoulibrary.utils.PlaySound;
import com.kuaishoupackaging.base.BaseAct;
import com.kuaishoupackaging.been.OperBody;
import com.kuaishoupackaging.been.ScanDatas;
import com.kuaishoupackaging.db.KuaiShouDatas;
import com.kuaishoupackaging.tcpip.TcpIpUtils;
import com.kuaishoupackaging.util.DBUitl;
import com.kuaishoupackaging.util.DeviceControl;
import com.kuaishoupackaging.util.BarcodeDrawView;
import com.kuaishoupackaging.util.SharedPreferencesUitl;
import com.kuaishoupackaging.view.CustomToolBar;
import com.speedata.hwlib.ActivationManager;
import com.speedata.hwlib.net.DialogShowMsg;
import com.speedata.hwlib.net.Global;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseAct implements WeightInterface.DisplayWeightDatasListener, VolumeInterface.DisplayVolumeListener, CustomToolBar.BtnClickListener, DecodeResultListener {

    private DecimalFormat df;
    private SurfaceView mSurfaceview;
    private HSMDecodeComponent mHsmDecodeComponent;
    /**
     * 保证条码在预览框内
     */
    private TextView mTvShowmsg;
    private ImageView mImage;
    /**
     * 条码
     */
    private TextView mSuccess;
    /**
     * 空
     */
    private TextView mCode;
    private LinearLayout mLayoutBarcode;
    /**
     * 0.00KG
     */
    private TextView mWeight;
    private LinearLayout mLayoutWeight;
    private TextView mVolume, mTvsocketerromsg;
    private TextView mIsPass;
    private LinearLayout mFloatId;
    private int dbCount = 0;
    private boolean weightState = false;
    private boolean volumeState = false;
    public SharedPreferencesUitl preferencesUitl;
    public DBUitl dbUitl;
    private String weightResult;
    private WeightInterface weightInterface;
    private VolumeInterface volumeInterface;
    //    private HuoniScan huoniScan;
    private HSMDecoder hsmDecoder;
    private DeviceControl deviceControl;
    /**
     * 条码设置
     */
    public CustomToolBar customToolBar;
    private String barcode = "";

    private Queue<String> BarCodeQueue = new ArrayDeque<>();//条码队列
    private LinearLayout mVolueLayout;
    private TcpIpUtils tcpIpUtils;//socket通讯
    private double[] volumeDatas;
    private Timer timer = null;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initLibrary();
    }

    private void initView() {
        setContentView(R.layout.kuaishou_layout);
        mTvsocketerromsg = findViewById(R.id.tv_socket_erro);
        customToolBar = findViewById(R.id.title_bar_layout);
        mVolueLayout = findViewById(R.id.volume_layout);
//        customToolBar.setCameraState("相机：已连接");
        customToolBar.setTvExportVisable(false);
        customToolBar.setSetingBackground(R.drawable.title_seting);
        customToolBar.setTitleBarListener(this);
        mSurfaceview = findViewById(R.id.surfaceview);
        mHsmDecodeComponent = findViewById(R.id.hsm_decodeComponent);
        mTvShowmsg = findViewById(R.id.tv_showmsg);
        mImage = findViewById(R.id.image);
        mSuccess = findViewById(R.id.success);
        mCode = findViewById(R.id.code);
        mLayoutBarcode = findViewById(R.id.layout_barcode);
        mWeight = findViewById(R.id.weight);
        mLayoutWeight = findViewById(R.id.layout_weight);
        mVolume = findViewById(R.id.volume);
        mIsPass = findViewById(R.id.is_pass);
        mFloatId = findViewById(R.id.float_id);
        try {
            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN);
//            deviceControl.MainPowerOn(93);//体积激光
            deviceControl.MainPowerOn(98);//扫码灯光
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLibrary() {
        EventBus.getDefault().register(this);
        PlaySound.initSoundPool(this);
        dbUitl = new DBUitl();//初始化数据库util
        preferencesUitl = SharedPreferencesUitl.getInstance(this, "decoeBar");
        df = new DecimalFormat("######0.00");

        //初始化霍尼扫描解码
        hsmDecoder = HSMDecoder.getInstance(this);
        //库激活调用
        new ActivationManager(this).activate();
        //获取库camera设置相关参数
        Camera camera1 = CameraManager.getInstance(this).getCamera();
        Camera.Parameters parameters1 = camera1.getParameters();
        parameters1.setExposureCompensation(-3);
        parameters1.setAutoWhiteBalanceLock(true);
        parameters1.setColorEffect(Camera.Parameters.EFFECT_MONO);
        parameters1.setPreviewSize(1920, 1080);
        camera1.setParameters(parameters1);
        hsmDecoder.enableSound(false);//屏蔽库解码声音
        hsmDecoder.addResultListener(this);//条码返回监听
        CameraManager.getInstance(this).reopenCamera();
        //初始化提及测量
//        volumeInterface = VolumeManage.getVolumeIntance();

        //初始化称重
        weightInterface = WeightManage.getKuaishouIntance();
        weightInterface.setWeightStatas(this);
        handler.postDelayed(runnable, 500);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };
    private long startTime = 0;

    @SuppressLint("HardwareIds")
    @Override
    protected void onResume() {

//        startSocket();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        imei = telephonyManager.getDeviceId();
//        volumeInterface.initVolumeCamera(MainActivity.this, mSurfaceview);
//        volumeInterface.setDisplayVolumeListener(this);
        weightInterface.initWeight();
//        customToolBar.setCameraState("相机：" + preferencesUitl.read("hsmDecoder", ""));
        barcode = "";
        mTvShowmsg.setText(R.string.layout_hint);
        //查询缓存的快件条码
        BarCodeQueue = preferencesUitl.readQueue("queue");
        Object[] oo = BarCodeQueue.toArray();
        for (Object anOo : oo) {
            Log.i("BarCodeQueue", "onResume: " + anOo);
        }
        starterTimer();//检测所有状态
//        setCameraParams();
//        startTimer();
        super.onResume();

    }

//    @SuppressWarnings("unchecked")
//    private void setCameraParams() {
//        Camera.Parameters parameters = cameraManager.getCamera().getParameters();
//        try {
//            //获取支持的参数
//            Method parametersSetEdgeMode = Camera.Parameters.class
//                    .getMethod("setEdgeMode", String.class);
//            Method parametersSetBrightnessMode = Camera.Parameters.class
//                    .getMethod("setBrightnessMode", String.class);
//            Method parametersSetContrastMode = Camera.Parameters.class
//                    .getMethod("setContrastMode", String.class);
//
//            //锐度 亮度 对比度
//            parametersSetEdgeMode.invoke(parameters, "high");
//            parametersSetBrightnessMode.invoke(parameters, "high");
//            parametersSetContrastMode.invoke(parameters, "high");
//            Method parametersGetEdgeMode = Camera.Parameters.class
//                    .getMethod("getEdgeMode");
//            Method parametersGetBrightnessMode = Camera.Parameters.class
//                    .getMethod("getBrightnessMode");
//            Method parametersGetContrastMode = Camera.Parameters.class
//                    .getMethod("getContrastMode");
//
//            //锐度亮度对比度 是否设置成功
//            String ruidu = (String) parametersGetEdgeMode.invoke(parameters);
//            String liangdu = (String) parametersGetBrightnessMode.invoke(parameters);
//            String duibidu = (String) parametersGetContrastMode.invoke(parameters);
//
//            Log.d("cameraSetting", "mlist is" + ruidu + "-----" + liangdu + "-----" + duibidu);
//
//
//        } catch (Exception e) {
//            Log.d("cameraSetting", "error is::" + Log.getStackTraceString(e));
//        }
//
//    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                MainActivity.this.onStop();
//                MainActivity.this.onStart();
//                Toast.makeText(MainActivity.this, "切換", Toast.LENGTH_SHORT).show();
//                setContentView(R.layout.kuaishou_layout);
//                parameters1.setAutoExposureLock(true);
//                if (camera1 != null)
//                    camera1.setParameters(parameters1);
            }
        }, 2000);
    }

    private int a = 0;

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_F3) {
//            //处理体积
//            volumeInterface.volumePreviewPicture(false);
//            return true;
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {//调整扫码焦距
//
//            final Gson gson = new GsonBuilder().serializeNulls().create();
//            String postData = gson.toJson(new OperBody("1254783678", "456464646", 78.00, 12.00, 45.50, 23.23, 45.23, SystemClock.currentThreadTimeMillis()));
//            String rusultData = gson.toJson(new ScanDatas(1, postData));
//            tcpIpUtils.Send(rusultData);
//            if (a > 10) {
//                a = 0;
//            }
//            switch (a) {
//                case 0:
//                    parameters1.setZoom(0);
//                    break;
//                case 1:
//                    parameters1.setZoom(1);
//                    break;
//                case 2:
//                    parameters1.setZoom(2);
//                    break;
//                case 3:
//                    parameters1.setZoom(3);
//                    break;
//                case 4:
//                    parameters1.setZoom(4);
//                    break;
//                case 5:
//                    parameters1.setZoom(5);
//                    break;
//                case 6:
//                    parameters1.setZoom(6);
//                    break;
//                case 7:
//                    parameters1.setZoom(7);
//                    break;
//                case 8:
//                    parameters1.setZoom(8);
//                    break;
//                case 9:
//                    parameters1.setZoom(9);
//                    break;
//                case 10:
//                    parameters1.setZoom(10);
//                    break;
//            }
//            camera1.setParameters(parameters1);
//            a++;
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    protected void onPause() {
        super.onPause();
//        stopSocket();
        weightInterface.releaseWeightDev();
//        volumeInterface.releaseVolumeCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
//            deviceControl.MainPowerOff(93);
            deviceControl.MainPowerOff(98);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        huoniScan.release();
        EventBus.getDefault().unregister(this);
        if (hsmDecoder != null) {
            hsmDecoder.removeResultListener(this);
        }
        HSMDecoder.disposeInstance();
        if (timer != null) {
            timer.cancel();
        }
//        stopSocket();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1: //体积测量返回结果
                    volumeDatas = (double[]) msg.obj;
                    if (volumeDatas[3] == 0) {
                        volumeInterface.stopVolume();
                        mVolume.setText(R.string.db_weight + df.format(volumeDatas[0]) + "宽" + df.format(volumeDatas[1]) + "高" + df.format(volumeDatas[2]));
                        mVolueLayout.setBackgroundColor(getResources().getColor(R.color.green));
                        volumeState = true;
                    } else if (volumeDatas[3] == 1) {
                        volumeState = false;
                    }
                    break;
                case 2://显示体积测量的图片
                    mImage.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 3://Tcp接收服务端返回的结果
                    mTvsocketerromsg.setText("");
                    String reuslt = String.valueOf(msg.obj);
                    //解析
                    try {
                        JSONObject jsonObject = new JSONObject(reuslt);
                        int StateCode = jsonObject.getInt("code");
                        String messageResult = jsonObject.getString("message");
                        if (StateCode == 200) {
                            PlaySound.play(PlaySound.PASS_SCAN, PlaySound.NO_CYCLE);
                            mTvShowmsg.setTextColor(getResources().getColor(R.color.green));
                            mTvShowmsg.setText(R.string.layout_hint_pass);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4://tcp连接服务端错误
                    Log.i("xintiao", "心跳失敗,再次重連");
                    mTvsocketerromsg.setText(R.string.socket_erro_msg);
                    break;
                case 5://当前tcp 服务端的 ip地址

                    break;
                case 6://

                    break;
                case 10:// 条码/重量/体积 三合一 测量完毕上传数据
                    dbUitl.insertDtata(new KuaiShouDatas((String) msg.obj, weightResult + "kg", String.valueOf(R.string.volume_l) + 10 + String.valueOf(R.string.volume_w) + 10 + String.valueOf(R.string.volume_h) + 10, testTime(System.currentTimeMillis())));
//                    dbUitl.insertDtata(new KuaiShouDatas((String) msg.obj, weightResult + "kg", "长" + volumeDatas[0] + "宽" + volumeDatas[1] + "高" + volumeDatas[2], testTime(System.currentTimeMillis())));
//                    final Gson gson = new GsonBuilder().serializeNulls().create();
//                    String postData = gson.toJson(new OperBody(barcode, imei, Double.valueOf(weightResult), Double.parseDouble(df.format(volumeDatas[0])), Double.parseDouble(df.format(volumeDatas[1])), Double.parseDouble(df.format(volumeDatas[2])), Double.parseDouble(df.format(Double.parseDouble(df.format(volumeDatas[0])) * Double.parseDouble(df.format(volumeDatas[1])) * Double.parseDouble(df.format(volumeDatas[2])))), SystemClock.currentThreadTimeMillis()));
//                    String rusultData = gson.toJson(new ScanDatas(1, postData));
//                    tcpIpUtils.Send(rusultData);
                    mTvShowmsg.setTextColor(getResources().getColor(R.color.green));
                    mTvShowmsg.setText(R.string.layout_hint_pass);
                    dbCount++;
                    customToolBar.setCount(String.valueOf(R.string.title_save) + dbCount);
                    break;
                case 11:  //重复扫描条码 或电子秤断开或者秤波动
                    mLayoutWeight.setBackgroundColor(getResources().getColor(R.color.red));
                    mTvShowmsg.setTextColor(getResources().getColor(R.color.white));
                    mTvShowmsg.setText(R.string.layout_hint);
                    volumeState = false;
                    mVolume.setText(R.string.layout_hint_barcode);
//                    mCode.setText("空");
//                    mLayoutBarcode.setBackgroundColor(getResources().getColor(R.color.red));
                    mVolueLayout.setBackgroundColor(getResources().getColor(R.color.red));
                    break;
                case 12://实时更新条码
                    mLayoutBarcode.setBackgroundColor(getResources().getColor(R.color.green));
                    mCode.setText((String) msg.obj);
                    break;
            }
        }
    };

    public String testTime(long l) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date curDate = new Date(l);//获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 计时器监听全局重量与条码状态
     */
    private void starterTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (barcode.equals("")) {
                    return;
                }
//                if (!BarCodeQueue.contains(barcode) && weightState && volumeState) {//查解码队列里是否存在  三合一
                if (!BarCodeQueue.contains(barcode) && weightState) {//查解码队列里是否存在
                    BarCodeQueue.offer(barcode);
                    if (BarCodeQueue.size() > 2) {
                        Object[] s = BarCodeQueue.toArray();
                        for (Object value : s) {
                            Log.i("BarCodeQueue", "timer: " + value);
                        }
                        BarCodeQueue.poll();
                    }
                    preferencesUitl.writeQueue("queue", BarCodeQueue);
                    PlaySound.play(PlaySound.PASS_SCAN, PlaySound.NO_CYCLE);
                    handler.sendMessage(handler.obtainMessage(10, barcode));
                } else {

                }
            }
        }, 0, 60);
    }

    @Override
    public void WeightStatas(final int i, final double v) {
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                switch (i) {
                    case 0:
                        mWeight.setText("0.00KG");
                        customToolBar.setWeightState(String.valueOf(R.string.title_weight_state_false));
                        handler.sendMessage(handler.obtainMessage(11));
                        break;
                    case 1:
                        customToolBar.setWeightState(String.valueOf(R.string.title_weight_state_true));
                        mLayoutWeight.setBackgroundColor(getResources().getColor(R.color.green));
                        weightResult = df.format(v);
                        mWeight.setText(df.format(v) + "KG");
                        weightState = true;
                        break;
                    case 2:
                        customToolBar.setWeightState(String.valueOf(R.string.title_weight_state_true));
                        mWeight.setText(df.format(v) + "KG");
                        if (v == 0) {
//                            volumeInterface.stopVolume();
                            weightState = false;
                            volumeState = false;
                        } else {
                            volumeState = false;
//                            Log.i("kankan", "run: " + volumeInterface.volumeIsRunning());
//                            if (!volumeInterface.volumeIsRunning()) {
//                                volumeInterface.volumePreviewPicture(false);
//                            }
                        }
                        handler.sendMessage(handler.obtainMessage(11));
                        break;
                    case 3:
                        mWeight.setText("0.00KG");
                        customToolBar.setWeightState(String.valueOf(R.string.title_weight_state_false));
                        break;
                }
            }
        });

    }

    private BarcodeDrawView drawView;

//    @Override
//    public void displayBarcodeData(String s, long l, HSMDecodeResult[] hsmDecodeResults) {
//        List<BarcodeBounds> barcodeBoundsList = new ArrayList<>();
////************************
////        hsmDecoder.enableSound(true);
////        StringBuilder result = new StringBuilder();
////        String[] codeBytes = new String[hsmDecodeResults.length];
////        for (int i = 0; i < hsmDecodeResults.length; i++) {
////            codeBytes[i] = hsmDecodeResults[i].getBarcodeData();
////            String bar = hsmDecodeResults[i].getBarcodeData();
////            result.append("码" + i + ":" + bar + "\n");
////        barcodeBoundsList.add(hsmDecodeResults[i].getBarcodeBounds());
////        }
////        mSuccess.setText("条码" + codeBytes.length + "个");
////        mCode.setTextSize(20);
////        handler.sendMessage(handler.obtainMessage(12, result.toString()));
//        //***********************
////        mTvShowmsg.setText(count+"次");
//
//        if (isJingdDongCodes("76196584344-1-1-23")) {//京东判断条码
//            handler.sendMessage(handler.obtainMessage(12, s));//将解到码实时更新界面
//            if (BarCodeQueue.contains(s)) {
//                if (!s.equals(barcode)) {
//                    PlaySound.play(PlaySound.REPETITION, PlaySound.NO_CYCLE);
//                    handler.sendMessage(handler.obtainMessage(11, "重复扫描\n请扫描下一件物品"));
//                    Log.i("db", "播放声音");
//                }
//            }
//            barcode = s;
//        }
//        barcodeBoundsList.add(hsmDecodeResults[0].getBarcodeBounds());
//        if (drawView != null) {
//            mHsmDecodeComponent.removeView(drawView);
//        }
//        drawView = new BarcodeDrawView(this, barcodeBoundsList);
//        mHsmDecodeComponent.addView(drawView);
//    }

    /**
     * 判断是否符合京東条码格式
     */
    public boolean isJingdDongCodes(String s) {

        Pattern pattern = Pattern.compile("^([A-Za-z0-9]{8,})(-(?=[0-9]{1,5}-)|N(?=[0-9]{1,5}S))([1-9]{1}[0-9]{0,4})(-(?=[0-9]{1,5}-)|S(?=[0-9]{1,5}H))([1-9]{1}[0-9]{0,4})([-|H][A-Za-z0-9]*)$");
        Matcher matcher = pattern.matcher(s);
        //正则改为2个字符|null+16位数字（8位日期+8位序列）
        return matcher.matches();

    }


    @Override
    public void getVolumeDatas(double[] bytes, Bitmap bitmap) {
        // TODO: 2018/4/10    体积计算结果
        handler.sendMessage(handler.obtainMessage(2, bitmap));
        handler.sendMessage(handler.obtainMessage(1, bytes));
    }

    @Override
    public void exportClick() {

    }

    @Override
    public void settingClick() {
        intentAct(SettingAct.class);
    }

    //    @Override
//    public void huoniLibraryState(String s) {
//        preferencesUitl.write("hsmDecoder", s);
//        customToolBar.setCameraState("相机：" + s);
//    }

    /**
     * 激活解码返回
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDialogMsg(DialogShowMsg event) {
        String msg = event.getMsg();
        // 需要初始或使能条码类型
        hsmDecoder.enableAimer(false);
        hsmDecoder.setOverlayText("");
        hsmDecoder.setOverlayTextColor(Color.RED);
        switch (event.getTag()) {
            case Global.REQUEST_PREPARE://请求服务准备
                Logcat.d("REQUEST_PREPARE:" + msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case Global.REQUEST_ERROR://请求服务失败y
                Logcat.d("error " + msg);
                if (msg != null) {
                    customToolBar.setCameraState(String.valueOf(R.string.title_camera_state_false));
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
            case Global.REQUEST_SUCCESS://请求服务成功
                Logcat.d("REQUEST_SUCCESS:" + msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case Global.REGISTER_SUCCESS://激活成功 需要初始或使能条码类型
                hsmDecoder.enableSymbology(Symbology.CODE128);
                hsmDecoder.enableSymbology(Symbology.CODE39);
//                hsmDecoder.enableSymbology(Symbology.QR);
                customToolBar.setCameraState(String.valueOf(R.string.title_camera_state_true));
                Logcat.d("REGISTER_SUCCESS");
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case Global.REGISTER_FAILED://激活失败  也需要初始或使能条码类型
                customToolBar.setCameraState(String.valueOf(R.string.title_camera_state_false));
                Logcat.d("REGISTER_FAILED:" + msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void startSocket() {
        tcpIpUtils = new TcpIpUtils(handler, MainActivity.this);
        tcpIpUtils.startss();
        tcpIpUtils.starterTimer();
    }

    private void stopSocket() {
        tcpIpUtils.close();
    }

    @Override
    public void onHSMDecodeResult(HSMDecodeResult[] hsmDecodeResults) {
        List<BarcodeBounds> barcodeBoundsList = new ArrayList<>();
//************************
//        hsmDecoder.enableSound(true);
//        StringBuilder result = new StringBuilder();
//        String[] codeBytes = new String[hsmDecodeResults.length];
//        for (int i = 0; i < hsmDecodeResults.length; i++) {
//            codeBytes[i] = hsmDecodeResults[i].getBarcodeData();
//            String bar = hsmDecodeResults[i].getBarcodeData();
//            result.append("码" + i + ":" + bar + "\n");
//        barcodeBoundsList.add(hsmDecodeResults[i].getBarcodeBounds());
//        }
//        mSuccess.setText("条码" + codeBytes.length + "个");
//        mCode.setTextSize(20);
//        handler.sendMessage(handler.obtainMessage(12, result.toString()));
        //***********************
//        mTvShowmsg.setText(count+"次");

//        if (isJingdDongCodes("76196584344-1-1-23")) {//京东判断条码
        String decode = "";
        if (hsmDecodeResults.length > 0) {
            HSMDecodeResult firstResult = hsmDecodeResults[0];
            byte[] da = firstResult.getBarcodeDataBytes();
            if (isUTF8(firstResult.getBarcodeDataBytes())) {
                //utf-8格式
                try {
                    decode = new String(firstResult.getBarcodeDataBytes(),
                            "utf8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                //gbk格式
                try {
                    decode = new String(firstResult.getBarcodeDataBytes(),
                            "gbk");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        handler.sendMessage(handler.obtainMessage(12, decode));//将解到码实时更新界面
        if (BarCodeQueue.contains(decode)) {
            if (!decode.equals(barcode)) {
                PlaySound.play(PlaySound.REPETITION, PlaySound.NO_CYCLE);
                handler.sendMessage(handler.obtainMessage(11, R.string.layout_hint_chongfu));
                Log.i("db", "播放声音");
            }
        }
        barcode = decode;
//        }
        barcodeBoundsList.add(hsmDecodeResults[0].getBarcodeBounds());
        if (drawView != null) {
            mHsmDecodeComponent.removeView(drawView);
        }
        drawView = new BarcodeDrawView(this, barcodeBoundsList);
        mHsmDecodeComponent.addView(drawView);

    }

    private boolean IsUtf8 = false;

    //判断扫描的内容是否是UTF8的中文内容
    private boolean isUTF8(byte[] sx) {
        //Log.d(TAG, "begian to set codeset");
        for (int i = 0; i < sx.length; ) {
            if (sx[i] < 0) {
                if ((sx[i] >>> 5) == 0x7FFFFFE) {
                    if (((i + 1) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE)) {
                        i = i + 2;
                        IsUtf8 = true;
                    } else {
                        if (IsUtf8)
                            return true;
                        else
                            return false;
                    }
                } else if ((sx[i] >>> 4) == 0xFFFFFFE) {
                    if (((i + 2) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE) && ((sx[i + 2] >>> 6) == 0x3FFFFFE)) {
                        i = i + 3;
                        IsUtf8 = true;
                    } else {
                        if (IsUtf8)
                            return true;
                        else
                            return false;
                    }
                } else {
                    if (IsUtf8)
                        return true;
                    else
                        return false;
                }
            } else {
                i++;
            }
        }
        return true;
    }
}
