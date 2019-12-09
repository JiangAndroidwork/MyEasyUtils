package com.jhl.audiolibrary.tools.interfaces;

public interface VoiceRecorderOperateInterface {
    public void recordVoiceBegin();

    public void recordVoiceStateChanged(int volume,int midi, long recordDuration);



    public void recordVoiceFail();

    public void recordVoiceFinish();

    public void recordPause();
}
