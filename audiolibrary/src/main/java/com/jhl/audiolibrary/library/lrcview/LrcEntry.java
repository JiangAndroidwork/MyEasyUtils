package com.jhl.audiolibrary.library.lrcview;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hzwangchenyan on 2016/10/19.
 */
class LrcEntry implements Comparable<LrcEntry> {
    private long time;
    public long timeline;
    private long startTime;
    private long endTime;
    private String text;
    private StaticLayout staticLayout;
    private float offset = Float.MIN_VALUE;

    private LrcEntry(long time, String text) {
        this.time = time;
        this.text = text;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getText() {
        return text;
    }

    public LrcEntry setText(String text) {
        this.text = text;
        return this;
    }

    void init(TextPaint paint, int width) {
        staticLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
    }

    long getTime() {
        return time;
    }

    StaticLayout getStaticLayout() {
        return staticLayout;
    }


    int getHeight() {
        if (staticLayout == null) {
            return 0;
        }
        return staticLayout.getHeight();
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    @Override
    public int compareTo(LrcEntry entry) {
        if (entry == null) {
            return -1;
        }
        return (int) (time - entry.getTime());
    }

    static List<LrcEntry> parseLrc(File lrcFile) {
        if (lrcFile == null || !lrcFile.exists()) {
            return null;
        }

        List<LrcEntry> entryList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                List<LrcEntry> list = parseLine(line);
                if (list != null && !list.isEmpty()) {
                    entryList.addAll(list);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(entryList);
        return entryList;
    }

    static List<LrcEntry> parseLrc(String lrcText) {
        if (TextUtils.isEmpty(lrcText)) {
            return null;
        }

        List<LrcEntry> entryList = new ArrayList<>();
        String[] array = lrcText.split("\\n");
        for (String line : array) {
            List<LrcEntry> list = parseLine(line);
            if (list != null && !list.isEmpty()) {
                entryList.addAll(list);
            }
        }

        Collections.sort(entryList);
        return entryList;
    }

    private static List<LrcEntry> parseLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }

        line = line.trim();
        Matcher lineMatcher = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(.+)").matcher(line);
        if (!lineMatcher.matches()) {
            return null;
        }

        String times = lineMatcher.group(1);
        String text = lineMatcher.group(3);
        List<LrcEntry> entryList = new ArrayList<>();

        Matcher timeMatcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d\\d)\\]").matcher(times);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
            long time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10;
            LrcEntry lrcEntry = new LrcEntry(time, text);
            lrcEntry.setStartTime(time);
            entryList.add(lrcEntry);
        }

        Log.i("获取到的歌词集合==",entryList.toString());
        return entryList;
    }

    @Override
    public String toString() {
        return "LrcEntry{" +
                "time=" + time +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", text='" + text + '\'' +
                ", staticLayout=" + staticLayout +
                ", offset=" + offset +
                '}';
    }
}
