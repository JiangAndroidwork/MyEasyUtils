package com.jiang.mylibrary.wights.viewpager.transformer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.jiang.mylibrary.slidingup.ScreenUtils;
import com.jiang.mylibrary.utils.common.DensityUtil;

/**
 * Created by hongliJiang on 2019/9/19 13:26
 * 描述：viewpager横滑动画 两边小
 */
public class HorizontalTransformer1 implements ViewPager.PageTransformer {
    private int MInt34;
    private int MInt23;
    private Context mContext;
    public static final float MIN_SCALE = 0.6f;
    public static final float MAX_SCALE = 0.81f;
    public HorizontalTransformer1(Context context) {
        mContext = context;
        MInt23 = DensityUtil.dip2px(mContext, 23);

    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        float scaleFactor = MIN_SCALE + (MAX_SCALE - MIN_SCALE) * (1 - Math.abs(position));
        if (position <= 0) {
            page.setTranslationX((0.3f*page.getWidth()-MInt23)*Math.abs(position));
            page.setPivotY(0.5f * page.getHeight());
            page.setPivotX(0.5f * page.getWidth());
            page.setScaleY(scaleFactor);
            page.setScaleX(scaleFactor);
        } else if (position > 0 ) {
            page.setTranslationX((0.3f*page.getWidth()-MInt23)*-position);
            page.setPivotY(0.5f * page.getHeight());
            page.setPivotX(0.5f * page.getWidth());
            page.setScaleY(scaleFactor);
            page.setScaleX(scaleFactor);
        }


    }
}
