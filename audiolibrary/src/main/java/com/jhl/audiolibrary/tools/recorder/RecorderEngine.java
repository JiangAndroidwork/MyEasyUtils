package com.jhl.audiolibrary.tools.recorder;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jhl.audiolibrary.Constant;
import com.jhl.audiolibrary.tools.interfaces.VoiceRecorderOperateInterface;
import com.jhl.audiolibrary.tools.recorder.thread.AudioRecorder;
import com.jhl.audiolibrary.utils.FileFunction;

import java.util.LinkedList;

/**
 * 类介绍（必填）：录音
 * Created by Jiang on 2018/7/22 .
 */

public class RecorderEngine {
    private final AudioRecorder recorder;
    private boolean recording;

    private final int sampleDuration = 500;// 间隔取样时间

    private long recordStartTime;
    private long recordDuration;
    private long currentPauseLog;//记录暂停时候的时间
    private long currentRestartLog;//记录开始时候的时间

    private String recordFileUrl;

    private VoiceRecorderOperateInterface voiceRecorderInterface;

    private AudioManager audioManager;
    private Handler handler;

    //    private static NativeRecorder recorder;

    private static RecorderEngine instance;
    private String fileName;
    private boolean isPause;
    private long preTotalTim;
    private boolean isNoPlay = false;//先不要播
    private RecorderEngine() {

        handler = new Handler(Looper.getMainLooper());

        //        recorder = new NativeRecorder();

        recorder = new AudioRecorder();
    }

    public static RecorderEngine getInstance() {
        if (instance == null) {
            synchronized (RecorderEngine.class) {
                if (instance == null) {
                    instance = new RecorderEngine();
                }
            }
        }
        return instance;
    }


    public boolean IsRecording() {
        return recording;
    }

    public void readyRecord(String fileName) {
        recordDuration = 0;
        preTotalTim = 0;
        currentPauseLog = 0;
        currentRestartLog = 0;
        recordStartTime = System.currentTimeMillis();
        recorder.clearFilesName();
        recorder.readyState();
        recorder.initAudioRecord(fileName);


    }

    private boolean startRecordVoice() {

        return recorder.startRecordVoice();
    }

    public void startRecordVoice(
            VoiceRecorderOperateInterface voiceRecorderOperateInterface) {
//        stopRecordVoice();
        currentRestartLog = System.currentTimeMillis();//记录点击开始的时间
        if (currentPauseLog != 0) {
            preTotalTim += currentRestartLog - currentPauseLog;
        }
        recording = startRecordVoice();
        isPause = false;
        if (recording) {
            this.voiceRecorderInterface = voiceRecorderOperateInterface;

            updateMicStatus();
            if (voiceRecorderOperateInterface != null) {
                voiceRecorderOperateInterface.recordVoiceBegin();
            }
        } else {

            if (voiceRecorderOperateInterface != null) {
                voiceRecorderOperateInterface.recordVoiceFail();
            }
        }
    }


    //暂停录音
    public void pauseRecordVoice() {
        isPause = true;
        currentPauseLog = System.currentTimeMillis();
        recorder.pauseRecord();
        if (voiceRecorderInterface != null) {
            voiceRecorderInterface.recordPause();
        }
    }

    public void onCancelVoice() {
        if (recorder != null)
            recorder.canel();
    }

    //获取是否暂停的状态
    public boolean getPauseState() {
        return isPause;
    }

    //是否正在录制
    public boolean isRecording() {
        return recording;
    }

    //获取耳返
    public AudioRecorder.EarReturnPlayer getEarReturnPlayer() {
        return recorder.getEarReturnPlayer();
    }

    public void stopRecordVoice() {
        if (recording) {
            boolean recordVoiceSuccess = recorder.stopRecordVoice();
            long recordDuration = System.currentTimeMillis() - recordStartTime;
            recording = false;

            if (recordDuration < Constant.OneSecond) {
                recordVoiceSuccess = false;
            }

            if (!recordVoiceSuccess) {
                Log.i("录音太短", "不行滴");
                if (voiceRecorderInterface != null) {
                    voiceRecorderInterface.recordVoiceFail();
                }

                FileFunction.DeleteFile(recordFileUrl);
                return;
            }

            if (voiceRecorderInterface != null) {
                voiceRecorderInterface.recordVoiceFinish();
            }
        } else {
            recorder.canel();
        }
    }

    //删除多余的文件
    public void deletePcm() {
        if (recorder != null) {
            recorder.clearPcmFiles();
        }
    }

    //获取录音数据==
    public LinkedList<byte[]> getSaveDataList() {
        return recorder.getSaveDataList();
    }

    public void onDestoryRecorder() {
        recorder.onDestory();
    }


    public void recordVoiceStateChanged(int volume, int midiNum) {
        if (voiceRecorderInterface != null) {
            voiceRecorderInterface.recordVoiceStateChanged(volume, midiNum, recordDuration);
        }
    }
    public void setEarFan(boolean isEarReturn){
        if (recorder!=null){
            recorder.setOpenEarReturn(isEarReturn);
        }
    }

    private void updateMicStatus() {
        int volume = recorder.getVolume();
        int midiNum = recorder.getMidiNum();
        recordVoiceStateChanged(volume, midiNum);

        handler.postDelayed(updateMicStatusThread, sampleDuration);
    }

    private Runnable updateMicStatusThread = new Runnable() {
        public void run() {
            if (recording && !isPause) {
                // 判断是否超时
                recordDuration = System.currentTimeMillis() - recordStartTime - preTotalTim;

                updateMicStatus();
            }
        }
    };
}
