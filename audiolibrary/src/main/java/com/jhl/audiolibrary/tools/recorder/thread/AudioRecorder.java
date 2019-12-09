package com.jhl.audiolibrary.tools.recorder.thread;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.jhl.audiolibrary.Constant;
import com.jhl.audiolibrary.Variable;
import com.jhl.audiolibrary.common.CommonFunction;
import com.jhl.audiolibrary.common.CommonThreadPool;
import com.jhl.audiolibrary.tools.recorder.FFT;
import com.jhl.audiolibrary.tools.recorder.PCMFormat;
import com.jhl.audiolibrary.tools.recorder.RecorderEngine;
import com.jhl.audiolibrary.utils.AudioEncodeUtil;
import com.jhl.audiolibrary.utils.CorverFileUtils;
import com.jhl.audiolibrary.utils.FileFunction;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.media.AudioTrack.WRITE_NON_BLOCKING;
import static com.jhl.audiolibrary.tools.recorder.FFT.SAMPLE_RATE;
import static java.lang.Math.log;

/**
 * 类介绍（必填）：录音  有暂停继续功能 最终由多个pcm合成一个wav。
 * Created by Jiang on 2018/7/22 .
 */

public class AudioRecorder {
    private AudioTrack audioTrack;
    private double currentFrequency;
    private int length;

    public static final float pi = (float) 3.1415926;
    private final static int sampleDuration = 100;

    private static final int recordSleepDuration = 500;
    private static final double FREQUENCY = 500; //Hz，标准频率（这里分析的是500Hz）
    private static final double RESOLUTION = 10; //Hz，误差
    //自定义 每160帧作为一个周期，通知一下需要进行编码
    private static final int FRAME_COUNT = 160;

    private int realSampleDuration;
    private static final PCMFormat pcmFormat = PCMFormat.PCM_16BIT;

    private short[] audioRecordBuffer;
    private int bufferSizeInBytes;
    private int realSampleNumberInOneDuration;
    private AudioRecord audioRecord;
    private LinkedList<byte[]> saveDataList;
    private Status status;
    //录音文件
    private List<String> filesName = new ArrayList<>();
    //wav文件名称
    private String fileName;
    private byte[] bytes_pkg;
    private BufferedOutputStream bufferedOutputStream;
    private int amplitude;
    private boolean isFinish;
    private ArrayList<int[]> outBuf = new ArrayList<int[]>();//处理后的数据
    private int number;
    private int tal;
    private long time;
    private long endtime;
    private long currenttime;
    private float midi;
    private EarReturnPlayer earReturnPlayer;
    private int frequencyEach;
    private int[] WaveHeight;
    private int playBufSize;
    private boolean isEarFan = true;//是否开启耳返

    public AudioRecorder() {
        init();
    }

    private void init() {

        //存储字节 用于边录边播
        saveDataList = new LinkedList<byte[]>();

    }

    public void clearFilesName() {
        filesName.clear();
    }

    public void readyState() {
        status = Status.STATUS_READY;
//        earReturnPlayer = new EarReturnPlayer();
//        CommonThreadPool.getThreadPool().addCachedTask(earReturnPlayer);
    }

    public EarReturnPlayer getEarReturnPlayer() {
        return earReturnPlayer;
    }

    public void initAudioRecord(String fileName) {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        this.fileName = fileName;
        int audioRecordMinBufferSize = AudioRecord
                .getMinBufferSize(Constant.RecordSampleRate, AudioFormat.CHANNEL_IN_MONO,//CHANNEL_CONFIGURATION_MONO  CHANNEL_IN_MONO
                        pcmFormat.getAudioFormat());

        bufferSizeInBytes =
                Constant.RecordSampleRate * pcmFormat.getBytesPerFrame() / (1000 / sampleDuration);

        if (audioRecordMinBufferSize > bufferSizeInBytes) {
            bufferSizeInBytes = audioRecordMinBufferSize;
        }

    /*
     * 使能被整除，方便下面的周期性通知
     * */
        int bytesPerFrame = pcmFormat.getBytesPerFrame();
        int frameSize = bufferSizeInBytes / bytesPerFrame;

        if (frameSize % FRAME_COUNT != 0) {
            frameSize += (FRAME_COUNT - frameSize % FRAME_COUNT);
            bufferSizeInBytes = frameSize * bytesPerFrame;
        }

        audioRecordBuffer = new short[bufferSizeInBytes];

        double sampleNumberInOneMicrosecond = (double) Constant.RecordSampleRate / 1000;

        realSampleDuration = bufferSizeInBytes * 1000 /
                (Constant.RecordSampleRate * pcmFormat.getBytesPerFrame());

        realSampleNumberInOneDuration = (int) (sampleNumberInOneMicrosecond * realSampleDuration);
        //VOICE_COMMUNICATION
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, Constant.RecordSampleRate,
                AudioFormat.CHANNEL_IN_MONO, pcmFormat.getAudioFormat(), bufferSizeInBytes);

//        //实时播放
//        playBufSize = AudioTrack.getMinBufferSize(Constant.RecordSampleRate,
//                AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                AudioFormat.ENCODING_PCM_16BIT);
//
//        //MediaRecorder.AudioSource.VOICE_COMMUNICATION
//        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, Constant.RecordSampleRate,//AudioManager.STREAM_MUSIC
//                AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, playBufSize,
//                AudioTrack.MODE_STREAM);
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        Log.d("AudioRecorder", "===pauseRecord===");
        if (status != Status.STATUS_START) {
//            throw new IllegalStateException("没有在录音");
        } else {
            isFinish = true;
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
            audioRecord.release();
            audioRecord = null;

//            releaseCanceler();
        }
    }

    /**
     * 停止录音
     */
    public boolean stopRecordVoice() {
        Log.d("AudioRecorder", "===stopRecord===");
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            throw new IllegalStateException("录音尚未开始");
        } else {
            release();
            onDestory();
        }
        return true;
    }

    public void onDestory() {
        if (audioRecord != null) {
            isFinish = true;
            if (status == Status.STATUS_START) {
                audioRecord.stop();
            }
            audioRecord.release();
            audioRecord = null;
            status = Status.STATUS_NO_READY;
        }
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }

    }

    /**
     * 释放资源
     */
    public void release() {
        Log.d("AudioRecorder", "===release===" + filesName.toString());
        //假如有暂停录音
        try {
            if (filesName.size() > 0) {
                List<String> filePaths = new ArrayList<>();
                for (String fileName : filesName) {
                    filePaths.add(CorverFileUtils.getPcmFileAbsolutePath(fileName));
                }
                filesName.clear();
                saveDataList.clear();
                //将多个pcm文件转化为wav文件
                mergePCMFilesToWAVFile(filePaths);

            } else {
                //这里由于只要录音过filesName.size都会大于0,没录音时fileName为null
                //会报空指针 NullPointerException
                // 将单个pcm文件转化为wav文件
                //Log.d("AudioRecorder", "=====makePCMFileToWAVFile======");
                //makePCMFileToWAVFile();
            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }


    }

    //清除当前pcm
    public void clearPcmFiles() {
        if (filesName.size() > 0) {
            for (String fileName : filesName) {
                String path = CorverFileUtils.getPcmFileAbsolutePath(fileName);
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 取消录音
     */
    public void canel() {
        filesName.clear();
        fileName = null;
        status = Status.STATUS_NO_READY;
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
//        releaseCanceler();
    }

    public int getVolume() {
        int volume = (int) (Math.sqrt(amplitude)) * Constant.RecordVolumeMaxRank / 60;
        return volume;
    }

    public int getMidiNum() {
        return (int) midi - 10;
    }

    //开启耳返
    public void setOpenEarReturn(boolean isEarFan) {
        this.isEarFan = isEarFan;
    }

    /**
     * 开始录音
     */
    public boolean startRecordVoice() {

        if (status == Status.STATUS_NO_READY || TextUtils.isEmpty(fileName)) {
//            return false;
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (status == Status.STATUS_START) {
            throw new IllegalStateException("正在录音");
        }

        isFinish = false;


        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if (audioRecord == null) {
                    initAudioRecord(fileName);
                }

//                //判断是否支持回声消除
//                if (isDeviceSupportAEC()) {
//                    initAEC(audioRecord.getAudioSessionId());
//                }
                writeDataTOFile();
            }
        };
        CommonThreadPool.getThreadPool().addCachedTask(runnable);


        return true;
    }

    private void writeDataTOFile() {

        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
//            audioTrack.play();
        } catch (IllegalStateException e) {
            Log.i("出现异常==", e.getMessage());
            e.printStackTrace();
        }
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];
        int sizeInBytes = bufferSizeInBytes;
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        int readsize = 0;
        try {

            String currentFileName = fileName;
            if (status == Status.STATUS_PAUSE) {
                //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
                currentFileName += filesName.size();
            }
            filesName.add(currentFileName);
//            File file = new File(CorverFileUtils.getPcmFileAbsolutePath(currentFileName));
//            if (file.exists()) {
//                file.delete();
//            }
            bufferedOutputStream = FileFunction
                    .GetBufferedOutputStreamFromFile(CorverFileUtils.getPcmFileAbsolutePath(currentFileName));
//            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
//            dos = new DataOutputStream(new BufferedOutputStream(fos));
        } catch (IllegalStateException e) {
            Log.e("AudioRecorder", e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
        if (saveDataList != null) {
            saveDataList.clear();
        } else {
            saveDataList = new LinkedList<>();
        }

        //将录音状态设置成正在录音状态
        status = Status.STATUS_START;
        byte[] bufferRead = new byte[sizeInBytes];
        while (status == Status.STATUS_START && !isFinish) {

            readsize = audioRecord.read(audioRecordBuffer, 0, sizeInBytes);

            if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
                if (readsize > 0) {
                    //耳返
//                    if (isEarFan) {
//                        short[] tmpBuf = new short[readsize];
//                        System.arraycopy(audioRecordBuffer, 0, tmpBuf, 0, readsize);
////                    audioTrack.write(outputByteArray, 0, outputByteArray.length);
//                        audioTrack.write(tmpBuf, 0, tmpBuf.length);
//                    }
//                    currenttime = System.currentTimeMillis();
                    calculateRealVolume(audioRecordBuffer, readsize);


                    byte[] outputByteArray = CommonFunction
                            .GetByteBuffer(audioRecordBuffer,
                                    readsize, Variable.isBigEnding);
                    for (int i = 0; i < outputByteArray.length; i++) {
                        outputByteArray[i] = (byte) (outputByteArray[i] * 1.2);
                    }


                    if (bufferedOutputStream != null) {
                        try {
                            bufferedOutputStream.write(outputByteArray);//写入文件
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //傅里叶计算频率
                        short[] data = new short[FFT.FFT_N];
                        System.arraycopy(audioRecordBuffer, audioRecordBuffer.length - FFT.FFT_N,
                                data, 0, FFT.FFT_N);

                        double frequence = FFT.GetFrequency(data);
                        midi = (float) (71 + 12 * log2(frequence / 440));


                    }
                }
            }
        }
        try {
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
        } catch (IOException e) {
            Log.e("AudioRecorder", e.getMessage());
        }


    }

    //快速傅里叶变换
    public void fft(Complex[] xin, int N) {
        int f, m, N2, nm, i, k, j, L;//L:运算级数
        float p;
        int e2, le, B, ip;
        Complex w = new Complex();
        Complex t = new Complex();
        N2 = N / 2;//每一级中蝶形的个数,同时也代表m位二进制数最高位的十进制权
        f = N;//f是为了求流程的级数而设立的
        for (m = 1; (f = f / 2) != 1; m++) ;                             //得到流程图的共几级
        nm = N - 2;
        j = N2;
        /******倒序运算——雷德算法******/
        for (i = 1; i <= nm; i++) {
            if (i < j)//防止重复交换
            {
                t = xin[j];
                xin[j] = xin[i];
                xin[i] = t;
            }
            k = N2;
            while (j >= k) {
                j = j - k;
                k = k / 2;
            }
            j = j + k;
        }
        /******蝶形图计算部分******/
        for (L = 1; L <= m; L++)                                    //从第1级到第m级
        {
            e2 = (int) Math.pow(2, L);
            //e2=(int)2.pow(L);
            le = e2 + 1;
            B = e2 / 2;
            for (j = 0; j < B; j++)                                    //j从0到2^(L-1)-1
            {
                p = 2 * pi / e2;
                w.real = Math.cos(p * j);
                //w.real=Math.cos((double)p*j);                                   //系数W
                w.image = Math.sin(p * j) * -1;
                //w.imag = -sin(p*j);
                for (i = j; i < N; i = i + e2)                                //计算具有相同系数的数据
                {
                    ip = i + B;                                           //对应蝶形的数据间隔为2^(L-1)
                    t = xin[ip].cc(w);
                    xin[ip] = xin[i].cut(t);
                    xin[i] = xin[i].sum(t);
                }
            }
        }
    }


    private double calculateVolume(short[] buffer) {


        double sumVolume = 0.0;

        double avgVolume = 0.0;

        double volume = 0.0;

        for (short b : buffer) {

            sumVolume += Math.abs(b);

        }

        avgVolume = sumVolume / buffer.length;

        volume = Math.log10(1 + avgVolume) * 10;


        return volume;

    }

    double log2(double dblpow2) {
        /**
         math.h中没有log2的接口
         用对数换底公式来间接计算
         对数换底公式
         Loga(B) = logc(B) / logc(A)
         a, c均大于0且不等于1
         math.h中有log和log10
         logc 可以用log或1og10
         */

        double dbllog2 = 0;
        dbllog2 = log((double) dblpow2) / log((double) 2);
        return dbllog2;
    }


    //对录音文件进行分析
    private void frequencyAnalyse(String filePath) {
        if (filePath == null) {
            return;
        }
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(filePath));
            //16bit采样，因此用short[]
            //如果是8bit采样，这里直接用byte[]
            //从文件中读出一段数据，这里长度是SAMPLE_RATE，也就是1s采样的数据
            short[] buffer = new short[SAMPLE_RATE];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = inputStream.readShort();
            }
            short[] data = new short[FFT.FFT_N];

            //为了数据稳定，在这里FFT分析只取最后的FFT_N个数据
            System.arraycopy(buffer, buffer.length - FFT.FFT_N,
                    data, 0, FFT.FFT_N);

            //FFT分析得到频率
            double frequence = FFT.GetFrequency(data);
            if (Math.abs(frequence - FREQUENCY) < RESOLUTION) {
                //测试通过
                Log.i("频率===", frequence + "");
            } else {
                //测试失败
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向上取最接近iint的2的幂次数.比如iint=320时,返回256
     *
     * @param iint
     * @return
     */

    private int up2int(int iint) {
        int ret = 1;
        while (ret <= iint) {
            ret = ret << 1;
        }
        return ret >> 1;
    }


    /**
     * 此计算方法来自samsung开发范例
     *
     * @param buffer   buffer
     * @param readSize readSize
     */
    private void calculateRealVolume(short[] buffer, int readSize) {
        int sum = 0;

        for (int index = 0; index < readSize; index++) {
            // 这里没有做运算的优化，为了更加清晰的展示代码
            sum += Math.abs(buffer[index]);
        }

        if (readSize > 0) {
            amplitude = sum / readSize;
        }
    }

    /**
     * 将pcm合并成wav
     *
     * @param filePaths
     */
    private void mergePCMFilesToWAVFile(final List<String> filePaths) {
        if (AudioEncodeUtil.mergePCMFilesToWAVFile(filePaths, CorverFileUtils.getWavFileAbsolutePath(fileName))) {
            //操作成功
            filePaths.clear();
        } else {
            //操作失败
            Log.e("AudioRecorder", "mergePCMFilesToWAVFile fail");
            throw new IllegalStateException("mergePCMFilesToWAVFile fail");
        }
        fileName = null;
    }

    //用于边录边播
    public LinkedList<byte[]> getSaveDataList() {
        if (saveDataList == null) saveDataList = new LinkedList<>();
        return saveDataList;
    }

    //回声消除
    private AcousticEchoCanceler canceler;

    //判断当前机型是否支持AEC
    public boolean isDeviceSupportAEC() {
        return AcousticEchoCanceler.isAvailable();
    }

    //初始化AEC
    public boolean initAEC(int audioSession) {
        if (canceler != null) {
            return false;
        }
        canceler = AcousticEchoCanceler.create(audioSession);
        canceler.setEnabled(true);
        return canceler.getEnabled();
    }

    public boolean setAECEnabled(boolean enable) {
        if (null == canceler) {
            return false;
        }
        canceler.setEnabled(enable);
        return canceler.getEnabled();
    }

    public boolean releaseCanceler() {
        if (null == canceler) {
            return false;
        }
        canceler.setEnabled(false);
        canceler.release();
        canceler = null;
        return true;
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

    //耳返
    public class EarReturnPlayer implements Runnable {
        private byte[] outBytes;
        private int outputBufferSize;
        /**
         * 播放流
         */

        private int sampleRateInHz = Constant.BehaviorSampleRate;
        /**
         * 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
         */
        private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        private boolean flag = true;
        private boolean isPause;//是否播放

        public EarReturnPlayer() {
            outputBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                    channelConfig,
                    AudioFormat.ENCODING_PCM_16BIT);

            //MediaRecorder.AudioSource.VOICE_COMMUNICATION
            audioTrack = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, sampleRateInHz,//AudioManager.STREAM_MUSIC
                    channelConfig,
                    AudioFormat.ENCODING_PCM_16BIT, outputBufferSize,
                    AudioTrack.MODE_STREAM);
            outBytes = new byte[outputBufferSize];
        }

        @Override
        public void run() {
            byte[] bytes_pkgR = null;
            audioTrack.setStereoVolume(1, 1);
            audioTrack.play();
//            setNoiseSuppressor(audioTrack.getAudioSessionId());
//            setCanceler(audioTrack.getAudioSessionId());
//            while (flag) {
//                try {
////                    if (outputByteArray.length > 0) {
////                        bytes_pkgR = bytes_pkg.clone();
////                        audioTrack.write(outputByteArray, 0, outputByteArray.length);
////                    }
//                    if (saveDataList != null && saveDataList.size() > 0) {
//                        outBytes = saveDataList.getFirst();
//                        bytes_pkgR = outBytes.clone();
//                        audioTrack.write(bytes_pkgR, 0, bytes_pkgR.length);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            saveDataList.clear();
//            stopRecoder();
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

//            setOnPauseRecord();
        }

        public void setHaveSound() {
            if (audioTrack != null) {
                audioTrack.setStereoVolume(1, 1);
                audioTrack.play();
            }
//            setOnRestartRecord();
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
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
                audioTrack = null;
                cancelSuppressor();
                cancelCanceler();
            }
        }

        NoiseSuppressor suppressor;

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

    //取消回声控制器
    private void setCanceler(int sessionId) {
        canceler = AcousticEchoCanceler.create(sessionId);
        if (AcousticEchoCanceler.isAvailable() && canceler != null) {
            canceler.setEnabled(true);
        }
    }

    private void cancelCanceler() {
        if (canceler != null) {
            canceler.setEnabled(false);
            canceler.release();
            canceler = null;
        }
    }
}
