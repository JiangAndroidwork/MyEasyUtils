package com.jhl.audiolibrary.tools.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.NoiseSuppressor;

import com.jhl.audiolibrary.Constant;
import com.jhl.audiolibrary.tools.recorder.RecorderEngine;

import java.util.LinkedList;

/**
 * 类介绍（必填）：边播边放 耳返功能
 * Created by Jiang on 2018/7/16 .
 */

public class EarReturnPlayer implements Runnable {
    private byte[] outBytes;
    private int outputBufferSize;
    /**
     * 播放流
     */
    private AudioTrack audioTrack;
    private int sampleRateInHz = Constant.BehaviorSampleRate;
    /**
     * 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
     */
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private boolean flag = true;
    private LinkedList<byte[]> saveDataList;
    private boolean isPause;//是否播放
    private NoiseSuppressor suppressor;

    public EarReturnPlayer() {
        outputBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, outputBufferSize,
                AudioTrack.MODE_STREAM);
        outBytes = new byte[outputBufferSize];
    }

    @Override
    public void run() {
        byte[] bytes_pkg = null;
        audioTrack.setStereoVolume(1, 1);
        audioTrack.play();
//        setNoiseSuppressor(audioTrack.getAudioSessionId());
        while (flag) {
            try {
                saveDataList = RecorderEngine.getInstance().getSaveDataList();
                if (saveDataList != null && saveDataList.size() > 0) {
                    outBytes = saveDataList.getFirst();
                    bytes_pkg = outBytes.clone();
                    audioTrack.write(bytes_pkg, 0, bytes_pkg.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveDataList.clear();
        stopRecoder();
    }

    //暂停
    public void setOnPauseRecord() {

        if (audioTrack != null)
            audioTrack.pause();
        this.isPause = true;
    }

    //重新播放
    public void setOnRestartRecord() {
        this.isPause = false;
        if (audioTrack != null)
            audioTrack.play();
    }

    public void setNoSound() {
        if (audioTrack != null) {
            audioTrack.setStereoVolume(0, 0);
            audioTrack.play();
        }
    }

    public void setHaveSound() {
        if (audioTrack != null) {
            audioTrack.setStereoVolume(1, 1);
            audioTrack.play();
        }
    }

    private void stopRecoder() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    public void setPlayState(boolean isState) {
        flag = isState;

    }

    public void stopErReturn() {
        flag = false;
//        cancelSuppressor();
//        if (audioTrack != null) {
//            audioTrack.stop();
//            audioTrack.release();
//            audioTrack = null;
//        }
    }

    //噪音压制控制器
    private void setNoiseSuppressor(int sessionId) {
        suppressor = NoiseSuppressor.create(sessionId);
        if (NoiseSuppressor.isAvailable() && suppressor != null) {
            suppressor.setEnabled(true);
        }
    }

    private void cancelSuppressor() {
        if (suppressor != null) {
            suppressor.setEnabled(false);
            suppressor.release();
            suppressor = null;
        }
    }
}
