package com.jiang.myeasyutils.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setView());
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    protected abstract int setView();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();
}
