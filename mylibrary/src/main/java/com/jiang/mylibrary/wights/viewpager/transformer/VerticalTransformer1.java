package com.jiang.mylibrary.wights.viewpager.transformer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jiang.mylibrary.utils.common.DensityUtil;

/**
 * Created by hongliJiang on 2019/9/19 16:38
 * 描述：竖直动画 重叠
 */
public class VerticalTransformer1 implements ViewPager.PageTransformer {
    public static final float MAX_SCALE = 0.94f; //竖直高度最大伸缩比


    @Override
    public void transformPage(View view, float position) {

        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            view.setAlpha(0);
        } else if (position > 1) {
            view.setAlpha(0);
        } else {
            view.setAlpha(1);
            if (position <= 0) {
                view.setAlpha(1);
                view.setPivotY(0);
                view.setPivotX(view.getWidth() / 2);
                view.setScaleY(MAX_SCALE);
                view.setScaleX(1);
            } else if (position > 0) {

                float scaleX = (1 - MAX_SCALE) * (1 - position) + MAX_SCALE;
                view.setPivotY(0);
                view.setPivotX(view.getWidth() / 2);

                view.setScaleY(MAX_SCALE);
                view.setScaleX(scaleX);
                //位移
                view.setTranslationY((-position * view.getHeight() + (1 - MAX_SCALE) * view.getHeight()) - ((1 - MAX_SCALE) * view.getHeight()) * (1 - position));
            }

        }
    }
}
