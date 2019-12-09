package com.jhl.audiolibrary.library.pitch;

import android.util.Log;

import com.jhl.audiolibrary.library.mylrc.LyricObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 类介绍（必填）：音高解析文件的util
 * Created by Jiang on 2018/8/6 .
 */

public class PitchUtil {


    public static List<PitchBean> read(String file) {
        float warp = 0;
        List<PitchBean> list = new ArrayList<>();
        TreeMap<Integer, LyricObject> lrc_read = new TreeMap<Integer, LyricObject>();
        String data = "";
        try {
            File saveFile = new File(file);
            if (!saveFile.isFile()) {
                return list;
            }

            // System.out.println("bllrc==="+blLrc);
            FileInputStream stream = new FileInputStream(saveFile);// context.openFileInput(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    stream));
            int i = 0;
            while ((data = br.readLine()) != null) {
                PitchBean bean = new PitchBean();
                if (i == 0) {
                    String[] split = data.trim().split(" ");
                    warp = Float.parseFloat(split[0]);
                } else if (i > 1) {
                    String[] split = data.trim().split(" ");

                    try {
                        if (split.length > 0) {
                            if (split[0].equals(" ")) {
                                bean.setWarp(warp);
                                bean.setWord(split[1])
                                        .setSpell(split[2])
                                        .setStartTime(Double.parseDouble(split[3]))
                                        .setLength(Double.parseDouble((split[4])))
                                        .setNormMidi(Integer.parseInt(split[5]));
                            } else {
                                bean.setWarp(warp);
                                bean.setWord(split[0])
                                        .setSpell(split[1])
                                        .setStartTime(Double.parseDouble(split[2]))
                                        .setLength(Double.parseDouble((split[3])))
                                        .setNormMidi(Integer.parseInt(split[4]));
                            }
                        }
                        Log.i("歌词==", bean.toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    list.add(bean);
                }

                i++;
            }


            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }
}
