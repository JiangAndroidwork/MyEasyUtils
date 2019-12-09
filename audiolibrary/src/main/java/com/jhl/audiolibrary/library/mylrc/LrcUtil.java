package com.jhl.audiolibrary.library.mylrc;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类介绍（必填）：歌词工具 用来读取内容
 * Created by Jiang on 2018/7/31 .
 */

public class LrcUtil {
    private static boolean blLrc;

    /**
     * @param file 传入的是歌词本地文件路径
     * @return
     */
    public static List<LrcBean> read(String file) {
        List<LrcBean> list = new ArrayList<>();
        TreeMap<Integer, LyricObject> lrc_read = new TreeMap<Integer, LyricObject>();
        String data = "";
        try {
            File saveFile = new File(file);
            if (!saveFile.isFile()) {
                blLrc = false;
                return list;
            }
            blLrc = true;

            // System.out.println("bllrc==="+blLrc);
            FileInputStream stream = new FileInputStream(saveFile);// context.openFileInput(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    stream));
            int i = 0;
            Pattern pattern = Pattern.compile("^\\d{2}$");
            while ((data = br.readLine()) != null) {
                // System.out.println("++++++++++++>>"+data);
                data = data.replace("[", "");// 将前面的替换成后面的
                data = data.replace("]", "@");

                String splitdata[] = data.split("@");// 分隔
                if (data.endsWith("@")) {

                    for (int k = 0; k < splitdata.length; k++) {
                        String str = splitdata[k];
                        str = str.replaceAll("\\:", ".");
                        str = str.replaceAll("\\.", "@");

                        String timedata[] = str.split("@");

                        Matcher matcher = pattern.matcher(timedata[0]);
                        if (timedata.length == 3 && matcher.matches()) {
                            int m = Integer.parseInt(timedata[0]); //
                            int s = Integer.parseInt(timedata[1]); //
                            int ms = Integer.parseInt(timedata[2]); //
                            int currTime = (m * 60 + s) * 1000 + ms * 10;
                            LyricObject item1 = new LyricObject();
                            item1.begintime = currTime;
                            item1.lrc = "";
                            lrc_read.put(currTime, item1);
                        }
                    }

                } else {

                    String lrcContenet = splitdata[splitdata.length - 1];

                    for (int j = 0; j < splitdata.length - 1; j++) {
                        String tmpstr = splitdata[j];

                        tmpstr = tmpstr.replace(":", ".");
                        tmpstr = tmpstr.replaceAll("\\.", "@");
                        String timedata[] = tmpstr.split("@");

                        Matcher matcher = pattern.matcher(timedata[0]);


                        if (timedata.length == 3 && matcher.matches()) {
                            int m = Integer.parseInt(timedata[0]); //
                            int s = Integer.parseInt(timedata[1]); //
                            int ms = Integer.parseInt(timedata[2]); //
                            int currTime = (m * 60 + s) * 1000 + ms * 10;
                            LyricObject item1 = new LyricObject();
                            item1.begintime = currTime;
                            item1.lrc = lrcContenet;
                            lrc_read.put(currTime, item1);//
                            i++;
                        }
                    }
                }

            }
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

		/*
         */
        Iterator<Integer> iterator = lrc_read.keySet().iterator();
        LyricObject oldval = null;
        int i = 0;
        while (iterator.hasNext()) {
            Object ob = iterator.next();

            LyricObject val = (LyricObject) lrc_read.get(ob);

            if (oldval == null) {
                oldval = val;
            } else {
                LyricObject item1 = new LyricObject();
                item1 = oldval;
                item1.timeline = val.begintime - oldval.begintime;
                list.add(new LrcBean(item1.lrc, item1.begintime, (item1.begintime + item1.timeline)));
                i++;
                oldval = val;
            }

            if (!iterator.hasNext()) {
                list.add(new LrcBean(val.lrc, val.begintime, val.begintime + val.timeline));
            }
        }
        return list;
    }

    /**
     * @param file 传入的歌词 字符串
     * @return
     */
    public static List<LrcBean> readString(String file) {
        List<LrcBean> list = new ArrayList<>();
        if (TextUtils.isEmpty(file)) return list;
        String[] split = file.split("\n");
        Log.i("整理后的歌词==", split.toString());

        TreeMap<Integer, LyricObject> lrc_read = new TreeMap<Integer, LyricObject>();
        String data = "";

        blLrc = true;
        Pattern pattern = Pattern.compile("^\\d{2}$");
        int i = 0;
        for (int l = 0; l < split.length; l++) {
            data = split[l];

            // System.out.println("++++++++++++>>"+data);
            data = data.replace("[", "");// 将前面的替换成后面的
            data = data.replace("]", "@");

            String splitdata[] = data.split("@");// 分隔
            if (data.endsWith("@")) {

                System.out.println("data.endsWith(\"@\")");
                for (int k = 0; k < splitdata.length; k++) {
                    String str = splitdata[k];
                    str = str.replaceAll("\\:", ".");
                    str = str.replaceAll("\\.", "@");

                    String timedata[] = str.split("@");

                    Matcher matcher = pattern.matcher(timedata[0]);
                    if (timedata.length == 3 && matcher.matches()) {
                        int m = Integer.parseInt(timedata[0]); //
                        int s = Integer.parseInt(timedata[1]); //
                        int ms = Integer.parseInt(timedata[2]); //
                        int currTime = (m * 60 + s) * 1000 + ms * 10;
                        LyricObject item1 = new LyricObject();
                        item1.begintime = currTime;
                        item1.lrc = "";
                        lrc_read.put(currTime, item1);
                    }
                }

            } else {

                String lrcContenet = splitdata[splitdata.length - 1];


                for (int j = 0; j < splitdata.length - 1; j++) {
                    String tmpstr = splitdata[j];

                    tmpstr = tmpstr.replace(":", ".");
                    tmpstr = tmpstr.replaceAll("\\.", "@");
                    String timedata[] = tmpstr.split("@");

                    Matcher matcher = pattern.matcher(timedata[0]);


                    if (timedata.length == 3 && matcher.matches()) {
                        int m = Integer.parseInt(timedata[0]); //
                        int s = Integer.parseInt(timedata[1]); //
                        int ms = Integer.parseInt(timedata[2]); //
                        int currTime = (m * 60 + s) * 1000 + ms * 10;
                        LyricObject item1 = new LyricObject();
                        item1.begintime = currTime;
                        item1.lrc = lrcContenet;
                        lrc_read.put(currTime, item1);//
                        i++;
                    }
                }
            }
        }



		/*
         */
        Iterator<Integer> iterator = lrc_read.keySet().iterator();
        LyricObject oldval = null;
        int h = 0;
        while (iterator.hasNext()) {
            Object ob = iterator.next();

            LyricObject val = (LyricObject) lrc_read.get(ob);

            if (oldval == null) {
                oldval = val;
            } else {
                LyricObject item1 = new LyricObject();
                item1 = oldval;
                item1.timeline = val.begintime - oldval.begintime;
                list.add(new LrcBean(item1.lrc, item1.begintime, (item1.begintime + item1.timeline)));
                h++;
                oldval = val;
            }

            if (!iterator.hasNext()) {
                list.add(new LrcBean(val.lrc, val.begintime, val.begintime + val.timeline));
            }
        }
        return list;
    }

    /**
     * 传入的参数为标准歌词字符串
     *
     * @return
     */
    public static List<LrcBean> parseStr2List(String file) {
        List<LrcBean> list = new ArrayList<>();
        File saveFile = new File(file);
        if (!saveFile.isFile()) {
            blLrc = false;
            return list;
        }
        blLrc = true;
        try {
            // System.out.println("bllrc==="+blLrc);
            FileInputStream stream = new FileInputStream(saveFile);// context.openFileInput(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                buffer.append(s.trim());
            }

            String lrcStr = buffer.toString();

            String lrcText = lrcStr.replaceAll("&#58;", ":")
                    .replaceAll("&#10;", "\n")
                    .replaceAll("&#46;", ".")
                    .replaceAll("&#32;", " ")
                    .replaceAll("&#45;", "-")
                    .replaceAll("&#13;", "\r").replaceAll("&#39;", "'");
            String[] split = lrcText.split("\n");
            for (int i = 0; i < split.length; i++) {
                String lrc = split[i];
                Log.i("获取歌词内容==", lrc + "====");
                if (lrc.contains(".")) {
                    String min = lrc.substring(lrc.indexOf("[") + 1, lrc.indexOf("[") + 3);
                    String seconds = lrc.substring(lrc.indexOf(":") + 1, lrc.indexOf(":") + 3);
                    String mills = lrc.substring(lrc.indexOf(".") + 1, lrc.indexOf(".") + 3);
                    long startTime = Long.valueOf(min) * 60 * 1000 + Long.valueOf(seconds) * 1000 + Long.valueOf(mills) * 10;
                    String text = lrc.substring(lrc.indexOf("]") + 1);
                    if (text == null || "".equals(text)) {
                        text = "music";
                    }
                    LrcBean lrcBean = new LrcBean();
                    lrcBean.setStart(startTime);
                    lrcBean.setLrc(text);
                    list.add(lrcBean);
                    if (list.size() > 1) {
                        list.get(list.size() - 2).setEnd(startTime);
                    }
                    if (i == split.length - 1) {
                        list.get(list.size() - 1).setEnd(startTime + 100000);
                    }
                }
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
