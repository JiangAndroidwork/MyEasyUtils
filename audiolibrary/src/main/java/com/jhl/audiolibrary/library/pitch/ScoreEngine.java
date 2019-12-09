package com.jhl.audiolibrary.library.pitch;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jhl.audiolibrary.library.mylrc.LrcBean;
import com.jhl.audiolibrary.tools.recorder.RecorderEngine;
import com.jhl.audiolibrary.tools.recorder.thread.AudioRecorder;

import java.util.Iterator;
import java.util.List;

/**
 * 类介绍：评分
 * Created by Jiang in 2018/8/7
 */
public class ScoreEngine {
    public static final int MIN_VOLUME = 30;//最低音量，低于这个音量不要计分
    private long score;//分数
    private static ScoreEngine instance;
    private List<PitchBean> listPitch;//音高
    private List<LrcBean> listLrc;//歌词
    private int minVolume;

    private ScoreEngine() {

    }

    public static ScoreEngine getInstance() {
        if (instance == null) {
            synchronized (ScoreEngine.class) {
                if (instance == null) {
                    instance = new ScoreEngine();
                }
            }
        }
        return instance;
    }

    /**
     * 设置音高列表
     *
     * @param listPitch
     */
    public void setPitch(List<PitchBean> listPitch) {
        this.listPitch = listPitch;

    }

    /**
     * 设置歌词列表
     *
     * @param listLrc
     */
    public void setLrc(List<LrcBean> listLrc) {
        if (listPitch == null) return;
        this.listLrc = listLrc;
        int i = 0;
        for (LrcBean lrcBean : listLrc) {
            double start = lrcBean.getStart();//每行的开始时间
            double end = lrcBean.getEnd();//每行的结束时间
            String lrc = lrcBean.getLrc();//歌词
            int j = 0;
            Iterator<PitchBean> pitchIterator = listPitch.iterator();
            while (pitchIterator.hasNext()) {
                PitchBean pitchBean = pitchIterator.next();
                double startTime = pitchBean.getStartTime();
                double endTime = pitchBean.getEndTime();
                if (start / 1000 <= startTime && end / 1000 >= endTime) {//歌词的字 在 第i行内
                    pitchBean.setLineNum(i + 1);
                    pitchBean.setWordsOneLine(lrcBean.getLrc().trim().length());
                }
                j++;
            }
            i++;
        }

    }

    /**
     * @param currentTime 当前时间
     * @param recordMidi  录制的音高
     * @param volume      音量的大小
     */
    public void manageScore(long currentTime, int recordMidi, int volume) {
        if (listPitch == null) return;
        long l = currentTime / 1000;
        Log.i("最初的录音音高是多少==", recordMidi + "==" + volume);
        Iterator<PitchBean> pitchIterator = listPitch.iterator();
        while (pitchIterator.hasNext()) {
            PitchBean pitchBean = pitchIterator.next();
            double startTime = pitchBean.getStartTime();
            double endTime = pitchBean.getEndTime();

            if ((int) startTime <= l && l <= (int) endTime) {

                if (recordMidi < 0 || volume < MIN_VOLUME) {
                    recordMidi = 0;
                }
                pitchBean.setRecordMidi((int) recordMidi);
            }
        }
//        for (PitchBean pitchBean : listPitch) {
//            double startTime = pitchBean.getStartTime();
//            double endTime = pitchBean.getEndTime();
//            if (startTime <= currentTime && endTime >= currentTime) {
//                if (recordMidi <= 0) {
//                    recordMidi = 0;
//                }
//                pitchBean.setRecordMidi((int) recordMidi);
//            }
//        }

    }

    /**
     * 结束评分
     * <p>
     * 每行歌词 是100分 对应每个字多少分
     * 然后每个字的音高取相应的百分比 分数相加
     * 总分=行数*100
     * 一个字的得分 = 每个字的音高/标准音高*(1/一行的字数*100)
     *
     * @return 分数
     */
    public long finishScore() {
        if (listPitch == null) return 0;
        float score = 0;
        int scoreWord = 0;
        Log.i("获取音高列表==", listPitch.toString());
        for (PitchBean bean : listPitch) {
            int wordsOneLine = bean.getWordsOneLine();//一行多少个字
            int lineNum = bean.getLineNum();//第几行
            float normMidi = bean.getNormMidi();//标准音高
            int recordMidi = bean.getRecordMidi();//录制的音高
            if (recordMidi < 0) {
                recordMidi = 0;
            }
            if (wordsOneLine > 0) {
                scoreWord = 100 / wordsOneLine;//每个字的得分
            }
            Log.i("每个字的总分==", scoreWord + "==" + normMidi + "===" + recordMidi);
            if (recordMidi < normMidi) {
                if (normMidi > 0) {
                    float i = recordMidi * scoreWord / normMidi;
                    if (i >= 0) {
                        score = (score + i);
                    }
                }
                Log.i("分数1==", score + "");
            } else if (recordMidi > normMidi) {
                if (recordMidi > 0) {
                    float i = normMidi * scoreWord / recordMidi;
                    if (i >= 0) {
                        score = (score + i);
                    }
//                    if (((recordMidi % normMidi) * scoreWord / normMidi) >= 0)
//                        score = (long) (score + ((recordMidi % normMidi) * scoreWord / normMidi));
                }
                Log.i("分数2==", score + "");
            } else if (recordMidi == normMidi) {
                score = (long) (score + scoreWord);
                Log.i("分数3==", score + "");
            }

        }
        return (long) score;
    }

    //算获取翻唱分数
    public int getCorverScore(List<PitchBean> listPitch, List<LrcBean> listLrc) {
        int score = 0;
        return score;
    }

    public void onDestory() {
        if (listLrc != null) {
            listLrc.clear();
            listLrc = null;
        }
        if (listPitch != null) {
            listPitch.clear();
            listLrc = null;
        }
    }

}
