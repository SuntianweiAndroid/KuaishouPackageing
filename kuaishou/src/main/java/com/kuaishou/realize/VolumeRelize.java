package com.kuaishou.realize;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.kuaishou.KuaishouInterface.VolumeInterface;
import com.kuaishou.utils.CameraEntity;

import org.greenrobot.eventbus.EventBus;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class VolumeRelize implements VolumeInterface, Camera.PreviewCallback, SurfaceHolder.Callback {

    @Override
    public void initVolumeCamera(Context context, SurfaceView surfaceView) {
        this.context = context;
        this.surfaceView = surfaceView;
        EventBus.getDefault().register(VolumeRelize.this);
        mLoaderCallback = new BaseLoaderCallback(context) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        initCamera();
    }

    @Override
    public void volumePreviewPicture(boolean isOne) {
        previewPicture(isOne);
    }


    @Override
    public void setDisplayVolumeListener(DisplayVolumeListener displayVolumeListener) {
        this.displayVolumeListener = displayVolumeListener;
    }

    @Override
    public void releaseVolumeCamera() {
        releaseVolume();
        EventBus.getDefault().unregister(VolumeRelize.this);
    }


    //#################################################
    private DisplayVolumeListener displayVolumeListener;
    private Camera mCamera;
    private Context context;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private static final String TAG = "TW";

    private BaseLoaderCallback mLoaderCallback;


    public void previewPicture(boolean b) {
        if (b) {
            mCamera.setOneShotPreviewCallback(VolumeRelize.this);
        } else {
            mCamera.setPreviewCallback(VolumeRelize.this);
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        // TODO: 2018/4/10   计算体积
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        displayVolumeListener.getVolumeDatas(new byte[]{0, 0});
        EventBus.getDefault().post(new CameraEntity(bytes, previewSize.height, previewSize.width));
    }

    public void initCamera() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(VolumeRelize.this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera = Camera.open(1);
            mCamera.setPreviewDisplay(surfaceHolder);
            Camera.Parameters parameter = mCamera.getParameters();
            parameter.setPreviewSize(1680, 1248);
            parameter.setRotation(90);
            parameter.setExposureCompensation(-3);
            parameter.setColorEffect(Camera.Parameters.EFFECT_MONO);
            mCamera.setParameters(parameter);
            mCamera.startPreview();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            Toast.makeText(context, "摄像头打开失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    /**
     * 释放摄像头
     */
    public void releaseVolume() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release(); // release the  mCamera for other applications
            mCamera = null;
        }

    }
}
