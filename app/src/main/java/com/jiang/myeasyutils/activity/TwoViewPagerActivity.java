package com.jiang.myeasyutils.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.myeasyutils.R;
import com.jiang.myeasyutils.adapters.MyPagerAdapter;
import com.jiang.myeasyutils.adapters.MyPagerAdapter2;
import com.jiang.myeasyutils.base.BaseActivity;
import com.jiang.myeasyutils.weight.transformer.Transformer1;
import com.jiang.mylibrary.utils.common.DensityUtil;
import com.jiang.mylibrary.wights.viewpager.LinkageViewPager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hongliJiang on 2019/9/24 11:39
 * 描述：两个viewpager联动
 */
public class TwoViewPagerActivity extends BaseActivity {
    @BindView(R.id.link_viewpager1)
    LinkageViewPager linkViewpager1;
    @BindView(R.id.link_viewpager2)
    LinkageViewPager linkViewpager2;
    private ArrayList<View> mPageViews;
    private ArrayList<View> mFramePageViews;
    private MyPagerAdapter mFramePageAdapter;
    private int[] colors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};
    private MyPagerAdapter mMyPagerAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, TwoViewPagerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setView() {
        return R.layout.activity_two_viewpager;
    }

    @Override
    protected void initView() {
        linkViewpager2.setOnPageChangeListener(new LinkageViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("偏移量==", position + "===" + positionOffset + "===" + positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPageViews = new ArrayList<View>();
        mFramePageViews = new ArrayList<View>();
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Random random = new Random();
        for (int i = 0; i < 20; i++) {

            View view = View.inflate(this, R.layout.item_view, null);
            TextView tvText = view.findViewById(R.id.tv_text);
            tvText.setText(i + "");
            TextView tv1 = new TextView(this);
            tv1.setLayoutParams(vl);
            ViewGroup.LayoutParams v2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v2.width = DensityUtil.dip2px(this, 55);
            v2.height = DensityUtil.dip2px(this, 55);
            tv1.setGravity(Gravity.CENTER);
            tv1.setText(i + "");
            int anInt = random.nextInt(colors.length);
            tv1.setBackgroundColor(colors[random.nextInt(colors.length)]);
            tvText.setBackgroundColor(colors[random.nextInt(colors.length)]);
            mPageViews.add(tv1);

            mFramePageViews.add(view);
        }
        mMyPagerAdapter = new MyPagerAdapter(mPageViews);
        linkViewpager1.setAdapter(mMyPagerAdapter);
        linkViewpager1.setOffscreenPageLimit(mPageViews.size());
        MyPagerAdapter2 myPagerAdapter = new MyPagerAdapter2(mFramePageViews);
        linkViewpager2.setOffscreenPageLimit(mPageViews.size());
        linkViewpager2.setAdapter(myPagerAdapter);
        linkViewpager2.setPageTransformer(true, new Transformer1(this));
        linkViewpager1.setFollowViewPager(linkViewpager2);
        linkViewpager2.setFollowViewPager(linkViewpager1);
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
