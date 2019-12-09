package com.jiang.myeasyutils.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.jiang.myeasyutils.R;
import com.jiang.myeasyutils.adapters.HVPagerAdapter;
import com.jiang.myeasyutils.base.BaseActivity;
import com.jiang.mylibrary.wights.viewpager.HorizontalViewPager;
import com.jiang.mylibrary.wights.viewpager.transformer.HorizontalTransformer1;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hongliJiang on 2019/9/19 11:25
 * 描述：横滑嵌套竖滑
 */
public class HVViewpagerActivity extends BaseActivity {
    @BindView(R.id.horizontal_viewpager)
    HorizontalViewPager horizontalViewpager;
    private HVPagerAdapter mHVPagerAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, HVViewpagerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setView() {
        return R.layout.activity_hv_viewpager;
    }

    @Override
    protected void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i + "");
        }
        mHVPagerAdapter = new HVPagerAdapter(list, this);
        horizontalViewpager.setOffscreenPageLimit(list.size());
        horizontalViewpager.setPageTransformer(true, new HorizontalTransformer1(this));
        horizontalViewpager.setAdapter(mHVPagerAdapter);
        horizontalViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

}
