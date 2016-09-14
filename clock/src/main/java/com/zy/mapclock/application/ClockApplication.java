package com.zy.mapclock.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by bobowich
 * Time: 2016/9/14.
 */
public class ClockApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
