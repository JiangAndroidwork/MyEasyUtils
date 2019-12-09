package com.jhl.audiolibrary.library.mylrc;

/**
 * 类介绍（必填）：歌词基类
 * Created by Jiang on 2018/7/31 .
 */

public class LrcBean {
    private String lrc;
    private double start;
    private double end;
    private float offset = Float.MIN_VALUE;
    private double time;
    public double getTime(){
        return end-start;
    }
    public LrcBean() {
    }

    public LrcBean(String text, long start, long end) {
        this.lrc = text;
        this.start = start;
        this.end = end;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "LrcBean{" +
                "lrc='" + lrc + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}
