package com.jhl.audiolibrary.tools.player;

import android.media.MediaPlayer;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;

import com.jhl.audiolibrary.common.CommonFunction;
import com.jhl.audiolibrary.tools.data.MusicData;
import com.jhl.audiolibrary.tools.data.MusicDataTwo;
import com.jhl.audiolibrary.tools.interfaces.VoicePlayerInterface;
import com.jhl.audiolibrary.utils.LogFunction;

/**
 * 类介绍（必填）：播放mp3  测试
 * Created by Jiang on 2018/7/22 .
 */
public class VoicePlayerTwoEngine {
    private int musicPlayerState;

    private String playingUrl;

    private VoicePlayerInterface voicePlayerInterface;

    private MediaPlayer voicePlayer;

    private static VoicePlayerTwoEngine instance;
    private boolean isPrepera;
    private boolean isNoPlay;

    private VoicePlayerTwoEngine() {
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        musicPlayerState = MusicDataTwo.MusicPlayerState.reset;

        voicePlayer = new MediaPlayer();

        voicePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                isPrepera = true;
                setCanceler(mediaPlayer);
                setNoiseSuppressor(mediaPlayer);
                start();
            }
        });


        voicePlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(final MediaPlayer mediaPlayer, int what, int extra) {
                Log.i("播放器出错==", what + "\n" + extra);
                playFail();
                isPrepera = false;
                return true;
            }
        });
        voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (voicePlayerInterface != null) {
                    voicePlayerInterface.playCompelte(getPlayingUrl());
                }
            }
        });
    }

    public static VoicePlayerTwoEngine getInstance() {
        if (instance == null) {
            synchronized (VoicePlayerTwoEngine.class) {
                if (instance == null) {
                    instance = new VoicePlayerTwoEngine();
                }
            }
        }

        return instance;
    }

    //获取mediaplayer
    public MediaPlayer getMediaPlayer() {
        return voicePlayer;
    }

    //获取当前的播放时间
    public int getCurrentPosition() {
        if (isPrepera && voicePlayer != null) {
            return voicePlayer.getCurrentPosition();
        } else {
            return -1;
        }
    }

    public void setNoPlay() {
        isNoPlay = true;
    }

    public int getDuraction() {
        if (isPrepera && voicePlayer != null) {
            return voicePlayer.getDuration();
        } else {
            return -1;
        }
    }

    public void seekTo(int progress) {
        if (isPrepera && voicePlayer != null) {
            voicePlayer.seekTo(progress);
        }
    }

    //设置循环模式
    public void setLooping(boolean isLoop) {
        if (voicePlayer != null) {
            voicePlayer.setLooping(isLoop);
        }
    }

    /**
     * 结束播放
     *
     * @param isWantReturn 是否想要有回调
     */
    public void destroy(boolean isWantReturn) {
        if (voicePlayer != null) {
            isPrepera = false;
            isNoPlay = false;
            if (voicePlayer.isPlaying()) {
                voicePlayer.stop();
            }

            voicePlayer.release();
            voicePlayer = null;

            if (isWantReturn && voicePlayerInterface != null) {
                voicePlayerInterface.playVoiceFinish(playingUrl);
            }

        }

    }

    public void playVoice(final String voiceUrl, final VoicePlayerInterface voicePlayerInterface) {

        Log.i("执行开始播放==", voiceUrl);
        if (CommonFunction.isEmpty(voiceUrl)) {
            return;
        }
        isPrepera = false;
        this.voicePlayerInterface = voicePlayerInterface;
        if (voicePlayer == null) {
            initMediaPlayer();
        }
        prepareMusic(voiceUrl);
    }

    private synchronized void prepareMusic(String voiceUrl) {
        playingUrl = voiceUrl;

        musicPlayerState = MusicDataTwo.MusicPlayerState.preparing;

        try {
            voicePlayer.reset();
            voicePlayer.setDataSource(voiceUrl);
            voicePlayer.prepareAsync();
        } catch (Exception e) {
            playFail();
            isPrepera = false;

            LogFunction.error("播放语音异常", e);
        }
    }

    private void playFail() {
        if (voicePlayerInterface != null) {
            voicePlayerInterface.playVoiceFail(playingUrl);
        }

        playingUrl = null;
    }


    public boolean isPlaying() {
        if (voicePlayer != null) {
            return voicePlayer.isPlaying();
        } else {
            return false;
        }
    }

    public boolean isPrepared() {
        return isPrepera;
    }

    public void start() {
        if (voicePlayer != null) {
            if (!isNoPlay) {
                voicePlayer.start();
            }
            musicPlayerState = MusicData.MusicPlayerState.playing;

            if (voicePlayerInterface != null) {
                voicePlayerInterface.playVoiceBegin(playingUrl);
            }


        }
    }

    public void reStart() {
        if (voicePlayer != null && (musicPlayerState == MusicData.MusicPlayerState.pausing || isPrepera)) {
            voicePlayer.start();
            musicPlayerState = MusicData.MusicPlayerState.playing;
        }
    }

    public void pause() {
        if (voicePlayer != null && !voicePlayer.isPlaying()) {
            return;
        }
        if (voicePlayer != null) {
            voicePlayer.pause();

            musicPlayerState = MusicData.MusicPlayerState.pausing;
        }


    }

    private void reset() {
        if (voicePlayer != null) {
            voicePlayer.reset();
            musicPlayerState = MusicDataTwo.MusicPlayerState.reset;
            isPrepera = false;
            playingUrl = null;
        }
    }

    public void stopVoice() {
        switch (musicPlayerState) {
            case MusicDataTwo.MusicPlayerState.playing:
                pause();
                break;
            case MusicDataTwo.MusicPlayerState.preparing:
                reset();
                break;
        }
    }

    public void setVolume(float num) {
        if (voicePlayer != null) {
            voicePlayer.setVolume(num, num);
//            voicePlayer.start();
        }
    }

    public String getPlayingUrl() {
        return playingUrl == null ? "" : playingUrl;
    }

    //取消回声控制器
    private void setCanceler(MediaPlayer mediaPlayer) {
        AcousticEchoCanceler canceler = AcousticEchoCanceler.create(mediaPlayer.getAudioSessionId());
        if (AcousticEchoCanceler.isAvailable() && canceler != null)
            canceler.setEnabled(true);
    }

    //噪音压制控制器
    private void setNoiseSuppressor(MediaPlayer mediaPlayer) {
        NoiseSuppressor suppressor = NoiseSuppressor.create(mediaPlayer.getAudioSessionId());
        if (NoiseSuppressor.isAvailable() && suppressor != null) {
            suppressor.setEnabled(true);
        }
    }
}