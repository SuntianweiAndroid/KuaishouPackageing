package com.kuaishoulibrary.KuaishouInterface;
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
public interface WeightInterface {
    interface DisplayWeightDatasListener {
        void WeightStatas(int i, double v);
    }

    /**
     * 初始电子串口
     */
    void initWeight();

    /**
     * 获取电子秤状态以及重量数据
     *
     * @param displayWeightDatasListener
     */
    void setWeightStatas(DisplayWeightDatasListener displayWeightDatasListener);

    /**
     * 释放串口
     */
    void releaseWeightDev();

}
