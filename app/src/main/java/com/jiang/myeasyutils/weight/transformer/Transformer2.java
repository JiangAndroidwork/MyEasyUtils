package com.jiang.myeasyutils.weight.transformer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jiang.myeasyutils.activity.TestAnimationActivity;
import com.jiang.mylibrary.slidingup.ScreenUtils;
import com.jiang.mylibrary.utils.common.DensityUtil;

public class Transformer2 implements ViewPager.PageTransformer {
    private final float mInt66;
    private final float mInt40;
    private final float mInt55;
    private final float mScreenWidth;
    private final float mInt10;

    public Transformer2(Context context) {
        mInt10 = DensityUtil.dip2px(context, 10);
        mInt66 = DensityUtil.dip2px(context, 66);
        mInt55 = DensityUtil.dip2px(context, 55);
        mInt40 = DensityUtil.dip2px(context, 40);
        mScreenWidth = ScreenUtils.getScreenWidth(context);

    }

    @Override
    public void transformPage(View page, float position) {

        if (position < -2 || position > 2) {
            page.setAlpha(0);
        } else {
            page.setAlpha(1);
        }
        float scalfloat = 1 - (1 - mInt55 / mInt66) * Math.abs(position);
//        page.setPivotX(page.getWidth()/2);
//        page.setPivotY(page.getHeight()/2);
//        page.setScaleY(scalfloat);

        if (position >= 0) {
            float transX = -(mScreenWidth - mInt66 - mInt10) * position;
            page.setTranslationX(transX);

        } else {
            float transX = (mScreenWidth - mInt66 -  mInt10) * Math.abs(position);
            page.setTranslationX(transX);
        }
        page.setPivotY(mInt66 / 2);
        page.setScaleY(scalfloat);
        page.setScaleX(scalfloat);
    }
}
