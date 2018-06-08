package com.kuaishou.KuaishouInterface;

import android.content.Context;
import android.view.SurfaceView;

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
 * @author :孙天伟 in  2018/4/10   17:48.
 *         联系方式:QQ:420401567
 *         功能描述:
 */
public interface VolumeInterface {
    interface DisplayVolumeListener {
        void getVolumeDatas(byte[] bytes);
    }

    /**
     * 初始化相机和相关库文件
     * @param context 上下文对象
     * @param surfaceView 客户自定义surfaceView
     */
    void initVolumeCamera(Context context, SurfaceView surfaceView);

    /**
     * 预览截图
     * @param isOne 回调一次与一直回调
     */
    void  volumePreviewPicture(boolean isOne);

    /**
     * 回调体积结果
     * @param displayVolumeListener
     */

    void setDisplayVolumeListener(DisplayVolumeListener displayVolumeListener);

    /**
     * 释放相机
     */
    void releaseVolumeCamera();
}
