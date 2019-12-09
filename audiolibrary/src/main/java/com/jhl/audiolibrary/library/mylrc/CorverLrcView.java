package com.jhl.audiolibrary.library.mylrc;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.jhl.audiolibrary.R;
import com.jhl.audiolibrary.utils.CorverDensityUtil;

import java.util.List;

/**
 * 类介绍（必填）：自定义歌词
 * Created by Jiang on 2018/7/31 .
 */

public class CorverLrcView extends View {
    private final int px_8 = CorverDensityUtil.dip2px(getContext(), 8);
    private final int px_10 = CorverDensityUtil.dip2px(getContext(), 4);
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
    private float mTextHeight;//文字高度
    private Paint pointPaintT;//画点 的paint
    private Paint pointPaintF;
    private ValueAnimator mAnimator;

    private float mOffset;
    private long startTime;
    private float lrcTextSizeBig;
    private float lrcTextSizeNormal;
    private GestureDetector mGestureDetector;
    private boolean isTouching;//是否点击
    private Scroller mScroller;
    private double v;

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
    public List<LrcBean> setLrc(String lrc) {
        list = LrcUtil.readString(lrc);

        return list;
    }


    public CorverLrcView(Context context) {
        this(context, null);
    }

    public CorverLrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CorverLrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CorverLrcView);
        mDividerHeight = ta.getDimension(R.styleable.CorverLrcView_lrcCorverDividerHeight, getResources().getDimension(R.dimen.lrc_divider_height));
        lrcTextSizeBig = ta.getDimension(R.styleable.CorverLrcView_lrcCorverTextSizeBig, getResources().getDimension(R.dimen.lrc_text_size));
        lrcTextSizeNormal = ta.getDimension(R.styleable.CorverLrcView_lrcCorverTextSizeNormal, getResources().getDimension(R.dimen.lrc_text_size));
        highLineColor = ta.getColor(R.styleable.CorverLrcView_hignLineColor, getResources().getColor(R.color.colorAccent));
        lrcColor = ta.getColor(R.styleable.CorverLrcView_lrcColor, getResources().getColor(android.R.color.darker_gray));
        mode = ta.getInt(R.styleable.CorverLrcView_lrcCorverMode, mode);
        ta.recycle();

        gPaint = new Paint();
        gPaint.setAntiAlias(true);
        gPaint.setColor(lrcColor);
        gPaint.setTextSize(lrcTextSizeNormal);
        gPaint.setTextAlign(Paint.Align.CENTER);
        hPaint = new Paint();
        hPaint.setAntiAlias(true);
        hPaint.setColor(highLineColor);
        hPaint.setTextSize(lrcTextSizeNormal);
        hPaint.setTextAlign(Paint.Align.CENTER);
        //画点
        pointPaintT = new Paint();
        pointPaintT.setColor(highLineColor);
        pointPaintT.setStyle(Paint.Style.FILL);
        pointPaintT.setTextAlign(Paint.Align.CENTER);
        pointPaintT.setTextSize(24);

        //画点
        pointPaintF = new Paint();
        pointPaintF.setColor(getResources().getColor(R.color.touming));
        pointPaintF.setStyle(Paint.Style.FILL);
        pointPaintF.setTextAlign(Paint.Align.CENTER);
        pointPaintT.setTextSize(24);

        //计算字体的高度
        float ascent = gPaint.ascent();
        float descent = gPaint.descent();
        mTextHeight = descent - ascent;

        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        mScroller = new Scroller(getContext());
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
            v = (currentMillis - start) > 500 ? currentPosition * (mDividerHeight + mDividerHeight) : lastPosition * (mDividerHeight + mDividerHeight) + (currentPosition - lastPosition) * (mDividerHeight + mDividerHeight) * ((currentMillis - start) / 500f);
//            float v = currentPosition * (mDividerHeight + mTextHeight)+(mDividerHeight + mTextHeight)+mDividerHeight;
            if (!isTouching) {
                setScrollY((int) v);
            }
            if (getScrollY() == currentPosition * (mDividerHeight + mDividerHeight)) {
                lastPosition = currentPosition;
            }
            postInvalidateDelayed(100);
        }

    }


    public void onFinish() {
        this.isFinish = true;
    }

    private void drawLrc2(final Canvas canvas, final int currentMillis) {
        if (mode == 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i == currentPosition) {
                    canvas.drawText(list.get(i).getLrc(), width / 2, 35 + mDividerHeight + mDividerHeight * i, hPaint);
                } else {
                    canvas.drawText(list.get(i).getLrc(), width / 2, 35 + mDividerHeight + mDividerHeight * i, gPaint);
                }
            }
        } else {
            //画点点
            double startFirsst = list.get(0).getStart();
            if (startFirsst >= 3000) {
                double startSecond = startFirsst - 1000;
                double startThree = startFirsst - 2000;
                if (currentMillis < startThree) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 1 + px_10, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 2 + px_10 * 2, mDividerHeight, 10, pointPaintT);
                } else if (currentMillis >= startThree && currentMillis < startSecond) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 1 + px_10, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 2 + px_10 * 2, mDividerHeight, 10, pointPaintF);
                } else if (currentMillis >= startSecond && currentMillis < startFirsst) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 1 + px_10, mDividerHeight, 10, pointPaintF);
                } else if (currentMillis >= startFirsst) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintF);
                }
            } else {
                long l = System.currentTimeMillis();
                if (0 <= l - startTime && l - startTime < 1000) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 1 + px_10, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 2 + px_10 * 2, mDividerHeight, 10, pointPaintT);
                } else if (1000 <= l - startTime && l - startTime < 2000) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 1 + px_10, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 2 + px_10 * 2, mDividerHeight, 10, pointPaintF);
                } else if (2000 <= l - startTime && l - startTime < 3000) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintT);
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 1 + px_10, mDividerHeight, 10, pointPaintF);
                } else if (l - startTime >= 3000) {
                    canvas.drawCircle(width / 2 - (px_8 * 2 + px_10 * 2) / 2 + px_8 * 0, mDividerHeight, 10, pointPaintF);
                }
            }

            for (int i = 0; i < list.size(); i++) {
                if (currentPosition == i) {
                    gPaint.setTextSize(lrcTextSizeBig);
                    canvas.drawText(list.get(i).getLrc(), width / 2, mDividerHeight + (mDividerHeight + mTextHeight) + (mTextHeight + mDividerHeight) * i, gPaint);
                } else {
//                    float v = mDividerHeight + (mDividerHeight + mTextHeight) + (mTextHeight + mDividerHeight) * i;
                    gPaint.setTextSize(lrcTextSizeNormal);
                    canvas.drawText(list.get(i).getLrc(), width / 2, mDividerHeight + (mDividerHeight + mTextHeight) + (mTextHeight + mDividerHeight) * i, gPaint);
                }
            }

            hPaint.setTextSize(lrcTextSizeBig);
            hPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            float ascent = hPaint.ascent();
            float descent = hPaint.descent();
            String highLineLrc = list.get(currentPosition).getLrc();

            int highLineWidth = (int) hPaint.measureText(highLineLrc);
            int leftOffset = (width - highLineWidth) / 2;
            LrcBean lrcBean = list.get(currentPosition);
            double start = lrcBean.getStart();
            double end = lrcBean.getEnd();
            int i = (int) ((currentMillis - start) * 1.0f / (end - start) * highLineWidth);
            if (i > 0 && i <= highLineWidth && !isTouching) {
                try {
                    //高亮的宽高
                    Bitmap textBitmap = Bitmap.createBitmap(i, 85 + (int) mTextHeight, Bitmap.Config.ARGB_8888);
                    Canvas textCanvas = new Canvas(textBitmap);

                    textCanvas.drawText(highLineLrc, highLineWidth / 2, mTextHeight + mDividerHeight, hPaint);
                    canvas.drawBitmap(textBitmap, leftOffset, mDividerHeight + (mDividerHeight + mTextHeight) + (mTextHeight + mDividerHeight) * (currentPosition - 1), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void scrollTo(int line, long duration) {
        float offset = getOffset(line);
        endAnimation();

        mAnimator = ValueAnimator.ofFloat(mOffset, offset);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    private void endAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }

    private int pointNum = 3;

    private float getOffset(int line) {
        if (list.get(line).getOffset() == Float.MIN_VALUE) {
            float offset = getHeight() / 2;
            for (int i = 1; i <= line; i++) {
                offset -= (mTextHeight + mTextHeight) / 2 + mDividerHeight;
            }
            list.get(line).setOffset(offset);
        }

        return list.get(line).getOffset();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void init() {
        isFinish = false;
        isFling = false;
        currentPosition = 0;
        lastPosition = 0;
        setScrollY(0);
        startTime = System.currentTimeMillis();
        mScroller.forceFinished(true);
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

    private int findShowLine(long time) {
        int left = 0;
        int right = list.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            double middleTime = list.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= list.size() || time < list.get(middle + 1).getTime()) {
                    return middle;
                }

                left = middle + 1;
            }
        }

        return 0;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrY();

            invalidate();
        }

        if (isFling && mScroller.isFinished()) {
            isFling = false;
            setScrollY((int) v);
        }
    }

    private boolean isFling;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouching = false;
            if (hasLrc() && !isFling) {
                setScrollY((int) v);
//                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (hasLrc()) {
                isTouching = true;
                invalidate();
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (hasLrc()) {
                mOffset += distanceY;
//                mOffset = Math.min(mOffset, getOffset(0));
//                mOffset = Math.max(mOffset, getOffset(list.size() - 1)
                scrollBy(0, (int) distanceY);
//                setScrollY((int) ( mOffset));
                invalidate();
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (hasLrc()) {
                mScroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 0, (int) getOffset(list.size() - 1), (int) getOffset(0));
                isFling = true;
//                setScrollY((int) ( mScroller.getCurrY()));
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (hasLrc()) {
//                int centerLine = getCenterLine();
//                long centerLineTime = mLrcEntryList.get(centerLine).getTime();
//                // onPlayClick 消费了才更新 UI
//                if (mOnPlayClickListener != null && mOnPlayClickListener.onPlayClick(centerLineTime)) {
//                    isShowTimeline = false;
//                    removeCallbacks(hideTimelineRunnable);
//                    mCurrentLine = centerLine;
//                    invalidate();
//                    return true;
//                }
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    private boolean hasLrc() {
        if (list == null || list.size() == 0)
            return false;
        return true;
    }

}
