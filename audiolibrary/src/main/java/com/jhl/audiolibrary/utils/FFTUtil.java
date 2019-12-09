package com.jhl.audiolibrary.utils;

/**
 * 傅里叶变换
 * Created by Jiang on 2018/8/6.
 */

public class FFTUtil {

    static {
        System.loadLibrary("FFT");
    }

    public static native double processSampleData(byte[] sample, int sampleRate);
}
