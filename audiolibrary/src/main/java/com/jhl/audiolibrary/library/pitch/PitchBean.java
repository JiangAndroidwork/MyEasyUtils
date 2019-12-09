package com.jhl.audiolibrary.library.pitch;

import java.util.ArrayList;
import java.util.List;

/**
 * 类介绍（必填）：音高文件基类
 * Created by Jiang on 2018/8/6 .
 */

public class PitchBean {
    private float warp;//曲速
    private String word;//一个字
    private String spell;//拼音
    private double startTime;//起始时间
    private double length;//长度 单位 32音符
    private int normMidi;//标准音高

    private double endTime;//末尾时间
    private int recordMidi;//录制的音高
    private int lineNum;//字在第几行
    private int wordsOneLine;//这个行有多少个字

    public PitchBean() {
        if (listCurrentMidi != null) {
            listCurrentMidi.clear();
        } else {
            listCurrentMidi = new ArrayList<>();
        }
    }

    public int getLineNum() {
        return lineNum;
    }

    public PitchBean setLineNum(int lineNum) {
        this.lineNum = lineNum;
        return this;
    }

    public int getWordsOneLine() {
        return wordsOneLine;
    }

    public PitchBean setWordsOneLine(int wordsOneLine) {
        this.wordsOneLine = wordsOneLine;
        return this;
    }

    @Override
    public String toString() {
        return "PitchBean{" +
                "warp=" + warp +
                ", word='" + word + '\'' +
                ", spell='" + spell + '\'' +
                ", startTime=" + startTime +
                ", length=" + getLength() +
                ", endTime=" + getEndTime() +
                ", lineNum=" + lineNum +
                ", wordsOneLine=" + wordsOneLine +
                ", normMidi=" + normMidi +
                ", recordMidi=" + recordMidi +
                ", listCurrentMidi=" + listCurrentMidi +
                '}';
    }

    public int getRecordMidi() {
        return recordMidi;
    }

    int num = 0;
    List<Integer> listCurrentMidi = new ArrayList<>();

    public PitchBean setRecordMidi(int recordMidi) {
        int midi = 0;
        listCurrentMidi.add(recordMidi);
        for (Integer integer : listCurrentMidi) {
            midi = midi + integer;
        }
        this.recordMidi = midi / listCurrentMidi.size();
        num++;
        return this;
    }

    public double getEndTime() {
        endTime = startTime + getLength();
        return endTime;
    }

    public float getWarp() {
        return warp;
    }

    public PitchBean setWarp(float warp) {
        num = 0;
        this.warp = warp;
        return this;
    }

    public String getWord() {
        return word;
    }

    public PitchBean setWord(String word) {
        this.word = word;
        return this;
    }

    public String getSpell() {
        return spell;
    }

    public PitchBean setSpell(String spell) {
        this.spell = spell;
        return this;
    }

    public double getStartTime() {
        return startTime;
    }

    public PitchBean setStartTime(double startTime) {
        double v = ((startTime / 8) * 60.0 / getWarp());
        this.startTime = v;
        return this;
    }

    public double getLength() {
        double v = (length / 8) * 60.0 / getWarp();
        return v;
    }

    public PitchBean setLength(double length) {
        this.length = length;
        return this;
    }

    public int getNormMidi() {
        return 71 - normMidi;
    }

    public PitchBean setNormMidi(int normMidi) {
        this.normMidi = normMidi;
        return this;
    }
}
