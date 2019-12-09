package com.jiang.myeasyutils.activity;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.Guideline;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.jiang.myeasyutils.R;
import com.jiang.myeasyutils.adapters.CommonPagerAdapter;
import com.jiang.myeasyutils.adapters.MyPagerAdapter;
import com.jiang.myeasyutils.base.BaseActivity;
import com.jiang.myeasyutils.weight.transformer.Transformer1;
import com.jiang.myeasyutils.weight.transformer.Transformer2;
import com.jiang.mylibrary.wights.viewpager.LinkageViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hongliJiang on 2019/10/14 11:53
 * 描述：项目test
 */
public class TestAnimationActivity extends BaseActivity {
    @BindView(R.id.viewpager_head)
    LinkageViewPager viewpagerHead;
    @BindView(R.id.guideline2)
    Guideline guideline2;
    @BindView(R.id.viewpager_small)
    LinkageViewPager viewpagerSmall;

    public static void start(Context context) {
        Intent starter = new Intent(context, TestAnimationActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setView() {
        return R.layout.activity_test_viewpager_spliding;
    }

    @Override
    protected void initView() {
        ArrayList<View> bigView = new ArrayList<>();
        ArrayList<View> smallView = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(R.mipmap.ic_bg_test);
            bigView.add(imageView);

            View view = View.inflate(this, R.layout.item_small_vp, null);
            smallView.add(view);
        }
        MyPagerAdapter adapterHead = new MyPagerAdapter(bigView);
        viewpagerHead.setAdapter(adapterHead);
        MyPagerAdapter smallAdapter2 = new MyPagerAdapter(smallView);
        viewpagerSmall.setOffscreenPageLimit(smallView.size());
        viewpagerSmall.setAdapter(smallAdapter2);
        viewpagerSmall.setPageTransformer(true, new Transformer1(this));
        viewpagerHead.setFollowViewPager(viewpagerSmall);
        viewpagerSmall.setFollowViewPager(viewpagerHead);
    }

    @Override
    protected void initData() {

    }

    int positionHead;
    int postionSmall;
    float percentHead;
    float percentSmall;
    float pxHead;
    float pxSmall;

    @Override
    protected void initListener() {
    }

}
