package com.kuaishoupackaging;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.DeviceControl;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetingVolumeAct extends Activity implements VolumeInterface.DisplayVolumeListener, View.OnClickListener {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_volume_layout);
        initView();
        try {
            DeviceControl deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        volumeInterface.releaseVolumeCamera();
        VolumeManage.disVolumeInterface();
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
        volumeInterface = VolumeManage.getVolumeIntance();
        volumeInterface.initVolumeCamera(this, surfaceview);
        volumeInterface.setDisplayVolumeListener(this);
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
                    if (V[3] == 1) {
                        mTvShow.setText("处理失败，请重新处理！");
//                        mVolueLayout.setBackgroundColor(getResources().getColor(R.color.red));
//                        mTvShowmsg.setText("您的摆放不正确");
                    } else if (V[3] == 2) {
                        mTvShow.setText("处理失败，请重新处理！");
//                        volumeState = false;
//                        mTvShowmsg.setText("线缺失");
                    } else if (V[3] == 3) {
                        mTvShow.setText("处理失败，请重新处理！");
//                        volumeState = false;
//                        mTvShowmsg.setText("请往中心位置摆放");
                    } else if (V[3] == 4) {
                        mTvShow.setText("处理失败，请重新处理！");
//                        volumeState = false;
//                        mTvShowmsg.setText("请往中心位置摆放");
                    } else if (V[3] == 0) {
//                        volumeInterface.stopVolume();
                        Testdata.add(new Datas(V[0], V[1], V[2]));
                        mTvShow.setText("处理成功，请换下一个标准体！");
                        Log.i("stw", "handleMessage: chenggklsjdgklsdajdklfhasldkhfklsd" + V[0] + "！！！" + V[1] + "!!!" + V[2]);
                        Toast.makeText(SetingVolumeAct.this, "成功", Toast.LENGTH_SHORT).show();
//                        mVolume.setText(volume);
//                        volumeInterface.stopVolume();
                    }
                    break;
                case 2:
                    imageviewShow.setImageBitmap((Bitmap) msg.obj);
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v == btnBackground) {
            volumeInterface.volumePreviewPicture(true);
        } else if (v == btnStart) {
            volumeInterface.volumeCheck();
        } else if (v == btnNihe) {
            CheckOutVolumeUtils checkOutVolumeUtils = new CheckOutVolumeUtils(this);
            checkOutVolumeUtils.nihe(Testdata);
        }
    }


}

