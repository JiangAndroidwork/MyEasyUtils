package com.jiang.myeasyutils.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.jiang.myeasyutils.weight.VerticalCardView;
import com.jiang.mylibrary.slidingup.ScreenUtils;
import com.jiang.mylibrary.utils.common.DensityUtil;

import java.util.List;
import java.util.Random;

public class HVPagerAdapter extends PagerAdapter {
    private final int MInt34;
    private List<String> mList;
    private Context mContext;

    private final int mViewWidth;
    private final int mViewHeight;
    private int[] colors =new int[]{Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN};

    public HVPagerAdapter(List<String> list, Context context) {
        mList = list;
        mContext = context;
        MInt34 = DensityUtil.dip2px(mContext, 34);
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        //获取正中间显示宽高
        mViewWidth = screenWidth - MInt34 * 2;
        mViewHeight = mViewWidth *485/305;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        VerticalCardView cardView = new VerticalCardView(mContext);
        container.addView(cardView);
        return cardView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
