package com.dy.learnokhttp.activity;

import android.app.Application;

import com.dy.learnokhttp.util.OkHttpUtils;

/**
 * Created by Dyan on 2016/3/29.
 */
public class MyApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		OkHttpUtils.configClient(this);
	}
}
