package com.jiang.mylibrary.utils.network.push;

class ProgressModel {
    private long bytesWritten;
    private long contentLength;
    private boolean isDone;

    public ProgressModel(long bytesWritten, long contentLength, boolean isDone) {
        this.bytesWritten = bytesWritten;
        this.contentLength = contentLength;
        this.isDone = isDone;
    }

    public long getCurrentBytes() {
        return bytesWritten;
    }

    public long getContentLength() {
        return contentLength;
    }

    public boolean isDone() {
        return isDone;
    }
}
