package com.kuaishoulibrary.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileUtils {
    public FileUtils() {
    }

    public static boolean saveVolumeFile(ArrayList<String> list) {
        File file = new File("/mnt/sdcard/" + "volume.txt");
        FileWriter fw = null;
        BufferedWriter bw = null;
        Iterator<String> iter = list.iterator();
        try {
            fw = new FileWriter(file, false);
            bw = new BufferedWriter(fw);
            while (iter.hasNext()) {
                bw.write(iter.next());
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;

    }

    public static ArrayList<String> getVolumeFile() {
        ArrayList<String> newList = new ArrayList<>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(new File("/mnt/sdcard/" + "volume.txt"));
            br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                newList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return newList;
        } finally {
            try {
                br.close();
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newList;
    }

}
