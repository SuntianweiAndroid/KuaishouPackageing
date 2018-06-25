package com.kuaishoulibrary.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.kuaishoulibrary.been.Datas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckOutVolumeUtils {
    private int n, N;
    private int n_deg = 2;
    private static ArrayList<String> resultDatas = new ArrayList<String>();
    private static ArrayList<String> H_measure = new ArrayList<String>();
    private Context context;
    private SharedPreferencesUitl preferencesUitl;

    public CheckOutVolumeUtils(Context context) {
        this.context = context;
        preferencesUitl = SharedPreferencesUitl.getInstance(context, "decoeBar");
    }

    private List<Datas> GetCriterionDatas() {
        List<Datas> CriterionList = new ArrayList<>();
        /* 将标准体的实际长宽高写入 */
        CriterionList.add(new Datas(25, 25, 10.2));    //第一个标准长、宽、高，以此类推
        CriterionList.add(new Datas(25, 25, 21.2));
        CriterionList.add(new Datas(25, 25, 31.8));
        CriterionList.add(new Datas(25, 25, 42.2));
        CriterionList.add(new Datas(25, 25, 52.5));
//        CriterionList.add(new Datas(22.8, 15.8, 15.5));    //第一个标准长、宽、高，以此类推
//        CriterionList.add(new Datas(15.5, 15.3, 23));
//        CriterionList.add(new Datas(37.8, 28.3, 30.2));
//        CriterionList.add(new Datas(30.5, 28, 41.8));
//        CriterionList.add(new Datas(30.5, 28, 53.8));
        return CriterionList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void nihe(List<Datas> Testdata, Handler handler) {
        /* 调用函数拟合， 并将拟合系数保存至savedata*/
        H_measure = getdata(Testdata, "H");
        resultDatas.addAll(calc_pfit(H_measure, getradio(getdata(GetCriterionDatas(), "L"), getdata(Testdata, "L"))));
        resultDatas.addAll(calc_pfit(H_measure, getradio(getdata(GetCriterionDatas(), "W"), getdata(Testdata, "W"))));
        resultDatas.addAll(calc_pfit(H_measure, getdata(GetCriterionDatas(), "H")));

        if (resultDatas.size() != 0) {
//            preferencesUitl.saveArray(resultDatas);  /* 保存拟合数据 */
//            SharedPreferencesUitl.saveArray(context, resultDatas);
            if (FileUtils.saveVolumeFile(resultDatas)) {
                handler.sendMessage(handler.obtainMessage(3));
                resultDatas.clear();
                H_measure.clear();
            } else {
                handler.sendMessage(handler.obtainMessage(4));
                resultDatas.clear();
                H_measure.clear();
            }
            Toast.makeText(context, "拟合成功！", Toast.LENGTH_SHORT).show();
        } else {
            handler.sendMessage(handler.obtainMessage(5));
        }
    }

    /* 拟合函数 */

    private ArrayList<String> calc_pfit(ArrayList<String> x_axis, ArrayList<String> y_axis) {
        n = n_deg;
        double a[] = new double[n + 1];
        ArrayList<String> coeff = new ArrayList<String>();
        if (n > x_axis.size()) {
//            Toast.makeText(getBaseContext(), "拟合数据不足，请测量并添加更多的数据！", Toast.LENGTH_LONG).show();
        } else if (x_axis.size() != y_axis.size()) {
//            Toast.makeText(getBaseContext(), "发生错误！请测量更多的标准体！", Toast.LENGTH_SHORT).show();
        } else {
            N = x_axis.size();
            double x[] = new double[N];
            double y[] = new double[N];
            for (int i = 0; i < N; i++) {
                x[i] = Double.parseDouble(x_axis.get(i));
                y[i] = Double.parseDouble(y_axis.get(i));
            }

            double X[] = new double[2 * n + 1];
            for (int i = 0; i < 2 * n + 1; i++) {
                X[i] = 0;
                for (int j = 0; j < N; j++)
                    X[i] = X[i] + Math.pow(x[j], i);        //consecutive positions of the array will store N,sigma(xi),sigma(xi^2),sigma(xi^3)....sigma(xi^2n)
            }
            double B[][] = new double[n + 1][n + 2];            //B is the Normal matrix(augmented) that will store the equations, 'a' is for value of the final coefficients
            for (int i = 0; i <= n; i++)
                for (int j = 0; j <= n; j++)
                    B[i][j] = X[i + j];            //Build the Normal matrix by storing the corresponding coefficients at the right positions except the last column of the matrix
            double Y[] = new double[n + 1];                    //Array to store the values of sigma(yi),sigma(xi*yi),sigma(xi^2*yi)...sigma(xi^n*yi)
            for (int i = 0; i < n + 1; i++) {
                Y[i] = 0;
                for (int j = 0; j < N; j++)
                    Y[i] = Y[i] + Math.pow(x[j], i) * y[j];        //consecutive positions will store sigma(yi),sigma(xi*yi),sigma(xi^2*yi)...sigma(xi^n*yi)
            }
            for (int i = 0; i <= n; i++)
                B[i][n + 1] = Y[i];                //load the values of Y as the last column of B(Normal Matrix but augmented)
            n = n + 1;
            for (int i = 0; i < n; i++)                    //From now Gaussian Elimination starts(can be ignored) to solve the set of linear equations (Pivotisation)
                for (int k = i + 1; k < n; k++)
                    if (B[i][i] < B[k][i])
                        for (int j = 0; j <= n; j++) {
                            double temp = B[i][j];
                            B[i][j] = B[k][j];
                            B[k][j] = temp;
                        }

            for (int i = 0; i < n - 1; i++)            //loop to perform the gauss elimination
                for (int k = i + 1; k < n; k++) {
                    double t = B[k][i] / B[i][i];
                    for (int j = 0; j <= n; j++)
                        B[k][j] = B[k][j] - t * B[i][j];    //make the elements below the pivot elements equal to zero or elimnate the variables
                }
            for (int i = n - 1; i >= 0; i--)                //back-substitution
            {                        //x is an array whose values correspond to the values of x,y,z..
                a[i] = B[i][n];                //make the variable to be calculated equal to the rhs of the last equation
                for (int j = 0; j < n; j++)
                    if (j != i)            //then subtract all the lhs values except the coefficient of the variable whose value                                   is being calculated
                        a[i] = a[i] - B[i][j] * a[j];
                a[i] = a[i] / B[i][i];            //now finally divide the rhs by the coefficient of the variable to be calculated
            }

            for (int i = 0; i < n; i++) {
                coeff.add(i, Double.toString(a[i]));
            }
        }
        return coeff;

    }

    /* 计算实际长宽与高的像素值的比例*/

    private ArrayList<String> getradio(ArrayList<String> actualist, ArrayList<String> testlist) {
        ArrayList<String> ratio = new ArrayList<>();
        if (actualist.size() != testlist.size()) {
//            Toast.makeText(getBaseContext(), "发生错误！请测量更多的标准体！", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < actualist.size(); i++) {
                double actual = Double.parseDouble(actualist.get(i));
                double measure = Double.parseDouble(testlist.get(i));
                ratio.add(Double.toString(actual / measure));
            }
        }
        return ratio;
    }
    /* 获取Datas类里的长宽高 */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList<String> getdata(List<Datas> data, String index) {
        ArrayList<String> list = new ArrayList<>();
        if (data.size() == 0) {
//            Toast.makeText(getBaseContext(), "未发现测量数据！", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < data.size(); i++) {
                if (Objects.equals(index, "L")) {
                    list.add(String.valueOf(data.get(i).getLength()));
                } else if (Objects.equals(index, "W")) {
                    list.add(String.valueOf(data.get(i).getWide()));
                } else {
                    list.add(String.valueOf(data.get(i).getHeight()));
                }
            }
        }
        return list;
    }
}
