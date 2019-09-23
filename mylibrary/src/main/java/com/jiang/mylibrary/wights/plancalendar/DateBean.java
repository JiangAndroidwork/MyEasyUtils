package com.jiang.mylibrary.wights.plancalendar;

import java.util.Date;

public class DateBean {
    //item类型
    public static int ITEM_TYPE_DAY = 1;//日期item
    public static int ITEM_TYPE_MONTH = 2;//月份item
    int itemType = 1;//默认是日期item

    //item状态
    public static int ITEM_STATE_BEGIN_DATE = 1;//开始日期
    public static int ITEM_STATE_END_DATE = 2;//结束日期
    public static int ITEM_STATE_SELECTED = 3;//选中状态
    public static int ITEM_STATE_NORMAL = 4;//正常状态
    public static int ITEM_STATE_STAR_CHANGE = 5;//开始日期 改变背景状态

    public int itemState = ITEM_STATE_NORMAL;

    Date date;//具体日期
    String day;//一个月的某天
    String monthStr;//月份
    boolean canClickDate;//是否可以点击 这个日期

    public boolean isCanClickDate() {
        return canClickDate;
    }

    public void setCanClickDate(boolean canClickDate) {
        this.canClickDate = canClickDate;
    }

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }

    public static int getItemTypeMonth() {
        return ITEM_TYPE_MONTH;
    }

    public static void setItemTypeMonth(int itemTypeMonth) {
        DateBean.ITEM_TYPE_MONTH = itemTypeMonth;
    }

    public static int getItemTypeDay() {
        return ITEM_TYPE_DAY;
    }

    public static void setItemTypeDay(int itemTypeDay) {
        DateBean.ITEM_TYPE_DAY = itemTypeDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
