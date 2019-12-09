package com.jhl.audiolibrary.library.mylrc;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.jhl.audiolibrary.R;

import java.util.List;

/**
 * 类介绍（必填）：自定义歌词
 * Created by Jiang on 2018/7/31 .
 */

public class CorverLrcViewTest extends View {
    private List<LrcBean> list;
    private Paint gPaint;
    private Paint hPaint;
    private int width = 0, height = 0;
    private int currentPosition = 0;
    private MediaPlayer player;
    private int lastPosition = 0;
    private int highLineColor;
    private int lrcColor;
    private int mode = 0;
    public final static int KARAOKE = 1;
    private boolean isFinish;
    //行间距
    private float mDividerHeight;


    public void setHighLineColor(int highLineColor) {
        this.highLineColor = highLineColor;
    }

    public void setLrcColor(int lrcColor) {
        this.lrcColor = lrcColor;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    /**
     * 标准歌词字符串
     *
     * @param lrc
     */
    public void setLrc(String lrc) {
        list = LrcUtil.readString(lrc);
    }

    public CorverLrcViewTest(Context context) {
        this(context, null);
    }

    public CorverLrcViewTest(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CorverLrcViewTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CorverLrcView);
        mDividerHeight = ta.getDimension(R.styleable.CorverLrcView_lrcCorverDividerHeight, getResources().getDimension(R.dimen.lrc_divider_height));
        float lrcTextSize = ta.getDimension(R.styleable.CorverLrcView_lrcCorverTextSizeNormal, getResources().getDimension(R.dimen.lrc_text_size));
        highLineColor = ta.getColor(R.styleable.CorverLrcView_hignLineColor, getResources().getColor(R.color.colorAccent));
        lrcColor = ta.getColor(R.styleable.CorverLrcView_lrcColor, getResources().getColor(android.R.color.darker_gray));
        mode = ta.getInt(R.styleable.CorverLrcView_lrcCorverMode, mode);
        ta.recycle();
        gPaint = new Paint();
        gPaint.setAntiAlias(true);
        gPaint.setColor(lrcColor);
        gPaint.setTextSize(lrcTextSize);
        gPaint.setTextAlign(Paint.Align.CENTER);
        hPaint = new Paint();
        hPaint.setAntiAlias(true);
        hPaint.setColor(highLineColor);
        hPaint.setTextSize(lrcTextSize);
        hPaint.setTextAlign(Paint.Align.CENTER);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
        if (list == null || list.size() == 0) {
            canvas.drawText("暂无歌词", width / 2, height / 2, gPaint);
            return;
        }

        getCurrentPosition();

//        drawLrc1(canvas);
        if (isFinish) return;

        if (player != null) {

            int currentMillis = player.getCurrentPosition();
            drawLrc2(canvas, currentMillis);
            double start = list.get(currentPosition).getStart();
            double v = (currentMillis - start) > 500 ? currentPosition * 80 : lastPosition * 80 + (currentPosition - lastPosition) * 80 * ((currentMillis - start) / 500f);
            setScrollY((int) v);
            if (getScrollY() == currentPosition * 80) {
                lastPosition = currentPosition;
            }
            postInvalidateDelayed(100);
        }
    }

    public void onFinish() {
        this.isFinish = true;
    }

    private void drawLrc2(Canvas canvas, int currentMillis) {
        if (mode == 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i == currentPosition) {
                    canvas.drawText(list.get(i).getLrc(), width / 2, 80 + 80 * i, hPaint);
                } else {
                    canvas.drawText(list.get(i).getLrc(), width / 2, 80 + 80 * i, gPaint);
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                canvas.drawText(list.get(i).getLrc(), width / 2, 80 + 80 * i, gPaint);
            }
            String highLineLrc = list.get(currentPosition).getLrc();
            int highLineWidth = (int) gPaint.measureText(highLineLrc);
            int leftOffset = (width - highLineWidth) / 2;
            LrcBean lrcBean = list.get(currentPosition);
            double start = lrcBean.getStart();
            double end = lrcBean.getEnd();
            int i = (int) ((currentMillis - start) * 1.0f / (end - start) * highLineWidth);
            if (i > 0 && i < highLineWidth) {
                try {
                    Bitmap textBitmap = Bitmap.createBitmap(i, 85, Bitmap.Config.ARGB_8888);
                    Canvas textCanvas = new Canvas(textBitmap);
                    textCanvas.drawText(highLineLrc, highLineWidth / 2, 80, hPaint);
                    canvas.drawBitmap(textBitmap, leftOffset, 80 + 80 * (currentPosition - 1), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void init() {
        isFinish = false;
        currentPosition = 0;
        lastPosition = 0;
        setScrollY(0);
        invalidate();
    }


    private void getCurrentPosition() {
        try {
            int currentMillis = player.getCurrentPosition();
            if (currentMillis < list.get(0).getStart()) {
                currentPosition = 0;
                return;
            }
            if (currentMillis > list.get(list.size() - 1).getStart()) {
                currentPosition = list.size() - 1;
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                if (currentMillis >= list.get(i).getStart() && currentMillis < list.get(i).getEnd()) {
                    currentPosition = i;
                    return;
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            postInvalidateDelayed(100);
        }
    }
}
