package com.kuaishoulibrary;


import com.kuaishoulibrary.KuaishouInterface.WeightInterface;
import com.kuaishoulibrary.realize.WeightRealize;

public class WeightManage {
    public static WeightInterface weightInterface;

    public static WeightInterface getKuaishouIntance() {
        if (weightInterface == null) {
            weightInterface = new WeightRealize();
        }
        return  weightInterface;
    }

}
