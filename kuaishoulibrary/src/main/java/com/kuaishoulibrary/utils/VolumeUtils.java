package com.kuaishoulibrary.utils;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class VolumeUtils {
    //解码计算方法
    public static Bitmap grayMap(Bitmap bg) {
        Mat rgbMat = new Mat();
        List<Mat> dst1 = new java.util.ArrayList<Mat>(3);
        Utils.bitmapToMat(bg, rgbMat);
        Core.split(rgbMat, dst1);
//        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(3,3));
//        Imgproc.dilate(rgbMat,rgbMat,element);
//        Imgproc.erode(rgbMat,rgbMat,element);
//        Imgproc.cvtColor(rgbMat,rgbMat,Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(dst1.get(2), rgbMat, 5, 255, Imgproc.THRESH_TOZERO);
        Utils.matToBitmap(rgbMat, bg);
        return bg;
    }

    public static Mat combineBitmap(Bitmap background1, Mat picture) {
        Mat ph = new Mat();
        Utils.bitmapToMat(background1, ph);
//        List<Mat> dst1=new java.util.ArrayList<Mat>(3);
//        Imgproc.cvtColor(background,background,Imgproc.COLOR_RGB2GRAY);
//        Imgproc.threshold(background,background,180,255,Imgproc.THRESH_OTSU);
//        Imgproc.cvtColor(picture,picture,Imgproc.COLOR_RGB2GRAY);
//        Imgproc.threshold(picture,picture,180,255,Imgproc.THRESH_OTSU);
//        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(3,3));
//        Imgproc.dilate(picture,picture,element);
//        Imgproc.erode(picture,picture,element);
        Core.bitwise_xor(picture, ph, ph);
        Core.bitwise_and(ph, picture, picture);
//        Imgproc.medianBlur(picture,picture,5);
        Imgproc.GaussianBlur(picture, picture, new Size(3, 3), 5);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3, 3));
        Imgproc.erode(picture, picture, element);
        return picture;
    }

    public static double[] imageDeal(List<String> downloaddata, Mat photo2, double[] V) {
        int q = 0;
        int th = 10;
        int th1 = 25;
        double[][] xx = new double[2][2];
        double[][] yy = new double[2][2];
        double[] data;
        Mat lines = new Mat();
        Mat rgbMat = new Mat();
        Imgproc.Canny(photo2, rgbMat, 100, 250);
        Imgproc.HoughLines(rgbMat, lines, 1, Math.PI / 180, 40);
        double[][] p = new double[lines.rows()][2];
        for (int i = 0; i < lines.rows(); i++) {
            data = lines.get(i, 0);
            p[i][0] = data[0];
            p[i][1] = (double) Math.round(data[1] / Math.PI * 180 * 10) / 10;
        }
        sort(p, new int[]{1, 1});
        double[][] cross = new double[4][2];
        double[] a1 = new double[4];
        double[] b1 = new double[4];
        double[][] set = new double[4][2];
        double[][] P = new double[4][2];
        double[][] P1 = new double[4][2];
        for (double[] ap1 : p) {
            if (ap1[1] > 0 && ap1[1] < 3) {
                if (set[0][0] == 0 && set[0][1] == 0 || Math.abs(ap1[0] - set[0][0]) < th && abs(ap1[1] - set[0][1]) < 1.5) {
                    if (set[0][0] == 0 && set[0][1] == 0) {
                        set[0][0] = ap1[0];
                        set[0][1] = ap1[1];
                    } else {
                        set[0][0] = (set[0][0] + ap1[0]) / 2;
                        set[0][1] = (set[0][1] + ap1[1]) / 2;
                    }
                } else {
                    if (set[1][0] == 0 && set[1][1] == 0 && Math.abs(ap1[0] - set[0][0]) > th1 && set[0][0] != 0 || Math.abs(ap1[0] - set[1][0]) < th && abs(ap1[1] - set[1][1]) < 1.5) {
                        if (set[1][0] == 0 && set[1][1] == 0) {
                            set[1][0] = ap1[0];
                            set[1][1] = ap1[1];
                        } else {
                            set[1][0] = (set[1][0] + ap1[0]) / 2;
                            set[1][1] = (set[1][1] + ap1[1]) / 2;
                        }
                    }
                }
            }
        }
        for (double[] aP : p) {
            if (aP[1] > 89.5 && aP[1] < 91.5) {
                if (set[2][0] == 0 && set[2][1] == 0 || Math.abs(aP[0] - set[2][0]) < th && abs(aP[1] - set[2][1]) < 1.5) {
                    if (set[2][0] == 0 && set[2][1] == 0) {
                        set[2][0] = aP[0];
                        set[2][1] = aP[1];
                    } else {
                        set[2][0] = (set[2][0] + aP[0]) / 2;
                        set[2][1] = (set[2][1] + aP[1]) / 2;
                    }
                } else if (set[3][0] == 0 && set[3][1] == 0 && Math.abs(aP[0] - set[2][0]) > th1 && set[2][0] != 0 || Math.abs(aP[0] - set[3][0]) < th && abs(aP[1] - set[3][1]) < 1.5) {
                    if (set[3][0] == 0 && set[3][1] == 0) {
                        set[3][0] = aP[0];
                        set[3][1] = aP[1];
                    } else {
                        set[3][0] = (set[3][0] + aP[0]) / 2;
                        set[3][1] = (set[3][1] + aP[1]) / 2;
                    }
                }
            }
        }
        sort(set, new int[]{1, 0});
        for (int i = 0; i < 4; i++) {
            if (set[i][0] == 0 && set[i][1] == 0) {
                V[3] = 2;
                return V;
            }
        }
        for (int i = 0; i < set.length; i++) {
            a1[i] = Math.cos(set[i][1] * Math.PI / 180);
        }
        for (int i = 0; i < set.length; i++) {
            b1[i] = Math.sin(set[i][1] * Math.PI / 180);
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 2; j < 4; j++) {
                cross[q][0] = (b1[j] * set[i][0] - b1[i] * set[j][0]) / (b1[j] * a1[i] - b1[i] * a1[j]);
                cross[q][1] = (a1[j] * set[i][0] - a1[i] * set[j][0]) / (b1[i] * a1[j] - a1[i] * b1[j]);
                q = q + 1;
            }
        }
        sort(cross, new int[]{1, 1});
        Point cross1;
        Point cross2;
        if (cross[0][0] > cross[1][0]) {
            cross1 = new Point(cross[1][0], cross[1][1]);
        } else {
            cross1 = new Point(cross[0][0], cross[0][1]);
        }
        if (cross[2][0] > cross[3][0]) {
            cross2 = new Point(cross[2][0], cross[2][1]);
        } else {
            cross2 = new Point(cross[3][0], cross[3][1]);
        }
        Imgproc.line(photo2, cross1, cross2, new Scalar(255, 0, 0), 2);
        Bitmap newMap1 = Bitmap.createBitmap(photo2.width(), photo2.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(photo2, newMap1);
        for (int i = 0; i < 4; i += 1) {
            findLines(newMap1, set[i][0], a1[i], b1[i], xx, yy);
            P[i][0] = xx[0][0];
            P1[i][0] = xx[1][0];
            P[i][1] = yy[0][0];
            P1[i][1] = yy[1][0];
        }
        for (int i = 0; i < 4; i += 1) {
            Point p1 = new Point(P[i][0], P[i][1]);
            Point p2 = new Point(P1[i][0], P1[i][1]);
            Imgproc.line(photo2, p1, p2, new Scalar(255, 0, 0), 2);
        }
        int b = photo2.width();
        int c = photo2.height();
//        Imgproc.line(photo2,new Point(0,b/2+50),new Point(c,b/2+50),new Scalar(255,0,0),2);
//        Imgproc.line(photo2,new Point(c/2,0),new Point(c/2,b),new Scalar(255,0,0),2);
        Utils.matToBitmap(photo2, newMap1);
        Point p1 = new Point(P[0][0], P[0][1]);
        Point p2 = new Point(P[1][0], P[1][1]);
        Point p3 = new Point(P[2][0], P[2][1]);
        Point p4 = new Point(P[3][0], P[3][1]);
        Point p5 = new Point(P1[0][0], P1[0][1]);
        Point p6 = new Point(P1[1][0], P1[1][1]);
        Point p7 = new Point(P1[2][0], P1[2][1]);
        Point p8 = new Point(P1[3][0], P1[3][1]);
        double[] X1 = new double[2];
        double[] X2 = new double[2];
        double[] Y1 = new double[2];
        double[] Y2 = new double[2];
        if (isInlines(p1, p2, p3, p7) || isInlines(p1, p2, p4, p8) || isInlines(p3, p4, p1, p5) || isInlines(p3, p4, p2, p6) || isInlines(p5, p6, p3, p7) || isInlines(p5, p6, p4, p8) || isInlines(p7, p8, p2, p6) || isInlines(p7, p8, p1, p5)) {
            V[3] = 1;
        } else {
            X1[0] = P[0][0];
            X1[1] = P[0][1];
            X2[0] = P[2][0];
            X2[1] = P[2][1];
            Y1[0] = P[1][0];
            Y1[1] = P[1][1];
            Y2[0] = P[3][0];
            Y2[1] = P[3][1];
            node(X1, X2, Y1, Y2, p1);
            X1[0] = P[0][0];
            X1[1] = P[0][1];
            X2[0] = P1[2][0];
            X2[1] = P1[2][1];
            Y1[0] = P[1][0];
            Y1[1] = P[1][1];
            Y2[0] = P1[3][0];
            Y2[1] = P1[3][1];
            node(X1, X2, Y1, Y2, p2);
            X1[0] = P[2][0];
            X1[1] = P[2][1];
            X2[0] = P1[0][0];
            X2[1] = P1[0][1];
            Y1[0] = P[3][0];
            Y1[1] = P[3][1];
            Y2[0] = P1[1][0];
            Y2[1] = P1[1][1];
            node(X1, X2, Y1, Y2, p3);
            X1[0] = P1[0][0];
            X1[1] = P1[0][1];
            X2[0] = P1[2][0];
            X2[1] = P1[2][1];
            Y1[0] = P1[1][0];
            Y1[1] = P1[1][1];
            Y2[0] = P1[3][0];
            Y2[1] = P1[3][1];
            node(X1, X2, Y1, Y2, p4);
            Imgproc.line(photo2, p1, p2, new Scalar(255, 0, 0), 3);
            Imgproc.line(photo2, p2, p4, new Scalar(255, 0, 0), 3);
            Imgproc.line(photo2, p1, p3, new Scalar(255, 0, 0), 3);
            Imgproc.line(photo2, p3, p4, new Scalar(255, 0, 0), 3);
            Utils.matToBitmap(photo2, newMap1);
            double[] points1 = new double[]{p1.x, p1.y};
            double[] points2 = new double[]{p2.x, p2.y};
            double[] points3 = new double[]{p3.x, p3.y};
            double[] points4 = new double[]{p4.x, p4.y};
            if (min(p1.x, p2.x, p3.x, p4.x) < -100 || max(p1.x, p2.x, p3.x, p4.x) > b + 100) {
                V[3] = 3;
                return V;
            } else if (min(p1.y, p2.y, p3.y, p4.y) < -100 || max(p1.y, p2.y, p3.y, p4.y) > c + 100) {
                V[3] = 4;
                return V;
            }
            int[] v1 = new int[]{(int) (p2.x - p1.x), (int) (p2.y - p1.y)};
            int[] v2 = new int[]{(int) (p4.x - p2.x), (int) (p4.y - p2.y)};
            int[] v3 = new int[]{(int) (p3.x - p4.x), (int) (p3.y - p4.y)};
            int[] v4 = new int[]{(int) (p1.x - p3.x), (int) (p1.y - p3.y)};
            double cos1 = dot(v1, v2) / (norm(v1) * norm(v2));
            double cos2 = dot(v2, v3) / (norm(v2) * norm(v3));
            double cos3 = dot(v3, v4) / (norm(v3) * norm(v4));
            double cos4 = dot(v4, v1) / (norm(v4) * norm(v1));
            double[] var = new double[]{cos1, cos2, cos3, cos4};
            double cos_var = Variance(var);
            if (cos_var > 0.08) {
                V[3] = 1;
                return V;
            }
            double d1 = Distance(points1, points2);
            double d2 = Distance(points1, points3);
            double d3 = Distance(points2, points4);
            double d4 = Distance(points3, points4);
            double[] Lwdistance = new double[]{d1, d2, d3, d4};
            double dis = Math.abs(cross1.x - cross2.x);
            if (downloaddata.size() == 0) {
                V[0] = (double) round((1 * (Lwdistance[0] + Lwdistance[3]) / 2) * 10) / 10;
                V[1] = (double) round((1 * (Lwdistance[1] + Lwdistance[2]) / 2) * 10) / 10;
                V[2] = (double) round(dis * 10) / 10;
                V[3] = 0;
//                V[4] = 1;
            } else {
                V[2] = (double) round((Double.parseDouble(downloaddata.get(8)) * Math.pow(dis, 2) + Double.parseDouble(downloaddata.get(7)) * dis + Double.parseDouble(downloaddata.get(6))) * 10) / 10;
                double k1 = Double.parseDouble(downloaddata.get(2)) * Math.pow(dis, 2) + Double.parseDouble(downloaddata.get(1)) * dis + Double.parseDouble(downloaddata.get(0));
                double k2 = Double.parseDouble(downloaddata.get(5)) * Math.pow(dis, 2) + Double.parseDouble(downloaddata.get(4)) * dis + Double.parseDouble(downloaddata.get(3));
                V[0] = (double) round((k1 * (Lwdistance[0] + Lwdistance[3]) / 2) * 10) / 10;
                V[1] = (double) round((k2 * (Lwdistance[1] + Lwdistance[2]) / 2) * 10) / 10;
                V[3] = 0;
            }
        }
        return V;
    }

    private static boolean isInlines(Point a, Point b, Point c, Point d) {
        boolean flag = false;
        double k0;
        double b0;
        double y, y1;
        k0 = (a.y - b.y) / (a.x - b.x);
        b0 = (a.y - k0 * a.x);
        if (k0 != 0) {
            y = k0 * c.x + b0;
            y1 = k0 * d.x + b0;
            if (Math.abs(y - c.y) < 2 || Math.abs(y1 - d.y) < 2) {
                flag = true;
            }
        } else {
            if (Math.abs(a.x - c.x) < 2 || Math.abs(a.x - d.x) < 2) {
                flag = true;
            }
        }
        return flag;
    }

    private static double GetPixel(Bitmap photo, int m, int n) {
        int col = photo.getPixel(m, n);
        if (col <= -15777216) {
            col = 0;
        } else {
            col = 1;
        }
        return col;
    }

    public static void findLines(Bitmap L, double p, double a, double b, double[][] xx, double[][] yy) {
        int w = L.getWidth();
        int h = L.getHeight();
        int m;
        int n;
        int th = 30;//点的个数
        int can = 15;//范围
        if (b > 0.70711) {
            for (int x = 20; x <= w - 20; x += 3) {
                int y = (int) Math.round((p - a * x) / b);
                if (y >= h - 25 || y <= 25) {
                    continue;
                }
                double num = 0;
                for (m = x - 3; m <= x + 3; m++) {
                    for (n = y - can; n <= y + can; n++) {
                        num = num + GetPixel(L, m, n);
                    }
                }
                if (num > th) {
                    xx[0][0] = x;
                    yy[0][0] = y;
                    break;
                }
            }
            for (int x = w - 20; x >= 20; x -= 3) {
                int y = (int) Math.round((p - a * x) / b);
                if (y >= h - 25 || y <= 25) {
                    continue;
                }
                double num = 0;
                for (m = x - 3; m <= x + 3; m++) {
                    for (n = y - can; n <= y + can; n++) {
                        num += GetPixel(L, m, n);
                    }
                }
                if (num > th) {
                    xx[1][0] = x;
                    yy[1][0] = y;
                    break;
                }
            }
        } else {
            for (int y = 20; y <= h - 20; y += 3) {
                int x = (int) Math.round((p - b * y) / a);
                if (x >= w - 25 || x <= 25) {
                    continue;
                }
                double num = 0;
                for (m = y - 3; m <= y + 3; m++) {
                    for (n = x - can; n <= x + can; n++) {
                        num += GetPixel(L, n, m);
                    }
                }
                if (num > th) {
                    xx[0][0] = x;
                    yy[0][0] = y;
                    break;
                }
            }
            for (int y = h - 20; y >= 20; y -= 3) {
                int x = (int) Math.round((p - b * y) / a);
                if (x >= w - 25 || x <= 25) {
                    continue;
                }
                double num = 0;
                for (m = y - 3; m <= y + 3; m++) {
                    for (n = x - can; n <= x + can; n++) {
                        num += GetPixel(L, n, m);
                    }
                }
                if (num > th) {
                    xx[1][0] = x;
                    yy[1][0] = y;
                    break;
                }
            }
        }
    }

    private static void sort(double[][] ob, final int[] order) {
        Arrays.sort(ob, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                double[] one = (double[]) o1;
                double[] two = (double[]) o2;
                for (int i = 0; i < order.length; i++) {
                    int k = order[i];
                    if (one[k] > two[k]) {
                        return 1;
                    } else if (one[k] < two[k]) {
                        return -1;
                    } else {
                        continue;
                    }
                }
                return 0;
            }
        });
    }

    private static double Distance(double[] points1, double[] points2) {
        return sqrt((points1[0] - points2[0]) * (points1[0] - points2[0]) + (points1[1] - points2[1]) * (points1[1] - points2[1]));
    }

    private static Point node(double[] X1, double[] X2, double[] Y1, double[] Y2, Point p) {
        double K2;
        double b2;
        double K1;
        double b1;
        if (X1[0] == Y1[0]) {
            p.x = X1[0];
            K2 = (Y2[1] - X2[1]) / (Y2[0] - X2[0]);
            b2 = X2[1] - K2 * X2[0];
            p.y = (K2 * p.x + b2);
        } else if (X2[0] == Y2[0]) {
            p.x = X2[0];
            K1 = (Y1[1] - X1[1]) / (Y1[0] - X1[0]);
            b1 = X1[1] - K1 * X1[0];
            p.y = (K1 * p.x + b1);
        } else if (X1[0] != Y1[0] && X2[0] != Y2[0]) {
            K1 = (Y1[1] - X1[1]) / (Y1[0] - X1[0]);
            K2 = (Y2[1] - X2[1]) / (Y2[0] - X2[0]);
            b1 = X1[1] - K1 * X1[0];
            b2 = X2[1] - K2 * X2[0];
            if (K1 == K2) {
                p.x = Integer.parseInt(null);
                p.y = Integer.parseInt(null);
            } else {
                p.x = ((b2 - b1) / (K1 - K2));
                p.y = (K1 * p.x + b1);
            }
        }
        return p;
    }

    private static double Variance(double[] var) {
        int m = var.length;
        double sum = 0;
        for (double aVar : var) {
            sum += aVar;
        }
        double dAve = sum / m;
        double dVar = 0;
        for (double aVar : var) {
            dVar += (aVar - dAve) * (aVar - dAve);
        }
        return dVar / m;
    }

    private static double norm(int[] v1) {
        return Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1]);
    }

    private static double dot(int[] v1, int[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1];
    }

    static int max(double x1, double x2, double x3, double x4) {
        int max;
        max = (int) ((x1 > x2) ? x1 : x2);
        max = (max > x3) ? max : (int) x3;
        max = (max > x4) ? max : (int) x4;
        return max;
    }

    static int min(double x1, double x2, double x3, double x4) {
        int min;
        min = (int) ((x1 < x2) ? x1 : x2);
        min = (min < x3) ? min : (int) x3;
        min = (min < x4) ? min : (int) x4;
        return min;
    }

}
