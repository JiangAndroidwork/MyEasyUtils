package com.jiang.myeasyutils.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jiang.myeasyutils.R;
import com.jiang.mylibrary.slidingup.ScreenUtils;
import com.jiang.mylibrary.utils.common.DensityUtil;
import com.jiang.mylibrary.verticalviewpager.VerticalViewPager;
import com.jiang.mylibrary.wights.glide.GlideRoundTransform;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VerticalPageAdapter extends PagerAdapter {
    private  VerticalViewPager viewPager;
    private List<String> mList;
    private List<ImageView> mCurray;
    private Context mContext;
    private int[] colors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};
    private final int mWidth;
    private final int mHeight;

    public VerticalPageAdapter(List<String> list, Context context, VerticalViewPager viewPager) {
        mList = list;

        mContext = context;
        this.viewPager = viewPager;
        mCurray = new ArrayList<>();

        initDatas(mList);

        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        mWidth = screenWidth - DensityUtil.dip2px(mContext, 70);
        mHeight = mWidth *475/375;
    }

    private void initDatas(List<String> list) {
        for (int i = 0; i < mList.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            Glide.with(mContext).load(mList.get(i)).override(500,700).into(imageView);
            mCurray.add(imageView);
        }
        if (mCurray.size() > 1 && mCurray.size() < 4) {
            initDatas(list);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(mWidth, mHeight);
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        RequestOptions options = new RequestOptions();
        options.bitmapTransform(new GlideRoundTransform(mContext, 4));
        Glide.with(mContext).load(mList.get(position)).override(mWidth,mHeight).apply(options).into(imageView);
        container.addView(imageView, vl);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);

        int position = viewPager.getCurrentItem();

        if (position == 0) {
            position = mList.size() - 2;
            viewPager.setCurrentItem(position,false);
        } else if (position == mList.size() - 1) {
            position = 1;
            viewPager.setCurrentItem(position,false);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String name = mList.get(position);
        return name;
    }
}
