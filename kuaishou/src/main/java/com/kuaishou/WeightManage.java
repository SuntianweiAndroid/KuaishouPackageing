package com.kuaishou;

import com.kuaishou.KuaishouInterface.WeightInterface;
import com.kuaishou.realize.WeightRealize;

public class WeightManage {
    public static WeightInterface weightInterface;

    public static WeightInterface getKuaishouIntance() {
        if (weightInterface == null) {
            weightInterface = new WeightRealize();
        }
        return  weightInterface;
    }

}
