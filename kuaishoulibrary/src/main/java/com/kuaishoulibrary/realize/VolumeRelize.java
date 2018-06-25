package com.kuaishoulibrary.realize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.kuaishoulibrary.KuaishouInterface.VolumeInterface;
import com.kuaishoulibrary.utils.BitmapUtil;
import com.kuaishoulibrary.utils.CameraEntity;
import com.kuaishoulibrary.utils.FileUtils;
import com.kuaishoulibrary.utils.SharedPreferencesUitl;
import com.kuaishoulibrary.utils.VolumeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VolumeRelize implements VolumeInterface, Camera.PreviewCallback, SurfaceHolder.Callback, Camera.PictureCallback {
    private SharedPreferencesUitl preferencesUitl;


    @Override
    public void initVolumeCamera(Context context, SurfaceView surfaceView) {
        this.context = context;
        this.surfaceView = surfaceView;
        isJiaozheng = false;
        isRunning = false;
        preferencesUitl = SharedPreferencesUitl.getInstance(context, "decoeBar");
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
        backMap = BitmapUtil.loadBitmap("/storage/emulated/0/DCIM/Camera/");
//        backMap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/" + 0 + ".jpg");
        initCamera();
    }

    @Override
    public void stopVolume() {

        if (mCamera != null) {

            isRunning = false;
            mCamera.setPreviewCallback(null);
        }
    }

    @Override
    public void volumePreviewPicture(boolean isBeiJing) {
        previewPicture(isBeiJing);
    }

    @Override
    public void volumeCheck() {
        isBeijing = false;
        isJiaozheng = true;
        if (mCamera != null)
            mCamera.setOneShotPreviewCallback(VolumeRelize.this);
    }

    @Override
    public boolean volumeIsRunning() {
        return isRunning;
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
    private boolean isBeijing;
    private Bitmap backMap = null;
    private boolean isJiaozheng = false;
    private boolean isRunning = false;

    public void previewPicture(boolean isBeiJing) {
        isBeijing = isBeiJing;
        if (isBeiJing) {
            if (mCamera != null) {
                isRunning = true;
                mCamera.setOneShotPreviewCallback(VolumeRelize.this);
            }
        } else {
            if (mCamera != null) {
                isRunning = true;
                mCamera.setPreviewCallback(VolumeRelize.this);

            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        // TODO: 2018/4/10   计算体积
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
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
            parameter.setExposureCompensation(-3);
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
        releaseVolume();
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

    public double[] V = new double[]{0, 0, 0, 5};

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void encodeData(CameraEntity entity) {
        // TODO: 2018/3/27   算法 /zhuanbitmap
        long a = System.currentTimeMillis();
        Mat picture = new Mat();
        YuvImage yuvimage = new YuvImage(entity.getYuvData(), ImageFormat.NV21, entity.getWidth(), entity.getHeight(), null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //2.5毫米镜头
        yuvimage.compressToJpeg(new android.graphics.Rect(500, 310, 1200, 900), 95, baos);// 80--JPG图片的质量[0-100],100最高
//        yuvimage.compressToJpeg(new android.graphics.Rect(150, 100, 1500, 1100), 95, baos);// 80--JPG图片的质量[0-100],100最高
        Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
        Log.i("tw", "拍照时间" + (System.currentTimeMillis() - a));
        bitmap = VolumeUtils.grayMap(bitmap);
        if (isBeijing) {
            backMap = bitmap;
            displayVolumeListener.getVolumeDatas(new double[]{0, 0, 0, 5}, backMap);
            try {
                if (BitmapUtil.saveFile(bitmap, "beijing", 100)) {
                    // TODO: 2018/3/9   成功
//                    Toast.makeText(context, "拍照背景成功", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: 2018/3/9   失败
//                    Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (backMap != null) {
                Utils.bitmapToMat(bitmap, picture);
                final Bitmap photo1 = Bitmap.createBitmap(backMap.getWidth(), backMap.getHeight(), Bitmap.Config.RGB_565);
                picture = VolumeUtils.combineBitmap(backMap, picture);
//                List<String> ss = new ArrayList<String>();
//                preferencesUitl.loadArray(ss);
//                SharedPreferencesUitl.loadArray(context, ss);
//                VolumeUtils.imageDeal(ss, picture, V);
                if (isJiaozheng) {
                    VolumeUtils.imageDeal(new ArrayList<String>(), picture, V);
                } else {
                    VolumeUtils.imageDeal(FileUtils.getVolumeFile(), picture, V);
                }
                Utils.matToBitmap(picture, photo1);
//                if (V[3] == 0) {
//                    isRunning = false;
//                    Log.i("tw", "全部处理消耗时间" + (System.currentTimeMillis() - a) + "毫秒");
//                    displayVolumeListener.getVolumeDatas(V, photo1);
//                }else {
//
//                    displayVolumeListener.getVolumeDatas(V, null);
//                }
                displayVolumeListener.getVolumeDatas(V, photo1);

            } else {
                // TODO: 2018/4/4   请拍背景
                Toast.makeText(context, "请拍背景", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCamera.stopPreview();
        mCamera.startPreview();
        // TODO: 2018/4/10   计算体积
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        EventBus.getDefault().post(new CameraEntity(data, previewSize.height, previewSize.width));
    }
}
