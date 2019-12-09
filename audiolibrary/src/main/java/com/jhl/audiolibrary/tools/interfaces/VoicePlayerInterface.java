package com.jhl.audiolibrary.tools.interfaces;

public interface VoicePlayerInterface {
    public void playVoiceBegin(String url);

    public void playVoiceFail(String url);

    public void playVoiceFinish(String url);//点击完成之后的监听

    public void playCompelte(String url);//自己播放完了

    public void playBufferPosition(int bufferPosition);
}
