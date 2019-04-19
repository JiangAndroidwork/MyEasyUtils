package com.jiang.mylibrary.utils.network.push;
/**
 * Created by hongliJiang on 2019/4/19 11:17
 * 描述：上传进度接口
 */
public interface ProgressListener {
    void onProgress(long bytesWritten, long contentLength, boolean isDone);
}
