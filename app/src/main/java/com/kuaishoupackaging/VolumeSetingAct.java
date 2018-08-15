package com.kuaishoupackaging;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaishoulibrary.KuaishouInterface.VolumeInterface;
import com.kuaishoulibrary.VolumeManage;
import com.kuaishoulibrary.been.Datas;
import com.kuaishoulibrary.utils.CheckOutVolumeUtils;
import com.kuaishoupackaging.util.DeviceControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VolumeSetingAct extends Activity implements VolumeInterface.DisplayVolumeListener, View.OnClickListener {


    private VolumeInterface volumeInterface;
    private SurfaceView surfaceview;
    private ImageView imageviewShow;

    public List<Datas> Testdata = new ArrayList<>();
    private Button btnBackground;
    private Button btnStart;
    private Button btnNihe;
    /**
     * 请点击处理按钮
     */
    private TextView mTvShow;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_volume_layout);
        initView();
        volumeInterface = VolumeManage.getVolumeIntance();
        try {
            DeviceControl deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        count = 1;
        volumeInterface.initVolumeCamera(this, surfaceview);
        volumeInterface.setDisplayVolumeListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        volumeInterface.releaseVolumeCamera();
//        VolumeManage.disVolumeInterface();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initView() {
        surfaceview = findViewById(R.id.surfaceview);
        imageviewShow = findViewById(R.id.imageview_show);
        btnBackground = findViewById(R.id.btn_background);
        btnStart = findViewById(R.id.btn_start);
        btnBackground.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnNihe = findViewById(R.id.btn_nihe);
        btnNihe.setOnClickListener(this);
        mTvShow = findViewById(R.id.tv_show);
    }

    @Override
    public void getVolumeDatas(double[] bytes, Bitmap bitmap) {
        handler.sendMessage(handler.obtainMessage(2, bitmap));
        handler.sendMessage(handler.obtainMessage(1, bytes));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F3) {
            volumeInterface.volumePreviewPicture(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    double[] V = (double[]) msg.obj;
                    if (V[3] == 0) {
                        if (count > 5) {
                            mTvShow.setText(R.string.volume_set_hint_true);
                        } else {
                            Testdata.add(new Datas(V[0], V[1], V[2]));
                            mTvShow.setText(count + R.string.volume_set_hint_chenggong);
                            Log.i("stw", "handleMessage: chenggklsjdgklsdajdklfhasldkhfklsd" + V[0] + "！！！" + V[1] + "!!!" + V[2]);
//                            Toast.makeText(VolumeSetingAct.this, "成功", Toast.LENGTH_SHORT).show();
                        }
                        count++;
                    } else if (V[3] == 5) {
                        mTvShow.setText(R.string.volume_set_hint_beijing_true);
                    } else if (V[3] == 6) {
                        mTvShow.setText(R.string.volume_set_hint_beijing_false);
                    } else {
                        if (count > 5) {
                            mTvShow.setText(R.string.volume_set_hint_true);
                        } else {
                            mTvShow.setText(count + R.string.volume_set_hint_shibai);
                        }
                    }
                    break;
                case 2:
                    imageviewShow.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 3:
                    Testdata.clear();
                    count = 1;
                    VolumeSetingAct.this.finish();
                    break;
                case 4:
                    mTvShow.setText(R.string.volume_set_hint_jiaoyanfale);
                    Testdata.clear();
                    count = 1;
                    break;
                case 5:
                    count = 1;
                    Testdata.clear();
                    mTvShow.setText(R.string.volume_set_hint_jiaoyanfale);
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v == btnBackground) {
            mTvShow.setText(R.string.volume_set_hint_loading);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SystemClock.sleep(1500);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                volumeInterface.volumePreviewPicture(true);
//                            }
//                        });
//                    }
//                }).start();
            aa = 0;
            handler.postDelayed(runnable, 1500);

        } else if (v == btnStart) {
            mTvShow.setText(R.string.volume_set_hint_loading);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            volumeInterface.volumeCheck();
                        }
                    });
                }
            }).start();
        } else if (v == btnNihe) {
            CheckOutVolumeUtils checkOutVolumeUtils = new CheckOutVolumeUtils(this);
            checkOutVolumeUtils.nihe(Testdata, handler);
        }
    }

    private int aa = 0;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            volumeInterface.volumePreviewPicture(true);
            Log.i("tw", "run: yanshi ");
            handler.postDelayed(runnable, 1000);
            if (aa > 2) {
                handler.removeCallbacks(runnable);
            }
            aa++;
        }
    };

}

