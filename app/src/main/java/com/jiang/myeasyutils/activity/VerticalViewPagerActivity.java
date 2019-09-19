package com.jiang.myeasyutils.activity;

import android.content.Context;
import android.content.Intent;

import com.jiang.myeasyutils.R;
import com.jiang.myeasyutils.adapters.VerticalPageAdapter;
import com.jiang.myeasyutils.base.BaseActivity;
import com.jiang.mylibrary.verticalviewpager.VerticalPageTransformer;
import com.jiang.mylibrary.verticalviewpager.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hongliJiang on 2019/9/18 10:55
 * 描述：
 */
public class VerticalViewPagerActivity extends BaseActivity {
    @BindView(R.id.viewpager)
    VerticalViewPager viewpager;

    public static void start(Context context) {
        Intent starter = new Intent(context, VerticalViewPagerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
        List<String> pages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pages.add("第" + (i + 1) + "页");
        }
        VerticalPageAdapter verticalPageAdapter = new VerticalPageAdapter(pages, this,viewpager);
        viewpager.setPageTransformer(true, new VerticalPageTransformer());
        viewpager.setAdapter(verticalPageAdapter);
//        if(pages.size() > 1) {
//            viewpager.setCurrentItem(((Short.MAX_VALUE / 2) / pages.size()) * pages.size(), false);
//        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int setView() {
        return R.layout.activity_vertical_viewpager;
    }


}
