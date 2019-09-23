package com.jiang.myeasyutils.weight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jiang.myeasyutils.R;
import com.jiang.myeasyutils.adapters.VerticalPageAdapter;
import com.jiang.mylibrary.verticalviewpager.VerticalPageTransformer;
import com.jiang.mylibrary.verticalviewpager.VerticalViewPager;
import com.jiang.mylibrary.wights.viewpager.transformer.VerticalTransformer1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class VerticalCardView extends FrameLayout {

    private VerticalViewPager viewPager;
    private VerticalPageAdapter mVerticalPageAdapter;
    private String[] imgs = new String[]{
            "https://assets.mydeertrip.com/1544693631791.jpg",
            "https://assets.mydeertrip.com/1544701572083.jpg"
//            "https://assets.mydeertrip.com/1551349556582.jpg",
//            "https://assets.mydeertrip.com/1551349541453.jpg",
//            "https://assets.mydeertrip.com/1544701572083.jpg",
//            "https://assets.mydeertrip.com/1544701539437.jpg"
    };

    public VerticalCardView(@NonNull Context context) {
        super(context);
        init();
    }
    public VerticalCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_vertical_card, this);
        viewPager = inflate.findViewById(R.id.vertical_viewpager);
        List<String> datas = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int anInt = random.nextInt(imgs.length);
            datas.add(imgs[i % 2]);
        }
        mVerticalPageAdapter = new VerticalPageAdapter(datas, getContext(), viewPager);
        viewPager.setOffscreenPageLimit(datas.size());
        viewPager.setPageTransformer(true, new VerticalTransformer1());
        viewPager.setAdapter(mVerticalPageAdapter);
    }

}
