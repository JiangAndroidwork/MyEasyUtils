package com.jiang.myeasyutils.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.jiang.myeasyutils.R;
import com.jiang.myeasyutils.base.BaseActivity;
import com.jiang.mylibrary.wights.plancalendar.CalendarSelectView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hongliJiang on 2019/9/23 15:28
 * 描述：日历
 */
public class CalendarActivity extends BaseActivity {
    @BindView(R.id.calendar_view)
    CalendarSelectView calendarView;

    public static void start(Context context) {
        Intent starter = new Intent(context, CalendarActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setView() {
        return R.layout.activity_calendar;
    }

    @Override
    protected void initView() {
        Calendar instance = Calendar.getInstance();
        Date time = instance.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sf.format(time);

        instance.add(Calendar.DAY_OF_MONTH, 10);
        Date date2 = instance.getTime();
        String format2 = sf.format(date2);
        calendarView.setSelectedRande(format, format2);
        instance.add(Calendar.MONTH, 12);
        Date date3 = instance.getTime();
        String format3 = sf.format(date3);
        calendarView.setShowDate(format, format3);
        calendarView.setOnDateSelected((startDate, endDate) -> {

        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

}
